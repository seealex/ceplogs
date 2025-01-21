package br.com.api.cep;

import jakarta.validation.ConstraintViolation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import jakarta.validation.ConstraintViolationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.*;

@ControllerAdvice(assignableTypes = {CepController.class})
public class CepExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleValidationExceptions(ConstraintViolationException ex) {

        Map<String, List<String>> errorMap = new HashMap<>();

        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            if (errorMap.containsKey(violation.getPropertyPath().toString())) {
                errorMap.get(violation.getPropertyPath().toString()).add(violation.getMessage());
            } else {
                ArrayList<String> list = new ArrayList<>();
                list.add(violation.getMessage());
                errorMap.put(violation.getPropertyPath().toString(), list);
            }

        }

        return ResponseEntity.badRequest().body(errorMap);
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        StringBuilder errorMessage = new StringBuilder("Erro de validação nos campos: ");

        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            errorMessage.append(fieldError.getField()).append(" - ").append(fieldError.getDefaultMessage()).append(". ");
        }

        return ResponseEntity.badRequest().body(errorMessage.toString());
    }

    @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleRequestParameter(MissingServletRequestParameterException ex) {

        Map<String, String> errorMap = new HashMap<>();

        String parameter = ex.getParameterName();
        errorMap.put(parameter, ex.getMessage());

        return ResponseEntity.badRequest().body(errorMap);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro inesperado: " + ex.getMessage());
    }

}
