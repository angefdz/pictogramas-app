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

import jakarta.validation.Valid;
import model.Usuario;
import repository.UsuarioRepository;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
	
	//Inyección de dependencias: desacopla clases y mejora a mantener el código
	//y a mantenerlo
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@GetMapping()
	public ResponseEntity<List<Usuario>> getUsuarios(){
		List<Usuario> usuarios = this.usuarioRepository.findAll();
		if (usuarios.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(usuarios,HttpStatus.OK);
		
	}
	
	
	@GetMapping("/{id}")
	public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id){
		return this.usuarioRepository.findById(id)
				.map(usuario->ResponseEntity.ok(usuario))
				.orElseGet(()->ResponseEntity.notFound().build());
		//build se pone cuando no se manda cuerpo, si se mandase un new Usuario no
		//se pondría
	}
	
	//@Valid valida que  el usuario cumpla con lo especificado en declaración
	@PostMapping()
	public ResponseEntity<Usuario> createUsuario(@Valid @RequestBody Usuario usuario){
		Usuario nuevoUsuario = this.usuarioRepository.save(usuario);
		return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
	}
	
	
	@PutMapping("/{id}")
	public ResponseEntity<Usuario> changePassword(@PathVariable Long id, 
			@RequestBody String nuevaContrasena){
		return this.usuarioRepository.findById(id)
				.map(user->{
					user.setContrasena(nuevaContrasena);
					this.usuarioRepository.save(user);
					return ResponseEntity.ok(user);
				}).orElseGet(()-> ResponseEntity.notFound().build());
	}
	
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteUsuario(@PathVariable Long id){
		return this.usuarioRepository.findById(id)
				.map(user->{
					this.usuarioRepository.delete(user);
					return ResponseEntity.noContent().build();
				}).orElseGet(()->ResponseEntity.notFound().build());
	}
	
	

}
