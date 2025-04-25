package com.spfantasy.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "jugadores")
public class Jugador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String posicion;

    @Column(name = "precio_venta", precision = 10, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "rendimiento", precision = 5, scale = 2)
    private BigDecimal rendimiento;

    @Column(name = "puntos_totales")
    private Integer puntosTotales;

    @ManyToOne
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;

    @Column(name = "foto_url", length = 255)
    private String fotoUrl;

    @Column(name = "disponible", nullable = false)
    private boolean disponible = true;

    @Column(name = "es_titular", nullable = false)
    private boolean esTitular = true;

    // ✅ Nuevo: Relación con el Propietario
    @ManyToOne
    @JoinColumn(name = "propietario_id")
    private Usuario propietario;

    // 🔥 Nuevas estadísticas
    @Column(name = "pts")
    private Integer pts;

    @Column(name = "min")
    private Integer min;

    @Column(name = "tl")
    private Integer tl;

    @Column(name = "t2")
    private Integer t2;

    @Column(name = "t3")
    private Integer t3;

    @Column(name = "fp")
    private Integer fp;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPosicion() {
        return posicion;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public BigDecimal getRendimiento() {
        return rendimiento;
    }

    public Integer getPuntosTotales() {
        return puntosTotales;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public boolean getDisponible() {
        return disponible;
    }

    public boolean getTitular() {
        return esTitular;
    }

    public Usuario getPropietario() {
        return propietario;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public void setRendimiento(BigDecimal rendimiento) {
        this.rendimiento = rendimiento;
    }

    public void setPuntosTotales(Integer puntosTotales) {
        this.puntosTotales = puntosTotales;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public void setTitular(boolean esTitular) {
        this.esTitular = esTitular;
    }

    public void setPropietario(Usuario propietario) {
        this.propietario = propietario;
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

}
