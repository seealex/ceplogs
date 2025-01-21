package br.com.api.interceptor;

import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class ApiRequestIdGenerator {

    public String generateRequestId() {
        return UUID.randomUUID().toString();
    }
}

