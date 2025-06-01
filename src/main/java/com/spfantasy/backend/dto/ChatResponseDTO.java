// ChatResponseDTO.java
package com.spfantasy.backend.dto;

public class ChatResponseDTO {
    private String respuesta;

    public ChatResponseDTO() {
    }

    public ChatResponseDTO(String respuesta) {
        this.respuesta = respuesta;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }
}
