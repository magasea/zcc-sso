package com.wensheng.sso.config;


import static com.wensheng.sso.config.PrimaryMongoConfig.MONGO_TEMPLATE;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author Marcos Barbero
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.wensheng.zcc.sso.module.dao.mongo",
        mongoTemplateRef = MONGO_TEMPLATE)
public class PrimaryMongoConfig {

    protected static final String MONGO_TEMPLATE = "mongoTemplate";
}
