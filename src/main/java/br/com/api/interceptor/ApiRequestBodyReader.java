package br.com.api.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.stereotype.Component;

@Component
public class ApiRequestBodyReader {

    public String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        return requestBody.toString();
    }
}

