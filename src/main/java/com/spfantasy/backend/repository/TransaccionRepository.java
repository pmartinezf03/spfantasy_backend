package com.spfantasy.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spfantasy.backend.model.Transaccion;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

        @Query("SELECT t FROM Transaccion t " +
                        "WHERE (t.comprador.id = :usuarioId OR t.vendedor.id = :usuarioId) " +
                        "AND t.liga.id = :ligaId " +
                        "ORDER BY t.fecha DESC")
        List<Transaccion> findByLigaIdAndUsuarioInvolucrado(
                        @Param("usuarioId") Long usuarioId,
                        @Param("ligaId") Long ligaId);

        List<Transaccion> findByLigaIdOrderByFechaDesc(Long ligaId);

}
