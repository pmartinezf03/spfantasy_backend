package com.spfantasy.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spfantasy.backend.dto.TransaccionDTO;
import com.spfantasy.backend.model.Transaccion;
import com.spfantasy.backend.repository.TransaccionRepository;

@Service
public class TransaccionService {

    @Autowired
    private TransaccionRepository transaccionRepository;

    public List<TransaccionDTO> obtenerTransaccionesPorUsuarioYLiga(Long usuarioId, Long ligaId) {
        List<Transaccion> transacciones = transaccionRepository.findByLigaIdAndUsuarioInvolucrado(usuarioId, ligaId);

        return transacciones.stream().map(t -> {
            String nombreJugador = t.getJugador() != null && t.getJugador().getJugadorBase() != null
                    ? t.getJugador().getJugadorBase().getNombre()
                    : "Desconocido";

            String fotoUrl = t.getJugador() != null
                    ? (t.getJugador().getFotoUrl() != null
                            ? t.getJugador().getFotoUrl()
                            : (t.getJugador().getJugadorBase() != null
                                    ? t.getJugador().getJugadorBase().getFotoUrl()
                                    : null))
                    : null;

            String compradorUsername = t.getComprador() != null ? t.getComprador().getUsername() : null;
            String vendedorUsername = t.getVendedor() != null ? t.getVendedor().getUsername() : "Mercado libre";

            Long ligaIdResult = t.getLiga() != null ? t.getLiga().getId() : null;
            String ligaNombre = t.getLiga() != null ? t.getLiga().getNombre() : null;

            return new TransaccionDTO(
                    t.getId(),
                    t.getFecha(),
                    t.getPrecio(),
                    nombreJugador,
                    fotoUrl,
                    compradorUsername,
                    vendedorUsername,
                    ligaIdResult,
                    ligaNombre);
        }).collect(Collectors.toList());
    }

    public Transaccion guardarTransaccion(Transaccion transaccion) {
        return transaccionRepository.save(transaccion);
    }

    public List<Transaccion> obtenerTodas() {
        return transaccionRepository.findAll();
    }

    private TransaccionDTO convertirADTO(Transaccion transaccion) {
        TransaccionDTO dto = new TransaccionDTO();
        dto.setId(transaccion.getId());
        dto.setFecha(transaccion.getFecha());
        dto.setPrecio(transaccion.getPrecio());

        if (transaccion.getJugador() != null) {
            dto.setNombreJugador(transaccion.getJugador().getJugadorBase().getNombre());
            dto.setFotoUrl(transaccion.getJugador().getFotoUrl());
        }

        if (transaccion.getComprador() != null) {
            dto.setCompradorUsername(transaccion.getComprador().getUsername());
        }

        if (transaccion.getVendedor() != null) {
            dto.setVendedorUsername(transaccion.getVendedor().getUsername());
        }

        if (transaccion.getLiga() != null) {
            dto.setLigaId(transaccion.getLiga().getId());
            dto.setLigaNombre(transaccion.getLiga().getNombre());
        }

        return dto;
    }

    public List<TransaccionDTO> obtenerTransaccionesPorLiga(Long ligaId) {
        List<Transaccion> transacciones = transaccionRepository.findByLigaIdOrderByFechaDesc(ligaId);
        return transacciones.stream()
                .map(this::convertirADTO)
                .toList();
    }

}
