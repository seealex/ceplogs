package br.com.api.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogServiceTest {

    @Mock
    private LogRepository logRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private LogConverter logConverter;

    @InjectMocks
    private LogService logService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        logService = new LogService(logRepository, objectMapper, logConverter);
    }

    @Test
    void testFindByCep() throws JsonProcessingException {
        String cep = "12345678";
        String expectedResponse = "[{\"cep\": \"12345678\", \"method\": \"GET\"}]";

        QueryRequest queryRequest = mock(QueryRequest.class);
        when(logRepository.queryWith(queryRequest)).thenReturn(new ArrayList<>());
        when(objectMapper.writeValueAsString(Mockito.anyList())).thenReturn(expectedResponse);

        String actualResponse = logService.findBy(cep);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testFindByRequestId() throws JsonProcessingException {
        UUID requestId = UUID.fromString("fec6442c-4ae5-4376-830b-4722ba341525");
        String expectedResponse = "[{\"requestId\": \"fec6442c-4ae5-4376-830b-4722ba341525\", \"method\": \"POST\"}]";

        QueryRequest queryRequest = mock(QueryRequest.class);
        when(logRepository.queryWith(queryRequest)).thenReturn(new ArrayList<>());
        when(objectMapper.writeValueAsString(Mockito.anyList())).thenReturn(expectedResponse);

        String actualResponse = logService.findBy(requestId);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testSave() {
        LogEntity logEntity = new LogEntity();
        when(logConverter.convertToAttributeMap(logEntity)).thenReturn(new HashMap<>());

        logService.save(logEntity);

        verify(logRepository).save(Mockito.any());
    }
}

