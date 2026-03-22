package wang.seamas.scratch.web.sm.util;

import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * SM2 国密算法密钥工具类
 * <p>
 * 提供 SM2 公私钥对的生成、编码和解码功能
 * </p>
 *
 * @author Seamas
 * @since 1.0.1
 */
public class SM2KeyUtil {

    /**
     * SM2 推荐曲线参数
     */
    private static final X9ECParameters SM2_ECC_PARAM = GMNamedCurves.getByName("sm2p256v1");
    private static final ECDomainParameters DOMAIN_PARAMS = new ECDomainParameters(
            SM2_ECC_PARAM.getCurve(),
            SM2_ECC_PARAM.getG(),
            SM2_ECC_PARAM.getN()
    );

    /**
     * 私钥字节数组长度
     */
    public static final int PRIVATE_KEY_LENGTH = 32;

    /**
     * 公钥字节数组长度（未压缩格式：0x04 + x + y）
     */
    public static final int PUBLIC_KEY_LENGTH = 65;

    /**
     * 私钥十六进制字符串长度
     */
    public static final int PRIVATE_KEY_HEX_LENGTH = 64;

    /**
     * 公钥十六进制字符串长度（未压缩格式）
     */
    public static final int PUBLIC_KEY_HEX_LENGTH = 130;

