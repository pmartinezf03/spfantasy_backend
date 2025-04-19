package com.spfantasy.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ligas")
public class Liga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;


    @Column(unique = true, nullable = false)
    private String codigoInvitacion;

    @ManyToOne
    @JoinColumn(name = "creador_id")
    private Usuario creador;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "max_participantes")
    private Integer maxParticipantes = 10;

    @Column(name = "iniciada")
    private boolean iniciada = false;
    @Column(name = "contrasena")
    private String contrasena;

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCodigoInvitacion(String codigoInvitacion) {
        this.codigoInvitacion = codigoInvitacion;
    }

    public void setCreador(Usuario creador) {
        this.creador = creador;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setMaxParticipantes(Integer maxParticipantes) {
        this.maxParticipantes = maxParticipantes;
    }

    public void setIniciada(boolean iniciada) {
        this.iniciada = iniciada;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCodigoInvitacion() {
        return codigoInvitacion;
    }

    public Usuario getCreador() {
        return creador;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public Integer getMaxParticipantes() {
        return maxParticipantes;
    }

    public boolean isIniciada() {
        return iniciada;
    }

}
