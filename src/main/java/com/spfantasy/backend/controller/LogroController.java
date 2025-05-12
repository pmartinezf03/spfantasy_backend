package com.spfantasy.backend.controller;

import org.springframework.web.bind.annotation.*;

import com.spfantasy.backend.dto.LogroConEstadoDTO;
import com.spfantasy.backend.dto.UsuarioLogroDTO;
import com.spfantasy.backend.model.UsuarioLogro;
import com.spfantasy.backend.service.LogroService;

import java.util.*;

@RestController
@RequestMapping("/api/logros")
public class LogroController {

    private final LogroService logroService;

    public LogroController(LogroService logroService) {
        this.logroService = logroService;
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<UsuarioLogroDTO> getLogrosPorUsuario(@PathVariable Long usuarioId) {
        return logroService.obtenerLogrosDTOPorUsuario(usuarioId);
    }

    @PostMapping("/desbloquear")
    public void desbloquearLogro(@RequestBody Map<String, Long> datos) {
        logroService.desbloquearLogro(datos.get("usuarioId"), datos.get("logroId"));
    }

    @GetMapping("/todos-con-estado/{usuarioId}")
    public List<LogroConEstadoDTO> getTodosLogrosConEstado(@PathVariable Long usuarioId) {
        return logroService.obtenerTodosConEstadoParaUsuario(usuarioId);
    }

}
