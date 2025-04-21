package com.spfantasy.backend.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrupoChatDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Long creadorId;
    private Set<Long> usuariosIds;
}
