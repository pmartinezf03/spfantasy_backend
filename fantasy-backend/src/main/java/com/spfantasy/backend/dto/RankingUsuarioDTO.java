package com.spfantasy.backend.dto;

public class RankingUsuarioDTO {

    private Long usuarioId;
    private String nombre;
    private int puntosTotales;

    public RankingUsuarioDTO(Long usuarioId, String nombre, int puntosTotales) {
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.puntosTotales = puntosTotales;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPuntosTotales() {
        return puntosTotales;
    }
}
