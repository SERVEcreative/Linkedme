package com.linkedme.gateway.config;

import com.linkedme.gateway.config.properties.GatewayProperties;
import com.linkedme.gateway.config.properties.JwtProperties;
import com.linkedme.gateway.config.properties.RateLimitProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

@Configuration
@EnableConfigurationProperties({
        GatewayProperties.class,
        JwtProperties.class,
        RateLimitProperties.class
})
public class GatewayConfig {

    @Bean
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(ReactiveRedisConnectionFactory factory) {
        return new ReactiveStringRedisTemplate(factory);
    }
}
