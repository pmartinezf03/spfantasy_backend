package com.spfantasy.backend.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.spfantasy.backend.dto.MensajeDTO;
import com.spfantasy.backend.model.Mensaje;
import com.spfantasy.backend.service.MensajeService;

@Controller
public class ChatWebSocketController {

    @Autowired
    private MensajeService mensajeService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/enviar")
    public void procesarMensaje(MensajeDTO mensajeDTO) {
        System.out.println("🟢 Mensaje recibido en /chat/enviar: " + mensajeDTO.getContenido());

        Long remitenteId = mensajeDTO.getRemitenteId();
        Long destinatarioId = mensajeDTO.getDestinatarioId();
        Long grupoId = mensajeDTO.getGrupoId();
        String contenido = mensajeDTO.getContenido();

        if (remitenteId == null || contenido == null || contenido.trim().isEmpty()) {
            System.out.println("❌ Mensaje inválido recibido.");
            return;
        }

        Mensaje mensaje = mensajeService.enviarMensaje(remitenteId, destinatarioId, grupoId, contenido);

        MensajeDTO dto = new MensajeDTO(
                mensaje.getId(),
                mensaje.getRemitente().getId(),
                mensaje.getRemitente().getUsername(),
                mensaje.getDestinatario() != null ? mensaje.getDestinatario().getId() : null,
                mensaje.getGrupo() != null ? mensaje.getGrupo().getId() : null,
                mensaje.getContenido(),
                mensaje.getTimestamp(),
                mensaje.getRemitente().getAlias());

        String destino = "";

        if (grupoId != null) {
            destino = "/chat/liga/" + grupoId;
            messagingTemplate.convertAndSend(destino, dto);
            System.out.println("📤 Enviado a GRUPO canal: " + destino);
        } else if (mensaje.getDestinatario() != null) {
            Long id1 = mensaje.getRemitente().getId();
            Long id2 = mensaje.getDestinatario().getId();
            String canal = id1 < id2 ? id1 + "-" + id2 : id2 + "-" + id1;
            destino = "/chat/privado/" + canal;
            System.out.println("📤 Enviado a PRIVADO canal: " + destino);
            messagingTemplate.convertAndSend(destino, dto);
        }

        System.out.println("🟢 Mensaje recibido vía WebSocket: " + mensaje);
        System.out.println("📤 Enviando a canal STOMP: " + destino);
    }

}
