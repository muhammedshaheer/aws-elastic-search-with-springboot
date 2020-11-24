package com.example.elasticsearch.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsCredentialsConfiguration {

    @Value("${aws.es.accessKey}")
    private String esAccessKey;

    @Value("${aws.es.secretKey}")
    private String esSecretKey;

    @Bean
    public AWSStaticCredentialsProvider awsStaticCredentialsProvider() {
        return new AWSStaticCredentialsProvider(new BasicAWSCredentials(esAccessKey, esSecretKey));
    }
}
