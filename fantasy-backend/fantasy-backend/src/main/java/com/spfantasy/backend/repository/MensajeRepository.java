package com.spfantasy.backend.repository;

import com.spfantasy.backend.model.Mensaje;
import com.spfantasy.backend.model.GrupoChat;
import com.spfantasy.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    // 🔥 Contar mensajes en un grupo
    long countByGrupo(GrupoChat grupo);

    // 🔥 Contar mensajes entre dos usuarios
    @Query("SELECT COUNT(m) FROM Mensaje m WHERE (m.remitente = :usuario1 AND m.destinatario = :usuario2) " +
           "OR (m.remitente = :usuario2 AND m.destinatario = :usuario1)")
    long countByUsuarios(Usuario usuario1, Usuario usuario2);

    // ✅ Obtener los últimos 500 mensajes de un grupo
    List<Mensaje> findTop500ByGrupoOrderByTimestampDesc(GrupoChat grupo);

    // ✅ Obtener los últimos 500 mensajes entre dos usuarios
    List<Mensaje> findTop500ByRemitenteAndDestinatarioOrDestinatarioAndRemitenteOrderByTimestampDesc(
            Usuario remitente1, Usuario destinatario1, Usuario remitente2, Usuario destinatario2);

    // 🔥 Eliminar el mensaje más antiguo en un grupo
    @Transactional
    @Modifying
    @Query("DELETE FROM Mensaje m WHERE m.id = (SELECT m2.id FROM Mensaje m2 WHERE m2.grupo = :grupo ORDER BY m2.timestamp ASC LIMIT 1)")
    void eliminarMasAntiguoDeGrupo(GrupoChat grupo);

    // 🔥 Eliminar el mensaje más antiguo entre dos usuarios
    @Transactional
    @Modifying
    @Query("DELETE FROM Mensaje m WHERE m.id = (SELECT m2.id FROM Mensaje m2 WHERE " +
           "(m2.remitente = :usuario1 AND m2.destinatario = :usuario2) OR " +
           "(m2.remitente = :usuario2 AND m2.destinatario = :usuario1) " +
           "ORDER BY m2.timestamp ASC LIMIT 1)")
    void eliminarMasAntiguoEntreUsuarios(Usuario usuario1, Usuario usuario2);
}
