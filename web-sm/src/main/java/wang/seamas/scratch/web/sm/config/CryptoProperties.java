package wang.seamas.scratch.web.sm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 国密算法配置属性
 * <p>
 * 配置 SM2 和 SM4 相关的参数，包括：
 * - SM2 密钥对（公钥下发给前端，私钥用于解密）
 * - SM4 解密相关配置（请求头、时间戳校验等）
 * </p>
 *
 * @author Seamas
 * @since 1.0.1
 */
@ConfigurationProperties(prefix = "scratch.crypto")
public class CryptoProperties {

    /**
     * SM2 配置
     */
    private Sm2Properties sm2 = new Sm2Properties();

    /**
     * SM4 配置
     */
    private Sm4Properties sm4 = new Sm4Properties();

    public Sm2Properties getSm2() {
        return sm2;
    }

    public void setSm2(Sm2Properties sm2) {
        this.sm2 = sm2;
    }

    public Sm4Properties getSm4() {
        return sm4;
    }

    public void setSm4(Sm4Properties sm4) {
        this.sm4 = sm4;
    }

    /**
     * SM2 密钥配置
     */
    public static class Sm2Properties {
        /**
         * SM2 公钥（十六进制字符串，下发给前端用于加密）
         */
        private String publicKey;

        /**
         * SM2 私钥（十六进制字符串，用于解密前端传来的 SM4 密钥和 IV）
         */
        private String privateKey;

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }

        /**
         * 验证 SM2 配置是否有效
         *
         * @return true 如果私钥已配置
         */
        public boolean isValid() {
            return privateKey != null && !privateKey.isEmpty();
        }
    }

    /**
     * SM4 解密配置
     */
    public static class Sm4Properties {
        /**
         * 是否启用 SM4 解密功能
         */
        private boolean enabled = true;

        /**
         * 加密标识请求头名称
         */
        private String headerName = "X-Encryption-Enabled";

        /**
         * 加密类型请求头名称
         */
        private String encryptionTypeHeader = "X-Encryption-Type";

        /**
         * 时间戳校验窗口（毫秒），默认 5 分钟
         * 用于防重放攻击，超过此时间窗口的请求将被拒绝
         */
        private long timestampWindow = 5 * 60 * 1000;

        /**
         * 是否启用时间戳校验（防重放攻击）
         */
        private boolean timestampCheckEnabled = false;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getHeaderName() {
            return headerName;
        }

        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }

        public String getEncryptionTypeHeader() {
            return encryptionTypeHeader;
        }

        public void setEncryptionTypeHeader(String encryptionTypeHeader) {
            this.encryptionTypeHeader = encryptionTypeHeader;
        }

        public long getTimestampWindow() {
            return timestampWindow;
        }

        public void setTimestampWindow(long timestampWindow) {
            this.timestampWindow = timestampWindow;
        }

        public boolean isTimestampCheckEnabled() {
            return timestampCheckEnabled;
        }

        public void setTimestampCheckEnabled(boolean timestampCheckEnabled) {
            this.timestampCheckEnabled = timestampCheckEnabled;
        }
    }
}
