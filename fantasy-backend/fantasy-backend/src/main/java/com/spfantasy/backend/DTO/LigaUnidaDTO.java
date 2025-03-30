package com.spfantasy.backend.dto;

public class LigaUnidaDTO {
    private Long ligaId;
    private String nombreLiga;
    private String mensaje;

    public LigaUnidaDTO() {
    }

    public LigaUnidaDTO(Long ligaId, String nombreLiga, String mensaje) {
        this.ligaId = ligaId;
        this.nombreLiga = nombreLiga;
        this.mensaje = mensaje;
    }

    public Long getLigaId() {
        return ligaId;
    }

    public void setLigaId(Long ligaId) {
        this.ligaId = ligaId;
    }

    public String getNombreLiga() {
        return nombreLiga;
    }

    public void setNombreLiga(String nombreLiga) {
        this.nombreLiga = nombreLiga;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
