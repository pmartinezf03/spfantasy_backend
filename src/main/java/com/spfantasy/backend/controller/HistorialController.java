package com.spfantasy.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spfantasy.backend.dto.HistorialTransaccionDTO;
import com.spfantasy.backend.service.JugadorLigaService;

@RestController
@RequestMapping("/api/historial")
public class HistorialController {

    @Autowired
    private JugadorLigaService jugadorLigaService;

    @GetMapping("/{usuarioId}/{ligaId}")
    public ResponseEntity<List<HistorialTransaccionDTO>> getHistorial(
            @PathVariable Long usuarioId,
            @PathVariable Long ligaId) {
        List<HistorialTransaccionDTO> historial = jugadorLigaService.obtenerHistorialTransacciones(usuarioId, ligaId);
        return ResponseEntity.ok(historial);
    }
}
