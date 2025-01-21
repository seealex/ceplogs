package br.com.api.interceptor;

import br.com.api.log.LogParserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class ApiResponseBodyInterceptor implements ResponseBodyAdvice<Object> {

    private static final String X_REQUEST_ID = "X-request-id";
    private final LogParserService logParserService;

    @Autowired
    public ApiResponseBodyInterceptor(LogParserService logParserService) {
        this.logParserService = logParserService;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletRequest servletRequest = null;
        HttpServletResponse servletResponse = null;
        if (request instanceof ServletServerHttpRequest servletServerHttpRequest) {
            servletRequest = servletServerHttpRequest.getServletRequest();
        }
        if (response instanceof ServletServerHttpResponse servletServerHttpResponse) {
            servletResponse = servletServerHttpResponse.getServletResponse();
            if (servletRequest != null) {
                servletResponse.setHeader(X_REQUEST_ID, (String) servletRequest.getAttribute(X_REQUEST_ID));
            }
        }
        logParserService.parserResponseFrom(servletRequest, servletResponse, body);
        return body;
    }
}
