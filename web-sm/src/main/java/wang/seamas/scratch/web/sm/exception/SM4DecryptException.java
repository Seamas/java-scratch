package wang.seamas.scratch.web.sm.exception;

/**
 * SM4 解密异常
 * <p>
 * 当请求解密过程中发生错误时抛出，包括：
 * - 请求格式错误
 * - SM2 解密失败
 * - SM4 解密失败
 * - 数据格式错误
 * </p>
 *
 * @author Seamas
 * @since 1.0.1
 */
public class SM4DecryptException extends RuntimeException {

    /**
     * 错误类型
     */
    public enum ErrorType {
        INVALID_REQUEST("请求格式错误"),
        SM2_DECRYPT_ERROR("SM2 解密失败"),
        SM4_DECRYPT_ERROR("SM4 解密失败"),
        INVALID_DATA_FORMAT("数据格式错误"),
        REPLAY_ATTACK("检测到重放攻击");

        private final String description;

        ErrorType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private final ErrorType errorType;

    public SM4DecryptException(ErrorType errorType) {
        super(errorType.getDescription());
        this.errorType = errorType;
    }

    public SM4DecryptException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    public SM4DecryptException(ErrorType errorType, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}
