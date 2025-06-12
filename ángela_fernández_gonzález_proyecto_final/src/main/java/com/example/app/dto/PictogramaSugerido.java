package com.example.app.dto;

public class PictogramaSugerido {
    private String palabra;
    private int pictogramaId;
    private String imagen;

    public PictogramaSugerido(String palabra, int pictogramaId, String imagen) {
        this.palabra = palabra;
        this.pictogramaId = pictogramaId;
        this.imagen = imagen;
    }

    public String getPalabra() {
        return palabra;
    }

    public int getPictogramaId() {
        return pictogramaId;
    }

    public String getImagen() {
        return imagen;
    }
}
