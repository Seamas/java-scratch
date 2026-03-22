package wang.seamas.scratch.web.sm.controller;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.seamas.scratch.web.annotation.IgnoreResponseWrapper;
import wang.seamas.scratch.web.sm.config.CryptoProperties;

/**
 * 国密算法控制器
 * <p>
 * 提供 SM2 公钥下发等接口
 * </p>
 *
 * @author Seamas
 * @since 1.0.1
 */
@RestController
@RequestMapping("/crypto")
@AllArgsConstructor
@ConditionalOnProperty(prefix = "scratch.crypto.sm4", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CryptoController {

    private final CryptoProperties cryptoProperties;

    /**
     * 获取 SM2 公钥
     * <p>
     * 前端使用该公钥加密 SM4 密钥
     * </p>
     *
     * @return SM2 公钥（十六进制字符串）
     */
    @GetMapping("public-key")
    @IgnoreResponseWrapper
    public String publicKey() {
        return cryptoProperties.getSm2().getPublicKey();
    }
}
