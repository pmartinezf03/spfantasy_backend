package com.spfantasy.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import com.spfantasy.backend.model.JugadorLiga;
import com.spfantasy.backend.model.Liga;
import com.spfantasy.backend.model.Oferta;

@Repository
public interface JugadorLigaRepository extends JpaRepository<JugadorLiga, Long> {

    List<JugadorLiga> findByLigaAndPropietarioIsNull(Liga liga);

    List<JugadorLiga> findByLiga_IdAndPropietario_Id(Long ligaId, Long propietarioId);

    List<JugadorLiga> findByLiga_IdAndPropietario_IdIsNull(Long ligaId);

    List<JugadorLiga> findByLiga_Id(Long ligaId);

    Optional<JugadorLiga> findByJugadorBase_IdAndLiga_Id(Long jugadorBaseId, Long ligaId);

    List<JugadorLiga> findByLigaIdAndPropietarioId(Long ligaId, Long propietarioId);

    @Query("SELECT j FROM JugadorLiga j WHERE j.liga.id = :ligaId ORDER BY j.t3 DESC")
    List<JugadorLiga> findTopByLiga_IdOrderByT3Desc(@Param("ligaId") Long ligaId, Pageable pageable);

    @Query("SELECT j FROM JugadorLiga j WHERE j.liga.id = :ligaId ORDER BY j.fp DESC")
    List<JugadorLiga> findTopByLiga_IdOrderByFpDesc(@Param("ligaId") Long ligaId, Pageable pageable);

    @Query("SELECT j FROM JugadorLiga j WHERE j.liga.id = :ligaId ORDER BY j.precioVenta DESC")
    List<JugadorLiga> findTopByLiga_IdOrderByPrecioVentaDesc(@Param("ligaId") Long ligaId, Pageable pageable);

    @Query("SELECT j FROM JugadorLiga j WHERE j.liga.id = :ligaId ORDER BY j.min DESC")
    List<JugadorLiga> findTopByLiga_IdOrderByMinDesc(@Param("ligaId") Long ligaId, Pageable pageable);

    @Query("SELECT j FROM JugadorLiga j WHERE j.liga.id = :ligaId ORDER BY j.tl DESC")
    List<JugadorLiga> findTopByLiga_IdOrderByTlDesc(@Param("ligaId") Long ligaId, Pageable pageable);

    @Query("SELECT j FROM JugadorLiga j WHERE j.liga.id = :ligaId AND j.esTitular = true ORDER BY j.fp DESC")
    List<JugadorLiga> findTopByLiga_IdOrderByEsTitularTrueDesc(@Param("ligaId") Long ligaId, Pageable pageable);

    @Query("""
                SELECT o FROM Oferta o
                WHERE o.estado = 'ACEPTADA' AND o.liga.id = :ligaId
                  AND (o.comprador.id = :usuarioId OR o.vendedor.id = :usuarioId)
            """)
    List<Oferta> findHistorialByUsuarioAndLiga(@Param("usuarioId") Long usuarioId, @Param("ligaId") Long ligaId);
}
