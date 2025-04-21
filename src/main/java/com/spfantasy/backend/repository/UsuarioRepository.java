package com.spfantasy.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.spfantasy.backend.model.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    // 🔥 Nueva consulta para obtener usuario y su plantilla de jugadores
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.plantilla WHERE u.username = :username")
    Optional<Usuario> findByUsernameWithPlantilla(@Param("username") String username);

    Optional<Usuario> findByAlias(String alias);

}
