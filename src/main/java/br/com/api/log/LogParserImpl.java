package br.com.api.log;


import br.com.api.viacep.ViaCep;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class LogParserImpl implements LogParserService {

    private static final String X_REQUEST_ID = "X-request-id";
    private final LogService logService;
    private final ObjectMapper objectMapper;

    @Override
    public void parserRequestFrom(HttpServletRequest request, Object body) {
        RequestParser requestParser = new RequestParser(request);
        LogEntity logEntity = requestParser.parseRequest();
        logEntity.setRequestId((String) request.getAttribute(X_REQUEST_ID));
        logService.save(logEntity);
    }

    @Override
    public void parserResponseFrom(HttpServletRequest request, HttpServletResponse response, Object body) {
        ResponseParser responseParser = new ResponseParser(response, body, request);
        LogEntity logEntity = responseParser.parseResponse(objectMapper);
        logEntity.setRequestId((String) request.getAttribute(X_REQUEST_ID));
        logService.save(logEntity);
    }
}

class RequestParser {

    private final HttpServletRequest request;

    public RequestParser(HttpServletRequest request) {
        this.request = request;
    }

    public LogEntity parseRequest() {
        Map<String, String> parameters = getParametersFrom(request);
        Map<String, String> headers = getHeadersFrom(request);

        String cep = getCepFrom(request);

        LogEntity logEntity = new LogEntity();
        logEntity.setParameters(parameters);
        logEntity.setHeaders(headers);
        logEntity.setCep(cep);
        logEntity.setTimestamp(Instant.now().toString());
        logEntity.setUrl(request.getRequestURL().toString());
        logEntity.setMethod(request.getMethod());
        logEntity.setResponse(null);
        logEntity.setStatusCode(null);
        return logEntity;
    }

    static String getCepFrom(HttpServletRequest request) {
        String cep = "";
        String urlPath = request.getRequestURI();
        String[] pathSegments = urlPath.split("/");
        if (pathSegments.length >= 4) {
            cep = pathSegments[3];
        }
        return cep;
    }

    private Map<String, String> getHeadersFrom(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();

        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                headers.put(headerName, headerValue);
            }
        }
        return headers;
    }

    private Map<String, String> getParametersFrom(HttpServletRequest request) {
        Map<String, String> parameters = new HashMap<>();
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String paramName = params.nextElement();
            String paramValue = request.getParameter(paramName);
            parameters.put(paramName, paramValue);
        }
        return parameters;
    }
}

@Slf4j
class ResponseParser {

    private final HttpServletResponse response;
    private final Object body;
    private final HttpServletRequest request;

    public ResponseParser(HttpServletResponse response, Object body, HttpServletRequest request) {
        this.response = response;
        this.body = body;
        this.request = request;
    }

    public LogEntity parseResponse(ObjectMapper objectMapper) {
        Map<String, String> headers = getHeadersFrom(response);
        int status = response.getStatus();
        String cep = getCepFrom(request);

        LogEntity logEntity = new LogEntity();
        logEntity.setHeaders(headers);
        logEntity.setCep(cep);
        logEntity.setTimestamp(Instant.now().toString());
        logEntity.setUrl(request.getRequestURL().toString());
        logEntity.setMethod(request.getMethod());
        logEntity.setStatusCode(String.valueOf(status));

        if (HttpStatus.valueOf(status) == HttpStatus.OK && request.getRequestURL().toString().contains("/api/ceps/")) {
            Optional.ofNullable(body).ifPresent(bodyValue -> {
                try {
                    logEntity.setResponse(objectMapper.readValue(bodyValue.toString(), ViaCep.class));
                } catch (JsonProcessingException e) {
                    log.error("Não foi possivel processar o response body");
                }
            });

        } else if (HttpStatus.valueOf(status) == HttpStatus.OK && request.getRequestURL().toString().contains("/api/ceps")) {
            Optional.ofNullable(body).ifPresent(bodyValue -> {
                try {
                    List<ViaCep> viaCepList = objectMapper.readValue(bodyValue.toString(), new TypeReference<List<ViaCep>>() {});
                    logEntity.setResponse(viaCepList);
                } catch (JsonProcessingException e) {
                    log.error("Não foi possivel processar o response body");
                }
            });

        } else if (status >= 400) {
            Optional.ofNullable(body).ifPresent(bodyValue -> logEntity.setError(bodyValue.toString()));
        }

        return logEntity;
    }

    private static String getCepFrom(HttpServletRequest request) {
        return RequestParser.getCepFrom(request);
    }

    private Map<String, String> getHeadersFrom(HttpServletResponse response) {
        Map<String, String> headers = new HashMap<>();
        Collection<String> headerMap = response.getHeaderNames();
        for (String str : headerMap) {
            headers.put(str, response.getHeader(str));
        }
        return headers;
    }
}

