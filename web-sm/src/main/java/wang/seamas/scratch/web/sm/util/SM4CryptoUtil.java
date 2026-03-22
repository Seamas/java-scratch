package wang.seamas.scratch.web.sm.util;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;

/**
 * SM4 国密对称加密算法工具类
 * <p>
 * 提供 SM4 加密和解密功能，支持 ECB 和 CBC 模式
 * </p>
 *
 * @author Seamas
 * @since 1.0.1
 */
public class SM4CryptoUtil {

    /**
     * 加密模式
     */
    public enum Mode {
        ECB,  // 电子密码本模式（不推荐用于大量数据）
        CBC   // 密码分组链接模式（推荐）
    }

    private SM4CryptoUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ==================== ECB 模式 ====================

    /**
     * SM4 ECB 模式加密
     *
     * @param plaintext 明文数据
     * @param key       密钥（十六进制字符串，32 个字符）
     * @return 密文（十六进制字符串）
     */
    public static String encryptECB(String plaintext, String key) {
        return encryptECB(plaintext.getBytes(StandardCharsets.UTF_8), key);
    }

    /**
     * SM4 ECB 模式加密
     *
     * @param plaintext 明文数据
     * @param key       密钥（十六进制字符串，32 个字符）
     * @return 密文（十六进制字符串）
     */
    public static String encryptECB(byte[] plaintext, String key) {
        if (plaintext == null) {
            throw new IllegalArgumentException("Plaintext cannot be null");
        }
        if (key == null || key.length() != SM4KeyUtil.KEY_HEX_LENGTH) {
            throw new IllegalArgumentException("Invalid key format, expected " + SM4KeyUtil.KEY_HEX_LENGTH + " hex characters");
        }

        byte[] keyBytes = SM4KeyUtil.parseKey(key);
        return encryptECB(plaintext, keyBytes);
    }

