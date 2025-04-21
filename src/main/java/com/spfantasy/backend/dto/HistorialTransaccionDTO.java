package com.spfantasy.backend.dto;

import java.time.LocalDateTime;

public class HistorialTransaccionDTO {
    public String nombreJugador;
    public String fotoUrl;
    public LocalDateTime fechaCompra;
    public Integer precioCompra;
    public String compradoA;
    public LocalDateTime fechaVenta;
    public Integer precioVenta;
    public String vendidoA;
    public Integer ganancia;

    public HistorialTransaccionDTO(String nombreJugador, String fotoUrl, LocalDateTime fechaCompra,
            Integer precioCompra,
            String compradoA, LocalDateTime fechaVenta, Integer precioVenta,
            String vendidoA, Integer ganancia) {
        this.nombreJugador = nombreJugador;
        this.fotoUrl = fotoUrl;
        this.fechaCompra = fechaCompra;
        this.precioCompra = precioCompra;
        this.compradoA = compradoA;
        this.fechaVenta = fechaVenta;
        this.precioVenta = precioVenta;
        this.vendidoA = vendidoA;
        this.ganancia = ganancia;
    }
}
