package com.spfantasy.backend.dto;

import java.util.Set;

public class GrupoChatDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Long creadorId;
    private Set<Long> usuariosIds;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public Long getCreadorId() {
        return creadorId;
    }
    public void setCreadorId(Long creadorId) {
        this.creadorId = creadorId;
    }
    public Set<Long> getUsuariosIds() {
        return usuariosIds;
    }
    public void setUsuariosIds(Set<Long> usuariosIds) {
        this.usuariosIds = usuariosIds;
    }
    public GrupoChatDTO() {
    }
    public GrupoChatDTO(Long id, String nombre, String descripcion, Long creadorId, Set<Long> usuariosIds) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.creadorId = creadorId;
        this.usuariosIds = usuariosIds;
    }
}

