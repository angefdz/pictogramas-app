package model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table (name="frases")
public class Frase {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int idFrase;
	
	@NotNull
	@NotEmpty
	@Column
	private String texto;
	
	@NotEmpty
	@NotNull
	@Column
	private Date fecha;
	
	public Frase (int idFrase, String text, Date date) {
		setIdFrase(idFrase);
		setTexto(text);
		setFecha(date);
	}
	
	public Frase (String text, Date date) {
		setTexto(text);
		setFecha(date);
	}

	public int getIdFrase() {
		return idFrase;
	}

	public void setIdFrase(int idPhrase) {
		this.idFrase = idPhrase;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String text) {
		this.texto = text;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date date) {
		this.fecha = date;
	}
}
