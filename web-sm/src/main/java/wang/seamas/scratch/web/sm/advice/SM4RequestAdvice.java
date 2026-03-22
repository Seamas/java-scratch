package wang.seamas.scratch.web.sm.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import wang.seamas.scratch.web.sm.config.CryptoProperties;
import wang.seamas.scratch.web.sm.context.CryptoContext;
import wang.seamas.scratch.web.sm.dto.EncryptedRequest;
import wang.seamas.scratch.web.sm.service.SM4DecryptService;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * SM4 请求解密 Advice
 * <p>
 * 拦截带有加密标识的请求，自动解密请求体
 * </p>
 * <p>
 * 注意：此类不使用 @RestControllerAdvice 注解，由 WebAutoConfiguration 显式配置
 * </p>
 *
 * @author Seamas
 * @since 1.0.1
 */
public class SM4RequestAdvice implements RequestBodyAdvice {

    private static final String ENCRYPTION_ENABLED = "true";
    private static final String ENCRYPTION_TYPE_SM4 = "SM4";

    private final CryptoProperties properties;
    private final SM4DecryptService decryptService;
    private final ObjectMapper objectMapper;

    public SM4RequestAdvice(CryptoProperties properties) {
        this.properties = properties;
        this.decryptService = new SM4DecryptService(properties);
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 如果未启用解密功能或未配置私钥，则不处理
        if (!properties.getSm4().isEnabled() || !properties.getSm2().isValid()) {
            return false;
        }
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        HttpHeaders headers = inputMessage.getHeaders();

        // 检查是否有加密标识
        if (!isEncryptionEnabled(headers)) {
            // 标记为非加密请求
            CryptoContext.setUnencrypted();
            return inputMessage;
        }

        // 读取原始请求体
        String requestBody = readRequestBody(inputMessage);

        // 解析加密请求
        EncryptedRequest encryptedRequest = objectMapper.readValue(requestBody, EncryptedRequest.class);

        // 解密数据
        SM4DecryptService.DecryptResult result = decryptService.decrypt(encryptedRequest);

        // 保存密钥信息到 ThreadLocal，供响应加密使用
        CryptoContext.set(result.sm4Key(), result.sm4Iv());

        // 返回解密后的请求消息
        return new DecryptHttpInputMessage(headers, result.data().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public @Nullable Object handleEmptyBody(@Nullable Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    /**
     * 清理加密上下文
     * <p>
     * 在请求处理完成后调用，避免内存泄漏
     * </p>
     */
    public static void clearContext() {
        CryptoContext.clear();
    }

    /**
     * 检查请求是否启用了加密
     *
     * @param headers HTTP 请求头
     * @return true 如果请求启用了加密
     */
    private boolean isEncryptionEnabled(HttpHeaders headers) {
        // 检查加密标识头
        String encryptionEnabled = headers.getFirst(properties.getSm4().getHeaderName());
        if (ENCRYPTION_ENABLED.equalsIgnoreCase(encryptionEnabled)) {
            return true;
        }

        // 检查加密类型头
        String encryptionType = headers.getFirst(properties.getSm4().getEncryptionTypeHeader());
        if (ENCRYPTION_TYPE_SM4.equalsIgnoreCase(encryptionType)) {
            return true;
        }

        return false;
    }

    /**
     * 读取请求体内容
     *
     * @param inputMessage HTTP 输入消息
     * @return 请求体字符串
     * @throws IOException 读取失败时抛出
     */
    private String readRequestBody(HttpInputMessage inputMessage) throws IOException {
        try (InputStream is = inputMessage.getBody()) {
            byte[] bytes = is.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }
}
