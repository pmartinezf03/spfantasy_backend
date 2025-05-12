package com.spfantasy.backend.service;

import org.springframework.stereotype.Service;

import com.spfantasy.backend.dto.LogroConEstadoDTO;
import com.spfantasy.backend.dto.UsuarioLogroDTO;
import com.spfantasy.backend.model.Logro;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.model.UsuarioLogro;
import com.spfantasy.backend.repository.LogroRepository;
import com.spfantasy.backend.repository.UsuarioLogroRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LogroService {

    private final UsuarioLogroRepository usuarioLogroRepository;
    private final LogroRepository logroRepository;

    public LogroService(UsuarioLogroRepository ulr, LogroRepository lr) {
        this.usuarioLogroRepository = ulr;
        this.logroRepository = lr;
    }

    public List<UsuarioLogro> obtenerLogrosPorUsuario(Long usuarioId) {
        return usuarioLogroRepository.findByUsuarioId(usuarioId);
    }

    public void desbloquearLogro(Long usuarioId, Long logroId) {
        if (!usuarioLogroRepository.existsByUsuarioIdAndLogroId(usuarioId, logroId)) {
            UsuarioLogro nuevo = new UsuarioLogro();
            nuevo.setUsuario(new Usuario(usuarioId)); // constructor vacío con ID
            nuevo.setLogro(new Logro(logroId));
            nuevo.setFechaDesbloqueo(LocalDate.now());
            usuarioLogroRepository.save(nuevo);
        }
    }

    public List<UsuarioLogroDTO> obtenerLogrosDTOPorUsuario(Long usuarioId) {
        List<UsuarioLogro> logros = usuarioLogroRepository.findByUsuarioId(usuarioId);

        return logros.stream().map(ul -> new UsuarioLogroDTO(
                ul.getLogro().getId(),
                ul.getLogro().getNombre(),
                ul.getLogro().getDescripcion(),
                ul.getLogro().getIcono(),
                ul.getFechaDesbloqueo() != null ? ul.getFechaDesbloqueo().toString() : null)).toList();
    }

    public List<LogroConEstadoDTO> obtenerTodosConEstadoParaUsuario(Long usuarioId) {
        List<Logro> todos = logroRepository.findAll();
        List<UsuarioLogro> desbloqueados = usuarioLogroRepository.findByUsuarioId(usuarioId);

        Map<Long, UsuarioLogro> desbloqueadosMap = desbloqueados.stream()
                .collect(Collectors.toMap(ul -> ul.getLogro().getId(), ul -> ul));

        return todos.stream().map(logro -> {
            UsuarioLogro ul = desbloqueadosMap.get(logro.getId());
            return new LogroConEstadoDTO(
                    logro.getId(),
                    logro.getNombre(),
                    logro.getDescripcion(),
                    logro.getIcono(),
                    ul != null,
                    ul != null ? ul.getFechaDesbloqueo().toString() : null);
        }).toList();
    }

}
