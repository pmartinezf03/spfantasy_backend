package com.spfantasy.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spfantasy.backend.dto.ActualizarLigaDTO;
import com.spfantasy.backend.dto.CrearLigaDTO;
import com.spfantasy.backend.dto.LigaUnidaDTO;
import com.spfantasy.backend.dto.MiembroLigaDTO;
import com.spfantasy.backend.dto.RankingUsuarioDTO;
import com.spfantasy.backend.dto.UnirseLigaDTO;
import com.spfantasy.backend.model.Liga;
import com.spfantasy.backend.service.LigaService;

@RestController
@RequestMapping("/api/ligas")

public class LigaController {

    @Autowired
    private LigaService ligaService;

    @PostMapping("/crear")
    public ResponseEntity<?> crearLiga(@RequestBody CrearLigaDTO dto) {
        try {
            Liga liga = ligaService.crearLiga(dto.getNombre(), dto.getCodigo(), dto.getCreadorId());
            return ResponseEntity.ok(liga);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/unirse")
    public ResponseEntity<LigaUnidaDTO> unirseALiga(@RequestBody UnirseLigaDTO dto) {
        LigaUnidaDTO respuesta = ligaService.unirseALiga(dto);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{ligaId}/miembros")
    public List<MiembroLigaDTO> obtenerMiembros(@PathVariable Long ligaId) {
        return ligaService.obtenerMiembrosLiga(ligaId);
    }

    @DeleteMapping("/{ligaId}/salir/{usuarioId}")
    public ResponseEntity<String> salirDeLaLiga(@PathVariable Long ligaId, @PathVariable Long usuarioId) {
        ligaService.salirDeLaLiga(ligaId, usuarioId);
        return ResponseEntity.ok("Has salido de la liga correctamente");
    }

    @DeleteMapping("/{ligaId}/expulsar/{usuarioId}")
    public ResponseEntity<String> expulsarDeLaLiga(@PathVariable Long ligaId, @PathVariable Long usuarioId,
            @RequestParam Long creadorId) {
        ligaService.expulsarDeLaLiga(ligaId, usuarioId, creadorId);
        return ResponseEntity.ok("Usuario expulsado de la liga");
    }

    @PostMapping("/{ligaId}/iniciar")
    public ResponseEntity<String> iniciarLiga(@PathVariable Long ligaId, @RequestParam Long creadorId) {
        ligaService.iniciarLiga(ligaId, creadorId);
        return ResponseEntity.ok("Liga iniciada correctamente");
    }

    @GetMapping("/{ligaId}/ranking")
    public ResponseEntity<List<RankingUsuarioDTO>> getRanking(@PathVariable Long ligaId) {
        List<RankingUsuarioDTO> ranking = ligaService.obtenerRanking(ligaId);
        return ResponseEntity.ok(ranking);
    }

    @PutMapping("/actualizar")
    public ResponseEntity<String> actualizarLiga(@RequestBody ActualizarLigaDTO dto) {
        try {
            ligaService.actualizarLiga(dto);
            return ResponseEntity.ok("Liga actualizada correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/todas")
    public ResponseEntity<List<Liga>> obtenerTodasLasLigas() {
        List<Liga> ligas = ligaService.obtenerTodasLasLigas();
        return ResponseEntity.ok(ligas);
    }

}
