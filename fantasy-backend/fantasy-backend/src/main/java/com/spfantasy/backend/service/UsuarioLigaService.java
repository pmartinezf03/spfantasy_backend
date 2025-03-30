package com.spfantasy.backend.service;

import com.spfantasy.backend.dto.MiembroLigaDTO;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.model.UsuarioLiga;
import com.spfantasy.backend.repository.UsuarioLigaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
}
