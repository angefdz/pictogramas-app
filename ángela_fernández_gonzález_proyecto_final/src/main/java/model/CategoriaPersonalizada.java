package model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("PERSONALIZADA")
public class CategoriaPersonalizada extends Categoria {

    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", nullable = false)
    private Usuario usuario;

    public CategoriaPersonalizada() {}

    public CategoriaPersonalizada(String nombre, String imagen, Usuario usuario) {
        super(nombre, imagen);
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
