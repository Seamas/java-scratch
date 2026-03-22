package wang.seamas.scratch.web.sm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SM4 解密配置属性
 * <p>
 * 配置 SM4 解密相关的参数，包括：
 * - SM2 私钥（用于解密 SM4 密钥）
 * - 请求头名称
 * - 时间戳校验窗口（防重放攻击）
 * </p>
 *
 * @author Seamas
 * @since 1.0.1
 */
@ConfigurationProperties(prefix = "scratch.crypto.sm4")
public class SM4DecryptProperties {

    /**
     * 是否启用 SM4 解密功能
     */
    private boolean enabled = true;

    /**
     * SM2 私钥（十六进制字符串，用于解密前端传来的 SM4 密钥和 IV）
     */
    private String privateKey;

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
    private boolean timestampCheckEnabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
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

    /**
     * 验证配置是否有效
     *
     * @return true 如果私钥已配置
     */
    public boolean isValid() {
        return privateKey != null && !privateKey.isEmpty();
    }
}
