package com.spfantasy.backend.repository;

import com.spfantasy.backend.model.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;
import java.util.List;

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
