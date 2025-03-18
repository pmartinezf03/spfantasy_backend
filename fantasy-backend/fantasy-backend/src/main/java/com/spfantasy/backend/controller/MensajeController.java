package com.spfantasy.backend.controller;

import com.spfantasy.backend.model.Mensaje;
import com.spfantasy.backend.service.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mensajes")
@CrossOrigin(origins = "http://localhost:4200") // 🔥 PERMITIR CORS PARA ANGULAR
public class MensajeController {

    @Autowired
    private MensajeService mensajeService;

    @PostMapping("/enviar")
    public Mensaje enviarMensaje(@RequestBody Map<String, Object> request) {
        Long remitenteId = Long.valueOf(request.get("remitenteId").toString());
        Long destinatarioId = request.get("destinatarioId") != null ? Long.valueOf(request.get("destinatarioId").toString()) : null;
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
}
