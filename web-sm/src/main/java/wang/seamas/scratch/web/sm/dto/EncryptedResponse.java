package wang.seamas.scratch.web.sm.dto;

/**
 * 加密响应数据传输对象
 * <p>
 * 用于返回加密后的响应数据，包含：
 * - message: 经 SM4 加密的业务数据（Base64 编码）
 * </p>
 * <p>
 * 注意：响应使用与请求相同的 key 和 iv 进行加密，前端可直接使用请求时的 key/iv 解密
 * </p>
 *
 * @author Seamas
 * @since 1.0.1
 */
public class EncryptedResponse {

    /**
     * 业务数据（经 SM4 加密后 Base64 编码）
     */
    private String message;

    public EncryptedResponse() {
    }

    public EncryptedResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "EncryptedResponse{" +
                "message='" + (message != null ? "***" : null) + '\'' +
                '}';
    }
}
