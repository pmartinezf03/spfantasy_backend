package com.spfantasy.backend.dto;

import com.spfantasy.backend.model.Usuario;

public class UsuarioNivelDTO {
    private int experienciaTotal;
    private int nivel;
    private int experienciaActualNivel;
    private int experienciaParaSubir;
    private int porcentajeProgreso;

    public UsuarioNivelDTO(Usuario usuario) {
        this.experienciaTotal = usuario.getExperiencia();
        this.nivel = usuario.getNivel(); // nivel persistido real

        // 🔧 Nueva lógica segura
        this.experienciaParaSubir = nivel * 10;

        int acumulada = 0;
        for (int i = 1; i < nivel; i++)
            acumulada += i * 10;

        // ⚠️ Evita negativos al reiniciar
        this.experienciaActualNivel = Math.max(0, experienciaTotal - acumulada);

        // 💯 Porcentaje entre 0 y 100
        this.porcentajeProgreso = Math.min(100, (int) ((double) experienciaActualNivel / experienciaParaSubir * 100));
    }

    public int getExperienciaTotal() {
        return experienciaTotal;
    }

    public void setExperienciaTotal(int experienciaTotal) {
        this.experienciaTotal = experienciaTotal;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public int getExperienciaActualNivel() {
        return experienciaActualNivel;
    }

    public void setExperienciaActualNivel(int experienciaActualNivel) {
        this.experienciaActualNivel = experienciaActualNivel;
    }

    public int getExperienciaParaSubir() {
        return experienciaParaSubir;
    }

    public void setExperienciaParaSubir(int experienciaParaSubir) {
        this.experienciaParaSubir = experienciaParaSubir;
    }

    public int getPorcentajeProgreso() {
        return porcentajeProgreso;
    }

    public void setPorcentajeProgreso(int porcentajeProgreso) {
        this.porcentajeProgreso = porcentajeProgreso;
    }

}
