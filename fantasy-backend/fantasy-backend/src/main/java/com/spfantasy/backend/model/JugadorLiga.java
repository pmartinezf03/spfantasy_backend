package com.spfantasy.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "jugadores_liga")
public class JugadorLiga {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "jugador_base_id")
    private Jugador jugadorBase;

    @ManyToOne
    @JoinColumn(name = "liga_id")
    private Liga liga;

    @ManyToOne
    @JoinColumn(name = "propietario_id")
    private Usuario propietario;

    private boolean disponible = true;
    private boolean esTitular = true;

    @Column(name = "precio_venta", precision = 10, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "puntos_totales")
    private Integer puntosTotales;

    private Integer pts;
    private Integer min;
    private Integer tl;
    private Integer t2;
    private Integer t3;
    private Integer fp;

    public void setId(Long id) {
        this.id = id;
    }

    public void setJugadorBase(Jugador jugadorBase) {
        this.jugadorBase = jugadorBase;
    }

    public void setLiga(Liga liga) {
        this.liga = liga;
    }

    public void setPropietario(Usuario propietario) {
        this.propietario = propietario;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public void setEsTitular(boolean esTitular) {
        this.esTitular = esTitular;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public void setPuntosTotales(Integer puntosTotales) {
        this.puntosTotales = puntosTotales;
    }

    public void setPts(Integer pts) {
        this.pts = pts;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public void setTl(Integer tl) {
        this.tl = tl;
    }

    public void setT2(Integer t2) {
        this.t2 = t2;
    }

    public void setT3(Integer t3) {
        this.t3 = t3;
    }

    public void setFp(Integer fp) {
        this.fp = fp;
    }

    public Long getId() {
        return id;
    }

    public Jugador getJugadorBase() {
        return jugadorBase;
    }

    public Liga getLiga() {
        return liga;
    }

    public Usuario getPropietario() {
        return propietario;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public boolean isEsTitular() {
        return esTitular;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public Integer getPuntosTotales() {
        return puntosTotales;
    }

    public Integer getPts() {
        return pts;
    }

    public Integer getMin() {
        return min;
    }

    public Integer getTl() {
        return tl;
    }

    public Integer getT2() {
        return t2;
    }

    public Integer getT3() {
        return t3;
    }

    public Integer getFp() {
        return fp;
    }
    
    

}
    