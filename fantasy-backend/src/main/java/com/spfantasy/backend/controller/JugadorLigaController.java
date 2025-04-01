package com.spfantasy.backend.controller;

import com.spfantasy.backend.model.JugadorLiga;
import com.spfantasy.backend.service.JugadorLigaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jugadores-liga")
public class JugadorLigaController {

    @Autowired
    private JugadorLigaService jugadorLigaService;

    // 🔹 Obtener jugadores de la liga del usuario
    @GetMapping("/mercado")
    public ResponseEntity<List<JugadorLiga>> obtenerJugadoresDisponibles(
            @RequestParam Long ligaId
    ) {
        List<JugadorLiga> disponibles = jugadorLigaService.obtenerDisponiblesDeLiga(ligaId);
        return ResponseEntity.ok(disponibles);
    }

    // 🔹 Obtener los jugadores que tiene un usuario en una liga (su plantilla)
    @GetMapping("/mis-jugadores")
    public ResponseEntity<List<JugadorLiga>> obtenerJugadoresDeUsuario(
            @RequestParam Long ligaId,
            @RequestParam Long usuarioId
    ) {
        List<JugadorLiga> plantilla = jugadorLigaService.obtenerJugadoresDeUsuarioEnLiga(ligaId, usuarioId);
        return ResponseEntity.ok(plantilla);
    }
}
