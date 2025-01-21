package br.com.api.viacep;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ViaCepService {

    private final ViaCepClient viaCepClient;

    @Autowired
    public ViaCepService(ViaCepClient viaCepClient) {
        this.viaCepClient = viaCepClient;
    }

    public String searchCep(String cep) {
        try {
            String format = "json";
            ViaCep viaCepResponse = viaCepClient.queryFor(cep, format);

            return new ObjectMapper().writeValueAsString(viaCepResponse);
        } catch (FeignException.NotFound | FeignException.BadRequest | JsonProcessingException e) {
            return null;
        }
    }

    public String searchAddress(String state, String city, String address) {
        try {
            String format = "json";
            List<ViaCep> viaCepResponse = viaCepClient.queryForAddress(state, city, address, format);

            return new ObjectMapper().writeValueAsString(viaCepResponse);
        } catch (FeignException.NotFound | FeignException.BadRequest | JsonProcessingException e) {
            return null;
        }
    }
}

