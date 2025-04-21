package com.spfantasy.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.spfantasy.backend.dto.TransaccionDTO;
import com.spfantasy.backend.model.Transaccion;
import com.spfantasy.backend.service.TransaccionService;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    @Autowired
    private TransaccionService transaccionService;

    @GetMapping("/{usuarioId}/{ligaId}")
    public ResponseEntity<List<TransaccionDTO>> obtenerHistorial(@PathVariable Long usuarioId,
            @PathVariable Long ligaId) {
        return ResponseEntity.ok(transaccionService.obtenerTransaccionesPorUsuarioYLiga(usuarioId, ligaId));
    }

    @PostMapping
    public ResponseEntity<Transaccion> crearTransaccion(@RequestBody Transaccion transaccion) {
        return ResponseEntity.ok(transaccionService.guardarTransaccion(transaccion));
    }

    @GetMapping
    public ResponseEntity<List<Transaccion>> obtenerTodas() {
        return ResponseEntity.ok(transaccionService.obtenerTodas());
    }

    @GetMapping("/liga/{ligaId}")
    public ResponseEntity<List<TransaccionDTO>> obtenerHistorialPorLiga(@PathVariable Long ligaId) {
        return ResponseEntity.ok(transaccionService.obtenerTransaccionesPorLiga(ligaId));
    }

}
