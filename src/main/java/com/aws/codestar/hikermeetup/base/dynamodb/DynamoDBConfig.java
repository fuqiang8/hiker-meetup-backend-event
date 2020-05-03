package com.aws.codestar.hikermeetup.base.dynamodb;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
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

    @Value("${dynamodb.accesskey}")
    private String accessKey;

    @Value("${dynamodb.secretkey}")
    private String secretKey;

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        AmazonDynamoDB amazonDynamoDB
                = new AmazonDynamoDBClient(new BasicAWSCredentials(accessKey, secretKey));

        amazonDynamoDB.setEndpoint(endpoint);
        amazonDynamoDB.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_1));

        return amazonDynamoDB;
    }
}
