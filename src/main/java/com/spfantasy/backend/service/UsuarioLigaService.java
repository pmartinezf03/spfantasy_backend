package com.spfantasy.backend.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spfantasy.backend.dto.MiembroLigaDTO;
import com.spfantasy.backend.model.JugadorLiga;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.model.UsuarioLiga;
import com.spfantasy.backend.repository.JugadorLigaRepository;
import com.spfantasy.backend.repository.UsuarioLigaRepository;

@Service
public class UsuarioLigaService {

    @Autowired
    private UsuarioLigaRepository usuarioLigaRepository;

    @Autowired
    private JugadorLigaRepository jugadorLigaRepository;

    public List<MiembroLigaDTO> obtenerMiembrosDeLiga(Long ligaId) {
        List<UsuarioLiga> usuariosLiga = usuarioLigaRepository.findByLigaId(ligaId);

        return usuariosLiga.stream()
                .map(ul -> {
                    Usuario u = ul.getUsuario();
                    return new MiembroLigaDTO(u.getId(), u.getUsername(), u.getEmail(), u.getUltimoLogin());
                })
                .collect(Collectors.toList());
    }

    public Optional<Long> obtenerLigaDelUsuario(Long usuarioId) {
        List<UsuarioLiga> ligas = usuarioLigaRepository.findAllByUsuarioId(usuarioId);

        if (ligas.size() > 1) {
            throw new IllegalStateException("El usuario pertenece a más de una liga, lo cual no está permitido.");
        }

        if (ligas.isEmpty())
            return Optional.empty();

        return Optional.of(ligas.get(0).getLiga().getId());
    }

    public List<JugadorLiga> obtenerJugadoresRepartidosHoy(Long usuarioId) {
        List<UsuarioLiga> relaciones = usuarioLigaRepository.findAllByUsuarioId(usuarioId);

        if (relaciones.isEmpty()) {
            return List.of();
        }

        UsuarioLiga relacion = relaciones.get(0);
        Timestamp fechaUnion = relacion.getFechaUnion();
        LocalDate fecha = fechaUnion.toLocalDateTime().toLocalDate();
        LocalDate hoy = LocalDate.now();

        // ✅ Comprobar si se unió hoy
        if (!fecha.equals(hoy)) {
            return List.of(); // ❌ No se unió hoy
        }

        Long ligaId = relacion.getLiga().getId();

        // ✅ Obtener los jugadores sin transacciones y de ese usuario
        return jugadorLigaRepository.findByLiga_IdAndPropietario_Id(ligaId, usuarioId);
    }

    public boolean haMostradoSobres(Long usuarioId, Long ligaId) {
        return usuarioLigaRepository.findByUsuarioIdAndLigaId(usuarioId, ligaId)
                .map(UsuarioLiga::isSobresMostrados)
                .orElse(false);
    }

    public void marcarSobresMostrados(Long usuarioId, Long ligaId) {
        usuarioLigaRepository.findByUsuarioIdAndLigaId(usuarioId, ligaId).ifPresent(ul -> {
            ul.setSobresMostrados(true);
            usuarioLigaRepository.save(ul);
        });
    }

}
