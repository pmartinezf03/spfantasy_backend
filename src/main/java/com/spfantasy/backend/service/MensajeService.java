package com.spfantasy.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spfantasy.backend.dto.MensajeDTO;
import com.spfantasy.backend.model.GrupoChat;
import com.spfantasy.backend.model.Mensaje;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.repository.GrupoChatRepository;
import com.spfantasy.backend.repository.MensajeRepository;
import com.spfantasy.backend.repository.UsuarioRepository;

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

        @Transactional
        public Mensaje enviarMensaje(Long remitenteId, Long destinatarioId, Long grupoId, String contenido) {
                Usuario remitente = usuarioRepository.findById(remitenteId)
                                .orElseThrow(() -> new RuntimeException("Usuario remitente no encontrado"));

                Mensaje mensaje = new Mensaje();
                mensaje.setRemitente(remitente);
                mensaje.setContenido(contenido);

                if (grupoId != null) {
                        GrupoChat grupo = grupoChatRepository.findById(grupoId)
                                        .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
                        mensaje.setGrupo(grupo);

                        List<Mensaje> mensajes = mensajeRepository.findTop500ByGrupoOrderByTimestampDesc(grupo);
                        if (mensajes.size() >= 500) {
                                mensajeRepository.delete(mensajes.get(mensajes.size() - 1));
                        }

                        mensajeRepository.save(mensaje);
                } else if (destinatarioId != null) {
                        Usuario destinatario = usuarioRepository.findById(destinatarioId)
                                        .orElseThrow(() -> new RuntimeException("Usuario destinatario no encontrado"));
                        mensaje.setDestinatario(destinatario);

                        List<Mensaje> mensajes = mensajeRepository
                                        .findTop500ByRemitenteAndDestinatarioOrDestinatarioAndRemitenteOrderByTimestampDesc(
                                                        remitente, destinatario, destinatario, remitente);
                        if (mensajes.size() >= 500) {
                                mensajeRepository.delete(mensajes.get(mensajes.size() - 1));
                        }

                        mensajeRepository.save(mensaje);
                } else {
                        throw new RuntimeException("Debe especificarse un destinatario o un grupo");
                }

                // Emitir mensaje por WebSocket (grupo o privado)
                MensajeDTO dto = convertirADTO(mensaje);
                emitirMensajeWebSocket(mensaje, dto);

                return mensaje;
        }

        private void emitirMensajeWebSocket(Mensaje mensaje, MensajeDTO dto) {
                if (mensaje.getGrupo() != null) {
                        messagingTemplate.convertAndSend("/chat/liga/" + mensaje.getGrupo().getId(), dto);
                } else if (mensaje.getRemitente().getAlias() != null && mensaje.getDestinatario() != null) {
                        String canal = generarNombreCanal(
                                        mensaje.getRemitente().getAlias(),
                                        mensaje.getDestinatario().getAlias());
                        messagingTemplate.convertAndSend("/chat/privado/" + canal, dto);
                }
        }

        public List<Mensaje> obtenerMensajesGrupo(Long grupoId) {
                GrupoChat grupo = grupoChatRepository.findById(grupoId)
                                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
                return mensajeRepository.findTop500ByGrupoOrderByTimestampDesc(grupo);
        }

        public List<Mensaje> obtenerMensajesPrivados(Long usuario1Id, Long usuario2Id) {
                Usuario usuario1 = usuarioRepository.findById(usuario1Id)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                Usuario usuario2 = usuarioRepository.findById(usuario2Id)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                return mensajeRepository
                                .findTop500ByRemitenteAndDestinatarioOrDestinatarioAndRemitenteOrderByTimestampDesc(
                                                usuario1, usuario2, usuario2, usuario1);
        }

        public List<MensajeDTO> obtenerMensajesGrupoDTO(Long grupoId) {
                GrupoChat grupo = grupoChatRepository.findById(grupoId)
                                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
                List<Mensaje> mensajes = mensajeRepository.findTop500ByGrupoOrderByTimestampDesc(grupo);
                return mensajes.stream().map(this::convertirADTO).toList();
        }

        public List<MensajeDTO> obtenerMensajesPrivadosDTO(Long usuario1Id, Long usuario2Id) {
                Usuario usuario1 = usuarioRepository.findById(usuario1Id)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                Usuario usuario2 = usuarioRepository.findById(usuario2Id)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                List<Mensaje> mensajes = mensajeRepository
                                .findTop500ByRemitenteAndDestinatarioOrDestinatarioAndRemitenteOrderByTimestampDesc(
                                                usuario1, usuario2, usuario2, usuario1);
                return mensajes.stream().map(this::convertirADTO).toList();
        }

        private MensajeDTO convertirADTO(Mensaje mensaje) {
                return new MensajeDTO(
                                mensaje.getId(),
                                mensaje.getRemitente().getId(),
                                mensaje.getRemitente().getUsername(),
                                mensaje.getDestinatario() != null ? mensaje.getDestinatario().getId() : null,
                                mensaje.getGrupo() != null ? mensaje.getGrupo().getId() : null,
                                mensaje.getContenido(),
                                mensaje.getTimestamp(),
                                mensaje.getRemitente().getAlias());
        }

        // ✅ NUEVO: obtener todos los mensajes donde participa el usuario
        public List<MensajeDTO> obtenerTodosMisMensajes(Long usuarioId) {
                Usuario usuario = usuarioRepository.findById(usuarioId)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                // Mensajes privados donde es remitente o destinatario
                List<Mensaje> privados = mensajeRepository
                                .findTop500ByRemitenteOrDestinatarioOrderByTimestampDesc(usuario, usuario);

                // Obtener todos los grupos donde está este usuario
                List<GrupoChat> grupos = grupoChatRepository.findByUsuariosContaining(usuario);

                // Mensajes de los grupos a los que pertenece
                List<Mensaje> grupales = mensajeRepository.findTop500ByGrupoInOrderByTimestampDesc(grupos);

                List<Mensaje> todos = new ArrayList<>();
                todos.addAll(privados);
                todos.addAll(grupales);

                return todos.stream().map(this::convertirADTO).toList();
        }

        @Transactional
        public Mensaje enviarMensajePorAlias(String aliasRemitente, String aliasDestinatario, String contenido) {
                Usuario remitente = usuarioRepository.findByAlias(aliasRemitente)
                                .orElseThrow(() -> new RuntimeException("Alias remitente no encontrado"));
                Usuario destinatario = usuarioRepository.findByAlias(aliasDestinatario)
                                .orElseThrow(() -> new RuntimeException("Alias destinatario no encontrado"));

                Mensaje mensaje = new Mensaje();
                mensaje.setRemitente(remitente);
                mensaje.setDestinatario(destinatario);
                mensaje.setContenido(contenido);

                // mantener 500 mensajes máximo
                List<Mensaje> mensajes = mensajeRepository
                                .findTop500ByRemitenteAndDestinatarioOrDestinatarioAndRemitenteOrderByTimestampDesc(
                                                remitente, destinatario, destinatario, remitente);

                if (mensajes.size() >= 500) {
                        mensajeRepository.delete(mensajes.get(mensajes.size() - 1));
                }

                mensajeRepository.save(mensaje);

                // Enviar a canal único basado en alias
                messagingTemplate.convertAndSend(
                                "/chat/privado/" + generarNombreCanal(aliasRemitente, aliasDestinatario),
                                mensaje);

                return mensaje;
        }

        private String generarNombreCanal(String alias1, String alias2) {
                return alias1.compareTo(alias2) < 0
                                ? alias1 + "-" + alias2
                                : alias2 + "-" + alias1;
        }

}
