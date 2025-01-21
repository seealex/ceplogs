package br.com.api.log;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class LogService {

    private static final String PK_VALUE_PLACEHOLDER = ":pkval";
    private final LogRepository logRepository;
    private final ObjectMapper objectMapper;
    private final LogConverter logConverter;
    public static final String API_LOG = "api_log";

    public String findBy(String cep) {
        QueryRequest request = createQueryRequestFor(cep);
        List<LogEntity> logList = logRepository.queryWith(request);
        return serializeLogList(logList);
    }

    public String findBy(UUID requestId) {
        QueryRequest request = createQueryRequestFor(requestId);
        List<LogEntity> logList = logRepository.queryWith(request);
        return serializeLogList(logList);
    }

    private String serializeLogList(List<LogEntity> logList) {
        try {
            return objectMapper.writeValueAsString(logList);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private QueryRequest createQueryRequestFor(String cep) {
        Map<String, String> expressionAttributeNames = Map.of("#pk", "cep");

        Map<String, AttributeValue> expressionAttributeValues = Map.of(PK_VALUE_PLACEHOLDER, AttributeValue.builder()
                .s(cep)
                .build());

        return QueryRequest.builder()
                .tableName(API_LOG)
                .indexName("CepIndex")
                .keyConditionExpression("#pk = " + PK_VALUE_PLACEHOLDER)
                .expressionAttributeNames(expressionAttributeNames)
                .expressionAttributeValues(expressionAttributeValues)
                .build();
    }

    private QueryRequest createQueryRequestFor(UUID requestId) {
        Map<String, AttributeValue> expressionAttributeValues = Map.of(PK_VALUE_PLACEHOLDER, AttributeValue.builder()
                .s(requestId.toString())
                .build());

        return QueryRequest.builder()
                .tableName(API_LOG)
                .keyConditionExpression("requestId = " + PK_VALUE_PLACEHOLDER)
                .expressionAttributeValues(expressionAttributeValues)
                .build();
    }

    public void save(LogEntity logEntity) {
        PutItemRequest request = PutItemRequest.builder()
                .tableName(API_LOG)
                .item(logConverter.convertToAttributeMap(logEntity))
                .build();
        logRepository.save(request);
    }
}
