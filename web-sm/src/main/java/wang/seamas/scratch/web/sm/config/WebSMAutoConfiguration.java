package wang.seamas.scratch.web.sm.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import wang.seamas.scratch.web.sm.advice.SM4ExceptionHandler;
import wang.seamas.scratch.web.sm.advice.SM4RequestAdvice;
import wang.seamas.scratch.web.sm.advice.SM4ResponseAdvice;
import wang.seamas.scratch.web.sm.controller.CryptoController;

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
@EnableConfigurationProperties(CryptoProperties.class)
@ComponentScan("wang.seamas.scratch.web.sm.controller")
public class WebSMAutoConfiguration {

    /**
     * 配置 SM4 请求解密 Advice
     *
     * @param properties 国密算法配置属性
     * @return SM4RequestAdvice 实例
     */
    @Bean
    @ConditionalOnProperty(prefix = "scratch.crypto.sm4", name = "enabled", havingValue = "true", matchIfMissing = true)
    public SM4RequestAdvice sm4RequestAdvice(CryptoProperties properties) {
        return new SM4RequestAdvice(properties);
    }

    /**
     * 配置 SM4 响应加密 Advice
     *
     * @return SM4ResponseAdvice 实例
     */
    @Bean
    @ConditionalOnProperty(prefix = "scratch.crypto.sm4", name = "enabled", havingValue = "true", matchIfMissing = true)
    public SM4ResponseAdvice sm4ResponseAdvice() {
        return new SM4ResponseAdvice();
    }

    /**
     * 配置 SM4 解密异常处理器
     *
     * @return SM4ExceptionHandler 实例
     */
    @Bean
    @ConditionalOnProperty(prefix = "scratch.crypto.sm4", name = "enabled", havingValue = "true", matchIfMissing = true)
    public SM4ExceptionHandler sm4ExceptionHandler() {
        return new SM4ExceptionHandler();
    }


}
