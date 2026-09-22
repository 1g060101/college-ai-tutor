package com.aistudy.tutor.infrastructure.config;

import com.aistudy.tutor.domain.report.service.MasteryEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 纯领域服务注册：领域服务不依赖框架注解，统一在此注册为 Bean 供 application 注入
 */
@Configuration
public class DomainServiceConfig {

    @Bean
    public MasteryEngine masteryEngine() {
        return new MasteryEngine();
    }
}
