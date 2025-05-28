package com.spfantasy.backend.dto;

import java.time.LocalDateTime;

import com.spfantasy.backend.model.ActividadLiga;
import com.spfantasy.backend.model.Usuario;

public class ActividadLigaDTO {
    private Long id;
    private String tipo;
    private String descripcion;
    private String timestamp;
    private UsuarioMiniDTO usuario;

    public ActividadLigaDTO() {
    }

    public ActividadLigaDTO(ActividadLiga actividad) {
        this.id = actividad.getId();
        this.tipo = actividad.getTipo();
        this.descripcion = actividad.getDescripcion();
        this.timestamp = actividad.getTimestamp().toString();

        Usuario u = actividad.getUsuario();
        this.usuario = new UsuarioMiniDTO(u.getId(), u.getUsername(), u.getUltimoLogin());
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public UsuarioMiniDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioMiniDTO usuario) {
        this.usuario = usuario;
    }

    public static class UsuarioMiniDTO {
        private Long id;
        private String username;
        private LocalDateTime ultimoLogin;

        public UsuarioMiniDTO() {
        }

        public UsuarioMiniDTO(Long id, String username, LocalDateTime ultimoLogin) {
            this.id = id;
            this.username = username;
            this.ultimoLogin = ultimoLogin;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public LocalDateTime getUltimoLogin() {
            return ultimoLogin;
        }

        public void setUltimoLogin(LocalDateTime ultimoLogin) {
            this.ultimoLogin = ultimoLogin;
        }
    }
}
