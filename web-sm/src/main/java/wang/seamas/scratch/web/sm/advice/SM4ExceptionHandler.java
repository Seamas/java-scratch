package wang.seamas.scratch.web.sm.advice;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wang.seamas.scratch.dto.ApiResponse;
import wang.seamas.scratch.web.sm.exception.SM4DecryptException;

/**
 * SM4 解密异常处理器
 * <p>
 * 处理 SM4 解密过程中抛出的异常
 * </p>
 * <p>
 * 注意：返回的 ApiResponse 会被 SM4ResponseAdvice 加密（如果是加密请求）
 * </p>
 *
 * @author Seamas
 * @since 1.0.1
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE + 100) // 在 GlobalResponseAdvice 之后，SM4ResponseAdvice 之前
public class SM4ExceptionHandler {

    /**
     * 处理 SM4 解密异常
     *
     * @param e SM4 解密异常
     * @return API 错误响应
     */
    @ExceptionHandler(SM4DecryptException.class)
    public ApiResponse<Void> handleSM4DecryptException(SM4DecryptException e) {
        SM4DecryptException.ErrorType errorType = e.getErrorType();

        return switch (errorType) {
            case INVALID_REQUEST -> ApiResponse.badRequest("加密请求格式错误: " + e.getMessage());
            case SM2_DECRYPT_ERROR -> ApiResponse.error(400, "密钥解密失败: " + e.getMessage());
            case SM4_DECRYPT_ERROR -> ApiResponse.error(400, "数据解密失败: " + e.getMessage());
            case INVALID_DATA_FORMAT -> ApiResponse.badRequest("数据格式错误: " + e.getMessage());
            case REPLAY_ATTACK -> ApiResponse.error(403, "请求已过期，请重新发送请求");
        };
    }
}
