package model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("PERSONALIZADO")
public class PictogramaPersonalizado extends Pictograma {

    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", nullable = false)
    private Usuario usuario;

    public PictogramaPersonalizado() {}

    public PictogramaPersonalizado(String nombre, String imagen, Usuario usuario) {
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
