package com.spfantasy.backend.dto;

import java.time.LocalDateTime;

public class MiembroLigaDTO {
    private Long id;
    private String username;

    public LocalDateTime getUltimoLogin() {
        return ultimoLogin;
    }

    public void setUltimoLogin(LocalDateTime ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }

    private String email;
    private LocalDateTime ultimoLogin;

    public MiembroLigaDTO(Long id, String username, String email, LocalDateTime ultimoLogin) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.ultimoLogin = ultimoLogin;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

}
