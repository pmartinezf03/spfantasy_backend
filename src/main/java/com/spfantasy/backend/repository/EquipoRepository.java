package com.spfantasy.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.spfantasy.backend.model.Equipo;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {
}
