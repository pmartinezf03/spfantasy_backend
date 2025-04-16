package com.spfantasy.backend.dto;

import java.math.BigDecimal;

import com.spfantasy.backend.model.Usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String username;
    private String role;
    private BigDecimal dinero;
    private BigDecimal dineroPendiente;

    public UsuarioDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.username = usuario.getUsername();
        this.role = usuario.getRole().name();
        this.dinero = usuario.getDinero();
        this.dineroPendiente = usuario.getDineroPendiente();
    }
}
