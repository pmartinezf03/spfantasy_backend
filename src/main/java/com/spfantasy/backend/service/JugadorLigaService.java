package com.spfantasy.backend.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spfantasy.backend.model.Jugador;
import com.spfantasy.backend.model.JugadorLiga;
import com.spfantasy.backend.model.Liga;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.repository.JugadorLigaRepository;
import com.spfantasy.backend.repository.JugadorRepository;

import jakarta.transaction.Transactional;

@Service
public class JugadorLigaService {

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private JugadorLigaRepository jugadorLigaRepository;

    /**
     * Al crear una liga, clona todos los jugadores base a esa liga
     */
    @Transactional
    public void generarJugadoresParaLiga(Liga liga) {
        List<Jugador> jugadores = jugadorRepository.findAll();

        for (Jugador jugador : jugadores) {
            JugadorLiga jl = new JugadorLiga();
            jl.setJugadorBase(jugador);
            jl.setLiga(liga);
            jl.setPrecioVenta(jugador.getPrecioVenta());
            jl.setDisponible(true);
            jl.setEsTitular(true);
            jl.setPts(jugador.getPts());
            jl.setMin(jugador.getMin());
            jl.setT2(jugador.getT2());
            jl.setT3(jugador.getT3());
            jl.setTl(jugador.getTl());
            jl.setFp(jugador.getFp());
            jl.setPuntosTotales(jugador.getPuntosTotales());
            jl.setFotoUrl(jugador.getFotoUrl());

            jugadorLigaRepository.save(jl);
        }
    }

    /**
     * Reparte 10 jugadores sin propietario al usuario que se une a una liga
     */
    @Transactional
    public void repartirJugadoresIniciales(Usuario usuario, Liga liga) {
        List<JugadorLiga> disponibles = jugadorLigaRepository.findByLigaAndPropietarioIsNull(liga);

        Collections.shuffle(disponibles);
        List<JugadorLiga> seleccionados = disponibles.stream().limit(10).toList();

        for (JugadorLiga jugador : seleccionados) {
            jugador.setPropietario(usuario);
            jugador.setDisponible(false); // ✅ Marcar como no disponible
            jugador.setEsTitular(false); // o true, según tu lógica
        }

        jugadorLigaRepository.saveAll(seleccionados);
    }

    public List<JugadorLiga> obtenerDisponiblesDeLiga(Long ligaId) {
        return jugadorLigaRepository.findByLiga_IdAndPropietario_IdIsNull(ligaId);
    }

    public List<JugadorLiga> obtenerJugadoresDeUsuarioEnLiga(Long ligaId, Long usuarioId) {
        return jugadorLigaRepository.findByLiga_IdAndPropietario_Id(ligaId, usuarioId);
    }

    public List<JugadorLiga> obtenerTodosEnLiga(Long ligaId) {
        return jugadorLigaRepository.findByLiga_Id(ligaId); // sin filtrar por propietario
    }

}
