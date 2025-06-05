package com.spfantasy.backend.dto;

import com.spfantasy.backend.model.Equipo;
import com.spfantasy.backend.model.Usuario;

public class JugadorDTO {

    private Long id;
    private Long idLiga; // <-- ID de JugadorLiga, importante para guardar plantilla

    private String nombre;
    private String posicion;
    private Double precioVenta;
    private Double rendimiento;
    private Integer puntosTotales;
    private Equipo equipo;
    private String fotoUrl;

    // Nuevas estadísticas
    private Integer pts;
    private Integer min;
    private Integer tl;
    private Integer t2;
    private Integer t3;
    private Integer fp;
    private boolean esTitular;

    // Datos del propietario
    private Long propietarioId;
    private String propietarioUsername;

    public JugadorDTO(Long idLiga, String nombre, String posicion, Double precioVenta, Double rendimiento,
            Integer puntosTotales,
            Equipo equipo, String fotoUrl, Integer pts, Integer min, Integer tl, Integer t2,
            Integer t3, Integer fp, Usuario propietario) {

        this.idLiga = idLiga; // ID de JugadorLiga
        this.id = idLiga; // También lo usamos como ID general del DTO

        this.nombre = nombre;
        this.posicion = posicion;
        this.precioVenta = precioVenta;
        this.rendimiento = rendimiento;
        this.puntosTotales = puntosTotales;
        this.equipo = equipo;
        this.fotoUrl = fotoUrl;

        this.pts = pts;
        this.min = min;
        this.tl = tl;
        this.t2 = t2;
        this.t3 = t3;
        this.fp = fp;

        // Propietario
        if (propietario != null) {
            this.propietarioId = propietario.getId();
            this.propietarioUsername = propietario.getUsername();
        } else {
            this.propietarioId = null;
            this.propietarioUsername = "Libre";
        }
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPosicion() {
        return posicion;
    }

    public Double getPrecioVenta() {
        return precioVenta;
    }

    public Double getRendimiento() {
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

    public Long getPropietarioId() {
        return propietarioId;
    } // ✅ Nuevo

    public String getPropietarioUsername() {
        return propietarioUsername;
    } // ✅ Nuevo

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public void setPrecioVenta(Double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public void setRendimiento(Double rendimiento) {
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

    public void setPropietarioId(Long propietarioId) {
        this.propietarioId = propietarioId;
    } // ✅ Nuevo

    public void setPropietarioUsername(String propietarioUsername) {
        this.propietarioUsername = propietarioUsername;
    } // ✅ Nuevo

    public boolean isEsTitular() {
        return esTitular;
    }

    public void setEsTitular(boolean esTitular) {
        this.esTitular = esTitular;
    }

    public Long getIdLiga() {
        return idLiga;
    }

    public void setIdLiga(Long idLiga) {
        this.idLiga = idLiga;
    }

}
