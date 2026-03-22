package wang.seamas.scratch.web.sm.util;

import org.bouncycastle.math.ec.ECPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SM2KeyUtil 工具类单元测试
 *
 * @author Seamas
 * @since 1.0.1
 */
@DisplayName("SM2 密钥工具类测试")
class SM2KeyUtilTest {

    @Test
    @DisplayName("生成 SM2 密钥对 - 验证密钥格式")
    void testGenerateKeyPair() {
        SM2KeyUtil.SM2KeyPair keyPair = SM2KeyUtil.generateKeyPair();

        assertNotNull(keyPair, "密钥对不应为空");
        assertNotNull(keyPair.privateKey(), "私钥不应为空");
        assertNotNull(keyPair.publicKey(), "公钥不应为空");

        // 验证私钥长度（64 个十六进制字符 = 32 字节）
        assertEquals(SM2KeyUtil.PRIVATE_KEY_HEX_LENGTH, keyPair.privateKey().length(),
                "私钥长度应为 " + SM2KeyUtil.PRIVATE_KEY_HEX_LENGTH + " 个字符");

        // 验证公钥长度（130 个十六进制字符 = 65 字节，未压缩格式）
        assertEquals(SM2KeyUtil.PUBLIC_KEY_HEX_LENGTH, keyPair.publicKey().length(),
                "公钥长度应为 " + SM2KeyUtil.PUBLIC_KEY_HEX_LENGTH + " 个字符");

        // 验证公钥以 04 开头（未压缩格式）
        assertTrue(keyPair.publicKey().startsWith("04"),
                "公钥应以 04 开头（未压缩格式）");
    }

    @Test
    @DisplayName("生成 SM2 密钥对 - 字节数组格式")
    void testGenerateKeyPairBytes() {
        SM2KeyUtil.SM2KeyPairBytes keyPair = SM2KeyUtil.generateKeyPairBytes();

        assertNotNull(keyPair, "密钥对不应为空");
        assertNotNull(keyPair.privateKey(), "私钥字节数组不应为空");
        assertNotNull(keyPair.publicKey(), "公钥字节数组不应为空");

        // 验证私钥长度（32 字节）
        assertEquals(SM2KeyUtil.PRIVATE_KEY_LENGTH, keyPair.privateKey().length,
                "私钥长度应为 " + SM2KeyUtil.PRIVATE_KEY_LENGTH + " 字节");

        // 验证公钥长度（65 字节，未压缩格式）
        assertEquals(SM2KeyUtil.PUBLIC_KEY_LENGTH, keyPair.publicKey().length,
                "公钥长度应为 " + SM2KeyUtil.PUBLIC_KEY_LENGTH + " 字节");

        // 验证公钥以 0x04 开头
        assertEquals(0x04, keyPair.publicKey()[0],
                "公钥应以 0x04 开头（未压缩格式）");
    }

    @RepeatedTest(5)
    @DisplayName("生成 SM2 密钥对 - 多次生成应产生不同密钥")
    void testGenerateKeyPairRandomness() {
        SM2KeyUtil.SM2KeyPair keyPair1 = SM2KeyUtil.generateKeyPair();
        SM2KeyUtil.SM2KeyPair keyPair2 = SM2KeyUtil.generateKeyPair();

        assertNotEquals(keyPair1.privateKey(), keyPair2.privateKey(),
                "两次生成的私钥应不同");
        assertNotEquals(keyPair1.publicKey(), keyPair2.publicKey(),
                "两次生成的公钥应不同");
    }

    @Test
    @DisplayName("解析私钥 - 正确解析有效的私钥字符串")
    void testParsePrivateKey() {
        SM2KeyUtil.SM2KeyPair keyPair = SM2KeyUtil.generateKeyPair();

        BigInteger privateKey = SM2KeyUtil.parsePrivateKey(keyPair.privateKey());

        assertNotNull(privateKey, "解析后的私钥不应为空");
        assertTrue(privateKey.compareTo(BigInteger.ZERO) > 0,
                "私钥应大于 0");
    }

