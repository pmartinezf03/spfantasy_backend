package com.spfantasy.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Base64;

import com.spfantasy.backend.model.Usuario;

public class UsuarioDTO {

    private Long id;
    private String username;
    private String role;
    private BigDecimal dinero;
    private BigDecimal dineroPendiente;
    private LocalDate vipHasta;
    private String avatarUrl;
    private int compras;
    private int ventas;
    private int puntos;
    private int logins;
    private int sesiones;

    private int experiencia;
    private int diasActivo;
    private int rachaLogin;
    private int partidasJugadas;

    public UsuarioDTO() {
    }

    public UsuarioDTO(Long id, String username, String role, BigDecimal dinero, BigDecimal dineroPendiente,
            LocalDate vipHasta) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.dinero = dinero;
        this.dineroPendiente = dineroPendiente;
        this.vipHasta = vipHasta;
    }

    public UsuarioDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.username = usuario.getUsername();
        this.role = usuario.getRole().name();
        this.dinero = usuario.getDinero();
        this.dineroPendiente = usuario.getDineroPendiente();
        this.vipHasta = usuario.getVipHasta() != null ? usuario.getVipHasta().toLocalDate() : null;
        this.avatarUrl = "/api/usuarios/" + usuario.getId() + "/avatar";
        this.compras = usuario.getCompras();
        this.ventas = usuario.getVentas();
        this.puntos = usuario.getPuntos();
        this.logins = usuario.getLogins();
        this.sesiones = usuario.getSesiones();
        this.experiencia = usuario.getExperiencia();
        this.diasActivo = usuario.getDiasActivo();
        this.rachaLogin = usuario.getRachaLogin();
        this.partidasJugadas = usuario.getPartidasJugadas();

    }

    public int getCompras() {
        return compras;
    }

    public void setCompras(int compras) {
        this.compras = compras;
    }

    public int getVentas() {
        return ventas;
    }

    public void setVentas(int ventas) {
        this.ventas = ventas;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public int getLogins() {
        return logins;
    }

    public void setLogins(int logins) {
        this.logins = logins;
    }

    public int getSesiones() {
        return sesiones;
    }

    public void setSesiones(int sesiones) {
        this.sesiones = sesiones;
    }

    public int getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(int experiencia) {
        this.experiencia = experiencia;
    }

    public int getDiasActivo() {
        return diasActivo;
    }

    public void setDiasActivo(int diasActivo) {
        this.diasActivo = diasActivo;
    }

    public int getRachaLogin() {
        return rachaLogin;
    }

    public void setRachaLogin(int rachaLogin) {
        this.rachaLogin = rachaLogin;
    }

    public int getPartidasJugadas() {
        return partidasJugadas;
    }

    public void setPartidasJugadas(int partidasJugadas) {
        this.partidasJugadas = partidasJugadas;
    }

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

    public LocalDate getVipHasta() {
        return vipHasta;
    }

    public void setVipHasta(LocalDate vipHasta) {
        this.vipHasta = vipHasta;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

}
