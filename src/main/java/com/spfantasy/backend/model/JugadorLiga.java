package com.spfantasy.backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

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

    @Column(name = "foto_url")
    private String fotoUrl;

    @Column(name = "fecha_adquisicion")
    private LocalDateTime fechaAdquisicion;

    private Integer pts;
    private Integer min;
    private Integer tl;
    private Integer t2;
    private Integer t3;
    private Integer fp;
    private BigDecimal rendimiento;

    // ====== Getters y Setters ======

    public Long getId() {
        return id;
    }

    public void setTitular(boolean titular) {
        this.esTitular = titular;
    }

    public boolean isTitular() {
        return esTitular;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Jugador getJugadorBase() {
        return jugadorBase;
    }

    public void setJugadorBase(Jugador jugadorBase) {
        this.jugadorBase = jugadorBase;
    }

    public Liga getLiga() {
        return liga;
    }

    public void setLiga(Liga liga) {
        this.liga = liga;
    }

    public Usuario getPropietario() {
        return propietario;
    }

    public void setPropietario(Usuario propietario) {
        this.propietario = propietario;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public boolean isEsTitular() {
        return esTitular;
    }

    public void setEsTitular(boolean esTitular) {
        this.esTitular = esTitular;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public Integer getPuntosTotales() {
        return puntosTotales;
    }

    public void setPuntosTotales(Integer puntosTotales) {
        this.puntosTotales = puntosTotales;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public LocalDateTime getFechaAdquisicion() {
        return fechaAdquisicion;
    }

    public void setFechaAdquisicion(LocalDateTime fechaAdquisicion) {
        this.fechaAdquisicion = fechaAdquisicion;
    }

    public Integer getPts() {
        return pts;
    }

    public void setPts(Integer pts) {
        this.pts = pts;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getTl() {
        return tl;
    }

    public void setTl(Integer tl) {
        this.tl = tl;
    }

    public Integer getT2() {
        return t2;
    }

    public void setT2(Integer t2) {
        this.t2 = t2;
    }

    public Integer getT3() {
        return t3;
    }

    public void setT3(Integer t3) {
        this.t3 = t3;
    }

    public Integer getFp() {
        return fp;
    }

    public void setFp(Integer fp) {
        this.fp = fp;
    }

    public BigDecimal getRendimiento() {
        return rendimiento;
    }

    public void setRendimiento(BigDecimal rendimiento) {
        this.rendimiento = rendimiento;
    }

    // ====== GETTERS DELEGADOS DESDE jugadorBase ======

    public String getNombre() {
        return jugadorBase != null ? jugadorBase.getNombre() : null;
    }

    public String getPosicion() {
        return jugadorBase != null ? jugadorBase.getPosicion() : null;
    }

    public String getEquipo() {
        return jugadorBase != null && jugadorBase.getEquipo() != null ? jugadorBase.getEquipo().getNombre() : null;
    }

    public Long getJugadorBaseId() {
        return jugadorBase != null ? jugadorBase.getId() : null;
    }
}
