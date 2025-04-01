package com.spfantasy.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.spfantasy.backend.model.Jugador;

public interface JugadorRepository extends JpaRepository<Jugador, Long> {
    List<Jugador> findAll();

    // 🔥 Método para obtener jugadores disponibles en el mercado
    List<Jugador> findByDisponibleTrue();

    // ✅ Nuevo método para liberar un jugador, marcándolo como disponible y eliminando su propietario
    @Modifying
    @Transactional
    @Query("UPDATE Jugador j SET j.propietario = NULL, j.disponible = true WHERE j.id = :jugadorId")
    void liberarJugador(@Param("jugadorId") Long jugadorId);
}
