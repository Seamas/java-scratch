package wang.seamas.scratch.web.sm.util;

import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * SM2 国密非对称加密算法工具类
 * <p>
 * 提供 SM2 加密、解密、签名和验签功能
 * </p>
 *
 * @author Seamas
 * @since 1.0.1
 */
public class SM2CryptoUtil {

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
     * SM2 加密模式
     */
    public enum Mode {
        C1C2C3,  // 旧标准模式
        C1C3C2   // 新标准模式（推荐）
    }

    private SM2CryptoUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ==================== 加密/解密 ====================

    /**
     * SM2 加密（使用 C1C3C2 模式）
     *
     * @param plaintext  明文数据
     * @param publicKey  公钥（十六进制字符串，未压缩格式）
     * @return 密文（十六进制字符串）
     */
    public static String encrypt(String plaintext, String publicKey) {
        return encrypt(plaintext.getBytes(StandardCharsets.UTF_8), publicKey, Mode.C1C3C2);
    }

    /**
     * SM2 加密（使用 C1C3C2 模式）
     *
     * @param plaintext  明文数据
     * @param publicKey  公钥（十六进制字符串，未压缩格式）
     * @return 密文（十六进制字符串）
     */
    public static String encrypt(byte[] plaintext, String publicKey) {
        return encrypt(plaintext, publicKey, Mode.C1C3C2);
    }

    /**
     * SM2 加密
     *
     * @param plaintext  明文数据
     * @param publicKey  公钥（十六进制字符串，未压缩格式）
     * @param mode       加密模式
     * @return 密文（十六进制字符串）
     */
    public static String encrypt(String plaintext, String publicKey, Mode mode) {
        return encrypt(plaintext.getBytes(StandardCharsets.UTF_8), publicKey, mode);
    }

    /**
     * SM2 加密
     *
     * @param plaintext  明文数据
     * @param publicKey  公钥（十六进制字符串，未压缩格式）
     * @param mode       加密模式
     * @return 密文（十六进制字符串）
     */
    public static String encrypt(byte[] plaintext, String publicKey, Mode mode) {
        if (plaintext == null || plaintext.length == 0) {
            throw new IllegalArgumentException("Plaintext cannot be null or empty");
        }
        if (publicKey == null || publicKey.isEmpty()) {
            throw new IllegalArgumentException("Public key cannot be null or empty");
        }

        SM2Engine engine = new SM2Engine(
                mode == Mode.C1C3C2 ? SM2Engine.Mode.C1C3C2 : SM2Engine.Mode.C1C2C3
        );

        ECPublicKeyParameters pubKeyParams = loadPublicKey(publicKey);
        ParametersWithRandom params = new ParametersWithRandom(pubKeyParams);
        engine.init(true, params);

        try {
            byte[] encrypted = engine.processBlock(plaintext, 0, plaintext.length);
            return Hex.toHexString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("SM2 encryption failed", e);
        }
    }

    /**
     * SM2 解密（使用 C1C3C2 模式）
     *
     * @param ciphertext 密文（十六进制字符串）
     * @param privateKey 私钥（十六进制字符串）
     * @return 明文（字符串）
     */
    public static String decryptToString(String ciphertext, String privateKey) {
        byte[] decrypted = decrypt(ciphertext, privateKey, Mode.C1C3C2);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    /**
     * SM2 解密（使用 C1C3C2 模式）
     *
     * @param ciphertext 密文（十六进制字符串）
     * @param privateKey 私钥（十六进制字符串）
     * @return 明文（字节数组）
     */
    public static byte[] decrypt(String ciphertext, String privateKey) {
        return decrypt(ciphertext, privateKey, Mode.C1C3C2);
    }

    /**
     * SM2 解密
     *
     * @param ciphertext 密文（十六进制字符串）
     * @param privateKey 私钥（十六进制字符串）
     * @param mode       加密模式
     * @return 明文（字节数组）
     */
    public static byte[] decrypt(String ciphertext, String privateKey, Mode mode) {
        if (ciphertext == null || ciphertext.isEmpty()) {
            throw new IllegalArgumentException("Ciphertext cannot be null or empty");
        }
        if (privateKey == null || privateKey.isEmpty()) {
            throw new IllegalArgumentException("Private key cannot be null or empty");
        }

        SM2Engine engine = new SM2Engine(
                mode == Mode.C1C3C2 ? SM2Engine.Mode.C1C3C2 : SM2Engine.Mode.C1C2C3
        );

        ECPrivateKeyParameters privKeyParams = loadPrivateKey(privateKey);
        engine.init(false, privKeyParams);

        try {
            byte[] encrypted = Hex.decode(ciphertext);
            return engine.processBlock(encrypted, 0, encrypted.length);
        } catch (Exception e) {
            throw new RuntimeException("SM2 decryption failed", e);
        }
    }

    // ==================== 签名/验签 ====================

    /**
     * SM2 签名
     *
     * @param data       待签名数据
     * @param privateKey 私钥（十六进制字符串）
     * @return 签名值（十六进制字符串，r + s 拼接）
     */
    public static String sign(String data, String privateKey) {
        return sign(data.getBytes(StandardCharsets.UTF_8), privateKey);
    }

    /**
     * SM2 签名
     *
     * @param data       待签名数据
     * @param privateKey 私钥（十六进制字符串）
     * @return 签名值（十六进制字符串，r + s 拼接）
     */
    public static String sign(byte[] data, String privateKey) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if (privateKey == null || privateKey.isEmpty()) {
            throw new IllegalArgumentException("Private key cannot be null or empty");
        }

        try {
            ECPrivateKeyParameters privKeyParams = loadPrivateKey(privateKey);

            // 计算 Z 值（用户标识）
            byte[] z = getZ(null, privKeyParams.getParameters().getG().multiply(privKeyParams.getD()));

            // 计算 H = SM3(Z || M)
            byte[] h = sm3Hash(concat(z, data));

            // 签名
            BigInteger[] signature = sm2Sign(h, privKeyParams);

            // 将 r 和 s 拼接成签名值
            byte[] rBytes = bigIntegerToBytes(signature[0], 32);
            byte[] sBytes = bigIntegerToBytes(signature[1], 32);

            return Hex.toHexString(concat(rBytes, sBytes));
        } catch (Exception e) {
            throw new RuntimeException("SM2 sign failed", e);
        }
    }

