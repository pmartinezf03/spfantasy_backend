package com.spfantasy.backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {

    public Usuario(Long id) {
        this.id = id;
    }

    public Usuario() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.usuario;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal dinero = BigDecimal.valueOf(3000000);
    @Column(name = "puntos")
    private int puntos;

    @Column(name = "dinero_pendiente", nullable = false)
    private BigDecimal dineroPendiente = BigDecimal.ZERO;

    @OneToMany(mappedBy = "propietario", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnore
    private List<JugadorLiga> plantilla;

    @ManyToMany(mappedBy = "usuarios", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnore
    private List<GrupoChat> grupos = new ArrayList<>();

    @Column(name = "nivel_felicitado")
    private Integer nivelFelicitado;

    @Column(name = "fecha_felicitacion_racha")
    private LocalDate fechaFelicitacionRacha;

    public Integer getNivelFelicitado() {
        return nivelFelicitado;
    }

    public void setNivelFelicitado(Integer nivelFelicitado) {
        this.nivelFelicitado = nivelFelicitado;
    }

    public LocalDate getFechaFelicitacionRacha() {
        return fechaFelicitacionRacha;
    }

    public void setFechaFelicitacionRacha(LocalDate fechaFelicitacionRacha) {
        this.fechaFelicitacionRacha = fechaFelicitacionRacha;
    }

    public byte[] getAvatarData() {
        return avatarData;
    }

    public void setAvatarData(byte[] avatarData) {
        this.avatarData = avatarData;
    }

    @Column(unique = true)
    private String alias;

    @Column(name = "vip_hasta")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime vipHasta;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsuarioLogro> logrosDesbloqueados = new ArrayList<>();

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Lob
    @Column(name = "avatar_data")
    private byte[] avatarData;

    @Lob
    @Column(name = "avatar_bin", columnDefinition = "LONGBLOB")
    private byte[] avatarBytes;

    public Boolean getTutorialVisto() {
        return tutorialVisto;
    }

    public Integer getPreguntasIaUsadas() {
        return preguntasIaUsadas != null ? preguntasIaUsadas : 0;
    }

    public void setPreguntasIaUsadas(Integer preguntasIaUsadas) {
        this.preguntasIaUsadas = preguntasIaUsadas != null ? preguntasIaUsadas : 0;
    }

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;

    @Column(name = "tutorial_visto")
    private Boolean tutorialVisto = false;

    // Usuario.java
    @Column(name = "preguntas_ia_usadas")
    private Integer preguntasIaUsadas = 0;

    @Column(name = "ha_visto_sobres")
    private Boolean haVistoSobres = false;

    @ElementCollection
    @Column(name = "rachas_felicitadas")
    private List<Integer> rachasFelicitadas = new ArrayList<>();

    public Boolean getHaVistoSobres() {
        return haVistoSobres;
    }

    public void setHaVistoSobres(Boolean haVistoSobres) {
        this.haVistoSobres = haVistoSobres;
    }

    public LocalDateTime getUltimoLogin() {
        return ultimoLogin;
    }

    public void setUltimoLogin(LocalDateTime ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }

    public List<Integer> getRachasFelicitadas() {
        return rachasFelicitadas;
    }

    public void setRachasFelicitadas(List<Integer> rachasFelicitadas) {
        this.rachasFelicitadas = rachasFelicitadas;
    }

    private int compras;
    private int ventas;
    private int logins;
    private int sesiones;

    private int experiencia; // Para la barra de experiencia
    private int diasActivo;
    private int rachaLogin;
    private int partidasJugadas;
    private int nivel;

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public List<UsuarioLogro> getLogrosDesbloqueados() {
        return logrosDesbloqueados;
    }

    public void setLogrosDesbloqueados(List<UsuarioLogro> logrosDesbloqueados) {
        this.logrosDesbloqueados = logrosDesbloqueados;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @JsonIgnore
    public boolean isVip() {
        return vipHasta != null && vipHasta.isAfter(LocalDateTime.now());
    }

    public LocalDateTime getVipHasta() {
        return vipHasta;
    }

    public void setVipHasta(LocalDateTime vipHasta) {
        this.vipHasta = vipHasta;
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

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public List<GrupoChat> getGrupos() {
        return grupos;
    }

    public BigDecimal getDineroPendiente() {
        return dineroPendiente;
    }

    public void setDineroPendiente(BigDecimal dineroPendiente) {
        this.dineroPendiente = dineroPendiente;
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

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public BigDecimal getDinero() {
        return dinero;
    }

    public List<JugadorLiga> getPlantilla() {
        return plantilla;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setDinero(BigDecimal dinero) {
        this.dinero = dinero;
    }

    public void setGrupos(List<GrupoChat> grupos) {
        this.grupos = grupos;
    }

    public void setPlantilla(List<JugadorLiga> plantilla) {
        this.plantilla = plantilla;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public byte[] getAvatarBytes() {
        return avatarBytes;
    }

    public void setAvatarBytes(byte[] avatarBytes) {
        this.avatarBytes = avatarBytes;
    }

    public int getExperienciaParaSiguienteNivel() {
        return nivel * 10;
    }

    public int getExperienciaActualNivel() {
        int totalXp = 0;
        for (int i = 1; i < nivel; i++) {
            totalXp += i * 10;
        }
        return this.experiencia - totalXp;
    }

    public boolean isTutorialVisto() {
        return Boolean.TRUE.equals(this.tutorialVisto);
    }

    public void setTutorialVisto(Boolean tutorialVisto) {
        this.tutorialVisto = tutorialVisto;
    }

}
