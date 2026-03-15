package wang.seamas.scratch.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    private int code;
    private boolean success;
    private String message;
    private T data;
    private long timestamp;



    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200,
                true,
                "success",
                data,
                System.currentTimeMillis());
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code,
                false,
                message,
                null,
                System.currentTimeMillis());
    }

    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    public static <T> ApiResponse<T> badRequest(String message) {
        return error(400, message);
    }

    public static <T> ApiResponse<T> businessError(int code, String message) {
        return error(code, message);
    }
}
