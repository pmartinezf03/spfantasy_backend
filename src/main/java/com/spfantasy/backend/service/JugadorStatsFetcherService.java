package com.spfantasy.backend.service;

import com.spfantasy.backend.model.Jugador;
import com.spfantasy.backend.repository.JugadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JugadorStatsFetcherService {

    @Autowired
    private JugadorRepository jugadorRepository;

    public void actualizarDatosSimuladosParaTodosLosJugadores() {
        List<Jugador> jugadores = jugadorRepository.findAll();

        for (Jugador jugador : jugadores) {
            jugador.setPts((int) (Math.random() * 30)); // puntos
            jugador.setMin((int) (Math.random() * 40)); // minutos
            jugador.setT3((int) (Math.random() * 7)); // triples
            jugador.setT2((int) (Math.random() * 10)); // dobles
            jugador.setTl((int) (Math.random() * 8)); // tiros libres
            jugador.setFp((int) (Math.random() * 10)); // valoración fantasy
        }

        jugadorRepository.saveAll(jugadores);
        System.out.println(" Datos simulados actualizados en la tabla `jugadores`");
    }
}
