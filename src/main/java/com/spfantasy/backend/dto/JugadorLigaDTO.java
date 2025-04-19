package com.spfantasy.backend.dto;

import com.spfantasy.backend.model.JugadorLiga;

public class JugadorLigaDTO {

    private Long id;
    private String nombre;
    private String posicion;
    private String fotoUrl;
    private Double precioVenta;
    private Double pts;
    private Double min;
    private Double tl;
    private Double t2;
    private Double t3;
    private Double fp;
    private String propietarioUsername;
    private Long propietarioId;

    public JugadorLigaDTO(JugadorLiga jugador) {
        this.id = jugador.getId();
        this.nombre = jugador.getJugadorBase().getNombre();
        this.posicion = jugador.getJugadorBase().getPosicion();
        this.fotoUrl = jugador.getFotoUrl();
        this.precioVenta = jugador.getPrecioVenta() != null ? jugador.getPrecioVenta().doubleValue() : 0.0;
        this.pts = jugador.getPts() != null ? jugador.getPts().doubleValue() : 0.0;
        this.min = jugador.getMin() != null ? jugador.getMin().doubleValue() : 0.0;
        this.tl = jugador.getTl() != null ? jugador.getTl().doubleValue() : 0.0;
        this.t2 = jugador.getT2() != null ? jugador.getT2().doubleValue() : 0.0;
        this.t3 = jugador.getT3() != null ? jugador.getT3().doubleValue() : 0.0;
        this.fp = jugador.getFp() != null ? jugador.getFp().doubleValue() : 0.0;

        if (jugador.getPropietario() != null) {
            this.propietarioUsername = jugador.getPropietario().getUsername();
            this.propietarioId = jugador.getPropietario().getId();
        }
    }

    // 🔽 Getters y Setters igual que antes, no hace falta repetir aquí

    // Getters y Setters

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

    public String getPosicion() {
        return posicion;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public Double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(Double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public Double getPts() {
        return pts;
    }

    public void setPts(Double pts) {
        this.pts = pts;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getTl() {
        return tl;
    }

    public void setTl(Double tl) {
        this.tl = tl;
    }

    public Double getT2() {
        return t2;
    }

    public void setT2(Double t2) {
        this.t2 = t2;
    }

    public Double getT3() {
        return t3;
    }

    public void setT3(Double t3) {
        this.t3 = t3;
    }

    public Double getFp() {
        return fp;
    }

    public void setFp(Double fp) {
        this.fp = fp;
    }

    public String getPropietarioUsername() {
        return propietarioUsername;
    }

    public void setPropietarioUsername(String propietarioUsername) {
        this.propietarioUsername = propietarioUsername;
    }

    public Long getPropietarioId() {
        return propietarioId;
    }

    public void setPropietarioId(Long propietarioId) {
        this.propietarioId = propietarioId;
    }
}
