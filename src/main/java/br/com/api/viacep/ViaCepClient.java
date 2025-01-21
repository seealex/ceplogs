package br.com.api.viacep;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "viaCepClient", url = "${viaCepClient.url}")
public interface ViaCepClient {

    @GetMapping("/{cep}/{format}/")
    ViaCep queryFor(@PathVariable("cep") String cep, @PathVariable("format") String format);

    @GetMapping("/{state}/{city}/{address}/{format}/")
    List<ViaCep> queryForAddress(@PathVariable("state") String state, @PathVariable("city") String city,
                          @PathVariable("address") String address, @PathVariable("format") String format);
}
