package com.spfantasy.backend.controller;

import com.spfantasy.backend.dto.MensajeDTO;
import com.spfantasy.backend.model.Mensaje;
import com.spfantasy.backend.service.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/mensajes")
public class MensajeController {

    @Autowired
    private MensajeService mensajeService;

    @PostMapping("/enviar")
    public Mensaje enviarMensaje(@RequestBody Map<String, Object> request) {
        Long remitenteId = Long.valueOf(request.get("remitenteId").toString());
        Long destinatarioId = request.get("destinatarioId") != null
                ? Long.valueOf(request.get("destinatarioId").toString())
                : null;
        Long grupoId = request.get("grupoId") != null ? Long.valueOf(request.get("grupoId").toString()) : null;
        String contenido = (String) request.get("contenido");

        return mensajeService.enviarMensaje(remitenteId, destinatarioId, grupoId, contenido);
    }

    @GetMapping("/grupo/{grupoId}")
    public List<Mensaje> obtenerMensajesGrupo(@PathVariable Long grupoId) {
        return mensajeService.obtenerMensajesGrupo(grupoId);
    }

    @GetMapping("/privado/{usuario1Id}/{usuario2Id}")
    public List<Mensaje> obtenerMensajesPrivados(@PathVariable Long usuario1Id, @PathVariable Long usuario2Id) {
        return mensajeService.obtenerMensajesPrivados(usuario1Id, usuario2Id);
    }

    @GetMapping("/grupo/{grupoId}/dto")
    public List<MensajeDTO> obtenerMensajesGrupoDTO(@PathVariable Long grupoId) {
        return mensajeService.obtenerMensajesGrupoDTO(grupoId);
    }

    @GetMapping("/privado/{usuario1Id}/{usuario2Id}/dto")
    public List<MensajeDTO> obtenerMensajesPrivadosDTO(@PathVariable Long usuario1Id, @PathVariable Long usuario2Id) {
        return mensajeService.obtenerMensajesPrivadosDTO(usuario1Id, usuario2Id);
    }

    @GetMapping("/{usuarioId}/todos")
    public ResponseEntity<?> obtenerTodosMisMensajesCompat(@PathVariable Long usuarioId) {
        List<MensajeDTO> mensajes = mensajeService.obtenerTodosMisMensajes(usuarioId);
        return ResponseEntity.ok(mensajes);
    }

    @PostMapping("/enviar/privado")
    public Mensaje enviarPorAlias(@RequestBody Map<String, String> request) {
        String remitenteAlias = request.get("remitenteAlias");
        String destinatarioAlias = request.get("destinatarioAlias");
        String contenido = request.get("contenido");

        return mensajeService.enviarMensajePorAlias(remitenteAlias, destinatarioAlias, contenido);
    }

}
