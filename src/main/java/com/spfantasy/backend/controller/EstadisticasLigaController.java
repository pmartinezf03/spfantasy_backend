package com.spfantasy.backend.controller;

import com.spfantasy.backend.model.JugadorLiga;
import com.spfantasy.backend.dto.JugadorLigaDTO;
import com.spfantasy.backend.repository.JugadorLigaRepository;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import com.spfantasy.backend.dto.JugadorLigaDTO;
import com.spfantasy.backend.service.EstadisticasLigaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RestController
@RequestMapping("/api/estadisticas-liga")
public class EstadisticasLigaController {

    @Autowired
    private EstadisticasLigaService estadisticasLigaService;

    @GetMapping("/top-t3")
    public ResponseEntity<List<JugadorLigaDTO>> topTriplistas(@RequestParam Long ligaId) {
        return ResponseEntity.ok(estadisticasLigaService.topT3(ligaId));
    }

    @GetMapping("/top-fp")
    public ResponseEntity<List<JugadorLigaDTO>> topFp(@RequestParam Long ligaId) {
        return ResponseEntity.ok(estadisticasLigaService.topFp(ligaId));
    }

    @GetMapping("/top-rendimiento")
    public ResponseEntity<List<JugadorLigaDTO>> topRendimiento(@RequestParam Long ligaId) {
        return ResponseEntity.ok(estadisticasLigaService.topRendimiento(ligaId));
    }

    @GetMapping("/top-precio")
    public ResponseEntity<List<JugadorLigaDTO>> topMasCaros(@RequestParam Long ligaId) {
        return ResponseEntity.ok(estadisticasLigaService.topPrecio(ligaId));
    }

    @GetMapping("/top-minutos")
    public ResponseEntity<List<JugadorLigaDTO>> topMinutos(@RequestParam Long ligaId) {
        return ResponseEntity.ok(estadisticasLigaService.topMinutos(ligaId));
    }

    @GetMapping("/top-tl")
    public ResponseEntity<List<JugadorLigaDTO>> topTirosLibres(@RequestParam Long ligaId) {
        return ResponseEntity.ok(estadisticasLigaService.topTl(ligaId));
    }

    @GetMapping("/comparativa-usuario")
    public ResponseEntity<Map<String, Object>> comparativa(@RequestParam Long ligaId, @RequestParam Long usuarioId) {
        return ResponseEntity.ok(estadisticasLigaService.compararUsuarioVsMedia(ligaId, usuarioId));
    }

    @GetMapping("/jugadores-mas-usados")
    public ResponseEntity<List<JugadorLigaDTO>> masUsados(@RequestParam Long ligaId) {
        return ResponseEntity.ok(estadisticasLigaService.jugadoresMasUtilizados(ligaId));
    }
}
