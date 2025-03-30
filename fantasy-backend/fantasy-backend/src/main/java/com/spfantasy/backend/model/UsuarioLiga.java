
package com.spfantasy.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.sql.Timestamp;

@Entity
@Table(name = "usuarios_liga", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"usuario_id", "liga_id"})
})
public class UsuarioLiga {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "liga_id", nullable = false)
    private Liga liga;

    private Timestamp fechaUnion = new Timestamp(System.currentTimeMillis());

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setLiga(Liga liga) {
        this.liga = liga;
    }

    public void setFechaUnion(Timestamp fechaUnion) {
        this.fechaUnion = fechaUnion;
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Liga getLiga() {
        return liga;
    }

    public Timestamp getFechaUnion() {
        return fechaUnion;
    }
    
    
}
