package com.example.elasticsearch.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "aws")
public class AwsCredentialsConfig {

    private Map<String, Object> es;
}
