package com.spfantasy.backend.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.usuario;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal dinero = BigDecimal.valueOf(3000000);
    @Column(name = "puntos")
    private int puntos;

    @Column(name = "dinero_pendiente", nullable = false)
    private BigDecimal dineroPendiente = BigDecimal.ZERO;

    @ManyToMany
    @JoinTable(name = "plantilla_jugadores", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "jugador_id"))
    @JsonIgnore
    private List<Jugador> plantilla;

    @ManyToMany(mappedBy = "usuarios")
    private List<GrupoChat> grupos = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "liga_id")
    private Liga liga;

    @Column(unique = true)
    private String alias;

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public List<GrupoChat> getGrupos() {
        return grupos;
    }

    public Liga getLiga() {
        return liga;
    }

    public void setLiga(Liga liga) {
        this.liga = liga;
    }

    public BigDecimal getDineroPendiente() {
        return dineroPendiente;
    }

    public void setDineroPendiente(BigDecimal dineroPendiente) {
        this.dineroPendiente = dineroPendiente;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public BigDecimal getDinero() {
        return dinero;
    }

    public List<Jugador> getPlantilla() {
        return plantilla;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setDinero(BigDecimal dinero) {
        this.dinero = dinero;
    }

    public void setPlantilla(List<Jugador> plantilla) {
        this.plantilla = plantilla;
    }

    public void setGrupos(List<GrupoChat> grupos) {
        this.grupos = grupos;
    }

}
