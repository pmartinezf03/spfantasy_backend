package com.spfantasy.backend.config;

import com.spfantasy.backend.dto.MensajeDTO;
import com.spfantasy.backend.model.Mensaje;
import com.spfantasy.backend.service.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class ChatWebSocketController {

    @Autowired
    private MensajeService mensajeService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/enviar") // Recibe desde Angular
    public void procesarMensaje(Map<String, Object> request) {
        Long remitenteId = Long.valueOf(request.get("remitenteId").toString());
        Long destinatarioId = request.get("destinatarioId") != null ? Long.valueOf(request.get("destinatarioId").toString()) : null;
        Long grupoId = request.get("grupoId") != null ? Long.valueOf(request.get("grupoId").toString()) : null;
        String contenido = request.get("contenido").toString();

        Mensaje mensaje = mensajeService.enviarMensaje(remitenteId, destinatarioId, grupoId, contenido);

        MensajeDTO mensajeDTO = new MensajeDTO(
                mensaje.getId(),
                mensaje.getRemitente().getId(),
                mensaje.getRemitente().getUsername(),
                mensaje.getDestinatario() != null ? mensaje.getDestinatario().getId() : null,
                mensaje.getGrupo() != null ? mensaje.getGrupo().getId() : null,
                mensaje.getContenido(),
                mensaje.getTimestamp()
        );

        // Emitir al frontend el DTO limpio
        messagingTemplate.convertAndSend("/chat/mensajes", mensajeDTO);
    }
}
