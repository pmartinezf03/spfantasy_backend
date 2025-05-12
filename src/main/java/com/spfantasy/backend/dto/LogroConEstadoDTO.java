package com.spfantasy.backend.dto;

public class LogroConEstadoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String icono;
    private boolean desbloqueado;
    private String fechaDesbloqueo;

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

    public boolean isDesbloqueado() {
        return desbloqueado;
    }

    public void setDesbloqueado(boolean desbloqueado) {
        this.desbloqueado = desbloqueado;
    }

    public String getFechaDesbloqueo() {
        return fechaDesbloqueo;
    }

    public void setFechaDesbloqueo(String fechaDesbloqueo) {
        this.fechaDesbloqueo = fechaDesbloqueo;
    }

    public LogroConEstadoDTO() {
    }

    public LogroConEstadoDTO(Long id, String nombre, String descripcion, String icono, boolean desbloqueado,
            String fechaDesbloqueo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.icono = icono;
        this.desbloqueado = desbloqueado;
        this.fechaDesbloqueo = fechaDesbloqueo;
    }

}
