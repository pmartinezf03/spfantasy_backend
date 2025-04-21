package com.spfantasy.backend.service;

import com.spfantasy.backend.model.GrupoChat;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.repository.GrupoChatRepository;
import com.spfantasy.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spfantasy.backend.dto.GrupoChatDTO;
import java.util.Set;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Optional;

@Service
public class GrupoChatService {

    @Autowired
    private GrupoChatRepository grupoChatRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ✅ Obtener todos los grupos de chat
    public List<GrupoChat> obtenerTodosLosGrupos() {
        return grupoChatRepository.findAll();
    }

    public List<GrupoChatDTO> obtenerTodosLosGruposDTO() {
        return grupoChatRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // ✅ Crear un nuevo grupo de chat con un usuario como creador
    public GrupoChat crearGrupo(String nombre, String descripcion, String passwordGrupo, Long creadorId) {
        Optional<Usuario> creador = usuarioRepository.findById(creadorId);
        if (creador.isEmpty()) {
            throw new RuntimeException("El usuario no existe");
        }

        GrupoChat grupo = new GrupoChat();
        grupo.setNombre(nombre);
        grupo.setDescripcion(descripcion);
        grupo.setPasswordGrupo(passwordGrupo.isEmpty() ? null : passwordGrupo);
        grupo.setCreador(creador.get());

        return grupoChatRepository.save(grupo);
    }

    // ✅ Unirse a un grupo reutilizando el usuario existente
    public GrupoChat unirseAGrupo(Long grupoId, Long usuarioId, String passwordGrupo) {
        GrupoChat grupo = grupoChatRepository.findById(grupoId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (grupo.getPasswordGrupo() != null && !grupo.getPasswordGrupo().equals(passwordGrupo)) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        grupo.getUsuarios().add(usuario);
        return grupoChatRepository.save(grupo);
    }

    public List<GrupoChat> obtenerGruposDelUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return grupoChatRepository.findByUsuariosContaining(usuario);
    }

    private GrupoChatDTO convertirADTO(GrupoChat grupo) {
        Set<Long> usuariosIds = grupo.getUsuarios().stream()
                .map(Usuario::getId)
                .collect(java.util.stream.Collectors.toSet());

        return new GrupoChatDTO(
                grupo.getId(),
                grupo.getNombre(),
                grupo.getDescripcion(),
                grupo.getCreador().getId(),
                usuariosIds);
    }

}
