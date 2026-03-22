package wang.seamas.scratch.web.sm.service;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wang.seamas.scratch.web.sm.config.CryptoProperties;
import wang.seamas.scratch.web.sm.dto.EncryptedRequest;
import wang.seamas.scratch.web.sm.exception.SM4DecryptException;
import wang.seamas.scratch.web.sm.util.SM2CryptoUtil;
import wang.seamas.scratch.web.sm.util.SM2KeyUtil;
import wang.seamas.scratch.web.sm.util.SM4CryptoUtil;
import wang.seamas.scratch.web.sm.util.SM4KeyUtil;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SM4DecryptService 单元测试
 *
 * @author Seamas
 * @since 1.0.1
 */
@DisplayName("SM4 解密服务测试")
class SM4DecryptServiceTest {

    private SM4DecryptService decryptService;
    private CryptoProperties properties;
    private String sm2PrivateKey;
    private String sm2PublicKey;

    @BeforeEach
    void setUp() {
        // 生成 SM2 密钥对
        SM2KeyUtil.SM2KeyPair keyPair = SM2KeyUtil.generateKeyPair();
        sm2PrivateKey = keyPair.privateKey();
        sm2PublicKey = keyPair.publicKey();

        // 配置属性
        properties = new CryptoProperties();
        properties.getSm4().setEnabled(true);
        properties.getSm2().setPrivateKey(sm2PrivateKey);
        properties.getSm4().setTimestampCheckEnabled(false); // 测试中关闭时间戳校验

        decryptService = new SM4DecryptService(properties);
    }

    @Test
    @DisplayName("解密成功 - 正常加密数据")
    void testDecryptSuccess() {
        // 原始业务数据
        String originalData = "{\"username\":\"test\",\"password\":\"123456\"}";

        // 生成 SM4 密钥和 IV
        String sm4Key = SM4KeyUtil.generateKeyHex();
        String sm4Iv = SM4KeyUtil.generateIVHex();

        // 前端加密过程
        EncryptedRequest encryptedRequest = encryptRequest(originalData, sm4Key, sm4Iv);

        // 后端解密
        SM4DecryptService.DecryptResult result = decryptService.decrypt(encryptedRequest);

        // 验证
        assertEquals(originalData, result.data());
        assertNotNull(result.sm4Key());
        assertNotNull(result.sm4Iv());
    }

    @Test
    @DisplayName("解密失败 - 无效的请求数据")
    void testDecryptInvalidRequest() {
        // 空请求
        assertThrows(SM4DecryptException.class,
                () -> decryptService.decrypt(null),
                "空请求应抛出异常");

        // 不完整的请求
        EncryptedRequest incompleteRequest = new EncryptedRequest();
        incompleteRequest.setKey("someKey");
        // 缺少 iv 和 message

        assertThrows(SM4DecryptException.class,
                () -> decryptService.decrypt(incompleteRequest),
                "不完整的请求应抛出异常");
    }

    @Test
    @DisplayName("解密失败 - 错误的 SM2 私钥")
    void testDecryptWithWrongPrivateKey() {
        // 使用错误的私钥创建服务
        SM2KeyUtil.SM2KeyPair wrongKeyPair = SM2KeyUtil.generateKeyPair();
        properties.getSm2().setPrivateKey(wrongKeyPair.privateKey());
        decryptService = new SM4DecryptService(properties);

        // 原始业务数据
        String originalData = "{\"test\":\"data\"}";
        String sm4Key = SM4KeyUtil.generateKeyHex();
        String sm4Iv = SM4KeyUtil.generateIVHex();

        EncryptedRequest encryptedRequest = encryptRequest(originalData, sm4Key, sm4Iv);

        // 使用错误的私钥解密应失败
        SM4DecryptException exception = assertThrows(SM4DecryptException.class,
                () -> decryptService.decrypt(encryptedRequest));

        assertEquals(SM4DecryptException.ErrorType.SM2_DECRYPT_ERROR, exception.getErrorType());
    }

    @Test
    @DisplayName("解密失败 - 被篡改的加密数据")
    void testDecryptTamperedData() {
        String originalData = "{\"test\":\"data\"}";
        String sm4Key = SM4KeyUtil.generateKeyHex();
        String sm4Iv = SM4KeyUtil.generateIVHex();

        EncryptedRequest encryptedRequest = encryptRequest(originalData, sm4Key, sm4Iv);

        // 篡改 message
        encryptedRequest.setMessage(encryptedRequest.getMessage() + "tampered");

        SM4DecryptException exception = assertThrows(SM4DecryptException.class,
                () -> decryptService.decrypt(encryptedRequest));

        assertEquals(SM4DecryptException.ErrorType.SM4_DECRYPT_ERROR, exception.getErrorType());
    }

