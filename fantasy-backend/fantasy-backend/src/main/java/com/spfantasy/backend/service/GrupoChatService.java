package com.spfantasy.backend.service;

import com.spfantasy.backend.model.GrupoChat;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.repository.GrupoChatRepository;
import com.spfantasy.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
