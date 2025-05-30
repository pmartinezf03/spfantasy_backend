package com.spfantasy.backend.controller;

import com.spfantasy.backend.service.UsuarioLigaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ligas")
public class UsuarioLigaController {

    private final UsuarioLigaService usuarioLigaService;

    @Autowired
    public UsuarioLigaController(UsuarioLigaService usuarioLigaService) {
        this.usuarioLigaService = usuarioLigaService;
    }

    @GetMapping("/{usuarioId}/ligas/{ligaId}/sobres-mostrados")
    public boolean haMostradoSobres(@PathVariable Long usuarioId, @PathVariable Long ligaId) {
        return usuarioLigaService.haMostradoSobres(usuarioId, ligaId);
    }

    @PostMapping("/{usuarioId}/ligas/{ligaId}/marcar-sobres-mostrados")
    public void marcarSobresMostrados(@PathVariable Long usuarioId, @PathVariable Long ligaId) {
        usuarioLigaService.marcarSobresMostrados(usuarioId, ligaId);
    }

}
