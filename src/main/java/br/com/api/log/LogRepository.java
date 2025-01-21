package br.com.api.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

@Slf4j
@Repository
public class LogRepository {


    private final DynamoDbClient dynamoDbClient;
    private final LogConverter logConverter;

    @Autowired
    public LogRepository(DynamoDbClient dynamoDbClient, LogConverter logConverter) {

        this.dynamoDbClient = dynamoDbClient;
        this.logConverter = logConverter;
    }

    public void save(PutItemRequest request) {
        try {

            dynamoDbClient.putItem(request);

        } catch (DynamoDbException e) {
            log.error("Error saving item: {}", e.getMessage());
        }
    }

    public List<LogEntity> queryWith(QueryRequest request) {
        List<LogEntity> logEntityList = new ArrayList<>();

        try {
            QueryResponse queryResponse = dynamoDbClient.query(request);
            queryResponse.items().forEach(item -> {
                LogEntity logEntity = this.logConverter.convertFromDynamoDB(item, LogEntity.class);
                logEntityList.add(logEntity);
            });
        } catch (DynamoDbException e) {
            log.error("Error executing query: {}", e.getMessage());
        }

        return logEntityList;
    }


}