    private SM2KeyUtil() {
        // 工具类，禁止实例化
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * SM2 密钥对
     *
     * @param privateKey 私钥（十六进制字符串）
     * @param publicKey  公钥（十六进制字符串，未压缩格式）
     */
    public record SM2KeyPair(String privateKey, String publicKey) {
    }

    /**
     * 生成 SM2 密钥对
     *
     * @return SM2KeyPair 包含十六进制格式的私钥和公钥
     */
    public static SM2KeyPair generateKeyPair() {
        ECKeyPairGenerator keyPairGenerator = new ECKeyPairGenerator();
        ECKeyGenerationParameters keyGenerationParameters = new ECKeyGenerationParameters(
                DOMAIN_PARAMS,
                new SecureRandom()
        );
        keyPairGenerator.init(keyGenerationParameters);

        AsymmetricCipherKeyPair keyPair = keyPairGenerator.generateKeyPair();

        ECPrivateKeyParameters privateKeyParams = (ECPrivateKeyParameters) keyPair.getPrivate();
        ECPublicKeyParameters publicKeyParams = (ECPublicKeyParameters) keyPair.getPublic();

        // 私钥：32字节大整数
        String privateKey = formatPrivateKey(privateKeyParams.getD());

        // 公钥：未压缩格式（0x04 + x坐标 + y坐标）
        String publicKey = formatPublicKey(publicKeyParams.getQ());

        return new SM2KeyPair(privateKey, publicKey);
    }

    /**
     * 生成 SM2 密钥对（字节数组格式）
     *
     * @return SM2KeyPairBytes 包含字节数组格式的私钥和公钥
     */
    public static SM2KeyPairBytes generateKeyPairBytes() {
        ECKeyPairGenerator keyPairGenerator = new ECKeyPairGenerator();
        ECKeyGenerationParameters keyGenerationParameters = new ECKeyGenerationParameters(
                DOMAIN_PARAMS,
                new SecureRandom()
        );
        keyPairGenerator.init(keyGenerationParameters);

        AsymmetricCipherKeyPair keyPair = keyPairGenerator.generateKeyPair();

        ECPrivateKeyParameters privateKeyParams = (ECPrivateKeyParameters) keyPair.getPrivate();
        ECPublicKeyParameters publicKeyParams = (ECPublicKeyParameters) keyPair.getPublic();

        byte[] privateKey = bigIntegerToBytes(privateKeyParams.getD(), PRIVATE_KEY_LENGTH);
        byte[] publicKey = encodePublicKey(publicKeyParams.getQ());

        return new SM2KeyPairBytes(privateKey, publicKey);
    }

    /**
     * SM2 密钥对（字节数组格式）
     *
     * @param privateKey 私钥字节数组
     * @param publicKey  公钥字节数组
     */
    public record SM2KeyPairBytes(byte[] privateKey, byte[] publicKey) {
    }

    /**
     * 格式化私钥为十六进制字符串（64字符，32字节）
     *
     * @param d 私钥大整数
     * @return 十六进制字符串
     */
    private static String formatPrivateKey(BigInteger d) {
        byte[] bytes = bigIntegerToBytes(d, PRIVATE_KEY_LENGTH);
        return Hex.toHexString(bytes);
    }

    /**
     * 格式化公钥为十六进制字符串（未压缩格式：0x04 + x + y）
     *
     * @param q 公钥点
     * @return 十六进制字符串
     */
    private static String formatPublicKey(ECPoint q) {
        byte[] bytes = encodePublicKey(q);
        return Hex.toHexString(bytes);
    }

    /**
     * 将公钥点编码为字节数组（未压缩格式）
     *
     * @param q 公钥点
     * @return 字节数组（65字节：0x04 + 32字节x + 32字节y）
     */
    private static byte[] encodePublicKey(ECPoint q) {
        byte[] x = bigIntegerToBytes(q.getAffineXCoord().toBigInteger(), PRIVATE_KEY_LENGTH);
        byte[] y = bigIntegerToBytes(q.getAffineYCoord().toBigInteger(), PRIVATE_KEY_LENGTH);

        byte[] encoded = new byte[PUBLIC_KEY_LENGTH];
        encoded[0] = 0x04; // 未压缩格式标识
        System.arraycopy(x, 0, encoded, 1, PRIVATE_KEY_LENGTH);
        System.arraycopy(y, 0, encoded, 1 + PRIVATE_KEY_LENGTH, PRIVATE_KEY_LENGTH);

        return encoded;
    }

    /**
     * 将大整数转换为定长字节数组
     *
     * @param value  大整数
     * @param length 目标字节数组长度
     * @return 字节数组
     */
    private static byte[] bigIntegerToBytes(BigInteger value, int length) {
        byte[] bytes = value.toByteArray();

        if (bytes.length == length) {
            return bytes;
        }

        byte[] result = new byte[length];
        if (bytes.length > length) {
            // 如果长度超过，截取后面部分
            System.arraycopy(bytes, bytes.length - length, result, 0, length);
        } else {
            // 如果长度不足，前面补零
            System.arraycopy(bytes, 0, result, length - bytes.length, bytes.length);
        }

        return result;
    }

    /**
     * 从十六进制字符串解析私钥
     *
     * @param privateKeyHex 私钥十六进制字符串（64字符）
     * @return 私钥大整数
     */
    public static BigInteger parsePrivateKey(String privateKeyHex) {
        if (privateKeyHex == null || privateKeyHex.length() != PRIVATE_KEY_HEX_LENGTH) {
            throw new IllegalArgumentException("Invalid private key format, expected " + PRIVATE_KEY_HEX_LENGTH + " hex characters");
        }
        return new BigInteger(privateKeyHex, 16);
    }

    /**
     * 从十六进制字符串解析公钥
     *
     * @param publicKeyHex 公钥十六进制字符串（130字符，未压缩格式）
     * @return 公钥点
     */
    public static ECPoint parsePublicKey(String publicKeyHex) {
        if (publicKeyHex == null || publicKeyHex.length() != PUBLIC_KEY_HEX_LENGTH) {
            throw new IllegalArgumentException("Invalid public key format, expected " + PUBLIC_KEY_HEX_LENGTH + " hex characters");
        }

        byte[] bytes = Hex.decode(publicKeyHex);

        if (bytes[0] != 0x04) {
            throw new IllegalArgumentException("Invalid public key format, expected uncompressed format (0x04)");
        }

        byte[] xBytes = new byte[PRIVATE_KEY_LENGTH];
        byte[] yBytes = new byte[PRIVATE_KEY_LENGTH];

        System.arraycopy(bytes, 1, xBytes, 0, PRIVATE_KEY_LENGTH);
        System.arraycopy(bytes, 1 + PRIVATE_KEY_LENGTH, yBytes, 0, PRIVATE_KEY_LENGTH);

        BigInteger x = new BigInteger(1, xBytes);
        BigInteger y = new BigInteger(1, yBytes);

        return SM2_ECC_PARAM.getCurve().createPoint(x, y);
    }

    /**
     * 验证密钥对是否匹配
     *
     * @param privateKeyHex 私钥十六进制字符串
     * @param publicKeyHex  公钥十六进制字符串
     * @return true 如果密钥对匹配
     */
    public static boolean validateKeyPair(String privateKeyHex, String publicKeyHex) {
        try {
            BigInteger privateKey = parsePrivateKey(privateKeyHex);
            ECPoint publicKey = parsePublicKey(publicKeyHex);

            // 通过私钥计算公钥
            ECPoint calculatedPublicKey = DOMAIN_PARAMS.getG().multiply(privateKey).normalize();

            return calculatedPublicKey.getAffineXCoord().toBigInteger().equals(publicKey.getAffineXCoord().toBigInteger())
                    && calculatedPublicKey.getAffineYCoord().toBigInteger().equals(publicKey.getAffineYCoord().toBigInteger());
        } catch (Exception e) {
            return false;
        }
    }
}
