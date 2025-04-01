package com.spfantasy.backend.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spfantasy.backend.dto.MiembroLigaDTO;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.model.UsuarioLiga;
import com.spfantasy.backend.repository.UsuarioLigaRepository;

@Service
public class UsuarioLigaService {

    @Autowired
    private UsuarioLigaRepository usuarioLigaRepository;

    public List<MiembroLigaDTO> obtenerMiembrosDeLiga(Long ligaId) {
        List<UsuarioLiga> usuariosLiga = usuarioLigaRepository.findByLigaId(ligaId);

        return usuariosLiga.stream()
                .map(ul -> {
                    Usuario u = ul.getUsuario();
                    return new MiembroLigaDTO(u.getId(), u.getUsername(), u.getEmail());
                })
                .collect(Collectors.toList());
    }

    public Optional<Long> obtenerLigaDelUsuario(Long usuarioId) {
        return usuarioLigaRepository.findByUsuarioId(usuarioId)
                .map(relacion -> relacion.getLiga().getId());
    }
}
