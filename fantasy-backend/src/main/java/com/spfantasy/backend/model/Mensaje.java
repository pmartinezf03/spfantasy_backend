package com.spfantasy.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "mensajes")
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "remitente_id", nullable = false)
    private Usuario remitente; // Usuario que envió el mensaje

    @ManyToOne
    @JoinColumn(name = "grupo_id", nullable = true)
    private GrupoChat grupo; // Grupo en el que se envió el mensaje (puede ser null si es privado)

    @ManyToOne
    @JoinColumn(name = "destinatario_id", nullable = true)
    private Usuario destinatario; // Si es un mensaje privado, aquí se guarda el destinatario

    @Column(nullable = false, length = 1000)
    private String contenido;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now(); // Marca de tiempo del mensaje
    
    
}
