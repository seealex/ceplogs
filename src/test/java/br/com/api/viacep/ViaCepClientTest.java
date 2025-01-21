package br.com.api.viacep;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ViaCepClientTest {

    private ViaCepClient viaCepClientMock;
    private ViaCepService viaCepService;

    @BeforeEach
    public void setUp() {
        viaCepClientMock = Mockito.mock(ViaCepClient.class);

        viaCepService = new ViaCepService(viaCepClientMock);
    }

    @Test
    public void testGetCepDetails() {
        ViaCep mockResponse = new ViaCep("12345678", "Rua Teste", "Bairro Teste", "","","","","");
        when(viaCepClientMock.queryFor("12345678", "json")).thenReturn(mockResponse);

        String result = viaCepService.searchCep("12345678");

        assertNotNull(result);
        assertTrue(result.contains("12345678"));
        assertTrue(result.contains("Rua Teste"));
        assertTrue(result.contains("Bairro Teste"));

        verify(viaCepClientMock, times(1)).queryFor("12345678", "json");
    }
}

