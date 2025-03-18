package com.spfantasy.backend.repository;

import com.spfantasy.backend.model.GrupoChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GrupoChatRepository extends JpaRepository<GrupoChat, Long> {
Optional<GrupoChat> findByNombreAndPasswordGrupo(String nombre, String passwordGrupo);
}
