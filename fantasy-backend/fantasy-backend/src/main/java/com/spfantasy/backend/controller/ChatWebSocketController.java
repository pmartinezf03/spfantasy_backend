package com.spfantasy.backend.controller;

import com.spfantasy.backend.model.Mensaje;
import com.spfantasy.backend.service.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {

    @Autowired
    private MensajeService mensajeService;

    @MessageMapping("/enviarMensaje")  // ✅ Recibir mensajes desde frontend
    @SendTo("/chat/mensajes")  // ✅ Enviar mensajes a todos los clientes
    public Mensaje enviarMensaje(Mensaje mensaje) {
        return mensajeService.enviarMensaje(
                mensaje.getRemitente().getId(),
                mensaje.getDestinatario() != null ? mensaje.getDestinatario().getId() : null,
                mensaje.getGrupo() != null ? mensaje.getGrupo().getId() : null,
                mensaje.getContenido()
        );
    }
}
