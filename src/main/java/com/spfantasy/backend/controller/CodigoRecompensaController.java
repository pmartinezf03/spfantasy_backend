package com.spfantasy.backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spfantasy.backend.service.CodigoRecompensaService;

@RestController
@RequestMapping("/api/codigos")
public class CodigoRecompensaController {

    @Autowired
    private CodigoRecompensaService codigoRecompensaService;

    @GetMapping("/verificar/{codigo}")
    public ResponseEntity<?> verificarCodigo(@PathVariable String codigo) {
        try {
            Map<String, Object> resultado = codigoRecompensaService.verificarCodigo(codigo);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

}
