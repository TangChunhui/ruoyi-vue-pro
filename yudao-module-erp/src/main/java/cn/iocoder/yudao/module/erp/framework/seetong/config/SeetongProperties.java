package cn.iocoder.yudao.module.erp.framework.seetong.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * Seetong 配置属性
 */
@ConfigurationProperties("yudao.seetong")
@Data
@Validated
public class SeetongProperties {

    /**
     * Seetong Cloud AppKey
     */
    @NotEmpty(message = "Seetong AppKey 不能为空")
    private String appKey;

    /**
     * Seetong Cloud AppSecret
     */
    @NotEmpty(message = "Seetong AppSecret 不能为空")
    private String appSecret;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * API 基础路径
     */
    private String baseUrl = "https://api.seetong.com";

    /**
     * 默认回放前置时间（分钟）
     */
    private Integer preMinutes = 1;

    /**
     * 默认回放后置时间（分钟）
     */
    private Integer postMinutes = 1;

    /**
     * 本地存储路径
     */
    private String localPath = "/data/video";

    /**
     * 本地存储 URL 前缀
     */
    private String urlPrefix = "/static/video";

}
