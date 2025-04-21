package com.spfantasy.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "transacciones")
public class Transaccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;

    private Integer precio;

    @ManyToOne
    @JoinColumn(name = "jugador_liga_id")
    private JugadorLiga jugador;

    @ManyToOne
    @JoinColumn(name = "comprador_id")
    private Usuario comprador;

    @ManyToOne
    @JoinColumn(name = "vendedor_id")
    private Usuario vendedor;

    @ManyToOne
    @JoinColumn(name = "liga_id")
    private Liga liga;

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

    public JugadorLiga getJugador() {
        return jugador;
    }

    public void setJugador(JugadorLiga jugador) {
        this.jugador = jugador;
    }

    public Usuario getComprador() {
        return comprador;
    }

    public void setComprador(Usuario comprador) {
        this.comprador = comprador;
    }

    public Usuario getVendedor() {
        return vendedor;
    }

    public void setVendedor(Usuario vendedor) {
        this.vendedor = vendedor;
    }

    public Liga getLiga() {
        return liga;
    }

    public void setLiga(Liga liga) {
        this.liga = liga;
    }

    public Transaccion() {
    }

}
