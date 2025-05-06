// PrediccionController.java
package controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import dto.PrediccionRequest;

@RestController
@RequestMapping("/prediccion")
public class PrediccionController {

    @PostMapping
    public ResponseEntity<String> predecir(@RequestBody PrediccionRequest dto) {
        // Crear RestTemplate para enviar la petición
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:5000/predecir";

        // Preparar cabeceras
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Crear cuerpo de la petición con JSON
        String cuerpo = "{\"frase\": \"" + dto.getFraseParcial() + "\"}";

        HttpEntity<String> request = new HttpEntity<>(cuerpo, headers);

        // Enviar POST al microservicio Flask
        ResponseEntity<String> respuesta = restTemplate.postForEntity(url, request, String.class);

        // Devolver la predicción tal cual
        return ResponseEntity.ok(respuesta.getBody());
    }
}
