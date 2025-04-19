package com.spfantasy.backend.dto;

import com.spfantasy.backend.model.Liga;

public class LigaDTO {
    private Long id;
    private String nombre;
    private boolean iniciada;
    private Long creadorId;

    public LigaDTO(Liga liga) {
        this.id = liga.getId();
        this.nombre = liga.getNombre();
        this.iniciada = liga.isIniciada();
        this.creadorId = liga.getCreador() != null ? liga.getCreador().getId() : null;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isIniciada() {
        return iniciada;
    }

    public Long getCreadorId() {
        return creadorId;
    }
}
