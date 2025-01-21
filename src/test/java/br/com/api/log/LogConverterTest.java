package br.com.api.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class LogConverterTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private LogConverter logConverter;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConvertToAttributeMap() {
        LogEntity logEntity = new LogEntity();
        logEntity.setRequestId("123");
        logEntity.setTimestamp("2025-01-01");

        Map<String, AttributeValue> result = logConverter.convertToAttributeMap(logEntity);

        assertTrue(result.containsKey("requestId"));
        assertTrue(result.containsKey("timestamp"));
    }

    @Test
    void testConvertFromDynamoDB() {
        Map<String, AttributeValue> dynamoDbItem = mock(Map.class);


        LogEntity logEntity = logConverter.convertFromDynamoDB(dynamoDbItem, LogEntity.class);

        assertNotNull(logEntity);
    }
}

