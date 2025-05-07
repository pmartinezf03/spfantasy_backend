package com.spfantasy.backend.controller;

import com.spfantasy.backend.dto.GrupoChatDTO;
import com.spfantasy.backend.model.GrupoChat;
import com.spfantasy.backend.service.GrupoChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grupos")
public class GrupoChatController {

    @Autowired
    private GrupoChatService grupoChatService;

    // ✅ Obtener todos los grupos de chat
    @GetMapping
    public List<GrupoChatDTO> obtenerTodosLosGrupos() {
        return grupoChatService.obtenerTodosLosGruposDTO();
    }

    // ✅ Crear un nuevo grupo de chat
    @PostMapping("/crear")
    public GrupoChat crearGrupo(@RequestBody Map<String, Object> request) {
        String nombre = (String) request.get("nombre");
        String descripcion = (String) request.get("descripcion");
        String passwordGrupo = (String) request.get("passwordGrupo");
        Long creadorId = Long.valueOf(request.get("creadorId").toString());

        return grupoChatService.crearGrupo(nombre, descripcion, passwordGrupo, creadorId);
    }

    @GetMapping("/liga/{usuarioId}")
    public ResponseEntity<GrupoChatDTO> obtenerGrupoDeLigaDelUsuario(@PathVariable Long usuarioId) {
        GrupoChat grupo = grupoChatService.obtenerGrupoLigaDelUsuario(usuarioId);
        GrupoChatDTO dto = grupoChatService.convertirADTO(grupo);
        return ResponseEntity.ok(dto);
    }

    // ✅ Unirse a un grupo de chat
    @PostMapping("/unirse")
    public GrupoChat unirseAGrupo(@RequestBody Map<String, Object> request) {
        Long grupoId = Long.valueOf(request.get("grupoId").toString());
        Long usuarioId = Long.valueOf(request.get("usuarioId").toString());
        String passwordGrupo = (String) request.get("passwordGrupo");

        return grupoChatService.unirseAGrupo(grupoId, usuarioId, passwordGrupo);
    }

    // ✅ Obtener grupos de un usuario
    @GetMapping("/usuario/{usuarioId}")
    public List<GrupoChat> obtenerGruposDelUsuario(@PathVariable Long usuarioId) {
        return grupoChatService.obtenerGruposDelUsuario(usuarioId);
    }

}
