package model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING)
@Table(name = "pictogramas")
public class Pictograma {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idPictograma;

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

    public Pictograma() {}

    public Pictograma(String nombre, String imagen) {
        this.nombre = nombre;
        this.imagen = imagen;
    }

    public Long getIdPictograma() {
        return idPictograma;
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
