package br.com.api.cep;

import br.com.api.viacep.ViaCepService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CepControllerTest {

    @Mock
    private ViaCepService viaCepService;

    @InjectMocks
    private CepController cepController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(cepController).build();
    }

    @Test
    void testSearchCep_ValidCep() throws Exception {
        String cep = "12345678";
        String mockResponse = "{\"cep\":\"12345678\",\"logradouro\":\"Rua Teste\"}";

        when(viaCepService.searchCep(cep)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/ceps/{cep}", cep))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }

    @Test
    void testSearchCep_InvalidCep() throws Exception {
        String cep = "1234";

        mockMvc.perform(get("/api/ceps/{cep}", cep))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    void testSearchCep_ServiceReturnsNull() throws Exception {
        String cep = "12345678";

        when(viaCepService.searchCep(cep)).thenReturn(null);

        mockMvc.perform(get("/api/ceps/{cep}", cep))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }
}


