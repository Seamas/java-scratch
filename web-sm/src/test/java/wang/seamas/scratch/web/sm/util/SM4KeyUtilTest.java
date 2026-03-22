package wang.seamas.scratch.web.sm.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SM4KeyUtil 工具类单元测试
 *
 * @author Seamas
 * @since 1.0.1
 */
@DisplayName("SM4 密钥工具类测试")
class SM4KeyUtilTest {

    @Test
    @DisplayName("生成 SM4 密钥 - 验证密钥长度")
    void testGenerateKey() {
        byte[] key = SM4KeyUtil.generateKey();

        assertNotNull(key, "密钥不应为空");
        assertEquals(SM4KeyUtil.KEY_LENGTH, key.length,
                "密钥长度应为 " + SM4KeyUtil.KEY_LENGTH + " 字节");
    }

    @Test
    @DisplayName("生成 SM4 密钥 - 验证十六进制格式")
    void testGenerateKeyHex() {
        String keyHex = SM4KeyUtil.generateKeyHex();

        assertNotNull(keyHex, "密钥不应为空");
        assertEquals(SM4KeyUtil.KEY_HEX_LENGTH, keyHex.length(),
                "密钥十六进制字符串长度应为 " + SM4KeyUtil.KEY_HEX_LENGTH + " 个字符");

        // 验证是有效的十六进制字符串
        assertTrue(keyHex.matches("[0-9a-fA-F]+"),
                "密钥应只包含十六进制字符");
    }

    @RepeatedTest(5)
    @DisplayName("生成 SM4 密钥 - 多次生成应产生不同密钥")
    void testGenerateKeyRandomness() {
        String key1 = SM4KeyUtil.generateKeyHex();
        String key2 = SM4KeyUtil.generateKeyHex();

        assertNotEquals(key1, key2, "两次生成的密钥应不同");
    }

    @Test
    @DisplayName("解析密钥 - 正确解析有效的密钥字符串")
    void testParseKey() {
        String keyHex = SM4KeyUtil.generateKeyHex();

        byte[] key = SM4KeyUtil.parseKey(keyHex);

        assertNotNull(key, "解析后的密钥不应为空");
        assertEquals(SM4KeyUtil.KEY_LENGTH, key.length,
                "解析后的密钥长度应为 " + SM4KeyUtil.KEY_LENGTH + " 字节");
    }

    @Test
    @DisplayName("解析密钥 - 无效的密钥格式应抛出异常")
    void testParseInvalidKey() {
        // 长度不足的密钥
        assertThrows(IllegalArgumentException.class,
                () -> SM4KeyUtil.parseKey("123456"),
                "长度不足的密钥应抛出异常");

        // 空密钥
        assertThrows(IllegalArgumentException.class,
                () -> SM4KeyUtil.parseKey(null),
                "空密钥应抛出异常");

        // 过长的密钥
        assertThrows(IllegalArgumentException.class,
                () -> SM4KeyUtil.parseKey("a".repeat(40)),
                "过长的密钥应抛出异常");

        // 包含非十六进制字符的密钥（长度正确）
        assertThrows(IllegalArgumentException.class,
                () -> SM4KeyUtil.parseKey("gggggggggggggggggggggggggggggggg"),
                "包含非十六进制字符的密钥应抛出异常");
    }

    @Test
    @DisplayName("编码密钥 - 正确编码字节数组")
    void testEncodeKey() {
        byte[] key = SM4KeyUtil.generateKey();

        String keyHex = SM4KeyUtil.encodeKey(key);

        assertNotNull(keyHex, "编码后的密钥不应为空");
        assertEquals(SM4KeyUtil.KEY_HEX_LENGTH, keyHex.length(),
                "编码后的密钥长度应为 " + SM4KeyUtil.KEY_HEX_LENGTH + " 个字符");
    }

