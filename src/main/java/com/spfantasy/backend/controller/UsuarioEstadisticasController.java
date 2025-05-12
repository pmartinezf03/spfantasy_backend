package com.spfantasy.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spfantasy.backend.service.UsuarioEstadisticasService;

@RestController
@RequestMapping("/api/usuarios/estadisticas")
public class UsuarioEstadisticasController {

    @Autowired
    private UsuarioEstadisticasService estadisticasService;

    @PostMapping("/{id}/login")
    public ResponseEntity<?> registrarLogin(@PathVariable Long id) {
        estadisticasService.registrarLogin(id);
        return ResponseEntity.ok("Login registrado");
    }

    @PostMapping("/{id}/compra")
    public ResponseEntity<?> registrarCompra(@PathVariable Long id) {
        estadisticasService.registrarCompra(id);
        return ResponseEntity.ok("Compra registrada");
    }

    @PostMapping("/{id}/venta")
    public ResponseEntity<?> registrarVenta(@PathVariable Long id) {
        estadisticasService.registrarVenta(id);
        return ResponseEntity.ok("Venta registrada");
    }

    @PostMapping("/{id}/partida")
    public ResponseEntity<?> registrarPartida(@PathVariable Long id) {
        estadisticasService.registrarPartida(id);
        return ResponseEntity.ok("Partida registrada");
    }
}
