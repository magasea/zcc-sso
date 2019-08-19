package com.wensheng.zcc.sso.config;

import com.mongodb.MongoClient;
import lombok.RequiredArgsConstructor;
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

    private final MongoProperties mongoProperties;




    @Primary
    @Bean(name = PrimaryMongoConfig.MONGO_TEMPLATE)
    public MongoTemplate wszccTemplate() throws Exception {
        return new MongoTemplate(wszccFactory(this.mongoProperties.getWszcc_sso()));
    }



    @Bean
    public MongoDbFactory wszccFactory(final org.springframework.boot.autoconfigure.mongo.MongoProperties mongo) throws Exception {
        return new SimpleMongoDbFactory(new MongoClient(mongo.getHost(), mongo.getPort()),
                mongo.getDatabase());
    }

}
