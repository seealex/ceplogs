package br.com.api.cep;

import br.com.api.log.LogParserImpl;
import br.com.api.log.LogEntity;
import br.com.api.log.LogService;
import br.com.api.viacep.ViaCep;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.mockito.Mockito.*;

public class LoggingImplTestEntity {

    @Mock
    private LogService logService;

    @Mock
    private ObjectMapper objectMapper;

    private LogParserImpl apiLoggingImpl;

    private MockHttpServletRequest mockRequest;
    private MockHttpServletResponse mockResponse;

    @Captor
    private ArgumentCaptor<LogEntity> apiLogCaptor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        apiLoggingImpl = new LogParserImpl(logService, objectMapper);

        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
    }

    @Test
    public void testParserRequestFrom() {

        mockRequest.setRequestURI("/api/ceps/12345678/json/");
        mockRequest.setMethod("GET");
        mockRequest.setAttribute("X-request-id", "12345");
        mockRequest.addParameter("param1", "value1");

        apiLoggingImpl.parserRequestFrom(mockRequest, null);

        verify(logService, times(1)).save(apiLogCaptor.capture());

        LogEntity capturedLogEntity = apiLogCaptor.getValue();
        assert capturedLogEntity != null;
        assert capturedLogEntity.getCep().equals("12345678");
        assert capturedLogEntity.getRequestId().equals("12345");
        assert capturedLogEntity.getMethod().equals("GET");
        assert capturedLogEntity.getParameters().containsKey("param1");
        assert capturedLogEntity.getParameters().get("param1").equals("value1");
    }

    @Test
    public void testHeadersParserRequestFrom() {
        mockRequest.setRequestURI("/api/ceps/12345678/json/");
        mockRequest.setMethod("GET");
        mockRequest.addHeader("any","any");
        mockRequest.setAttribute("X-request-id", "12345");
        mockRequest.addParameter("param1", "value1");

        apiLoggingImpl.parserRequestFrom(mockRequest, null);

        verify(logService, times(1)).save(apiLogCaptor.capture());

        LogEntity capturedLogEntity = apiLogCaptor.getValue();
        assert capturedLogEntity != null;
        assert capturedLogEntity.getCep().equals("12345678");
        assert capturedLogEntity.getHeaders().get("any").equals("any");
        assert capturedLogEntity.getRequestId().equals("12345");
    }

    @Test
    public void testParserResponseFrom() throws JsonProcessingException {
        mockRequest.setRequestURI("/api/ceps/12345678/json/");
        mockRequest.setMethod("GET");
        mockRequest.setAttribute("X-request-id", "12345");
        mockResponse.setStatus(200);

        ViaCep viaCep = new ViaCep("12345678", "Rua Teste", "Bairro Teste", "", "","","","");
        when(objectMapper.readValue(anyString(), eq(ViaCep.class))).thenReturn(viaCep);

        apiLoggingImpl.parserResponseFrom(mockRequest, mockResponse, "{\"cep\":\"12345678\",\"logradouro\":\"Rua Teste\",\"bairro\":\"Bairro Teste\"}");

        verify(logService, times(1)).save(apiLogCaptor.capture());

        LogEntity capturedLogEntity = apiLogCaptor.getValue();
        assert capturedLogEntity != null;
        assert capturedLogEntity.getCep().equals("12345678");
        assert capturedLogEntity.getRequestId().equals("12345");
        assert capturedLogEntity.getStatusCode().equals("200");
        assert capturedLogEntity.getResponse() != null;
    }

    @Test
    public void testParserResponseFrom_withErrorStatus() {
        mockRequest.setRequestURI("/api/ceps/12345678/json/");
        mockRequest.setMethod("GET");
        mockRequest.setAttribute("X-request-id", "12345");
        mockResponse.setStatus(400);

        apiLoggingImpl.parserResponseFrom(mockRequest, mockResponse, "Error occurred");

        verify(logService, times(1)).save(apiLogCaptor.capture());

        LogEntity capturedLogEntity = apiLogCaptor.getValue();
        assert capturedLogEntity != null;
        assert capturedLogEntity.getCep().equals("12345678");
        assert capturedLogEntity.getRequestId().equals("12345");
        assert capturedLogEntity.getStatusCode().equals("400");
        assert capturedLogEntity.getError().equals("Error occurred");
    }

}

