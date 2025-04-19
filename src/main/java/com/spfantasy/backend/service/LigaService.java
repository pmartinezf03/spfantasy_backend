package com.spfantasy.backend.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spfantasy.backend.dto.ActualizarLigaDTO;
import com.spfantasy.backend.dto.LigaUnidaDTO;
import com.spfantasy.backend.dto.MiembroLigaDTO;
import com.spfantasy.backend.dto.RankingUsuarioDTO;
import com.spfantasy.backend.dto.UnirseLigaDTO;
import com.spfantasy.backend.model.JugadorLiga;
import com.spfantasy.backend.model.Liga;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.model.UsuarioLiga;
import com.spfantasy.backend.repository.JugadorLigaRepository;
import com.spfantasy.backend.repository.LigaRepository;
import com.spfantasy.backend.repository.UsuarioLigaRepository;
import com.spfantasy.backend.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class LigaService {

    @Autowired
    private LigaRepository ligaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioLigaRepository usuarioLigaRepository;

    @Autowired
    private JugadorLigaService jugadorLigaService;

    @Autowired
    private JugadorLigaRepository jugadorLigaRepository;

    @Transactional
    public Liga crearLiga(String nombre, String codigoInvitacion, Long creadorId) {
        Usuario creador = usuarioRepository.findById(creadorId)
                .orElseThrow(() -> new RuntimeException("Creador no encontrado"));

        // ❌ Si ya pertenece a una liga, no puede crear otra
        if (usuarioLigaRepository.existsByUsuarioId(creadorId)) {
            throw new RuntimeException("Este usuario ya pertenece a una liga");
        }

        Liga liga = new Liga();
        liga.setNombre(nombre);
        liga.setCodigoInvitacion(codigoInvitacion);
        liga.setCreador(creador);

        ligaRepository.save(liga);

        // Relacionar creador con la liga
        UsuarioLiga ul = new UsuarioLiga();
        ul.setUsuario(creador);
        ul.setLiga(liga);
        usuarioLigaRepository.save(ul);

        // Crear los jugadores de la liga
        jugadorLigaService.generarJugadoresParaLiga(liga);

        return liga;
    }

    @Transactional
    public LigaUnidaDTO unirseALiga(UnirseLigaDTO dto) {
        Liga liga = ligaRepository.findByCodigoInvitacion(dto.getCodigoInvitacion())
                .orElseThrow(() -> new RuntimeException("Código de invitación inválido"));

        // ❌ Ya pertenece a cualquier liga
        if (usuarioLigaRepository.existsByUsuarioId(dto.getUsuarioId())) {
            throw new RuntimeException("Este usuario ya pertenece a una liga");
        }

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 👉 Actualizar el campo liga_id en el usuario
        usuario.setLiga(liga);
        usuarioRepository.save(usuario);

        // 👉 Guardar la relación en UsuarioLiga
        UsuarioLiga ul = new UsuarioLiga();
        ul.setLiga(liga);
        ul.setUsuario(usuario);
        usuarioLigaRepository.save(ul);

        // 👉 Repartir jugadores iniciales
        jugadorLigaService.repartirJugadoresIniciales(usuario, liga);

        return new LigaUnidaDTO(liga.getId(), liga.getNombre(), "Te has unido correctamente");
    }

    public List<MiembroLigaDTO> obtenerMiembrosLiga(Long ligaId) {
        List<UsuarioLiga> relaciones = usuarioLigaRepository.findByLigaId(ligaId);
        return relaciones.stream()
                .map(ul -> {
                    Usuario u = ul.getUsuario();
                    return new MiembroLigaDTO(u.getId(), u.getUsername(), u.getEmail());
                })
                .toList();
    }

    @Transactional
    public void salirDeLaLiga(Long ligaId, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Liga liga = ligaRepository.findById(ligaId)
                .orElseThrow(() -> new RuntimeException("Liga no encontrada"));

        // Eliminar relación en UsuarioLiga
        usuarioLigaRepository.deleteByUsuarioIdAndLigaId(usuarioId, ligaId);

        // ❌ Quitar el campo liga_id del usuario
        usuario.setLiga(null);
        usuarioRepository.save(usuario);

        // Liberar los jugadores del usuario en esta liga
        List<JugadorLiga> jugadores = jugadorLigaRepository.findByLiga_IdAndPropietario_Id(ligaId, usuarioId);
        for (JugadorLiga jugador : jugadores) {
            jugador.setPropietario(null);
            jugador.setDisponible(true);
            jugador.setEsTitular(false);
            jugadorLigaRepository.save(jugador);
        }
    }

    @Transactional
    public void expulsarDeLaLiga(Long ligaId, Long usuarioId, Long creadorId) {
        Liga liga = ligaRepository.findById(ligaId)
                .orElseThrow(() -> new RuntimeException("Liga no encontrada"));

        if (!liga.getCreador().getId().equals(creadorId)) {
            throw new RuntimeException("Solo el creador de la liga puede expulsar a usuarios");
        }

        UsuarioLiga ul = usuarioLigaRepository.findByUsuarioIdAndLigaId(usuarioId, ligaId)
                .orElseThrow(() -> new RuntimeException("El usuario no pertenece a esta liga"));

        // Eliminar la relación del usuario con la liga
        usuarioLigaRepository.delete(ul);

        // Liberar jugadores del usuario en esa liga
        List<JugadorLiga> jugadores = jugadorLigaRepository.findByLiga_IdAndPropietario_Id(ligaId, usuarioId);
        for (JugadorLiga j : jugadores) {
            j.setPropietario(null);
            j.setDisponible(true);
            j.setEsTitular(false);
        }

        jugadorLigaRepository.saveAll(jugadores); // Aquí se actualizan en base de datos
    }

    @Transactional
    public void iniciarLiga(Long ligaId, Long creadorId) {
        Liga liga = ligaRepository.findById(ligaId)
                .orElseThrow(() -> new RuntimeException("Liga no encontrada"));

        if (!liga.getCreador().getId().equals(creadorId)) {
            throw new RuntimeException("Solo el creador puede iniciar la liga");
        }

        if (liga.isIniciada()) {
            throw new RuntimeException("La liga ya ha sido iniciada");
        }

        liga.setIniciada(true);
        ligaRepository.save(liga);
    }

    public List<RankingUsuarioDTO> obtenerRanking(Long ligaId) {
        List<UsuarioLiga> relaciones = usuarioLigaRepository.findByLigaId(ligaId);

        return relaciones.stream()
                .map(ul -> new RankingUsuarioDTO(
                        ul.getUsuario().getId(),
                        ul.getUsuario().getUsername(),
                        ul.getUsuario().getPuntos()))
                .sorted((u1, u2) -> Integer.compare(u2.getPuntosTotales(), u1.getPuntosTotales()))
                .collect(Collectors.toList());
    }

    public void actualizarLiga(ActualizarLigaDTO dto) {
        Liga liga = ligaRepository.findById(dto.getLigaId())
                .orElseThrow(() -> new RuntimeException("Liga no encontrada"));

        if (!liga.getCreador().getId().equals(dto.getCreadorId())) {
            throw new RuntimeException("Solo el creador puede modificar la liga");
        }

        if (dto.getMaxParticipantes() != null) {
            liga.setMaxParticipantes(dto.getMaxParticipantes());
        }

        if (dto.getContrasena() != null && !dto.getContrasena().isEmpty()) {
            liga.setContrasena(dto.getContrasena());
        }

        ligaRepository.save(liga);
    }

    public List<Liga> obtenerTodasLasLigas() {
        return ligaRepository.findAll();
    }

    public Optional<Liga> obtenerLigaDelUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .map(Usuario::getLiga);
    }

}
