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
 * erp 模块的 web 组件的 Configuration
 *
 * @author 芋道源码
 */
@Configuration(proxyBeanMethods = false)
public class ErpWebConfiguration implements WebMvcConfigurer {

    @Resource
    private SeetongProperties seetongProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源处理：将本地存储的监控视频映射到 URL
        registry.addResourceHandler(seetongProperties.getUrlPrefix() + "/**")
                .addResourceLocations("file:" + seetongProperties.getLocalPath() + "/");
    }

    /**
     * erp 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi erpGroupedOpenApi() {
        return YudaoSwaggerAutoConfiguration.buildGroupedOpenApi("erp");
    }

}
