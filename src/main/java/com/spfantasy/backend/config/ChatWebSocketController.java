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
        Long destinatarioId = request.get("destinatarioId") != null
                ? Long.valueOf(request.get("destinatarioId").toString())
                : null;
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
                mensaje.getTimestamp(),
                mensaje.getRemitente().getAlias());

        // Emitir al frontend el DTO limpio
        messagingTemplate.convertAndSend("/chat/mensajes", mensajeDTO);
    }

    @MessageMapping("/chat/liga/{grupoId}")
    public void procesarMensajeGrupo(
            @org.springframework.messaging.handler.annotation.DestinationVariable Long grupoId,
            MensajeDTO mensajeDTO) {

        Mensaje mensaje = mensajeService.enviarMensaje(
                mensajeDTO.getRemitenteId(),
                null, // no hay destinatario directo
                grupoId,
                mensajeDTO.getContenido());

        MensajeDTO dto = new MensajeDTO(
                mensaje.getId(),
                mensaje.getRemitente().getId(),
                mensaje.getRemitente().getUsername(),
                null,
                grupoId,
                mensaje.getContenido(),
                mensaje.getTimestamp(),
                mensaje.getRemitente().getAlias());

        messagingTemplate.convertAndSend("/chat/liga/" + grupoId, dto);
    }

    @MessageMapping("/chat/privado/{canal}")
    public void procesarMensajePrivado(
            @org.springframework.messaging.handler.annotation.DestinationVariable String canal,
            MensajeDTO mensajeDTO) {

        // Separar los alias del canal tipo "pepe-maria"
        String[] partes = canal.split("-");
        if (partes.length != 2) {
            throw new RuntimeException("Canal privado mal formado: " + canal);
        }

        String alias1 = partes[0];
        String alias2 = partes[1];

        // Enviar el mensaje usando alias
        Mensaje mensaje = mensajeService.enviarMensajePorAlias(
                mensajeDTO.getRemitenteAlias(),
                alias1.equals(mensajeDTO.getRemitenteAlias()) ? alias2 : alias1,
                mensajeDTO.getContenido());

        MensajeDTO dto = new MensajeDTO(
                mensaje.getId(),
                mensaje.getRemitente().getId(),
                mensaje.getRemitente().getUsername(),
                mensaje.getDestinatario() != null ? mensaje.getDestinatario().getId() : null,
                null,
                mensaje.getContenido(),
                mensaje.getTimestamp(),
                mensaje.getRemitente().getAlias());

        messagingTemplate.convertAndSend("/chat/privado/" + canal, dto);
    }

}
