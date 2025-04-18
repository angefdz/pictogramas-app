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

import model.Categoria;
import repository.CategoriaRepository;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	
	@PostMapping
	public ResponseEntity<Categoria> createCategoria(@RequestBody Categoria c){
		Categoria nuevaCategoria = this.categoriaRepository.save(c);
		return new ResponseEntity<>(nuevaCategoria,HttpStatus.CREATED);
	}
	
	
	@GetMapping
	public ResponseEntity<List<Categoria>> getCategoriasDefault(){
		List<Categoria> categorias = this.categoriaRepository.findAllGenerales();
		if(categorias.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(categorias,HttpStatus.OK);
	}
	
	@GetMapping("/usuarios/{usuario_id}")
	public ResponseEntity<List<Categoria>> getCategoriasPersonalizados(@PathVariable long usuario_id){
		List<Categoria> categorias = this.categoriaRepository.findAllPersonalizados(usuario_id);
		if(categorias.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(categorias,HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Categoria> getCategoriaById(@PathVariable int id){
		return this.categoriaRepository.findById(id)
				.map(c -> ResponseEntity.ok(c))
				.orElseGet(()->ResponseEntity.notFound().build());
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Categoria> updateCategoria(@PathVariable int id){
		return this.categoriaRepository.findById(id)
				.map((c)->{
					this.categoriaRepository.save(c);
					return ResponseEntity.ok(c);
				})
				.orElseGet(()-> ResponseEntity.notFound().build());
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteCategoria(@PathVariable int id){
		return this.categoriaRepository.findById(id)
				.map((c)->{
					this.categoriaRepository.delete(c);
					return ResponseEntity.noContent().build();
				}).orElseGet(()->ResponseEntity.notFound().build());
	}
	

}
