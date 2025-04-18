package controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import model.Pictograma;
import repository.PictogramaRepository;

@RestController
@RequestMapping("/pictogramas")
public class PictogramaController {
	
	@Autowired
	private PictogramaRepository pictogramaRepository;
	
	@PostMapping
	public ResponseEntity<Pictograma> createPictograma(@RequestBody Pictograma p){
		Pictograma nuevoPictograma = this.pictogramaRepository.save(p);
		return new ResponseEntity<>(nuevoPictograma,HttpStatus.CREATED);
	}
	
	
	@GetMapping
	public ResponseEntity<List<Pictograma>> getPictogramasDefault(){
		List<Pictograma> pictogramas = this.pictogramaRepository.findAllGenerales();
		if(pictogramas.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(pictogramas,HttpStatus.OK);
	}
	
	@GetMapping("/usuario/{usuario_id}")
	public ResponseEntity<List<Pictograma>> getPictogramasByUser(@PathVariable long usuario_id){
		List<Pictograma> pictogramas = this.pictogramaRepository.findAllPersonalizados(usuario_id);
		if(pictogramas.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(pictogramas,HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Pictograma> getPictogramaById(@PathVariable int id){
		return this.pictogramaRepository.findById(id)
				.map(p -> ResponseEntity.ok(p))
				.orElseGet(()->ResponseEntity.notFound().build());
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Pictograma> updatePictograma(@PathVariable int id){
		return this.pictogramaRepository.findById(id)
				.map((p)->{
					this.pictogramaRepository.save(p);
					return ResponseEntity.ok(p);
				})
				.orElseGet(()-> ResponseEntity.notFound().build());
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deletePictograma(@PathVariable int id){
		return this.pictogramaRepository.findById(id)
				.map((p)->{
					this.pictogramaRepository.delete(p);
					return ResponseEntity.noContent().build();
				}).orElseGet(()->ResponseEntity.notFound().build());
	}
	
}
