package com.spfantasy.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spfantasy.backend.model.UsuarioLogro;

import java.util.List;

public interface UsuarioLogroRepository extends JpaRepository<UsuarioLogro, Long> {
    List<UsuarioLogro> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioIdAndLogroId(Long usuarioId, Long logroId);
}
