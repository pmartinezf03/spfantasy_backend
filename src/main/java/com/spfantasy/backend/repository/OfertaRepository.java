package com.spfantasy.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spfantasy.backend.model.Oferta;
import com.spfantasy.backend.model.Oferta.EstadoOferta;

@Repository
public interface OfertaRepository extends JpaRepository<Oferta, Long> {

  @Query("SELECT o FROM Oferta o JOIN FETCH o.jugador j WHERE o.vendedor.id = :vendedorId")
  List<Oferta> findByVendedorId(@Param("vendedorId") Long vendedorId);

  @Query("SELECT o FROM Oferta o JOIN FETCH o.jugador j WHERE o.comprador.id = :compradorId")
  List<Oferta> findByCompradorId(@Param("compradorId") Long compradorId);

  List<Oferta> findByVendedorIdAndLeidaPorVendedorFalse(Long vendedorId);

  List<Oferta> findByVendedor_IdAndLiga_Id(Long vendedorId, Long ligaId);

  List<Oferta> findByComprador_IdAndLiga_Id(Long compradorId, Long ligaId);

  List<Oferta> findByVendedor_IdAndLeidaPorVendedorFalse(Long vendedorId);

  Optional<Oferta> findTopByCompradorIdAndJugadorLigaIdAndLigaIdOrderByTimestampDesc(Long compradorId,
      Long jugadorLigaId, Long ligaId);

  List<Oferta> findByLiga_IdAndEstado(Long ligaId, EstadoOferta estado);

  @Query("""
        SELECT o FROM Oferta o
        WHERE o.estado = 'ACEPTADA'
          AND o.liga.id = :ligaId
          AND (o.comprador.id = :usuarioId OR o.vendedor.id = :usuarioId)
        ORDER BY o.jugador.id, o.timestamp
      """)
  List<Oferta> findHistorialTransacciones(@Param("usuarioId") Long usuarioId, @Param("ligaId") Long ligaId);

}
