package wang.seamas.scratch.web.advice;

import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import wang.seamas.scratch.dto.ApiResponse;
import wang.seamas.scratch.web.annotation.IgnoreResponseWrapper;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * 全局响应包装 Advice
 * <p>
 * 将所有响应包装为统一的 ApiResponse 格式
 * 优先级最高，确保最先执行
 * </p>
 *
 * @author Seamas
 * @since 1.0.0
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, 
                           Class<? extends HttpMessageConverter<?>> converterType) {
        if (returnType.hasMethodAnnotation(IgnoreResponseWrapper.class)) {
            return false;
        }
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        if (body instanceof ApiResponse) {
            return body;
        }
        if (shouldNotWrap(body, selectedContentType)) {
            return body;
        }
        return ApiResponse.success(body);
    }

    private boolean shouldNotWrap(Object body, MediaType contentType) {
        if (body instanceof byte[]) {
            return true;
        }
        if (body instanceof InputStream || body instanceof OutputStream) {
            return true;
        }
        if (body instanceof Reader || body instanceof Writer) {
            return true;
        }
        if (contentType != null && !contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
            return true;
        }
        return false;
    }
}
