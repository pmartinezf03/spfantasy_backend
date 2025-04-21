package com.spfantasy.backend.repository;

import com.spfantasy.backend.model.GrupoChat;
import com.spfantasy.backend.model.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GrupoChatRepository extends JpaRepository<GrupoChat, Long> {
    Optional<GrupoChat> findByNombreAndPasswordGrupo(String nombre, String passwordGrupo);

    List<GrupoChat> findByUsuariosContaining(Usuario usuario);

    Optional<GrupoChat> findByNombre(String nombre);

}
