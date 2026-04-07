package cn.iocoder.yudao.module.erp.framework.seetong.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Seetong 配置属性
 */
@Component
@ConfigurationProperties(prefix = "yudao.seetong")
@Data
public class SeetongProperties {

    /** Seetong Cloud AppKey */
    private String appKey = "";

    /** Seetong Cloud AppSecret */
    private String appSecret = "";

    /** 用户名 */
    private String username = "";

    /** 密码 */
    private String password = "";

    /** API 基础路径 */
    private String baseUrl = "https://api.seetong.com";

    /** 回放前置时间（分钟），取交易前10分钟 */
    private Integer preMinutes = 10;

    /** 回放后置时间（分钟），取交易后10分钟 */
    private Integer postMinutes = 10;

    /** 本地存储路径 */
    private String localPath = "/data/video";

    /** 本地存储 URL 前缀（对应静态资源映射） */
    private String urlPrefix = "/static/video";

    /** 是否启用视频存证（未配置摄像头时可关闭） */
    private boolean enabled = false;

}
