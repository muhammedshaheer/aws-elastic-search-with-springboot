package com.example.elasticsearch.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class AwsCredentialsConfiguration {

    private final String esAccessKey;
    private final String esSecretKey;

    public AwsCredentialsConfiguration(AwsCredentialsConfig awsCredentialsConfig) {
        Map<String, Object> configEs = awsCredentialsConfig.getEs();
        esAccessKey = (String) configEs.get("accessKey");
        esSecretKey = (String) configEs.get("secretKey");
    }

    @Bean
    public AWSStaticCredentialsProvider awsStaticCredentialsProvider() {
        return new AWSStaticCredentialsProvider(new BasicAWSCredentials(esAccessKey, esSecretKey));
    }
}
