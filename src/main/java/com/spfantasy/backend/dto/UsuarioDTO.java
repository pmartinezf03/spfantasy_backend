package com.spfantasy.backend.dto;

import java.math.BigDecimal;

import com.spfantasy.backend.model.Usuario;


public class UsuarioDTO {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public BigDecimal getDinero() {
        return dinero;
    }

    public void setDinero(BigDecimal dinero) {
        this.dinero = dinero;
    }

    public BigDecimal getDineroPendiente() {
        return dineroPendiente;
    }

    public void setDineroPendiente(BigDecimal dineroPendiente) {
        this.dineroPendiente = dineroPendiente;
    }
    

    public UsuarioDTO() {
    }

    public UsuarioDTO(Long id, String username, String role, BigDecimal dinero, BigDecimal dineroPendiente) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.dinero = dinero;
        this.dineroPendiente = dineroPendiente;
    }


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
