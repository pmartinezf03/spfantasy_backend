package com.spfantasy.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spfantasy.backend.dto.JugadorLigaDTO;
import com.spfantasy.backend.model.JugadorLiga;
import com.spfantasy.backend.service.JugadorLigaService;

@RestController
@RequestMapping("/api/jugadores-liga")
public class JugadorLigaController {

    @Autowired
    private JugadorLigaService jugadorLigaService;

    @GetMapping("/mercado")
    public ResponseEntity<List<JugadorLigaDTO>> obtenerJugadoresDeTodaLaLiga(@RequestParam Long ligaId) {
        List<JugadorLiga> jugadores = jugadorLigaService.obtenerTodosEnLiga(ligaId); // 🔁 Cambia el método
        List<JugadorLigaDTO> dtoList = jugadores.stream()
                .map(JugadorLigaDTO::new)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    // 🔹 Obtener los jugadores que tiene un usuario en una liga (su plantilla)
    @GetMapping("/mis-jugadores")
    public ResponseEntity<List<JugadorLigaDTO>> obtenerJugadoresDeUsuario(
            @RequestParam Long ligaId,
            @RequestParam Long usuarioId) {
        List<JugadorLiga> plantilla = jugadorLigaService.obtenerJugadoresDeUsuarioEnLiga(ligaId, usuarioId);
        List<JugadorLigaDTO> dtoList = plantilla.stream()
                .map(JugadorLigaDTO::new)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/liga")
    public ResponseEntity<List<JugadorLigaDTO>> obtenerJugadoresDeLiga(
            @RequestParam Long ligaId) {
        List<JugadorLiga> jugadores = jugadorLigaService.obtenerTodosEnLiga(ligaId);
        List<JugadorLigaDTO> jugadoresDTO = jugadores.stream()
                .map(JugadorLigaDTO::new)
                .toList();
        return ResponseEntity.ok(jugadoresDTO);
    }

    @GetMapping("/destacados")
    public ResponseEntity<List<JugadorLigaDTO>> obtenerJugadoresDestacados(@RequestParam Long ligaId) {
        List<JugadorLigaDTO> destacados = jugadorLigaService.obtenerJugadoresDestacados(ligaId);
        return ResponseEntity.ok(destacados);
    }

}
