package br.com.api.log;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface LogParserService {

    void parserRequestFrom(HttpServletRequest request, Object body);

    void parserResponseFrom(HttpServletRequest request, HttpServletResponse response, Object body);
}