    /**
     * SM2 验签
     *
     * @param data      原始数据
     * @param signature 签名值（十六进制字符串，r + s 拼接）
     * @param publicKey 公钥（十六进制字符串，未压缩格式）
     * @return true 如果验签成功
     */
    public static boolean verify(String data, String signature, String publicKey) {
        return verify(data.getBytes(StandardCharsets.UTF_8), signature, publicKey);
    }

    /**
     * SM2 验签
     *
     * @param data      原始数据
     * @param signature 签名值（十六进制字符串，r + s 拼接）
     * @param publicKey 公钥（十六进制字符串，未压缩格式）
     * @return true 如果验签成功
     */
    public static boolean verify(byte[] data, String signature, String publicKey) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if (signature == null || signature.isEmpty()) {
            throw new IllegalArgumentException("Signature cannot be null or empty");
        }
        if (publicKey == null || publicKey.isEmpty()) {
            throw new IllegalArgumentException("Public key cannot be null or empty");
        }

        try {
            ECPublicKeyParameters pubKeyParams = loadPublicKey(publicKey);

            // 解析签名值
            byte[] sigBytes = Hex.decode(signature);
            if (sigBytes.length != 64) {
                return false;
            }

            byte[] rBytes = new byte[32];
            byte[] sBytes = new byte[32];
            System.arraycopy(sigBytes, 0, rBytes, 0, 32);
            System.arraycopy(sigBytes, 32, sBytes, 0, 32);

            BigInteger r = new BigInteger(1, rBytes);
            BigInteger s = new BigInteger(1, sBytes);

            // 计算 Z 值
            byte[] z = getZ(null, pubKeyParams.getQ());

            // 计算 H = SM3(Z || M)
            byte[] h = sm3Hash(concat(z, data));

            // 验签
            return sm2Verify(h, r, s, pubKeyParams);
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * 加载公钥
     */
    private static ECPublicKeyParameters loadPublicKey(String publicKeyHex) {
        ECPoint pubPoint = SM2KeyUtil.parsePublicKey(publicKeyHex);
        return new ECPublicKeyParameters(pubPoint, DOMAIN_PARAMS);
    }

    /**
     * 加载私钥
     */
    private static ECPrivateKeyParameters loadPrivateKey(String privateKeyHex) {
        BigInteger privateKey = SM2KeyUtil.parsePrivateKey(privateKeyHex);
        return new ECPrivateKeyParameters(privateKey, DOMAIN_PARAMS);
    }

    /**
     * 获取 Z 值（用户标识）
     */
    private static byte[] getZ(byte[] id, ECPoint pubPoint) {
        if (id == null) {
            id = "1234567812345678".getBytes(StandardCharsets.UTF_8);
        }

        // 确保点在仿射坐标下（归一化）
        ECPoint normalizedPoint = pubPoint.normalize();

        int entlenA = id.length * 8;
        byte[] entlenABytes = new byte[2];
        entlenABytes[0] = (byte) (entlenA >> 8);
        entlenABytes[1] = (byte) entlenA;

        byte[] aBytes = bigIntegerToBytes(SM2_ECC_PARAM.getCurve().getA().toBigInteger(), 32);
        byte[] bBytes = bigIntegerToBytes(SM2_ECC_PARAM.getCurve().getB().toBigInteger(), 32);
        byte[] gxBytes = bigIntegerToBytes(SM2_ECC_PARAM.getG().getAffineXCoord().toBigInteger(), 32);
        byte[] gyBytes = bigIntegerToBytes(SM2_ECC_PARAM.getG().getAffineYCoord().toBigInteger(), 32);
        byte[] pxBytes = bigIntegerToBytes(normalizedPoint.getAffineXCoord().toBigInteger(), 32);
        byte[] pyBytes = bigIntegerToBytes(normalizedPoint.getAffineYCoord().toBigInteger(), 32);

        byte[] z = sm3Hash(concat(
                entlenABytes, id,
                aBytes, bBytes,
                gxBytes, gyBytes,
                pxBytes, pyBytes
        ));

        return z;
    }

    /**
     * SM3 哈希
     */
    private static byte[] sm3Hash(byte[] data) {
        org.bouncycastle.crypto.digests.SM3Digest digest = new org.bouncycastle.crypto.digests.SM3Digest();
        digest.update(data, 0, data.length);
        byte[] result = new byte[digest.getDigestSize()];
        digest.doFinal(result, 0);
        return result;
    }

    /**
     * SM2 签名
     */
    private static BigInteger[] sm2Sign(byte[] hash, ECPrivateKeyParameters privKey) {
        BigInteger e = new BigInteger(1, hash);
        BigInteger k;
        BigInteger r;
        BigInteger s;

        java.security.SecureRandom random = new java.security.SecureRandom();

        do {
            do {
                k = new BigInteger(DOMAIN_PARAMS.getN().bitLength(), random);
            } while (k.equals(BigInteger.ZERO) || k.compareTo(DOMAIN_PARAMS.getN()) >= 0);

            ECPoint p = DOMAIN_PARAMS.getG().multiply(k).normalize();
            r = e.add(p.getAffineXCoord().toBigInteger()).mod(DOMAIN_PARAMS.getN());
        } while (r.equals(BigInteger.ZERO) || r.add(k).equals(DOMAIN_PARAMS.getN()));

        BigInteger d = privKey.getD();
        s = d.add(BigInteger.ONE).modInverse(DOMAIN_PARAMS.getN())
                .multiply(k.subtract(r.multiply(d)).mod(DOMAIN_PARAMS.getN()))
                .mod(DOMAIN_PARAMS.getN());

        return new BigInteger[]{r, s};
    }

    /**
     * SM2 验签
     */
    private static boolean sm2Verify(byte[] hash, BigInteger r, BigInteger s, ECPublicKeyParameters pubKey) {
        if (r.compareTo(BigInteger.ONE) < 0 || r.compareTo(DOMAIN_PARAMS.getN()) >= 0) {
            return false;
        }
        if (s.compareTo(BigInteger.ONE) < 0 || s.compareTo(DOMAIN_PARAMS.getN()) >= 0) {
            return false;
        }

        BigInteger e = new BigInteger(1, hash);
        BigInteger t = r.add(s).mod(DOMAIN_PARAMS.getN());

        if (t.equals(BigInteger.ZERO)) {
            return false;
        }

        ECPoint p1 = DOMAIN_PARAMS.getG().multiply(s).normalize();
        ECPoint p2 = pubKey.getQ().multiply(t).normalize();
        ECPoint p = p1.add(p2).normalize();

        BigInteger x = p.getAffineXCoord().toBigInteger();
        BigInteger r1 = e.add(x).mod(DOMAIN_PARAMS.getN());

        return r1.equals(r);
    }

    /**
     * 拼接字节数组
     */
    private static byte[] concat(byte[]... arrays) {
        int totalLength = 0;
        for (byte[] array : arrays) {
            totalLength += array.length;
        }

        byte[] result = new byte[totalLength];
        int pos = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, pos, array.length);
            pos += array.length;
        }

        return result;
    }

    /**
     * 将大整数转换为定长字节数组
     */
    private static byte[] bigIntegerToBytes(BigInteger value, int length) {
        byte[] bytes = value.toByteArray();

        if (bytes.length == length) {
            return bytes;
        }

        byte[] result = new byte[length];
        if (bytes.length > length) {
            System.arraycopy(bytes, bytes.length - length, result, 0, length);
        } else {
            System.arraycopy(bytes, 0, result, length - bytes.length, bytes.length);
        }

        return result;
    }
}
