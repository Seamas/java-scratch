package wang.seamas.scratch.web.sm.dto;

/**
 * 加密请求数据传输对象
 * <p>
 * 用于接收前端加密后的请求数据，包含：
 * - key: 经 SM2 加密的 SM4 密钥（Base64 编码）
 * - iv: 经 SM2 加密的 SM4 初始化向量（Base64 编码）
 * - message: 经 SM4 加密的业务数据（Base64 编码）
 * </p>
 *
 * @author Seamas
 * @since 1.0.1
 */
public class EncryptedRequest {

    /**
     * SM4 密钥（经 SM2 加密后 Base64 编码）
     */
    private String key;

    /**
     * SM4 初始化向量（经 SM2 加密后 Base64 编码）
     */
    private String iv;

    /**
     * 业务数据（经 SM4 加密后 Base64 编码）
     */
    private String message;

    /**
     * 时间戳（可选，用于防重放攻击）
     */
    private Long timestamp;

    public EncryptedRequest() {
    }

    public EncryptedRequest(String key, String iv, String message) {
        this.key = key;
        this.iv = iv;
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * 验证请求数据是否完整
     *
     * @return true 如果 key、iv、message 都不为空
     */
    public boolean isValid() {
        return key != null && !key.isEmpty()
                && iv != null && !iv.isEmpty()
                && message != null && !message.isEmpty();
    }

    @Override
    public String toString() {
        return "EncryptedRequest{" +
                "key='" + (key != null ? "***" : null) + '\'' +
                ", iv='" + (iv != null ? "***" : null) + '\'' +
                ", message='" + (message != null ? "***" : null) + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
