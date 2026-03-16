package com.dailystar.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "dailystar.auth.jwt")
public class AuthProperties {

    private String secret;
    private long expireSeconds;
}
