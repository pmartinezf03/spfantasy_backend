package com.spfantasy.backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MensajeDTO {
    private Long id;
    private Long remitenteId;
    private String remitenteNombre;
    private Long destinatarioId;
    private Long grupoId;
    private String contenido;
    private LocalDateTime timestamp;
}
