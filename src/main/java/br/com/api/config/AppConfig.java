package br.com.api.config;

import br.com.api.log.LogParserService;
import br.com.api.interceptor.ApiRequestBodyReader;
import br.com.api.interceptor.ApiRequestIdGenerator;
import br.com.api.log.LogEntity;
import br.com.api.interceptor.ApiRequestInterceptor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    public LogEntity apiLog() {
        return new LogEntity();
    }


    @Bean
    public ApiRequestInterceptor loggingInterceptor(LogParserService logParserService,
                                                    ApiRequestIdGenerator apiRequestIdGenerator,
                                                    ApiRequestBodyReader apiRequestBodyReader) {
        return new ApiRequestInterceptor(logParserService, apiRequestIdGenerator, apiRequestBodyReader);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor(null, null, null));
    }

}
