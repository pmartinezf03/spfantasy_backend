package com.spfantasy.backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

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

    @OneToMany(mappedBy = "propietario")
    @JsonIgnore
    private List<JugadorLiga> plantilla;

    @ManyToMany(mappedBy = "usuarios")
    @JsonIgnore
    private List<GrupoChat> grupos = new ArrayList<>();

    @Column(unique = true)
    private String alias;

    @Column(name = "vip_hasta")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime vipHasta;

    @JsonIgnore
    public boolean isVip() {
        return vipHasta != null && vipHasta.isAfter(LocalDateTime.now());
    }

    public LocalDateTime getVipHasta() {
        return vipHasta;
    }

    public void setVipHasta(LocalDateTime vipHasta) {
        this.vipHasta = vipHasta;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public List<GrupoChat> getGrupos() {
        return grupos;
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

    public List<JugadorLiga> getPlantilla() {
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

    public void setGrupos(List<GrupoChat> grupos) {
        this.grupos = grupos;
    }

    public void setPlantilla(List<JugadorLiga> plantilla) {
        this.plantilla = plantilla;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

}
