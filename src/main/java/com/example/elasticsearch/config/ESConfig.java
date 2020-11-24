package com.example.elasticsearch.config;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.example.elasticsearch.repository")
public class ESConfig extends AbstractElasticsearchConfiguration {

    @Value("${aws.es.endPoint}")
    private String endPoint;

    @Value("${aws.es.region}")
    private String region;

    private final AWSCredentialsProvider awsCredentialsProvider;

    public ESConfig(AWSCredentialsProvider awsCredentialsProvider) {
        this.awsCredentialsProvider = awsCredentialsProvider;
    }

    @Override
    @Bean(value = "es-config")
    public RestHighLevelClient elasticsearchClient() {
        AWS4Signer signer = new AWS4Signer();
        String serviceName = "es";
        signer.setServiceName(serviceName);
        signer.setRegionName(region);
        HttpRequestInterceptor interceptor = new AwsESRequestSigningInterceptor(serviceName, signer, awsCredentialsProvider);
        return new RestHighLevelClient(RestClient.builder(HttpHost.create(endPoint)).setHttpClientConfigCallback(e -> e.addInterceptorLast(interceptor)));
    }
}
