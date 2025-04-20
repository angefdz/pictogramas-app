package controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import model.Frase;
import service.FraseService;

@RestController
@RequestMapping("/frases")
public class FraseController {
	
	@Autowired
	private FraseService fraseService;

	@PostMapping
	public ResponseEntity<Frase> createFrase(@RequestBody Frase f){
		Frase nuevaFrase = this.fraseService.crearFrase(f);
		return new ResponseEntity<>(nuevaFrase,HttpStatus.CREATED);
	}
	
	
	@GetMapping("/{usuario_id}")
	public ResponseEntity<List<Frase>> getFraseByUser(@PathVariable long usuario_id){
		List<Frase> frases = this.fraseService.obtenerFrasesByUsuario(usuario_id);
		if (frases.isEmpty()) {
	        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	    }
	    return new ResponseEntity<>(frases, HttpStatus.OK);
	}
}
