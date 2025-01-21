package br.com.api.log;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

public class LogControllerTest {

    @Mock
    private LogService logService;

    @InjectMocks
    private LogController logController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(logController).build();
    }

    @Test
    void testFindByCep() throws Exception {
        String mockResponse = "[{\"cep\": \"12345678\", \"method\": \"GET\"}]";
        when(logService.findBy("12345678")).thenReturn(mockResponse);

        mockMvc.perform(get("/api/logs/ceps/{cep}", "12345678"))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }

    @Test
    void testFindByRequestId() throws Exception {
        String mockResponse = "[{\"requestId\": \"fec6442c-4ae5-4376-830b-4722ba341525\", \"method\": \"POST\"}]";
        when(logService.findBy(UUID.fromString("fec6442c-4ae5-4376-830b-4722ba341525"))).thenReturn(mockResponse);

        mockMvc.perform(get("/api/logs/requests/{requestId}", "fec6442c-4ae5-4376-830b-4722ba341525"))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }
}
