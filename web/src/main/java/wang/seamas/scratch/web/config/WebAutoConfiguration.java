package wang.seamas.scratch.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import wang.seamas.scratch.web.advice.GlobalExceptionHandler;
import wang.seamas.scratch.web.advice.GlobalResponseAdvice;

@Configuration
@Import({
    GlobalExceptionHandler.class,
    GlobalResponseAdvice.class
})
public class WebAutoConfiguration {
}
