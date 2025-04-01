package com.spfantasy.backend.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spfantasy.backend.service.UsuarioLigaService;

@RestController
@RequestMapping("/api/ligas")
public class UsuarioLigaController {

    @Autowired
    private UsuarioLigaService usuarioLigaService;

    @GetMapping("/usuario/{usuarioId}/liga")
    public ResponseEntity<Long> obtenerLigaDelUsuario(@PathVariable Long usuarioId) {
        Optional<Long> ligaId = usuarioLigaService.obtenerLigaDelUsuario(usuarioId);
        return ligaId.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
