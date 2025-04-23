package com.spfantasy.backend.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spfantasy.backend.dto.ActualizarLigaDTO;
import com.spfantasy.backend.dto.LigaUnidaDTO;
import com.spfantasy.backend.dto.MiembroLigaDTO;
import com.spfantasy.backend.dto.RankingUsuarioDTO;
import com.spfantasy.backend.dto.UnirseLigaDTO;
import com.spfantasy.backend.model.GrupoChat;
import com.spfantasy.backend.model.JugadorLiga;
import com.spfantasy.backend.model.Liga;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.model.UsuarioLiga;
import com.spfantasy.backend.repository.GrupoChatRepository;
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

    @Autowired
    private GrupoChatRepository grupoChatRepository;

    @Transactional
    public Liga crearLiga(String nombre, String codigoInvitacion, Long creadorId) {
        Usuario creador = usuarioRepository.findById(creadorId)
                .orElseThrow(() -> new RuntimeException("Creador no encontrado"));

        if (!creador.getRole().name().equalsIgnoreCase("admin") &&
                !creador.getRole().name().equalsIgnoreCase("vip")) {
            throw new RuntimeException("Solo administradores o usuarios VIP pueden crear ligas.");
        }

        if (usuarioLigaRepository.existsByUsuarioId(creadorId)) {
            throw new RuntimeException("Este usuario ya pertenece a una liga");
        }

        if (ligaRepository.existsByCodigoInvitacion(codigoInvitacion)) {
            throw new RuntimeException("El código de invitación ya está en uso");
        }

        Liga liga = new Liga();
        liga.setNombre(nombre);
        liga.setCodigoInvitacion(codigoInvitacion);
        liga.setCreador(creador);

        Liga ligaGuardada = ligaRepository.save(liga);

        // 🔧 Asignar la liga al usuario y forzar persistencia inmediata
        // ✅ Guardar la relación en usuarios_liga
        UsuarioLiga ul = new UsuarioLiga();
        ul.setUsuario(creador);
        ul.setLiga(ligaGuardada);
        usuarioLigaRepository.save(ul);

        // ✅ Ya NO hace falta setLiga, ni save, ni verificación

        // Guardar relación en usuarios_liga
        ul.setUsuario(creador);
        ul.setLiga(ligaGuardada);
        usuarioLigaRepository.save(ul);

        // Repartir jugadores
        // ✅ Primero generar todos los jugadores para la liga
        jugadorLigaService.generarJugadoresParaLiga(ligaGuardada);

        // ✅ Luego repartir 10 jugadores aleatorios al creador
        jugadorLigaService.repartirJugadoresIniciales(creador, ligaGuardada);

        // Crear grupo de chat
        GrupoChat grupoLiga = new GrupoChat();
        grupoLiga.setNombre("liga-" + ligaGuardada.getId());
        grupoLiga.setDescripcion("Chat para la liga " + ligaGuardada.getNombre());
        grupoLiga.setCreador(creador);
        grupoLiga.getUsuarios().add(creador);
        grupoChatRepository.save(grupoLiga);

        return ligaGuardada;
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

        // 👉 Guardar la relación en UsuarioLiga
        UsuarioLiga ul = new UsuarioLiga();
        ul.setLiga(liga);
        ul.setUsuario(usuario);
        usuarioLigaRepository.save(ul);

        // 👉 Guardar la relación en UsuarioLiga
        ul.setLiga(liga);
        ul.setUsuario(usuario);
        usuarioLigaRepository.save(ul);

        // 👉 Repartir jugadores iniciales
        jugadorLigaService.repartirJugadoresIniciales(usuario, liga);

        // ✅ Buscar o crear grupo de chat de la liga
        String nombreGrupo = "liga-" + liga.getId();

        GrupoChat grupo = grupoChatRepository.findByNombre(nombreGrupo).orElse(null);

        if (grupo == null) {
            grupo = new GrupoChat();
            grupo.setNombre(nombreGrupo);
            grupo.setDescripcion("Chat para la liga " + liga.getNombre());
            grupo.setCreador(liga.getCreador());
            grupo.setUsuarios(new HashSet<>()); // Asegúrate que no sea null
        }

        // Añadir el usuario si no estaba
        grupo.getUsuarios().add(usuario);

        grupoChatRepository.save(grupo);

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
                .map(ul -> {
                    Usuario usuario = ul.getUsuario();
                    List<JugadorLiga> plantilla = jugadorLigaRepository.findByLiga_IdAndPropietario_Id(ligaId,
                            usuario.getId());

                    int puntosTotales = plantilla.stream()
                            .mapToInt(JugadorLiga::getFp)
                            .sum();

                    return new RankingUsuarioDTO(usuario.getId(), usuario.getUsername(), puntosTotales);
                })
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
        return usuarioLigaRepository.findAllByUsuarioId(usuarioId)
                .stream()
                .findFirst()
                .map(UsuarioLiga::getLiga);

    }

}
