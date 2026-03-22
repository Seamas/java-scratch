package wang.seamas.scratch.web.sm.advice;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 解密后的 HTTP 输入消息
 * <p>
 * 包装解密后的请求体，供后续处理器使用
 * </p>
 *
 * @author Seamas
 * @since 1.0.1
 */
public class DecryptHttpInputMessage implements HttpInputMessage {

    private final HttpHeaders headers;
    private final byte[] decryptedBody;

    public DecryptHttpInputMessage(HttpHeaders headers, byte[] decryptedBody) {
        this.headers = headers;
        this.decryptedBody = decryptedBody;
    }

    @Override
    public InputStream getBody() throws IOException {
        return new ByteArrayInputStream(decryptedBody);
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }
}
