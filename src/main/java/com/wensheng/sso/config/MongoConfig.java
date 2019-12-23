package com.wensheng.sso.config;

import com.mongodb.MongoClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

/**
 * @author Marcos Barbero
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(MongoProperties.class)
public class MongoConfig {

    @Value("${spring.data.mongodb.database}")
    String dbName;

    @Value("${spring.data.mongodb.host}")
    String host;

    @Value("${spring.data.mongodb.port}")
    int port;

    @Bean
    public MongoClient mongoClient(){
        return new MongoClient(host, port);
    }




    @Bean
    public MongoTemplate mongoTemplate() {



        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient(), dbName);

        return mongoTemplate;

    }


}
