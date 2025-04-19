package com.spfantasy.backend.dto;

import java.math.BigDecimal;

import com.spfantasy.backend.model.Oferta;

public class OfertaDTO {
    private Long id;
    private String compradorUsername;
    private String vendedorUsername;
    private Long jugadorId;
    private String nombreJugador;
    private BigDecimal montoOferta;
    private String estado;
    private JugadorLigaDTO jugador;

    // Constructor
    public OfertaDTO(Oferta oferta) {
        this.id = oferta.getId();
        this.compradorUsername = oferta.getComprador().getUsername();
        this.vendedorUsername = oferta.getVendedor().getUsername();
        this.jugadorId = oferta.getJugador().getId();
        this.nombreJugador = oferta.getJugador().getJugadorBase().getNombre();
        this.montoOferta = oferta.getMontoOferta();
        this.estado = oferta.getEstado().name();
        this.jugador = new JugadorLigaDTO(oferta.getJugador());
    }

    public Long getId() {
        return id;
    }

    public String getCompradorUsername() {
        return compradorUsername;
    }

    public String getVendedorUsername() {
        return vendedorUsername;
    }

    public Long getJugadorId() {
        return jugadorId;
    }

    public String getNombreJugador() {
        return nombreJugador;
    }

    public BigDecimal getMontoOferta() {
        return montoOferta;
    }

    public String getEstado() {
        return estado;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCompradorUsername(String compradorUsername) {
        this.compradorUsername = compradorUsername;
    }

    public void setVendedorUsername(String vendedorUsername) {
        this.vendedorUsername = vendedorUsername;
    }

    public void setJugadorId(Long jugadorId) {
        this.jugadorId = jugadorId;
    }

    public void setNombreJugador(String nombreJugador) {
        this.nombreJugador = nombreJugador;
    }

    public void setMontoOferta(BigDecimal montoOferta) {
        this.montoOferta = montoOferta;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public JugadorLigaDTO getJugador() {
        return jugador;
    }

    public void setJugador(JugadorLigaDTO jugador) {
        this.jugador = jugador;
    }

}
