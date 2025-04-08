package com.spfantasy.backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ofertas")
public class Oferta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "jugador_liga_id", nullable = false)
    private JugadorLiga jugador;

    @ManyToOne
    @JoinColumn(name = "comprador_id", nullable = false)
    private Usuario comprador;

    @ManyToOne
    @JoinColumn(name = "vendedor_id", nullable = false)
    private Usuario vendedor;

    @Column(nullable = false)
    private BigDecimal montoOferta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoOferta estado;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime timestamp = LocalDateTime.now();

    public enum EstadoOferta {
        PENDIENTE, ACEPTADA, RECHAZADA, CONTRAOFERTA
    }

    @Column(name = "leida_por_vendedor")
    private Boolean leidaPorVendedor = false;

    @ManyToOne
    @JoinColumn(name = "liga_id")
    private Liga liga;

    public Long getId() {
        return id;
    }

    public JugadorLiga getJugador() {
        return jugador;
    }

    public Usuario getComprador() {
        return comprador;
    }

    public Usuario getVendedor() {
        return vendedor;
    }

    public BigDecimal getMontoOferta() {
        return montoOferta;
    }

    public EstadoOferta getEstado() {
        return estado;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Boolean getLeidaPorVendedor() {
        return leidaPorVendedor;
    }

    public Liga getLiga() {
        return liga;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setJugador(JugadorLiga jugador) {
        this.jugador = jugador;
    }

    public void setComprador(Usuario comprador) {
        this.comprador = comprador;
    }

    public void setVendedor(Usuario vendedor) {
        this.vendedor = vendedor;
    }

    public void setMontoOferta(BigDecimal montoOferta) {
        this.montoOferta = montoOferta;
    }

    public void setEstado(EstadoOferta estado) {
        this.estado = estado;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setLeidaPorVendedor(Boolean leidaPorVendedor) {
        this.leidaPorVendedor = leidaPorVendedor;
    }

    public void setLiga(Liga liga) {
        this.liga = liga;
    }
}
