package com.spfantasy.backend.dto;

import java.time.LocalDateTime;

public class TransaccionDTO {
    private Long id;
    private LocalDateTime fecha;
    private Integer precio;

    private String nombreJugador;
    private String fotoUrl;

    private String compradorUsername;
    private String vendedorUsername;

    private Long ligaId;
    private String ligaNombre;

    // ✅ Constructor completo
    public TransaccionDTO(Long id, LocalDateTime fecha, Integer precio,
            String nombreJugador, String fotoUrl,
            String compradorUsername, String vendedorUsername,
            Long ligaId, String ligaNombre) {
        this.id = id;
        this.fecha = fecha;
        this.precio = precio;
        this.nombreJugador = nombreJugador;
        this.fotoUrl = fotoUrl;
        this.compradorUsername = compradorUsername;
        this.vendedorUsername = vendedorUsername;
        this.ligaId = ligaId;
        this.ligaNombre = ligaNombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Integer getPrecio() {
        return precio;
    }

    public void setPrecio(Integer precio) {
        this.precio = precio;
    }

    public String getNombreJugador() {
        return nombreJugador;
    }

    public void setNombreJugador(String nombreJugador) {
        this.nombreJugador = nombreJugador;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public String getCompradorUsername() {
        return compradorUsername;
    }

    public void setCompradorUsername(String compradorUsername) {
        this.compradorUsername = compradorUsername;
    }

    public String getVendedorUsername() {
        return vendedorUsername;
    }

    public void setVendedorUsername(String vendedorUsername) {
        this.vendedorUsername = vendedorUsername;
    }

    public Long getLigaId() {
        return ligaId;
    }

    public void setLigaId(Long ligaId) {
        this.ligaId = ligaId;
    }

    public String getLigaNombre() {
        return ligaNombre;
    }

    public void setLigaNombre(String ligaNombre) {
        this.ligaNombre = ligaNombre;
    }

    public TransaccionDTO() {
        // Constructor vacío requerido para setters
    }

}
