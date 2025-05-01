package model;

import java.util.List;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table (name = "usuarios")
public class Usuario {
	
	//Clave primaria
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long idUsuario;
	
	@NotNull
	@NotEmpty
	@Column(name="nombre")
	private String nombre;
	
	@NotEmpty
	@NotNull
	@Column(name="correo", unique = true)
	private String email;
	
	@NotEmpty
	@NotNull
	@Column(name="contrasena_hash")
	private String contrasena;
	
	private List<Pictograma> pictogramasOcultos;
	
	public Usuario(Long idUsuario, String nombre, String email, String contrasenaHash) {
		setIdUsuario(idUsuario);
		setNombre(nombre);
		setEmail(email);
		setContrasena(contrasenaHash);
	}
	
	public Usuario(String nombre, String email, String contrasenaHash) {
		setNombre(nombre);
		setEmail(email);
		setContrasena(contrasenaHash);
	}

	public Long getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasenaHash) {
		this.contrasena = contrasenaHash;
	}

	public List<Pictograma> getPictogramasOcultos() {
		return pictogramasOcultos;
	}

	public void setPictogramasOcultos(List<Pictograma> pictogramasOcultos) {
		this.pictogramasOcultos = pictogramasOcultos;
	}

}
