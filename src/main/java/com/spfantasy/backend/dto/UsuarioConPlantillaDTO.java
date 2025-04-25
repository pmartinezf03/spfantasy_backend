package com.spfantasy.backend.dto;

import java.math.BigDecimal;
import java.util.List;

import com.spfantasy.backend.dto.JugadorDTO;

public class UsuarioConPlantillaDTO {
    private String username;
    private String email; // <-- Añadir
    private String role; // <-- Añadir
    private BigDecimal dinero; // <-- Mejor usar BigDecimal
    private List<JugadorDTO> titulares;
    private List<JugadorDTO> suplentes;
    private String alias;

    public UsuarioConPlantillaDTO(String username, String email, String role, BigDecimal dinero,
            List<JugadorDTO> titulares, List<JugadorDTO> suplentes) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.dinero = dinero;
        this.titulares = titulares;
        this.suplentes = suplentes;
    }

    public UsuarioConPlantillaDTO() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public List<JugadorDTO> getTitulares() {
        return titulares;
    }

    public void setTitulares(List<JugadorDTO> titulares) {
        this.titulares = titulares;
    }

    public List<JugadorDTO> getSuplentes() {
        return suplentes;
    }

    public void setSuplentes(List<JugadorDTO> suplentes) {
        this.suplentes = suplentes;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
