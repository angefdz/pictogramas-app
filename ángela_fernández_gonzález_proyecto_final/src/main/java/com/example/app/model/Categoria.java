package com.example.app.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING)
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;  // <-- Cambio aquí

    @NotNull
    @NotEmpty
    @Column(nullable = false)
    private String nombre;

    @NotNull
    @NotEmpty
    @Column(nullable = false)
    private String imagen;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = true)
    private Usuario usuario;
    
    @ManyToMany
    @JoinTable(
        name = "pictograma_categoria",
        joinColumns = @JoinColumn(name = "categoria_id"),
        inverseJoinColumns = @JoinColumn(name = "pictograma_id")
    )
    private List<Pictograma> pictogramas;


    public List<Pictograma> getPictogramas() {
        return pictogramas;
    }

    public void setPictogramas(List<Pictograma> pictogramas) {
        this.pictogramas = pictogramas;
    }


    public Categoria() {}

    public Categoria(String nombre, String imagen) {
        this.nombre = nombre;
        this.imagen = imagen;
    }

    public Long getId() {  // <-- Cambio aquí
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getImagen() {
        return imagen;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
