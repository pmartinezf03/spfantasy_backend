package com.spfantasy.backend.service;

import com.spfantasy.backend.dto.JugadorDTO;
import com.spfantasy.backend.model.Jugador;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.repository.JugadorRepository;
import com.spfantasy.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JugadorService {

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ✅ Obtener jugadores disponibles en el mercado
    public List<JugadorDTO> obtenerJugadoresDisponibles() {
        return jugadorRepository.findByDisponibleTrue().stream()
                .map(jugador -> new JugadorDTO(
                jugador.getId(),
                jugador.getNombre(),
                jugador.getPosicion(),
                jugador.getPrecioVenta() != null ? jugador.getPrecioVenta().doubleValue() : 0.0,
                jugador.getRendimiento() != null ? jugador.getRendimiento().doubleValue() : 0.0,
                jugador.getPuntosTotales(),
                jugador.getEquipo(),
                jugador.getFotoUrl(),
                jugador.getPts(),
                jugador.getMin(),
                jugador.getTl(),
                jugador.getT2(),
                jugador.getT3(),
                jugador.getFp(),
                jugador.getPropietario()
        )).collect(Collectors.toList());
    }

    // ✅ Obtener todos los jugadores
    public List<JugadorDTO> obtenerJugadores() {
        return jugadorRepository.findAll().stream()
                .map(jugador -> new JugadorDTO(
                jugador.getId(),
                jugador.getNombre(),
                jugador.getPosicion(),
                jugador.getPrecioVenta() != null ? jugador.getPrecioVenta().doubleValue() : 0.0,
                jugador.getRendimiento() != null ? jugador.getRendimiento().doubleValue() : 0.0,
                jugador.getPuntosTotales(),
                jugador.getEquipo(),
                jugador.getFotoUrl(),
                jugador.getPts(),
                jugador.getMin(),
                jugador.getTl(),
                jugador.getT2(),
                jugador.getT3(),
                jugador.getFp(),
                jugador.getPropietario()
        )).collect(Collectors.toList());
    }

    public Jugador guardarJugador(Jugador jugador) {
        return jugadorRepository.save(jugador);
    }

    public void eliminarJugador(Long id) {
        jugadorRepository.deleteById(id);
    }

public boolean venderJugador(String username, Jugador jugador) {
    Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

    if (usuarioOpt.isPresent()) {
        Usuario usuario = usuarioOpt.get();

        Optional<Jugador> jugadorEnPlantilla = usuario.getPlantilla().stream()
                .filter(j -> j.getId().equals(jugador.getId()))
                .findFirst();

        if (jugadorEnPlantilla.isPresent()) {
            Jugador jugadorAEliminar = jugadorEnPlantilla.get();

            usuario.getPlantilla().remove(jugadorAEliminar);
            usuario.setDinero(usuario.getDinero().add(jugadorAEliminar.getPrecioVenta()));

            usuarioRepository.save(usuario);

            // ✅ Marcar el jugador como disponible y SIN PROPIETARIO
            jugadorAEliminar.setDisponible(true);
            jugadorAEliminar.setPropietario(null); // ✅ Aquí se asegura que propietario sea NULL
            jugadorRepository.save(jugadorAEliminar);

            System.out.println("✅ Jugador vendido correctamente.");
            return true;
        }
    }
    return false;
}


    public List<JugadorDTO> obtenerEstadisticasLiga() {
        return jugadorRepository.findAll().stream()
                .map(jugador -> new JugadorDTO(
                jugador.getId(),
                jugador.getNombre(),
                jugador.getPosicion(),
                jugador.getPrecioVenta().doubleValue(),
                jugador.getRendimiento().doubleValue(),
                jugador.getPuntosTotales(),
                jugador.getEquipo(),
                jugador.getFotoUrl(),
                jugador.getPts(),
                jugador.getMin(),
                jugador.getTl(),
                jugador.getT2(),
                jugador.getT3(),
                jugador.getFp(),
                jugador.getPropietario()
        )).collect(Collectors.toList());
    }
}
