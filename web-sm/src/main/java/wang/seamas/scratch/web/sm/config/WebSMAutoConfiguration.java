package wang.seamas.scratch.web.sm.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import wang.seamas.scratch.web.sm.advice.SM4ExceptionHandler;
import wang.seamas.scratch.web.sm.advice.SM4RequestAdvice;
import wang.seamas.scratch.web.sm.advice.SM4ResponseAdvice;

/**
 * Web SM 自动配置类
 * <p>
 * 自动配置 SM4 解密和响应包装功能
 * </p>
 *
 * @author Seamas
 * @since 1.0.1
 */
@Configuration
@EnableConfigurationProperties(SM4DecryptProperties.class)
@Import({
    SM4ResponseAdvice.class,
    SM4ExceptionHandler.class
})
public class WebSMAutoConfiguration {

    /**
     * 配置 SM4 请求解密 Advice
     *
     * @param properties SM4 解密配置属性
     * @return SM4RequestAdvice 实例
     */
    @Bean
    public SM4RequestAdvice sm4RequestAdvice(SM4DecryptProperties properties) {
        return new SM4RequestAdvice(properties);
    }
}
