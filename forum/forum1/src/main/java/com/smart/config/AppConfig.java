package com.smart.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({DaoConfig.class,ServiceConfig.class,ShiroConfig.class})
public class AppConfig {
}
