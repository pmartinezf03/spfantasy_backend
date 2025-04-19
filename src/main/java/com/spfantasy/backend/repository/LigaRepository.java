package com.spfantasy.backend.repository;

import com.spfantasy.backend.model.Liga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LigaRepository extends JpaRepository<Liga, Long> {
    Optional<Liga> findByCodigoInvitacion(String codigoInvitacion);
}