    @Test
    @DisplayName("编码密钥 - 无效的密钥长度应抛出异常")
    void testEncodeInvalidKey() {
        // 长度不足的字节数组
        assertThrows(IllegalArgumentException.class,
                () -> SM4KeyUtil.encodeKey(new byte[8]),
                "长度不足的字节数组应抛出异常");

        // 空字节数组
        assertThrows(IllegalArgumentException.class,
                () -> SM4KeyUtil.encodeKey(null),
                "空字节数组应抛出异常");

        // 过长的字节数组
        assertThrows(IllegalArgumentException.class,
                () -> SM4KeyUtil.encodeKey(new byte[20]),
                "过长的字节数组应抛出异常");
    }

    @Test
    @DisplayName("批量生成密钥")
    void testGenerateKeys() {
        int count = 5;
        String[] keys = SM4KeyUtil.generateKeys(count);

        assertNotNull(keys, "密钥数组不应为空");
        assertEquals(count, keys.length, "密钥数组长度应正确");

        // 验证每个密钥
        for (String key : keys) {
            assertNotNull(key, "密钥不应为空");
            assertEquals(SM4KeyUtil.KEY_HEX_LENGTH, key.length(),
                    "每个密钥长度应正确");
            assertTrue(SM4KeyUtil.isValidKey(key), "每个密钥应有效");
        }

        // 验证密钥不重复
        for (int i = 0; i < keys.length; i++) {
            for (int j = i + 1; j < keys.length; j++) {
                assertNotEquals(keys[i], keys[j],
                        "批量生成的密钥应不重复");
            }
        }
    }

    @Test
    @DisplayName("批量生成密钥 - 无效的数量应抛出异常")
    void testGenerateKeysInvalidCount() {
        assertThrows(IllegalArgumentException.class,
                () -> SM4KeyUtil.generateKeys(0),
                "数量为 0 应抛出异常");

        assertThrows(IllegalArgumentException.class,
                () -> SM4KeyUtil.generateKeys(-1),
                "负数数量应抛出异常");
    }

    @Test
    @DisplayName("验证密钥有效性 - 有效的密钥")
    void testIsValidKey() {
        String validKey = SM4KeyUtil.generateKeyHex();

        assertTrue(SM4KeyUtil.isValidKey(validKey), "有效的密钥应返回 true");
        assertTrue(SM4KeyUtil.isValidKey(validKey.toUpperCase()),
                "大写的有效密钥应返回 true");
    }

    @Test
    @DisplayName("验证密钥有效性 - 无效的密钥")
    void testIsInvalidKey() {
        // 长度不足
        assertFalse(SM4KeyUtil.isValidKey("123456"),
                "长度不足的密钥应返回 false");

        // 空密钥
        assertFalse(SM4KeyUtil.isValidKey((String)null),
                "空密钥应返回 false");

        // 过长的密钥
        assertFalse(SM4KeyUtil.isValidKey("a".repeat(40)),
                "过长的密钥应返回 false");

        // 包含非十六进制字符
        assertFalse(SM4KeyUtil.isValidKey("gggggggggggggggggggggggggggggggg"),
                "包含非十六进制字符的密钥应返回 false");
    }

    @Test
    @DisplayName("验证密钥有效性 - 字节数组格式")
    void testIsValidKeyBytes() {
        byte[] validKey = SM4KeyUtil.generateKey();

        assertTrue(SM4KeyUtil.isValidKey(validKey), "有效的密钥字节数组应返回 true");

        // 长度不足
        assertFalse(SM4KeyUtil.isValidKey(new byte[8]),
                "长度不足的字节数组应返回 false");

        // 空密钥
        assertFalse(SM4KeyUtil.isValidKey((byte[])null),
                "空字节数组应返回 false");

        // 过长的密钥
        assertFalse(SM4KeyUtil.isValidKey(new byte[20]),
                "过长的字节数组应返回 false");
    }

    @Test
    @DisplayName("生成初始化向量 IV")
    void testGenerateIV() {
        byte[] iv = SM4KeyUtil.generateIV();

        assertNotNull(iv, "IV 不应为空");
        assertEquals(SM4KeyUtil.BLOCK_SIZE, iv.length,
                "IV 长度应为 " + SM4KeyUtil.BLOCK_SIZE + " 字节");
    }

