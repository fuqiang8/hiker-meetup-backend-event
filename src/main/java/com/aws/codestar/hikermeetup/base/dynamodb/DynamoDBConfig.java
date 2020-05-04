package com.aws.codestar.hikermeetup.base.dynamodb;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.aws.codestar.hikermeetup.event.data.EventRepository;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDynamoDBRepositories(basePackageClasses = EventRepository.class)
public class DynamoDBConfig {

    @Value("${dynamodb.endpoint}")
    private String endpoint;

    @Value("${dynamodb.region}")
    private String region;

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
        String secretKey = System.getenv("AWS_SECRET_KEY");

        AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
                .standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(accessKey, secretKey)))
                .build();

        return amazonDynamoDB;
    }
}
