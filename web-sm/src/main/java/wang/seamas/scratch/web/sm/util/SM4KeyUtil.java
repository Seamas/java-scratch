package wang.seamas.scratch.web.sm.util;

import org.bouncycastle.util.encoders.Hex;

import java.security.SecureRandom;

/**
 * SM4 国密对称加密算法密钥工具类
 * <p>
 * 提供 SM4 密钥的生成、编码和解码功能
 * </p>
 * <p>
 * SM4 算法是我国自主设计的分组对称密码算法，密钥长度和分组长度均为 128 位（16 字节）
 * </p>
 *
 * @author Seamas
 * @since 1.0.1
 */
public class SM4KeyUtil {

    /**
     * SM4 密钥字节数组长度（128 位 = 16 字节）
     */
    public static final int KEY_LENGTH = 16;

    /**
     * SM4 密钥十六进制字符串长度（16 字节 = 32 个十六进制字符）
     */
    public static final int KEY_HEX_LENGTH = 32;

    /**
     * SM4 分组长度（128 位 = 16 字节）
     */
    public static final int BLOCK_SIZE = 16;

    private SM4KeyUtil() {
        // 工具类，禁止实例化
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 生成随机 SM4 密钥
     *
     * @return 密钥字节数组（16 字节）
     */
    public static byte[] generateKey() {
        byte[] key = new byte[KEY_LENGTH];
        new SecureRandom().nextBytes(key);
        return key;
    }

    /**
     * 生成随机 SM4 密钥（十六进制字符串格式）
     *
     * @return 密钥十六进制字符串（32 个字符）
     */
    public static String generateKeyHex() {
        return Hex.toHexString(generateKey());
    }

    /**
     * 从十六进制字符串解析 SM4 密钥
     *
     * @param keyHex 密钥十六进制字符串（32 个字符）
     * @return 密钥字节数组（16 字节）
     * @throws IllegalArgumentException 如果密钥格式无效
     */
    public static byte[] parseKey(String keyHex) {
        if (keyHex == null || keyHex.length() != KEY_HEX_LENGTH) {
            throw new IllegalArgumentException(
                    "Invalid SM4 key format, expected " + KEY_HEX_LENGTH + " hex characters, but got " +
                            (keyHex == null ? "null" : keyHex.length())
            );
        }

        // 检查是否为有效的十六进制字符串
        if (!isValidHex(keyHex)) {
            throw new IllegalArgumentException("Invalid hex string: " + keyHex);
        }

        return Hex.decode(keyHex);
    }

    /**
     * 将字节数组编码为十六进制字符串
     *
     * @param key 密钥字节数组
     * @return 密钥十六进制字符串
     * @throws IllegalArgumentException 如果密钥长度无效
     */
    public static String encodeKey(byte[] key) {
        if (key == null || key.length != KEY_LENGTH) {
            throw new IllegalArgumentException(
                    "Invalid SM4 key length, expected " + KEY_LENGTH + " bytes, but got " +
                            (key == null ? "null" : key.length)
            );
        }
        return Hex.toHexString(key);
    }

    /**
     * 生成指定数量的随机 SM4 密钥
     *
     * @param count 密钥数量
     * @return 密钥数组（十六进制字符串格式）
     * @throws IllegalArgumentException 如果 count 小于等于 0
     */
    public static String[] generateKeys(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Key count must be greater than 0");
        }

        String[] keys = new String[count];
        for (int i = 0; i < count; i++) {
            keys[i] = generateKeyHex();
        }
        return keys;
    }

    /**
     * 验证密钥是否为有效的 SM4 密钥
     *
     * @param keyHex 密钥十六进制字符串
     * @return true 如果密钥格式有效
     */
    public static boolean isValidKey(String keyHex) {
        if (keyHex == null || keyHex.length() != KEY_HEX_LENGTH) {
            return false;
        }
        return isValidHex(keyHex);
    }

    /**
     * 验证密钥是否为有效的 SM4 密钥
     *
     * @param key 密钥字节数组
     * @return true 如果密钥格式有效
     */
    public static boolean isValidKey(byte[] key) {
        return key != null && key.length == KEY_LENGTH;
    }

    /**
     * 检查字符串是否为有效的十六进制字符串
     *
     * @param str 待检查的字符串
     * @return true 如果是有效的十六进制字符串
     */
    private static boolean isValidHex(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!((c >= '0' && c <= '9') ||
                    (c >= 'a' && c <= 'f') ||
                    (c >= 'A' && c <= 'F'))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 生成初始化向量（IV）
     * <p>
     * SM4 在 CBC 模式下需要使用 IV，IV 长度与分组长度相同（16 字节）
     * </p>
     *
     * @return IV 字节数组（16 字节）
     */
    public static byte[] generateIV() {
        byte[] iv = new byte[BLOCK_SIZE];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    /**
     * 生成初始化向量（IV，十六进制字符串格式）
     *
     * @return IV 十六进制字符串（32 个字符）
     */
    public static String generateIVHex() {
        return Hex.toHexString(generateIV());
    }

    /**
     * 从十六进制字符串解析初始化向量（IV）
     *
     * @param ivHex IV 十六进制字符串（32 个字符）
     * @return IV 字节数组（16 字节）
     * @throws IllegalArgumentException 如果 IV 格式无效
     */
    public static byte[] parseIV(String ivHex) {
        if (ivHex == null || ivHex.length() != KEY_HEX_LENGTH) {
            throw new IllegalArgumentException(
                    "Invalid IV format, expected " + KEY_HEX_LENGTH + " hex characters, but got " +
                            (ivHex == null ? "null" : ivHex.length())
            );
        }

        if (!isValidHex(ivHex)) {
            throw new IllegalArgumentException("Invalid hex string: " + ivHex);
        }

        return Hex.decode(ivHex);
    }

    /**
     * SM4 密钥和 IV 组合
     *
     * @param key 密钥（十六进制字符串）
     * @param iv  初始化向量（十六进制字符串）
     */
    public record SM4KeyWithIV(String key, String iv) {
    }

    /**
     * 生成 SM4 密钥和 IV
     *
     * @return SM4KeyWithIV 包含密钥和 IV 的组合
     */
    public static SM4KeyWithIV generateKeyWithIV() {
        return new SM4KeyWithIV(generateKeyHex(), generateIVHex());
    }
}
