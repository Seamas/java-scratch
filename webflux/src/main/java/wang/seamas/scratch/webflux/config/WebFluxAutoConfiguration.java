package wang.seamas.scratch.webflux.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import wang.seamas.scratch.webflux.advice.GlobalExceptionHandler;
import wang.seamas.scratch.webflux.advice.GlobalResponseAdvice;

@Configuration
@Import({
    GlobalExceptionHandler.class,
    GlobalResponseAdvice.class
})
public class WebFluxAutoConfiguration {
}
