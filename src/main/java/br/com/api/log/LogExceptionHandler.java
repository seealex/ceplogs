package br.com.api.log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.*;

@ControllerAdvice(assignableTypes = {LogController.class})
public class LogExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleValidationException(MethodArgumentTypeMismatchException ex) {

        Map<String, String> errorMap = new HashMap<>();

        String parameter = ex.getParameter().getParameter().getName();
        errorMap.put(parameter, ex.getMessage());

        return ResponseEntity.badRequest().body(errorMap);
    }

}
