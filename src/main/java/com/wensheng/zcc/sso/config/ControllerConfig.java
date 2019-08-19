package com.wensheng.zcc.sso.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author chenwei on 1/3/19
 * @project zcc-backend
 */
@Configuration
public class ControllerConfig implements WebMvcConfigurer {
  @Bean(name = "multipartResolver")
  public CommonsMultipartResolver multipartResolver()
  {
    CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
    multipartResolver.setMaxUploadSize(20848820);
    return multipartResolver;
  }



  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**");
  }


  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(byteArrayHttpMessageConverter());
  }

  @Bean
  public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter() {
    ByteArrayHttpMessageConverter arrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
    arrayHttpMessageConverter.setSupportedMediaTypes(getSupportedMediaTypes());
    return arrayHttpMessageConverter;
  }

  private List<MediaType> getSupportedMediaTypes() {
    List<MediaType> list = new ArrayList<MediaType>();
    list.add(MediaType.IMAGE_JPEG);
    list.add(MediaType.IMAGE_PNG);
    list.add(MediaType.APPLICATION_OCTET_STREAM);
    return list;
  }
}
