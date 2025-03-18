package com.spfantasy.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ofertas")
public class Oferta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "jugador_id", nullable = false)
    private Jugador jugador;

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

    public Long getId() { return id; }
    public Jugador getJugador() { return jugador; }
    public Usuario getComprador() { return comprador; }
    public Usuario getVendedor() { return vendedor; }
    public BigDecimal getMontoOferta() { return montoOferta; }
    public EstadoOferta getEstado() { return estado; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public void setId(Long id) { this.id = id; }
    public void setJugador(Jugador jugador) { this.jugador = jugador; }
    public void setComprador(Usuario comprador) { this.comprador = comprador; }
    public void setVendedor(Usuario vendedor) { this.vendedor = vendedor; }
    public void setMontoOferta(BigDecimal montoOferta) { this.montoOferta = montoOferta; }
    public void setEstado(EstadoOferta estado) { this.estado = estado; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
