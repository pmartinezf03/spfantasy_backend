package com.spfantasy.backend.controller;

import com.spfantasy.backend.service.OdooService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/codigos")
public class CodigoController {

    @Autowired
    private OdooService odooService;

    @GetMapping("/disponibles")
    public String getCodigosDisponibles() {
        return odooService.getCodigosDisponibles();
    }

    @PutMapping("/{id}/canjear")
    public String canjearCodigo(@PathVariable int id) {
        return odooService.marcarCodigoComoUsado(id);
    }

    @GetMapping("/{id}")
    public String getCodigoPorId(@PathVariable int id) {
        return odooService.getCodigoPorId(id);
    }

    @PostMapping
    public String crearCodigo(@RequestBody String json) {
        return odooService.crearCodigo(json);
    }

    @GetMapping("/todos")
    public String getTodos() {
        return odooService.getTodosCodigos();
    }
}
