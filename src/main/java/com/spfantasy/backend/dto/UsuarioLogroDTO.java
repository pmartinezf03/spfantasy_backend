package com.spfantasy.backend.dto;

public class UsuarioLogroDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String icono;
    private String fechaDesbloqueo;

    public UsuarioLogroDTO() {
    }

    public UsuarioLogroDTO(Long id, String nombre, String descripcion, String icono, String fechaDesbloqueo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.icono = icono;
        this.fechaDesbloqueo = fechaDesbloqueo;
    }

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

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getFechaDesbloqueo() {
        return fechaDesbloqueo;
    }

    public void setFechaDesbloqueo(String fechaDesbloqueo) {
        this.fechaDesbloqueo = fechaDesbloqueo;
    }

}
