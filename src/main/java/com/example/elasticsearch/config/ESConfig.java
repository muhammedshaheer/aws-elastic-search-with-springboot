package com.example.elasticsearch.config;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.util.Map;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.example.elasticsearch.repository")
public class ESConfig extends AbstractElasticsearchConfiguration {

    private final String endPoint;
    private final String region;

    private final AWSCredentialsProvider awsCredentialsProvider;

    public ESConfig(AWSCredentialsProvider awsCredentialsProvider, AwsCredentialsConfig awsCredentialsConfig) {
        this.awsCredentialsProvider = awsCredentialsProvider;
        Map<String, Object> configEs = awsCredentialsConfig.getEs();
        endPoint = (String) configEs.get("endPoint");
        region = (String) configEs.get("region");
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
