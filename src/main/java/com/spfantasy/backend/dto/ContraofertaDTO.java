package com.spfantasy.backend.dto;

import java.math.BigDecimal;

public class ContraofertaDTO {
    private Long ofertaOriginalId;
    private BigDecimal montoOferta;

    public Long getOfertaOriginalId() {
        return ofertaOriginalId;
    }

    public void setOfertaOriginalId(Long ofertaOriginalId) {
        this.ofertaOriginalId = ofertaOriginalId;
    }

    public BigDecimal getMontoOferta() {
        return montoOferta;
    }

    public void setMontoOferta(BigDecimal montoOferta) {
        this.montoOferta = montoOferta;
    }
}