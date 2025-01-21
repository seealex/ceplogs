package br.com.api.log;

import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("/ceps/{cep}")
    public ResponseEntity<String> findByCep(
            @PathVariable @Pattern(regexp = "^[0-9]{8}$", message = "CEP deve conter 8 dígitos numéricos") String cep) {

        String result = logService.findBy(cep);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/requests/{requestId}")
    public ResponseEntity<String> findByRequestId(
            @PathVariable UUID requestId) {

        String result = logService.findBy(requestId);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return ResponseEntity.ok(result);
    }


}
