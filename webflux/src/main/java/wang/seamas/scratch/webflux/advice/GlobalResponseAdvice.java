package wang.seamas.scratch.webflux.advice;

import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.reactive.result.method.annotation.ResponseBodyResultHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import wang.seamas.scratch.dto.ApiResponse;
import wang.seamas.scratch.webflux.annotation.IgnoreResponseWrapper;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

@RestControllerAdvice
public class GlobalResponseAdvice extends ResponseBodyResultHandler {

    public GlobalResponseAdvice(List<HttpMessageWriter<?>> writers,
                                 RequestedContentTypeResolver resolver,
                                 ReactiveAdapterRegistry registry) {
        super(writers, resolver, registry);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mono<Void> handleResult(ServerWebExchange exchange, HandlerResult result) {
        Object body = result.getReturnValue();
        MethodParameter returnType = result.getReturnTypeSource();

        if (returnType.hasMethodAnnotation(IgnoreResponseWrapper.class)) {
            return super.handleResult(exchange, result);
        }

        Object wrappedBody;
        if (body instanceof Mono) {
            wrappedBody = ((Mono<Object>) body).map(this::wrapResponse);
        } else if (body instanceof Flux) {
            wrappedBody = ((Flux<Object>) body).collectList().map(this::wrapResponse);
        } else {
            wrappedBody = wrapResponse(body);
        }

        HandlerResult newResult = new HandlerResult(
                result.getHandler(),
                wrappedBody,
                returnType,
                result.getBindingContext()
        );

        return super.handleResult(exchange, newResult);
    }

    private Object wrapResponse(Object body) {
        if (body instanceof ApiResponse) {
            return body;
        }
        if (shouldNotWrap(body)) {
            return body;
        }
        return ApiResponse.success(body);
    }

    private boolean shouldNotWrap(Object body) {
        if (body instanceof byte[]) {
            return true;
        }
        if (body instanceof InputStream || body instanceof OutputStream) {
            return true;
        }
        if (body instanceof Reader || body instanceof Writer) {
            return true;
        }
        if (body instanceof org.springframework.core.io.Resource) {
            return true;
        }
        if (body instanceof org.springframework.http.ResponseEntity) {
            return true;
        }
        return false;
    }
}
