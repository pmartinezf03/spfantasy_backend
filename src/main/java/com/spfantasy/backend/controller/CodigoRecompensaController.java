package com.spfantasy.backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
        System.out.println("🔎 [CONTROLLER] Verificando código: " + codigo);
        try {
            Map<String, Object> resultado = codigoRecompensaService.verificarCodigo(codigo);
            System.out.println("✅ [CONTROLLER] Resultado verificación: " + resultado);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            System.out.println("❌ [CONTROLLER] Error verificando código: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/canjear/{codigo}")
    public ResponseEntity<?> canjearCodigo(@PathVariable String codigo) {
        System.out.println("🎯 [CONTROLLER] Intentando canjear código: " + codigo);
        try {
            boolean exito = codigoRecompensaService.canjearCodigo(codigo);
            if (exito) {
                System.out.println("✅ [CONTROLLER] Código canjeado correctamente");
                return ResponseEntity.ok(Map.of("mensaje", "Código canjeado correctamente."));
            } else {
                System.out.println("⚠️ [CONTROLLER] El código ya estaba usado o no existe");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "El código ya estaba usado o no existe."));
            }
        } catch (Exception e) {
            System.out.println("❌ [CONTROLLER] Error al canjear código: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
