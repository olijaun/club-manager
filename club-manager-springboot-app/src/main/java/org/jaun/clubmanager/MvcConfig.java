package org.jaun.clubmanager;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// https://medium.com/@kshep92/single-page-applications-with-spring-boot-b64d8d37015d
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    public MvcConfig() {
        System.out.println("mvc config");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/*.js", "/*.css", "/*.txt", "/*.ico", "/*.html", "/*").addResourceLocations("classpath:static/");
    }

}