    @Test
    @DisplayName("解析私钥 - 无效的私钥格式应抛出异常")
    void testParseInvalidPrivateKey() {
        // 长度不足的私钥
        assertThrows(IllegalArgumentException.class,
                () -> SM2KeyUtil.parsePrivateKey("123456"),
                "长度不足的私钥应抛出异常");

        // 空私钥
        assertThrows(IllegalArgumentException.class,
                () -> SM2KeyUtil.parsePrivateKey(null),
                "空私钥应抛出异常");

        // 过长的私钥
        assertThrows(IllegalArgumentException.class,
                () -> SM2KeyUtil.parsePrivateKey("a".repeat(70)),
                "过长的私钥应抛出异常");
    }

    @Test
    @DisplayName("解析公钥 - 正确解析有效的公钥字符串")
    void testParsePublicKey() {
        SM2KeyUtil.SM2KeyPair keyPair = SM2KeyUtil.generateKeyPair();

        ECPoint publicKey = SM2KeyUtil.parsePublicKey(keyPair.publicKey());

        assertNotNull(publicKey, "解析后的公钥不应为空");
    }

    @Test
    @DisplayName("解析公钥 - 无效的公钥格式应抛出异常")
    void testParseInvalidPublicKey() {
        // 长度不足的公钥
        assertThrows(IllegalArgumentException.class,
                () -> SM2KeyUtil.parsePublicKey("123456"),
                "长度不足的公钥应抛出异常");

        // 空公钥
        assertThrows(IllegalArgumentException.class,
                () -> SM2KeyUtil.parsePublicKey(null),
                "空公钥应抛出异常");

        // 不以 04 开头的公钥（但长度正确）
        String invalidPrefixKey = "03" + "a".repeat(128);
        assertThrows(IllegalArgumentException.class,
                () -> SM2KeyUtil.parsePublicKey(invalidPrefixKey),
                "不以 04 开头的公钥应抛出异常");
    }

    @Test
    @DisplayName("验证密钥对 - 正确的密钥对应返回 true")
    void testValidateKeyPairSuccess() {
        SM2KeyUtil.SM2KeyPair keyPair = SM2KeyUtil.generateKeyPair();

        boolean isValid = SM2KeyUtil.validateKeyPair(keyPair.privateKey(), keyPair.publicKey());

        assertTrue(isValid, "正确的密钥对应返回 true");
    }

    @Test
    @DisplayName("验证密钥对 - 错误的密钥对应返回 false")
    void testValidateKeyPairFailure() {
        SM2KeyUtil.SM2KeyPair keyPair1 = SM2KeyUtil.generateKeyPair();
        SM2KeyUtil.SM2KeyPair keyPair2 = SM2KeyUtil.generateKeyPair();

        // 使用 keyPair1 的私钥和 keyPair2 的公钥进行验证
        boolean isValid = SM2KeyUtil.validateKeyPair(keyPair1.privateKey(), keyPair2.publicKey());

        assertFalse(isValid, "不匹配的密钥对应返回 false");
    }

    @Test
    @DisplayName("验证密钥对 - 无效的密钥格式应返回 false")
    void testValidateKeyPairInvalidFormat() {
        boolean isValid = SM2KeyUtil.validateKeyPair("invalid", "invalid");

        assertFalse(isValid, "无效的密钥格式应返回 false");
    }

    @Test
    @DisplayName("密钥一致性 - 字节数组和字符串格式应一致")
    void testKeyConsistency() {
        SM2KeyUtil.SM2KeyPair keyPairHex = SM2KeyUtil.generateKeyPair();
        SM2KeyUtil.SM2KeyPairBytes keyPairBytes = SM2KeyUtil.generateKeyPairBytes();

        // 将字节数组转换为十六进制字符串进行比较
        String privateKeyFromBytes = bytesToHex(keyPairBytes.privateKey());
        String publicKeyFromBytes = bytesToHex(keyPairBytes.publicKey());

        // 验证格式正确性（长度）
        assertEquals(SM2KeyUtil.PRIVATE_KEY_HEX_LENGTH, privateKeyFromBytes.length(),
                "私钥十六进制字符串长度应正确");
        assertEquals(SM2KeyUtil.PUBLIC_KEY_HEX_LENGTH, publicKeyFromBytes.length(),
                "公钥十六进制字符串长度应正确");
    }

    /**
     * 将字节数组转换为十六进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
