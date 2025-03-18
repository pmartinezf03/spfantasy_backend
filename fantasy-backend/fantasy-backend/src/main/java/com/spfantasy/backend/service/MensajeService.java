package com.spfantasy.backend.service;

import com.spfantasy.backend.model.Mensaje;
import com.spfantasy.backend.model.GrupoChat;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.repository.MensajeRepository;
import com.spfantasy.backend.repository.GrupoChatRepository;
import com.spfantasy.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MensajeService {

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private GrupoChatRepository grupoChatRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ✅ Guardar un mensaje y enviarlo por WebSockets
    @Transactional
    public Mensaje enviarMensaje(Long remitenteId, Long destinatarioId, Long grupoId, String contenido) {
        Usuario remitente = usuarioRepository.findById(remitenteId)
                .orElseThrow(() -> new RuntimeException("Usuario remitente no encontrado"));

        Mensaje mensaje = new Mensaje();
        mensaje.setRemitente(remitente);
        mensaje.setContenido(contenido);

        if (grupoId != null) { // ✅ Mensaje grupal
            GrupoChat grupo = grupoChatRepository.findById(grupoId)
                    .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
            mensaje.setGrupo(grupo);

            List<Mensaje> mensajes = mensajeRepository.findTop500ByGrupoOrderByTimestampDesc(grupo);
            if (mensajes.size() >= 500) {
                mensajeRepository.delete(mensajes.get(mensajes.size() - 1));
            }

            mensajeRepository.save(mensaje);
            messagingTemplate.convertAndSend("/chat/mensajes", mensaje);
        } else if (destinatarioId != null) { // ✅ Mensaje privado
            Usuario destinatario = usuarioRepository.findById(destinatarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario destinatario no encontrado"));
            mensaje.setDestinatario(destinatario);

            List<Mensaje> mensajes = mensajeRepository.findTop500ByRemitenteAndDestinatarioOrDestinatarioAndRemitenteOrderByTimestampDesc(
                    remitente, destinatario, destinatario, remitente);
            if (mensajes.size() >= 500) {
                mensajeRepository.delete(mensajes.get(mensajes.size() - 1));
            }

            mensajeRepository.save(mensaje);
            messagingTemplate.convertAndSend("/chat/mensajes", mensaje);
        } else {
            throw new RuntimeException("Debe especificarse un destinatario o un grupo");
        }

        return mensaje;
    }

    // ✅ Obtener los últimos 500 mensajes de un grupo
    public List<Mensaje> obtenerMensajesGrupo(Long grupoId) {
        GrupoChat grupo = grupoChatRepository.findById(grupoId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
        return mensajeRepository.findTop500ByGrupoOrderByTimestampDesc(grupo);
    }

    // ✅ Obtener los últimos 500 mensajes entre dos usuarios
    public List<Mensaje> obtenerMensajesPrivados(Long usuario1Id, Long usuario2Id) {
        Usuario usuario1 = usuarioRepository.findById(usuario1Id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Usuario usuario2 = usuarioRepository.findById(usuario2Id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return mensajeRepository.findTop500ByRemitenteAndDestinatarioOrDestinatarioAndRemitenteOrderByTimestampDesc(
                usuario1, usuario2, usuario2, usuario1);
    }
}