    @Test
    @DisplayName("生成初始化向量 IV - 十六进制格式")
    void testGenerateIVHex() {
        String ivHex = SM4KeyUtil.generateIVHex();

        assertNotNull(ivHex, "IV 不应为空");
        assertEquals(SM4KeyUtil.KEY_HEX_LENGTH, ivHex.length(),
                "IV 十六进制字符串长度应为 " + SM4KeyUtil.KEY_HEX_LENGTH + " 个字符");
        assertTrue(ivHex.matches("[0-9a-fA-F]+"),
                "IV 应只包含十六进制字符");
    }

    @Test
    @DisplayName("解析初始化向量 IV")
    void testParseIV() {
        String ivHex = SM4KeyUtil.generateIVHex();

        byte[] iv = SM4KeyUtil.parseIV(ivHex);

        assertNotNull(iv, "解析后的 IV 不应为空");
        assertEquals(SM4KeyUtil.BLOCK_SIZE, iv.length,
                "解析后的 IV 长度应为 " + SM4KeyUtil.BLOCK_SIZE + " 字节");
    }

    @Test
    @DisplayName("解析初始化向量 IV - 无效的格式应抛出异常")
    void testParseInvalidIV() {
        assertThrows(IllegalArgumentException.class,
                () -> SM4KeyUtil.parseIV("123456"),
                "长度不足的 IV 应抛出异常");

        assertThrows(IllegalArgumentException.class,
                () -> SM4KeyUtil.parseIV(null),
                "空 IV 应抛出异常");

        assertThrows(IllegalArgumentException.class,
                () -> SM4KeyUtil.parseIV("gggggggggggggggggggggggggggggggg"),
                "包含非十六进制字符的 IV 应抛出异常");
    }

    @Test
    @DisplayName("生成密钥和 IV 组合")
    void testGenerateKeyWithIV() {
        SM4KeyUtil.SM4KeyWithIV keyWithIV = SM4KeyUtil.generateKeyWithIV();

        assertNotNull(keyWithIV, "密钥和 IV 组合不应为空");
        assertNotNull(keyWithIV.key(), "密钥不应为空");
        assertNotNull(keyWithIV.iv(), "IV 不应为空");

        // 验证密钥
        assertEquals(SM4KeyUtil.KEY_HEX_LENGTH, keyWithIV.key().length(),
                "密钥长度应正确");
        assertTrue(SM4KeyUtil.isValidKey(keyWithIV.key()), "密钥应有效");

        // 验证 IV
        assertEquals(SM4KeyUtil.KEY_HEX_LENGTH, keyWithIV.iv().length(),
                "IV 长度应正确");
    }

    @Test
    @DisplayName("密钥和 IV 应不同")
    void testKeyAndIVAreDifferent() {
        SM4KeyUtil.SM4KeyWithIV keyWithIV = SM4KeyUtil.generateKeyWithIV();

        assertNotEquals(keyWithIV.key(), keyWithIV.iv(),
                "密钥和 IV 应不同");
    }

    @Test
    @DisplayName("密钥编解码一致性")
    void testKeyEncodeDecodeConsistency() {
        byte[] originalKey = SM4KeyUtil.generateKey();
        String keyHex = SM4KeyUtil.encodeKey(originalKey);
        byte[] decodedKey = SM4KeyUtil.parseKey(keyHex);

        assertArrayEquals(originalKey, decodedKey,
                "编码后再解码的密钥应与原始密钥一致");
    }

    @Test
    @DisplayName("IV 编解码一致性")
    void testIVEncodeDecodeConsistency() {
        byte[] originalIV = SM4KeyUtil.generateIV();
        String ivHex = bytesToHex(originalIV);
        byte[] decodedIV = SM4KeyUtil.parseIV(ivHex);

        assertArrayEquals(originalIV, decodedIV,
                "编码后再解码的 IV 应与原始 IV 一致");
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
