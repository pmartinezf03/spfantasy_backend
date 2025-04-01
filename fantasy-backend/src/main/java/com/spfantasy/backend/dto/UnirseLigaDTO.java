package com.spfantasy.backend.dto;

public class UnirseLigaDTO {
    private Long usuarioId;
    private String codigoInvitacion;

    public UnirseLigaDTO() {
    }

    public UnirseLigaDTO(Long usuarioId, String codigoInvitacion) {
        this.usuarioId = usuarioId;
        this.codigoInvitacion = codigoInvitacion;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getCodigoInvitacion() {
        return codigoInvitacion;
    }

    public void setCodigoInvitacion(String codigoInvitacion) {
        this.codigoInvitacion = codigoInvitacion;
    }
}
