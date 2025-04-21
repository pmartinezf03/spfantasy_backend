package com.spfantasy.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spfantasy.backend.model.JugadorLiga;
import com.spfantasy.backend.model.Liga;
import com.spfantasy.backend.model.Oferta;

@Repository
public interface JugadorLigaRepository extends JpaRepository<JugadorLiga, Long> {
    List<JugadorLiga> findByLigaAndPropietarioIsNull(Liga liga);

    List<JugadorLiga> findByLiga_IdAndPropietario_Id(Long ligaId, Long propietarioId);

    List<JugadorLiga> findByLiga_IdAndPropietario_IdIsNull(Long ligaId);

    List<JugadorLiga> findByLiga_Id(Long ligaId);

    @Query("""
              SELECT o FROM Oferta o
              WHERE o.estado = 'ACEPTADA' AND o.liga.id = :ligaId
                AND (o.comprador.id = :usuarioId OR o.vendedor.id = :usuarioId)
            """)
    List<Oferta> findHistorialByUsuarioAndLiga(@Param("usuarioId") Long usuarioId, @Param("ligaId") Long ligaId);

    Optional<JugadorLiga> findByJugadorBase_IdAndLiga_Id(Long jugadorBaseId, Long ligaId);

}
