package com.spfantasy.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spfantasy.backend.dto.MiembroLigaDTO;
import com.spfantasy.backend.service.UsuarioLigaService;

@RestController
@RequestMapping("/ligas")
public class UsuarioLigaController {

    @Autowired
    private UsuarioLigaService usuarioLigaService;

    @GetMapping("/{ligaId}/miembros")
    public List<MiembroLigaDTO> obtenerMiembrosDeLiga(@PathVariable Long ligaId) {
        return usuarioLigaService.obtenerMiembrosDeLiga(ligaId);
    }

    @GetMapping("/usuario/{usuarioId}/liga")
    public ResponseEntity<Long> obtenerLigaDelUsuario(@PathVariable Long usuarioId) {
        Optional<Long> ligaId = usuarioLigaService.obtenerLigaDelUsuario(usuarioId);
        return ligaId.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
