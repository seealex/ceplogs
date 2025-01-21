package br.com.api.log;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogEntity {

    private String requestId;

    private String timestamp;

    private String cep;

    private String method;

    private String url;

    private Map<String, String>  headers;

    private Map<String, String> parameters;

    private String statusCode;

    private String request;

    private Object response;

    private String error;

}

