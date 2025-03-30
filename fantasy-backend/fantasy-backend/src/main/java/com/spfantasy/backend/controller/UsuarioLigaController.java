package com.spfantasy.backend.controller;

import com.spfantasy.backend.dto.MiembroLigaDTO;
import com.spfantasy.backend.service.UsuarioLigaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ligas")
public class UsuarioLigaController {

    @Autowired
    private UsuarioLigaService usuarioLigaService;

    @GetMapping("/{ligaId}/miembros")
    public List<MiembroLigaDTO> obtenerMiembrosDeLiga(@PathVariable Long ligaId) {
        return usuarioLigaService.obtenerMiembrosDeLiga(ligaId);
    }
}
