package com.spfantasy.backend.dto;

import java.time.LocalDateTime;


public class MensajeDTO {
    private Long id;
    private Long remitenteId;
    private String remitenteNombre;
    private Long destinatarioId;
    private Long grupoId;
    private String contenido;
    private LocalDateTime timestamp;
    private String remitenteAlias;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getRemitenteId() {
        return remitenteId;
    }
    public void setRemitenteId(Long remitenteId) {
        this.remitenteId = remitenteId;
    }
    public String getRemitenteNombre() {
        return remitenteNombre;
    }
    public void setRemitenteNombre(String remitenteNombre) {
        this.remitenteNombre = remitenteNombre;
    }
    public Long getDestinatarioId() {
        return destinatarioId;
    }
    public void setDestinatarioId(Long destinatarioId) {
        this.destinatarioId = destinatarioId;
    }
    public Long getGrupoId() {
        return grupoId;
    }
    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }
    public String getContenido() {
        return contenido;
    }
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public String getRemitenteAlias() {
        return remitenteAlias;
    }
    public void setRemitenteAlias(String remitenteAlias) {
        this.remitenteAlias = remitenteAlias;
    }
    public MensajeDTO() {
    }
    public MensajeDTO(Long id, Long remitenteId, String remitenteNombre, Long destinatarioId, Long grupoId,
            String contenido, LocalDateTime timestamp, String remitenteAlias) {
        this.id = id;
        this.remitenteId = remitenteId;
        this.remitenteNombre = remitenteNombre;
        this.destinatarioId = destinatarioId;
        this.grupoId = grupoId;
        this.contenido = contenido;
        this.timestamp = timestamp;
        this.remitenteAlias = remitenteAlias;
    }
}
