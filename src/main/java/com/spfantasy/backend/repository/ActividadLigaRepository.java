package com.spfantasy.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spfantasy.backend.model.ActividadLiga;

public interface ActividadLigaRepository extends JpaRepository<ActividadLiga, Long> {
    List<ActividadLiga> findTop10ByLigaIdOrderByTimestampDesc(Long ligaId);
}
