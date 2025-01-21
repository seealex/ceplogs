package br.com.api.log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.*;

@Component
public class LogConverter {

    private final ObjectMapper objectMapper;

    @Autowired
    public LogConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Map<String, AttributeValue> convertToAttributeMap(LogEntity apilog) {
        Map<String, AttributeValue> attributeMap = new HashMap<>();

        addFieldToMap(attributeMap, "requestId", apilog.getRequestId());
        addFieldToMap(attributeMap, "timestamp", apilog.getTimestamp());
        addFieldToMap(attributeMap, "cep", apilog.getCep());
        addFieldToMap(attributeMap, "method", apilog.getMethod());
        addFieldToMap(attributeMap, "url", apilog.getUrl());
        addFieldToMap(attributeMap, "statusCode", apilog.getStatusCode());
        addFieldToMap(attributeMap, "request", apilog.getRequest());
        addFieldToMap(attributeMap, "error", apilog.getError());

        convertNestedFieldsToDynamoDBMap(attributeMap, "response", apilog.getResponse());
        convertNestedFieldsToDynamoDBMap(attributeMap, "headers", apilog.getHeaders());
        convertNestedFieldsToDynamoDBMap(attributeMap, "parameters", apilog.getParameters());

        return attributeMap;
    }

    private void addFieldToMap(Map<String, AttributeValue> map, String field, String value) {
        Optional.ofNullable(value).filter(v -> !v.isEmpty())
                .ifPresent(val -> map.put(field, AttributeValue.builder().s(val).build()));
    }

    private void convertNestedFieldsToDynamoDBMap(Map<String, AttributeValue> attributeMap, String fieldName, Object fieldValue) {
        Optional.ofNullable(fieldValue).ifPresent(value -> {
            if (value instanceof List<?>) {
                attributeMap.put(fieldName, AttributeValue.builder().l(convertToDynamoDBList((List<?>) value)).build());
            } else {
                attributeMap.put(fieldName, AttributeValue.builder().m(convertToDynamoDBMap(objectMapper.convertValue(value, new TypeReference<Map<String, Object>>() {
                }))).build());
            }
        });
    }

    private List<AttributeValue> convertToDynamoDBList(List<?> list) {
        List<AttributeValue> attributeValueList = new ArrayList<>();
        for (Object item : list) {
            attributeValueList.add(AttributeValue.builder().m(convertToDynamoDBMap(objectMapper.convertValue(item, new TypeReference<Map<String, Object>>() {
            }))).build());
        }
        return attributeValueList;
    }

    private static Map<String, AttributeValue> convertToDynamoDBMap(Map<String, Object> map) {
        Map<String, AttributeValue> dynamoMap = new HashMap<>();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof String string) {
                dynamoMap.put(key, AttributeValue.builder().s(string).build());
            } else if (value instanceof Map mapValue) {
                dynamoMap.put(key, AttributeValue.builder().m(convertToDynamoDBMap(mapValue)).build());
            } else if (value instanceof Boolean) {
                dynamoMap.put(key, AttributeValue.builder().bool((Boolean) value).build());
            } else if (value instanceof Number) {
                dynamoMap.put(key, AttributeValue.builder().n(value.toString()).build());
            }
        }

        return dynamoMap;
    }

    public <T> T convertFromDynamoDB(Map<String, AttributeValue> dynamoDbItem, Class<T> clazz) {
        try {

            Map<String, Object> objectMap = convertToObjectMapThis(dynamoDbItem);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return objectMapper.convertValue(objectMap, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Error converting DynamoDB map to object", e);
        }
    }

    private static Map<String, Object> convertToObjectMapThis(Map<String, AttributeValue> dynamoDbItem) {
        Map<String, Object> objectMap = new HashMap<>();

        for (Map.Entry<String, AttributeValue> entry : dynamoDbItem.entrySet()) {
            String key = entry.getKey();
            AttributeValue value = entry.getValue();

            if (value.s() != null) {
                objectMap.put(key, value.s());
            } else if (value.n() != null) {
                objectMap.put(key, value.n());
            } else if (value.bool() != null) {
                objectMap.put(key, value.bool());
            } else if (value.l() != null && !value.l().isEmpty()) {
                List<Object> list = new ArrayList<>();
                for (AttributeValue listItem : value.l()) {
                    if (listItem.s() != null) {
                        list.add(listItem.s());
                    } else if (listItem.n() != null) {
                        list.add(listItem.n());
                    } else if (listItem.bool() != null) {
                        list.add(listItem.bool());
                    } else if (listItem.m() != null) {
                        list.add(convertToObjectMapThis(listItem.m()));
                    }
                }
                objectMap.put(key, list);
            } else if (value.m() != null) {
                objectMap.put(key, convertToObjectMapThis(value.m()));
            }
        }

        return objectMap;
    }

}

