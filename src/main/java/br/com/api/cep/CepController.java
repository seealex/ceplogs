package br.com.api.cep;

import br.com.api.viacep.ViaCepService;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@Validated
@RequestMapping("/api/ceps")
public class CepController {

    private final ViaCepService viaCepService;

    public CepController(ViaCepService viaCepService) {
        this.viaCepService = viaCepService;
    }

    @GetMapping("/{cep}")
    public ResponseEntity<String> searchCep(
            @PathVariable @Pattern(regexp = "^[0-9]{8}$", message = "CEP deve conter 8 dígitos numéricos") String cep) {

        String result = viaCepService.searchCep(cep);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("")
    public ResponseEntity<String> searchCepBy(
            @RequestParam(required = true) String state,
            @RequestParam(required = true) String city,
            @RequestParam(required = true) String address) {

        String result = viaCepService.searchAddress(state, city, address);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return ResponseEntity.ok(result);
    }


}