    @Test
    @DisplayName("时间戳校验 - 有效的请求")
    void testValidTimestamp() {
        properties.getSm4().setTimestampCheckEnabled(true);
        properties.getSm4().setTimestampWindow(60000); // 1分钟窗口
        decryptService = new SM4DecryptService(properties);

        String originalData = "{\"test\":\"data\"}";
        String sm4Key = SM4KeyUtil.generateKeyHex();
        String sm4Iv = SM4KeyUtil.generateIVHex();

        EncryptedRequest encryptedRequest = encryptRequest(originalData, sm4Key, sm4Iv);
        encryptedRequest.setTimestamp(System.currentTimeMillis());

        SM4DecryptService.DecryptResult result = decryptService.decrypt(encryptedRequest);
        assertEquals(originalData, result.data());
    }

    @Test
    @DisplayName("时间戳校验 - 过期的请求（重放攻击）")
    void testExpiredTimestamp() {
        properties.getSm4().setTimestampCheckEnabled(true);
        properties.getSm4().setTimestampWindow(1000); // 1秒窗口
        decryptService = new SM4DecryptService(properties);

        String originalData = "{\"test\":\"data\"}";
        String sm4Key = SM4KeyUtil.generateKeyHex();
        String sm4Iv = SM4KeyUtil.generateIVHex();

        EncryptedRequest encryptedRequest = encryptRequest(originalData, sm4Key, sm4Iv);
        encryptedRequest.setTimestamp(System.currentTimeMillis() - 5000); // 5秒前的时间戳

        SM4DecryptException exception = assertThrows(SM4DecryptException.class,
                () -> decryptService.decrypt(encryptedRequest));

        assertEquals(SM4DecryptException.ErrorType.REPLAY_ATTACK, exception.getErrorType());
    }

    @Test
    @DisplayName("时间戳校验 - 缺少时间戳")
    void testMissingTimestamp() {
        properties.getSm4().setTimestampCheckEnabled(true);
        decryptService = new SM4DecryptService(properties);

        String originalData = "{\"test\":\"data\"}";
        String sm4Key = SM4KeyUtil.generateKeyHex();
        String sm4Iv = SM4KeyUtil.generateIVHex();

        EncryptedRequest encryptedRequest = encryptRequest(originalData, sm4Key, sm4Iv);
        // 不设置时间戳

        SM4DecryptException exception = assertThrows(SM4DecryptException.class,
                () -> decryptService.decrypt(encryptedRequest));

        assertEquals(SM4DecryptException.ErrorType.INVALID_REQUEST, exception.getErrorType());
    }

    @Test
    @DisplayName("解密复杂 JSON 数据")
    void testDecryptComplexJson() {
        String originalData = """
                {
                    "user": {
                        "id": 12345,
                        "name": "张三",
                        "email": "zhangsan@example.com"
                    },
                    "items": [
                        {"id": 1, "name": "商品1"},
                        {"id": 2, "name": "商品2"}
                    ],
                    "total": 199.99
                }
                """;

        String sm4Key = SM4KeyUtil.generateKeyHex();
        String sm4Iv = SM4KeyUtil.generateIVHex();

        EncryptedRequest encryptedRequest = encryptRequest(originalData, sm4Key, sm4Iv);
        SM4DecryptService.DecryptResult result = decryptService.decrypt(encryptedRequest);

        assertEquals(originalData, result.data());
    }

    /**
     * 模拟前端加密过程
     */
    private EncryptedRequest encryptRequest(String data, String sm4Key, String sm4Iv) {
        // 1. SM4 加密业务数据
        String encryptedMessageHex = SM4CryptoUtil.encryptCBC(data, sm4Key, sm4Iv);
        byte[] encryptedMessageBytes = Hex.decode(encryptedMessageHex);
        String encryptedMessageBase64 = Base64.toBase64String(encryptedMessageBytes);

        // 2. SM2 加密 SM4 密钥
        String encryptedKeyHex = SM2CryptoUtil.encrypt(Hex.decode(sm4Key), sm2PublicKey);
        byte[] encryptedKeyBytes = Hex.decode(encryptedKeyHex);
        String encryptedKeyBase64 = Base64.toBase64String(encryptedKeyBytes);

        // 3. SM2 加密 SM4 IV
        String encryptedIvHex = SM2CryptoUtil.encrypt(Hex.decode(sm4Iv), sm2PublicKey);
        byte[] encryptedIvBytes = Hex.decode(encryptedIvHex);
        String encryptedIvBase64 = Base64.toBase64String(encryptedIvBytes);

        EncryptedRequest request = new EncryptedRequest();
        request.setKey(encryptedKeyBase64);
        request.setIv(encryptedIvBase64);
        request.setMessage(encryptedMessageBase64);

        return request;
    }
}
