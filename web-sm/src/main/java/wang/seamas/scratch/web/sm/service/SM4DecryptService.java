package wang.seamas.scratch.web.sm.service;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import wang.seamas.scratch.web.sm.config.CryptoProperties;
import wang.seamas.scratch.web.sm.dto.EncryptedRequest;
import wang.seamas.scratch.web.sm.exception.SM4DecryptException;
import wang.seamas.scratch.web.sm.util.SM2CryptoUtil;
import wang.seamas.scratch.web.sm.util.SM4CryptoUtil;

import java.nio.charset.StandardCharsets;

/**
 * SM4 解密服务
 * <p>
 * 提供加密请求的解密功能，包括：
 * - SM2 解密 SM4 密钥和 IV
 * - SM4 解密业务数据
 * - 时间戳校验（防重放攻击）
 * </p>
 *
 * @author Seamas
 * @since 1.0.1
 */
public class SM4DecryptService {

    private final CryptoProperties properties;

    public SM4DecryptService(CryptoProperties properties) {
        this.properties = properties;
    }

    /**
     * 解密请求数据
     *
     * @param encryptedRequest 加密请求对象
     * @return 解密结果，包含 JSON 数据和密钥信息
     * @throws SM4DecryptException 解密失败时抛出
     */
    public DecryptResult decrypt(EncryptedRequest encryptedRequest) {
        // 验证请求数据完整性
        if (encryptedRequest == null || !encryptedRequest.isValid()) {
            throw new SM4DecryptException(
                    SM4DecryptException.ErrorType.INVALID_REQUEST,
                    "加密请求数据不完整，key、iv、message 都不能为空"
            );
        }

        // 时间戳校验（防重放攻击）
        if (properties.getSm4().isTimestampCheckEnabled()) {
            validateTimestamp(encryptedRequest.getTimestamp());
        }

        try {
            // 1. 使用 SM2 私钥解密 SM4 密钥
            String sm4Key = decryptSm4Key(encryptedRequest.getKey());

            // 2. 使用 SM2 私钥解密 SM4 IV
            String sm4Iv = decryptSm4Iv(encryptedRequest.getIv());

            // 3. 使用 SM4 密钥和 IV 解密业务数据
            String jsonData = decryptMessage(encryptedRequest.getMessage(), sm4Key, sm4Iv);

            return new DecryptResult(jsonData, sm4Key, sm4Iv);

        } catch (SM4DecryptException e) {
            throw e;
        } catch (Exception e) {
            throw new SM4DecryptException(
                    SM4DecryptException.ErrorType.SM4_DECRYPT_ERROR,
                    "解密过程发生未知错误: " + e.getMessage(),
                    e
            );
        }
    }

    /**
     * 解密结果
     */
    public record DecryptResult(String data, String sm4Key, String sm4Iv) {
    }

    /**
     * 解密 SM4 密钥
     *
     * @param encryptedKey 经 SM2 加密的 SM4 密钥（Base64 编码）
     * @return SM4 密钥（十六进制字符串）
     */
    private String decryptSm4Key(String encryptedKey) {
        try {
            // Base64 解码
            byte[] encryptedKeyBytes = Base64.decode(encryptedKey);
            // 转为十六进制字符串（SM2CryptoUtil 需要十六进制格式）
            String encryptedKeyHex = Hex.toHexString(encryptedKeyBytes);
            // SM2 解密
            byte[] keyBytes = SM2CryptoUtil.decrypt(encryptedKeyHex, properties.getSm2().getPrivateKey());
            // 返回十六进制格式的密钥
            return Hex.toHexString(keyBytes);
        } catch (Exception e) {
            throw new SM4DecryptException(
                    SM4DecryptException.ErrorType.SM2_DECRYPT_ERROR,
                    "SM2 解密 SM4 密钥失败: " + e.getMessage(),
                    e
            );
        }
    }

    /**
     * 解密 SM4 IV
     *
     * @param encryptedIv 经 SM2 加密的 SM4 IV（Base64 编码）
     * @return SM4 IV（十六进制字符串）
     */
    private String decryptSm4Iv(String encryptedIv) {
        try {
            // Base64 解码
            byte[] encryptedIvBytes = Base64.decode(encryptedIv);
            // 转为十六进制字符串
            String encryptedIvHex = Hex.toHexString(encryptedIvBytes);
            // SM2 解密
            byte[] ivBytes = SM2CryptoUtil.decrypt(encryptedIvHex, properties.getSm2().getPrivateKey());
            // 返回十六进制格式的 IV
            return Hex.toHexString(ivBytes);
        } catch (Exception e) {
            throw new SM4DecryptException(
                    SM4DecryptException.ErrorType.SM2_DECRYPT_ERROR,
                    "SM2 解密 SM4 IV 失败: " + e.getMessage(),
                    e
            );
        }
    }

    /**
     * 解密业务数据
     *
     * @param encryptedMessage 经 SM4 加密的业务数据（Base64 编码）
     * @param sm4Key SM4 密钥（十六进制字符串）
     * @param sm4Iv SM4 IV（十六进制字符串）
     * @return 解密后的 JSON 字符串
     */
    private String decryptMessage(String encryptedMessage, String sm4Key, String sm4Iv) {
        try {
            // Base64 解码
            byte[] encryptedMessageBytes = Base64.decode(encryptedMessage);
            // 转为十六进制字符串
            String encryptedMessageHex = Hex.toHexString(encryptedMessageBytes);
            // SM4 CBC 模式解密
            byte[] decryptedBytes = SM4CryptoUtil.decryptCBC(encryptedMessageHex, sm4Key, sm4Iv);
            // 转为字符串
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new SM4DecryptException(
                    SM4DecryptException.ErrorType.SM4_DECRYPT_ERROR,
                    "SM4 解密业务数据失败: " + e.getMessage(),
                    e
            );
        }
    }

    /**
     * 验证时间戳（防重放攻击）
     *
     * @param timestamp 请求时间戳
     */
    private void validateTimestamp(Long timestamp) {
        if (timestamp == null) {
            throw new SM4DecryptException(
                    SM4DecryptException.ErrorType.INVALID_REQUEST,
                    "缺少时间戳，无法校验请求时效性"
            );
        }

        long currentTime = System.currentTimeMillis();
        long timeDiff = Math.abs(currentTime - timestamp);

        if (timeDiff > properties.getSm4().getTimestampWindow()) {
            throw new SM4DecryptException(
                    SM4DecryptException.ErrorType.REPLAY_ATTACK,
                    "请求已过期，可能存在重放攻击风险"
            );
        }
    }
}
