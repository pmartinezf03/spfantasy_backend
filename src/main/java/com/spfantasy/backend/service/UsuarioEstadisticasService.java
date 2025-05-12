package com.spfantasy.backend.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spfantasy.backend.repository.UsuarioRepository;

@Service
public class UsuarioEstadisticasService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public void registrarLogin(Long usuarioId) {
        usuarioRepository.findById(usuarioId).ifPresent(usuario -> {
            usuario.setLogins(usuario.getLogins() + 1);
            usuario.setSesiones(usuario.getSesiones() + 1);

            // Actualizar racha
            LocalDateTime ultimoLogin = usuario.getVipHasta(); // usa otro campo real si ya tienes `ultimoLogin`
            LocalDateTime ahora = LocalDateTime.now();

            if (ultimoLogin != null && ultimoLogin.toLocalDate().plusDays(1).equals(ahora.toLocalDate())) {
                usuario.setRachaLogin(usuario.getRachaLogin() + 1);
            } else if (ultimoLogin == null || !ultimoLogin.toLocalDate().equals(ahora.toLocalDate())) {
                usuario.setRachaLogin(1);
            }

            usuario.setDiasActivo(usuario.getDiasActivo() + 1); // o contar días únicos desde historial real

            // Aumentar experiencia por login
            usuario.setExperiencia(usuario.getExperiencia() + 10);

            usuarioRepository.save(usuario);
        });
    }

    public void registrarCompra(Long usuarioId) {
        usuarioRepository.findById(usuarioId).ifPresent(usuario -> {
            usuario.setCompras(usuario.getCompras() + 1);
            usuario.setExperiencia(usuario.getExperiencia() + 15);
            usuarioRepository.save(usuario);
        });
    }

    public void registrarVenta(Long usuarioId) {
        usuarioRepository.findById(usuarioId).ifPresent(usuario -> {
            usuario.setVentas(usuario.getVentas() + 1);
            usuario.setExperiencia(usuario.getExperiencia() + 10);
            usuarioRepository.save(usuario);
        });
    }

    public void registrarPartida(Long usuarioId) {
        usuarioRepository.findById(usuarioId).ifPresent(usuario -> {
            usuario.setPartidasJugadas(usuario.getPartidasJugadas() + 1);
            usuario.setExperiencia(usuario.getExperiencia() + 25);
            usuarioRepository.save(usuario);
        });
    }
}