    /**
     * SM4 ECB 模式加密
     *
     * @param plaintext 明文数据
     * @param key       密钥（字节数组，16 字节）
     * @return 密文（十六进制字符串）
     */
    public static String encryptECB(byte[] plaintext, byte[] key) {
        validateKey(key);

        try {
            PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new SM4Engine(), new PKCS7Padding());
            cipher.init(true, new KeyParameter(key));

            byte[] output = new byte[cipher.getOutputSize(plaintext.length)];
            int len = cipher.processBytes(plaintext, 0, plaintext.length, output, 0);
            cipher.doFinal(output, len);

            return Hex.toHexString(output);
        } catch (Exception e) {
            throw new RuntimeException("SM4 ECB encryption failed", e);
        }
    }

    /**
     * SM4 ECB 模式解密
     *
     * @param ciphertext 密文（十六进制字符串）
     * @param key        密钥（十六进制字符串，32 个字符）
     * @return 明文（字符串）
     */
    public static String decryptECBToString(String ciphertext, String key) {
        byte[] decrypted = decryptECB(ciphertext, key);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    /**
     * SM4 ECB 模式解密
     *
     * @param ciphertext 密文（十六进制字符串）
     * @param key        密钥（十六进制字符串，32 个字符）
     * @return 明文（字节数组）
     */
    public static byte[] decryptECB(String ciphertext, String key) {
        if (ciphertext == null || ciphertext.isEmpty()) {
            throw new IllegalArgumentException("Ciphertext cannot be null or empty");
        }
        if (key == null || key.length() != SM4KeyUtil.KEY_HEX_LENGTH) {
            throw new IllegalArgumentException("Invalid key format, expected " + SM4KeyUtil.KEY_HEX_LENGTH + " hex characters");
        }

        byte[] keyBytes = SM4KeyUtil.parseKey(key);
        return decryptECB(ciphertext, keyBytes);
    }

    /**
     * SM4 ECB 模式解密
     *
     * @param ciphertext 密文（十六进制字符串）
     * @param key        密钥（字节数组，16 字节）
     * @return 明文（字节数组）
     */
    public static byte[] decryptECB(String ciphertext, byte[] key) {
        validateKey(key);

        try {
            byte[] encrypted = Hex.decode(ciphertext);

            PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new SM4Engine(), new PKCS7Padding());
            cipher.init(false, new KeyParameter(key));

            byte[] output = new byte[cipher.getOutputSize(encrypted.length)];
            int len = cipher.processBytes(encrypted, 0, encrypted.length, output, 0);
            int finalLen = cipher.doFinal(output, len);

            byte[] result = new byte[len + finalLen];
            System.arraycopy(output, 0, result, 0, result.length);

            return result;
        } catch (Exception e) {
            throw new RuntimeException("SM4 ECB decryption failed", e);
        }
    }

    // ==================== CBC 模式 ====================

    /**
     * SM4 CBC 模式加密
     *
     * @param plaintext 明文数据
     * @param key       密钥（十六进制字符串，32 个字符）
     * @param iv        初始化向量（十六进制字符串，32 个字符）
     * @return 密文（十六进制字符串）
     */
    public static String encryptCBC(String plaintext, String key, String iv) {
        return encryptCBC(plaintext.getBytes(StandardCharsets.UTF_8), key, iv);
    }

    /**
     * SM4 CBC 模式加密
     *
     * @param plaintext 明文数据
     * @param key       密钥（十六进制字符串，32 个字符）
     * @param iv        初始化向量（十六进制字符串，32 个字符）
     * @return 密文（十六进制字符串）
     */
    public static String encryptCBC(byte[] plaintext, String key, String iv) {
        if (plaintext == null) {
            throw new IllegalArgumentException("Plaintext cannot be null");
        }
        if (key == null || key.length() != SM4KeyUtil.KEY_HEX_LENGTH) {
            throw new IllegalArgumentException("Invalid key format, expected " + SM4KeyUtil.KEY_HEX_LENGTH + " hex characters");
        }
        if (iv == null || iv.length() != SM4KeyUtil.KEY_HEX_LENGTH) {
            throw new IllegalArgumentException("Invalid IV format, expected " + SM4KeyUtil.KEY_HEX_LENGTH + " hex characters");
        }

        byte[] keyBytes = SM4KeyUtil.parseKey(key);
        byte[] ivBytes = SM4KeyUtil.parseIV(iv);
        return encryptCBC(plaintext, keyBytes, ivBytes);
    }

    /**
     * SM4 CBC 模式加密
     *
     * @param plaintext 明文数据
     * @param key       密钥（字节数组，16 字节）
     * @param iv        初始化向量（字节数组，16 字节）
     * @return 密文（十六进制字符串）
     */
    public static String encryptCBC(byte[] plaintext, byte[] key, byte[] iv) {
        validateKey(key);
        validateIV(iv);

        try {
            PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(
                    new CBCBlockCipher(new SM4Engine()),
                    new PKCS7Padding()
            );
            cipher.init(true, new ParametersWithIV(new KeyParameter(key), iv));

            byte[] output = new byte[cipher.getOutputSize(plaintext.length)];
            int len = cipher.processBytes(plaintext, 0, plaintext.length, output, 0);
            cipher.doFinal(output, len);

            return Hex.toHexString(output);
        } catch (Exception e) {
            throw new RuntimeException("SM4 CBC encryption failed", e);
        }
    }

    /**
     * SM4 CBC 模式解密
     *
     * @param ciphertext 密文（十六进制字符串）
     * @param key        密钥（十六进制字符串，32 个字符）
     * @param iv         初始化向量（十六进制字符串，32 个字符）
     * @return 明文（字符串）
     */
    public static String decryptCBCToString(String ciphertext, String key, String iv) {
        byte[] decrypted = decryptCBC(ciphertext, key, iv);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    /**
     * SM4 CBC 模式解密
     *
     * @param ciphertext 密文（十六进制字符串）
     * @param key        密钥（十六进制字符串，32 个字符）
     * @param iv         初始化向量（十六进制字符串，32 个字符）
     * @return 明文（字节数组）
     */
    public static byte[] decryptCBC(String ciphertext, String key, String iv) {
        if (ciphertext == null || ciphertext.isEmpty()) {
            throw new IllegalArgumentException("Ciphertext cannot be null or empty");
        }
        if (key == null || key.length() != SM4KeyUtil.KEY_HEX_LENGTH) {
            throw new IllegalArgumentException("Invalid key format, expected " + SM4KeyUtil.KEY_HEX_LENGTH + " hex characters");
        }
        if (iv == null || iv.length() != SM4KeyUtil.KEY_HEX_LENGTH) {
            throw new IllegalArgumentException("Invalid IV format, expected " + SM4KeyUtil.KEY_HEX_LENGTH + " hex characters");
        }

        byte[] keyBytes = SM4KeyUtil.parseKey(key);
        byte[] ivBytes = SM4KeyUtil.parseIV(iv);
        return decryptCBC(ciphertext, keyBytes, ivBytes);
    }

    /**
     * SM4 CBC 模式解密
     *
     * @param ciphertext 密文（十六进制字符串）
     * @param key        密钥（字节数组，16 字节）
     * @param iv         初始化向量（字节数组，16 字节）
     * @return 明文（字节数组）
     */
    public static byte[] decryptCBC(String ciphertext, byte[] key, byte[] iv) {
        validateKey(key);
        validateIV(iv);

        try {
            byte[] encrypted = Hex.decode(ciphertext);

            PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(
                    new CBCBlockCipher(new SM4Engine()),
                    new PKCS7Padding()
            );
            cipher.init(false, new ParametersWithIV(new KeyParameter(key), iv));

            byte[] output = new byte[cipher.getOutputSize(encrypted.length)];
            int len = cipher.processBytes(encrypted, 0, encrypted.length, output, 0);
            int finalLen = cipher.doFinal(output, len);

            byte[] result = new byte[len + finalLen];
            System.arraycopy(output, 0, result, 0, result.length);

            return result;
        } catch (Exception e) {
            throw new RuntimeException("SM4 CBC decryption failed", e);
        }
    }

    // ==================== 通用方法 ====================

    /**
     * SM4 加密（自动使用 CBC 模式）
     *
     * @param plaintext 明文数据
     * @param key       密钥（十六进制字符串，32 个字符）
     * @param iv        初始化向量（十六进制字符串，32 个字符）
     * @return 密文（十六进制字符串）
     */
    public static String encrypt(String plaintext, String key, String iv) {
        return encryptCBC(plaintext, key, iv);
    }

    /**
     * SM4 解密（自动使用 CBC 模式）
     *
     * @param ciphertext 密文（十六进制字符串）
     * @param key        密钥（十六进制字符串，32 个字符）
     * @param iv         初始化向量（十六进制字符串，32 个字符）
     * @return 明文（字符串）
     */
    public static String decrypt(String ciphertext, String key, String iv) {
        return decryptCBCToString(ciphertext, key, iv);
    }

    // ==================== 辅助方法 ====================

    /**
     * 验证密钥
     */
    private static void validateKey(byte[] key) {
        if (key == null || key.length != SM4KeyUtil.KEY_LENGTH) {
            throw new IllegalArgumentException("Invalid key length, expected " + SM4KeyUtil.KEY_LENGTH + " bytes");
        }
    }

    /**
     * 验证 IV
     */
    private static void validateIV(byte[] iv) {
        if (iv == null || iv.length != SM4KeyUtil.BLOCK_SIZE) {
            throw new IllegalArgumentException("Invalid IV length, expected " + SM4KeyUtil.BLOCK_SIZE + " bytes");
        }
    }
}
