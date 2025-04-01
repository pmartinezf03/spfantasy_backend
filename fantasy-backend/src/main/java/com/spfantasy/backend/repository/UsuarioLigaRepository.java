package com.spfantasy.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spfantasy.backend.model.UsuarioLiga;

@Repository
public interface UsuarioLigaRepository extends JpaRepository<UsuarioLiga, Long> {

    boolean existsByUsuarioIdAndLigaId(Long usuarioId, Long ligaId);

    List<UsuarioLiga> findByLigaId(Long ligaId);

    void deleteByUsuarioIdAndLigaId(Long usuarioId, Long ligaId);

    Optional<UsuarioLiga> findByUsuarioIdAndLigaId(Long usuarioId, Long ligaId);

    Optional<UsuarioLiga> findByUsuarioId(Long usuarioId);

}
