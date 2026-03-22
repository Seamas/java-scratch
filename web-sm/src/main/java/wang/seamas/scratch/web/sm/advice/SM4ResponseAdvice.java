package wang.seamas.scratch.web.sm.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import wang.seamas.scratch.web.sm.context.CryptoContext;
import wang.seamas.scratch.web.sm.dto.EncryptedResponse;
import wang.seamas.scratch.web.sm.util.SM4CryptoUtil;

/**
 * SM4 响应加密 Advice
 * <p>
 * 对加密请求的响应进行 SM4 加密，确保请求和响应的数据结构一致性
 * </p>
 * <p>
 * 执行顺序：最低优先级，确保在所有其他 ResponseBodyAdvice 之后执行
 * </p>
 *
 * @author Seamas
 * @since 1.0.1
 */
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class SM4ResponseAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supports(MethodParameter returnType,
                           Class<? extends HttpMessageConverter<?>> converterType) {
        // 所有响应都支持，由 beforeBodyWrite 决定是否加密
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
        // 检查是否需要加密响应
        if (!CryptoContext.isEncrypted()) {
            // 非加密请求，直接返回原始响应
            return body;
        }

        // 如果已经是加密响应，不再处理
        if (body instanceof EncryptedResponse) {
            return body;
        }

        try {
            // 获取当前请求的 SM4 密钥和 IV
            String sm4Key = CryptoContext.getSm4Key();
            String sm4Iv = CryptoContext.getSm4Iv();

            if (sm4Key == null || sm4Iv == null) {
                // 密钥信息不存在，返回原始响应
                return body;
            }

            // 将响应对象转换为 JSON 字符串
            String jsonResponse;
            if (body instanceof String) {
                jsonResponse = (String) body;
            } else {
                jsonResponse = objectMapper.writeValueAsString(body);
            }

            // SM4 加密响应数据
            String encryptedMessageHex = SM4CryptoUtil.encryptCBC(jsonResponse, sm4Key, sm4Iv);
            byte[] encryptedMessageBytes = Hex.decode(encryptedMessageHex);
            String encryptedMessageBase64 = Base64.toBase64String(encryptedMessageBytes);

            // 返回加密响应
            return new EncryptedResponse(encryptedMessageBase64);

        } catch (Exception e) {
            // 加密失败，返回原始响应（记录日志）
            // 实际生产环境可能需要更严格的错误处理
            return body;
        } finally {
            // 清理 ThreadLocal，避免内存泄漏
            CryptoContext.clear();
        }
    }
}
