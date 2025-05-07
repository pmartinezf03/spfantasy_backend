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
                mensaje.getRemitente().getAlias()
            );
        
            if (grupoId != null) {
                messagingTemplate.convertAndSend("/chat/liga/" + grupoId, dto);
            } else if (mensaje.getRemitente().getAlias() != null && mensaje.getDestinatario() != null) {
                String alias1 = mensaje.getRemitente().getAlias();
                String alias2 = mensaje.getDestinatario().getAlias();
                String canal = alias1.compareTo(alias2) < 0 ? alias1 + "-" + alias2 : alias2 + "-" + alias1;
                messagingTemplate.convertAndSend("/chat/privado/" + canal, dto);
            }
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
