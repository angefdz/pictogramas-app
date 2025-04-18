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
	private int idUsuario;
	
	@NotNull
	@NotEmpty
	@Column(name="nombre")
	private String nombre;
	
	@NotEmpty
	@NotNull
	@Column(name="correo")
	private String email;
	
	@NotEmpty
	@NotNull
	@Column(name="contrasena_hash")
	private String contrasenaHash;
	
	private List<Pictograma> pictogramasOcultos;
	
	public Usuario(int idUsuario, String nombre, String email, String contrasenaHash) {
		setIdUsuario(idUsuario);
		setNombre(nombre);
		setEmail(email);
		setContrasenaHash(contrasenaHash);
	}
	
	public Usuario(String nombre, String email, String contrasenaHash) {
		setNombre(nombre);
		setEmail(email);
		setContrasenaHash(contrasenaHash);
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
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

	public String getContrasenaHash() {
		return contrasenaHash;
	}

	public void setContrasenaHash(String contrasenaHash) {
		this.contrasenaHash = contrasenaHash;
	}

	public List<Pictograma> getPictogramasOcultos() {
		return pictogramasOcultos;
	}

	public void setPictogramasOcultos(List<Pictograma> pictogramasOcultos) {
		this.pictogramasOcultos = pictogramasOcultos;
	}

}
