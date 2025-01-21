package br.com.api.viacep;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ViaCep {

    private String cep;

    private String logradouro;

    private String complemento;

    private String unidade;

    private String bairro;

    private String localidade;

    private String uf;

    private String estado;

    @JsonCreator
    public ViaCep(
            @JsonProperty("cep") String cep,
            @JsonProperty("logradouro") String logradouro,
            @JsonProperty("complemento") String complemento,
            @JsonProperty("unidade") String unidade,
            @JsonProperty("bairro") String bairro,
            @JsonProperty("localidade") String localidade,
            @JsonProperty("uf") String uf,
            @JsonProperty("estado") String estado

    ) {
        this.cep = cep;
        this.logradouro = logradouro;
        this.complemento = complemento;
        this.unidade = unidade;
        this.bairro = bairro;
        this.localidade = localidade;
        this.uf = uf;
        this.estado = estado;

    }
}

