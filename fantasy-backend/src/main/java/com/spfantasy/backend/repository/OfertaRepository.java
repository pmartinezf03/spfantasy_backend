package com.spfantasy.backend.repository;

import com.spfantasy.backend.model.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OfertaRepository extends JpaRepository<Oferta, Long> {

    @Query("SELECT o FROM Oferta o JOIN FETCH o.jugador j WHERE o.vendedor.id = :vendedorId")
    List<Oferta> findByVendedorId(@Param("vendedorId") Long vendedorId);

    @Query("SELECT o FROM Oferta o JOIN FETCH o.jugador j WHERE o.comprador.id = :compradorId")
    List<Oferta> findByCompradorId(@Param("compradorId") Long compradorId);
    List<Oferta> findByVendedorIdAndLeidaPorVendedorFalse(Long vendedorId);

}
