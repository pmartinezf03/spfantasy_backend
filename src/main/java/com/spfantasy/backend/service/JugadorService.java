package com.spfantasy.backend.service;

import com.spfantasy.backend.dto.JugadorDTO;
import com.spfantasy.backend.model.Jugador;
import com.spfantasy.backend.model.JugadorLiga;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.repository.JugadorLigaRepository;
import com.spfantasy.backend.repository.JugadorRepository;
import com.spfantasy.backend.repository.UsuarioRepository;

import com.spfantasy.backend.util.JugadorEstadisticasCalculator;

import io.jsonwebtoken.lang.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JugadorService {

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JugadorLigaService jugadorLigaService;

    @Autowired
    private JugadorLigaRepository jugadorLigaRepository;

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
                        jugador.getPropietario()))
                .collect(Collectors.toList());
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
                        jugador.getPropietario()))
                .collect(Collectors.toList());
    }

    public Jugador guardarJugador(Jugador jugador) {
        return jugadorRepository.save(jugador);
    }

    public void eliminarJugador(Long id) {
        jugadorRepository.deleteById(id);
    }

    public boolean venderJugador(String username, JugadorLiga jugador) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            Optional<JugadorLiga> jugadorEnPlantilla = usuario.getPlantilla().stream()
                    .filter(j -> j.getId().equals(jugador.getId()))
                    .findFirst();

            if (jugadorEnPlantilla.isPresent()) {
                JugadorLiga jugadorAEliminar = jugadorEnPlantilla.get(); // <- JugadorLiga es el tipo correcto

                usuario.getPlantilla().remove(jugadorAEliminar);
                usuario.setDinero(usuario.getDinero().add(jugadorAEliminar.getPrecioVenta()));

                usuarioRepository.save(usuario);

                // ✅ Marcar el jugador como disponible y SIN PROPIETARIO
                jugadorAEliminar.setDisponible(true);
                jugadorAEliminar.setPropietario(null); // ✅ Aquí se asegura que propietario sea NULL
                jugadorLigaRepository.save(jugadorAEliminar);

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
                        jugador.getPropietario()))
                .collect(Collectors.toList());
    }

    public void actualizarJugadorDesdeApi(Jugador jugadorActualizado) {
        // Guardas el jugador actualizado
        jugadorRepository.save(jugadorActualizado);

        // Y propagas los cambios
        jugadorLigaService.actualizarJugadoresLigaDesdeJugadorBase(jugadorActualizado);
    }

    public void recalcularEstadisticasParaTodos() {
        List<Jugador> jugadores = jugadorRepository.findAll();

        for (Jugador jugador : jugadores) {
            // 1. Recalcular estadísticas del jugador base
            JugadorEstadisticasCalculator.calcularEstadisticas(jugador);
            jugadorRepository.save(jugador);

            // 2. Propagar los datos a todos los JugadorLiga que dependen de este jugador
            jugadorLigaService.actualizarJugadoresLigaDesdeJugadorBase(jugador);
        }

        System.out.println(
                "✅ Estadísticas recalculadas para " + jugadores.size() + " jugadores y propagadas a jugadores_liga.");
    }

    public Map<String, Double> calcularMediasPorPosicion(String posicion) {
        List<Jugador> jugadores = jugadorRepository.findByPosicionIgnoreCase(posicion);

        if (jugadores.isEmpty()) {
            return Collections.emptyMap();
        }

        double promedioFp = jugadores.stream().mapToDouble(Jugador::getFp).average().orElse(0);
        double promedioMin = jugadores.stream().mapToDouble(Jugador::getMin).average().orElse(0);
        double promedioT2 = jugadores.stream().mapToDouble(Jugador::getT2).average().orElse(0);
        double promedioT3 = jugadores.stream().mapToDouble(Jugador::getT3).average().orElse(0);

        Map<String, Double> medias = new HashMap<>();
        medias.put("fp", promedioFp);
        medias.put("min", promedioMin);
        medias.put("t2", promedioT2);
        medias.put("t3", promedioT3);

        return medias;
    }

}
