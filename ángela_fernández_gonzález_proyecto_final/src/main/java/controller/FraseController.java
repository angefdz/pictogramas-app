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
import repository.FraseRepository;

@RestController
@RequestMapping("/frases")
public class FraseController {
	
	@Autowired
	private FraseRepository fraseRepository;

	@PostMapping
	public ResponseEntity<Frase> createFrase(@RequestBody Frase f){
		Frase nuevaFrase = this.fraseRepository.save(f);
		return new ResponseEntity<>(nuevaFrase,HttpStatus.CREATED);
	}
	
	
	@GetMapping("/{usuario_id}")
	public ResponseEntity<List<Frase>> getFraseByUser(@PathVariable long usuario_id){
		List<Frase> frases = this.fraseRepository.findByUsuario_id(usuario_id);
		if (frases.isEmpty()) {
	        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	    }
	    return new ResponseEntity<>(frases, HttpStatus.OK);
	}
}
