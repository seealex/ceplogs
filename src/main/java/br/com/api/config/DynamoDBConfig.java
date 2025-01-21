package br.com.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.List;
import java.net.URI;

@Configuration
public class DynamoDBConfig {

    private static final String TIMESTAMP = "timestamp";
    @Value("${amazon.aws.accesskey}")
    private String amazonAWSAccessKey;

    @Value("${amazon.aws.secretkey}")
    private String amazonAWSSecretKey;

    @Value("${localstack.host:https://localhost.localstack.cloud:4566}")
    private String localstackHost;

    @Bean
    public AwsBasicCredentials amazonAWSCredentials() {
        return AwsBasicCredentials.create(amazonAWSAccessKey, amazonAWSSecretKey);
    }

    String tableName = "api_log";

    List<AttributeDefinition> attributeDefinitions = List.of(
            AttributeDefinition.builder()
                    .attributeName("requestId")
                    .attributeType(ScalarAttributeType.S)
                    .build(),
            AttributeDefinition.builder()
                    .attributeName("cep")
                    .attributeType(ScalarAttributeType.S)
                    .build(),
            AttributeDefinition.builder()
                    .attributeName(TIMESTAMP)
                    .attributeType(ScalarAttributeType.S)
                    .build()
    );

    List<KeySchemaElement> keySchema = List.of(
            KeySchemaElement.builder()
                    .attributeName("requestId")
                    .keyType(KeyType.HASH)
                    .build(),
            KeySchemaElement.builder()
                    .attributeName(TIMESTAMP)
                    .keyType(KeyType.RANGE)
                    .build()
    );

    ProvisionedThroughput provisionedThroughput = ProvisionedThroughput.builder()
            .readCapacityUnits(5L)
            .writeCapacityUnits(5L)
            .build();

    List<GlobalSecondaryIndex> globalSecondaryIndexes = List.of(
            GlobalSecondaryIndex.builder()
                    .indexName("CepIndex")
                    .keySchema(List.of(
                            KeySchemaElement.builder()
                                    .attributeName("cep")
                                    .keyType(KeyType.HASH)
                                    .build(),
                            KeySchemaElement.builder()
                                    .attributeName(TIMESTAMP)
                                    .keyType(KeyType.RANGE)
                                    .build()
                    ))
                    .projection(Projection.builder()
                            .projectionType(ProjectionType.ALL)
                            .build())
                    .provisionedThroughput(provisionedThroughput)
                    .build()
    );

    @Bean
    public DynamoDbClient amazonDynamoDB() {
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .endpointOverride(URI.create(localstackHost))
                .region(Region.of("us-east-1"))
                .credentialsProvider(this::amazonAWSCredentials)
                .build();


        ListTablesResponse response = dynamoDbClient.listTables();
        if (response.tableNames().isEmpty()) {
            dynamoDbClient.createTable(CreateTableRequest.builder()
                            .tableName(tableName)
                            .provisionedThroughput(provisionedThroughput)
                    .attributeDefinitions(attributeDefinitions)
                            .keySchema(
                                    keySchema
                            )
                            .globalSecondaryIndexes(globalSecondaryIndexes)
                    .build());
        }

        return dynamoDbClient;
    }
}
