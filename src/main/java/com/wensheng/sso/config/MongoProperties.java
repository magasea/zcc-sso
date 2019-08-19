package com.wensheng.sso.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Marcos Barbero
 */
@Data
@ConfigurationProperties(prefix = "mongodb")
public class MongoProperties {



    private org.springframework.boot.autoconfigure.mongo.MongoProperties wszcc_sso = new org.springframework.boot.autoconfigure.mongo.MongoProperties();


}
