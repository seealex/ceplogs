package br.com.api.interceptor;
import br.com.api.log.LogParserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class ApiRequestInterceptor implements HandlerInterceptor {

    private final ApiRequestIdGenerator apiRequestIdGenerator;
    private final ApiRequestBodyReader apiRequestBodyReader;
    private final LogParserService logParserService;

    @Autowired
    public ApiRequestInterceptor(LogParserService logParserService,
                                 ApiRequestIdGenerator apiRequestIdGenerator,
                                 ApiRequestBodyReader apiRequestBodyReader) {
        this.logParserService = logParserService;
        this.apiRequestIdGenerator = apiRequestIdGenerator;
        this.apiRequestBodyReader = apiRequestBodyReader;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (isValidRequestMethod(request)) {
            String requestId = apiRequestIdGenerator.generateRequestId();
            log.info("requestId: {}", requestId);
            request.setAttribute("X-request-id", requestId);

            String requestBody = apiRequestBodyReader.readRequestBody(request);
            logParserService.parserRequestFrom(request, requestBody);
        }
        return true;
    }

    private boolean isValidRequestMethod(HttpServletRequest request) {
        String method = request.getMethod();
        return method.equals(HttpMethod.GET.name()) ||
                method.equals(HttpMethod.DELETE.name()) ||
                method.equals(HttpMethod.POST.name()) ||
                method.equals(HttpMethod.PUT.name());
    }
}