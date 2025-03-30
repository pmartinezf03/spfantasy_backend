package com.spfantasy.backend.repository;

import com.spfantasy.backend.model.JugadorLiga;
import com.spfantasy.backend.model.Liga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JugadorLigaRepository extends JpaRepository<JugadorLiga, Long> {
    List<JugadorLiga> findByLigaAndPropietarioIsNull(Liga liga);
    List<JugadorLiga> findByLiga_IdAndPropietario_Id(Long ligaId, Long propietarioId);
    List<JugadorLiga> findByLiga_IdAndPropietario_IdIsNull(Long ligaId);

}
