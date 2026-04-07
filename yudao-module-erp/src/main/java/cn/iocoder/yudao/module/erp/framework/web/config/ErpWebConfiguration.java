package cn.iocoder.yudao.module.erp.framework.web.config;

import cn.iocoder.yudao.framework.swagger.config.YudaoSwaggerAutoConfiguration;
import cn.iocoder.yudao.module.erp.framework.seetong.config.SeetongProperties;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * ERP 模块 Web 配置
 */
@Configuration(proxyBeanMethods = false)
public class ErpWebConfiguration implements WebMvcConfigurer {

    @Resource
    private SeetongProperties seetongProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将本地视频存证目录映射为静态资源，使前端可直接访问
        // 例：GET /static/video/2024-03-15/sale_order_1001.mp4
        if (seetongProperties.isEnabled()) {
            registry.addResourceHandler(seetongProperties.getUrlPrefix() + "/**")
                    .addResourceLocations("file:" + seetongProperties.getLocalPath() + "/");
        }
    }

    /**
     * ERP 模块的 Swagger API 分组
     */
    @Bean
    public GroupedOpenApi erpGroupedOpenApi() {
        return YudaoSwaggerAutoConfiguration.buildGroupedOpenApi("erp");
    }

}
