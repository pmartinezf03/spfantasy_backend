package com.spfantasy.backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spfantasy.backend.dto.HistorialTransaccionDTO;
import com.spfantasy.backend.dto.JugadorLigaDTO;
import com.spfantasy.backend.model.Jugador;
import com.spfantasy.backend.model.JugadorLiga;
import com.spfantasy.backend.model.Liga;
import com.spfantasy.backend.model.Oferta;
import com.spfantasy.backend.model.Oferta.EstadoOferta;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.repository.JugadorLigaRepository;
import com.spfantasy.backend.repository.JugadorRepository;
import com.spfantasy.backend.repository.OfertaRepository;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

@Service
public class JugadorLigaService {

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private JugadorLigaRepository jugadorLigaRepository;

    @Autowired
    private OfertaRepository ofertaRepository;

    /**
     * Al crear una liga, clona todos los jugadores base a esa liga
     */
    @Transactional
    public void generarJugadoresParaLiga(Liga liga) {
        List<Jugador> jugadores = jugadorRepository.findAll();

        for (Jugador jugador : jugadores) {
            JugadorLiga jl = new JugadorLiga();
            jl.setJugadorBase(jugador);
            jl.setLiga(liga);
            jl.setPrecioVenta(jugador.getPrecioVenta());
            jl.setDisponible(true);
            jl.setEsTitular(true);
            jl.setPts(jugador.getPts());
            jl.setMin(jugador.getMin());
            jl.setT2(jugador.getT2());
            jl.setT3(jugador.getT3());
            jl.setTl(jugador.getTl());
            jl.setFp(jugador.getFp());
            jl.setPuntosTotales(jugador.getPuntosTotales());
            jl.setFotoUrl(jugador.getFotoUrl());

            jugadorLigaRepository.save(jl);
        }
    }

    /**
     * Reparte 10 jugadores sin propietario al usuario que se une a una liga
     */
    @Transactional
    public void repartirJugadoresIniciales(Usuario usuario, Liga liga) {
        List<JugadorLiga> disponibles = jugadorLigaRepository.findByLigaAndPropietarioIsNull(liga);

        Collections.shuffle(disponibles);
        List<JugadorLiga> seleccionados = disponibles.stream().limit(10).toList();

        for (JugadorLiga jugador : seleccionados) {
            jugador.setPropietario(usuario);
            jugador.setDisponible(false); // ✅ Marcar como no disponible
            jugador.setEsTitular(false); // o true, según tu lógica
        }

        jugadorLigaRepository.saveAll(seleccionados);
    }

    public List<JugadorLiga> obtenerDisponiblesDeLiga(Long ligaId) {
        return jugadorLigaRepository.findByLiga_IdAndPropietario_IdIsNull(ligaId);
    }

    public List<JugadorLiga> obtenerJugadoresDeUsuarioEnLiga(Long ligaId, Long usuarioId) {
        return jugadorLigaRepository.findByLiga_IdAndPropietario_Id(ligaId, usuarioId);
    }

    public List<JugadorLiga> obtenerTodosEnLiga(Long ligaId) {
        return jugadorLigaRepository.findByLiga_Id(ligaId); // sin filtrar por propietario
    }

    public List<HistorialTransaccionDTO> obtenerHistorialTransacciones(Long usuarioId, Long ligaId) {
        List<Oferta> ofertas = ofertaRepository.findByLiga_IdAndEstado(ligaId, EstadoOferta.ACEPTADA);
        Map<Long, HistorialTransaccionDTO> mapa = new HashMap<>();

        for (Oferta oferta : ofertas) {
            Long jugadorId = oferta.getJugador().getId();
            String nombreJugador = oferta.getJugador().getJugadorBase().getNombre();
            String fotoUrl = oferta.getJugador().getFotoUrl();

            // Si ya existe, reutiliza. Si no, crea DTO nuevo.
            HistorialTransaccionDTO dto = mapa.getOrDefault(jugadorId, new HistorialTransaccionDTO(
                    nombreJugador, fotoUrl, null, null, null, null, null, null, null));

            // ✅ Si fue comprador, solo asigna si los datos aún están vacíos
            if (oferta.getComprador().getId().equals(usuarioId)) {
                if (dto.fechaCompra == null) {
                    dto.fechaCompra = oferta.getTimestamp();
                    dto.precioCompra = oferta.getMontoOferta().intValue();
                    dto.compradoA = oferta.getVendedor() != null ? oferta.getVendedor().getUsername() : "Mercado libre";
                }
            }

            // ✅ Si fue vendedor, agrega datos sin borrar lo anterior
            if (oferta.getVendedor() != null && oferta.getVendedor().getId().equals(usuarioId)) {
                dto.fechaVenta = oferta.getTimestamp();
                dto.precioVenta = oferta.getMontoOferta().intValue();
                dto.vendidoA = oferta.getComprador().getUsername();
            }

            // ✅ Si hay ambos precios, calcula ganancia
            if (dto.precioCompra != null && dto.precioVenta != null) {
                dto.ganancia = dto.precioVenta - dto.precioCompra;
            }

            mapa.put(jugadorId, dto);
        }

        // Añadir compras directas del mercado libre que no estén en ofertas
        List<JugadorLiga> jugadoresComprados = jugadorLigaRepository.findByLiga_IdAndPropietario_Id(ligaId, usuarioId);

        for (JugadorLiga jugador : jugadoresComprados) {
            Long jugadorId = jugador.getId();
            if (!mapa.containsKey(jugadorId)) {
                HistorialTransaccionDTO dto = new HistorialTransaccionDTO(
                        jugador.getJugadorBase().getNombre(),
                        jugador.getFotoUrl(),
                        jugador.getFechaAdquisicion(), // ✅ Fecha de adquisición en compras libres
                        jugador.getPrecioVenta() != null ? jugador.getPrecioVenta().intValue() : 0,
                        "Mercado libre",
                        null,
                        null,
                        null,
                        null);
                mapa.put(jugadorId, dto);
            } else {
                HistorialTransaccionDTO dto = mapa.get(jugadorId);
                if (dto.fechaCompra == null) {
                    dto.fechaCompra = jugador.getFechaAdquisicion();
                    dto.precioCompra = jugador.getPrecioVenta() != null ? jugador.getPrecioVenta().intValue() : 0;
                    dto.compradoA = "Mercado libre";
                }
            }
        }

        return new ArrayList<>(mapa.values());
    }

    public HistorialTransaccionDTO registrarCompraDirecta(JugadorLiga jugador, Usuario comprador) {
        return new HistorialTransaccionDTO(
                jugador.getJugadorBase().getNombre(),
                jugador.getFotoUrl(),
                LocalDateTime.now(),
                jugador.getPrecioVenta().intValue(),
                "Mercado libre",
                null,
                null,
                null,
                null);
    }

    private JugadorLigaDTO convertirADTO(JugadorLiga jugadorLiga) {
        return new JugadorLigaDTO(jugadorLiga);
    }

    public List<JugadorLigaDTO> obtenerJugadoresDestacados(Long ligaId) {
        Pageable topCinco = PageRequest.of(0, 5);
        List<JugadorLiga> top = jugadorLigaRepository.findTopByLiga_IdOrderByFpDesc(ligaId, topCinco);
        return top.stream().map(this::convertirADTO).toList();
    }

    public void recalcularEstadisticasJugadorLiga(JugadorLiga j) {
        double puntos = calcularPuntosTotales(j);
        double rendimiento = calcularRendimiento(j);
        BigDecimal precio = calcularPrecioVenta(puntos, rendimiento);

        j.setPuntosTotales((int) Math.round(puntos));
        j.setRendimiento(BigDecimal.valueOf(rendimiento));
        j.setPrecioVenta(precio);
    }

    public void actualizarJugadoresLigaDesdeJugadorBase(Jugador jugadorBase) {
        List<JugadorLiga> jugadoresLiga = jugadorLigaRepository.findByJugadorBase_Id(jugadorBase.getId());

        for (JugadorLiga j : jugadoresLiga) {
            j.setPts(jugadorBase.getPts());
            j.setMin(jugadorBase.getMin());
            j.setTl(jugadorBase.getTl());
            j.setT2(jugadorBase.getT2());
            j.setT3(jugadorBase.getT3());
            j.setFp(jugadorBase.getFp());
            j.setPuntosTotales(jugadorBase.getPuntosTotales());
            j.setRendimiento(jugadorBase.getRendimiento());
            j.setPrecioVenta(jugadorBase.getPrecioVenta());
        }

        jugadorLigaRepository.saveAll(jugadoresLiga);
    }

    public void actualizarTodosLosJugadoresLigaDesdeTodosLosJugadorBase() {
        List<Jugador> jugadoresBase = jugadorRepository.findAll();

        for (Jugador jugador : jugadoresBase) {
            actualizarJugadoresLigaDesdeJugadorBase(jugador);
        }
    }

    private int calcularPuntosTotales(JugadorLiga jugador) {
        int pts = jugador.getPts() != null ? jugador.getPts() : 0;
        int t3 = jugador.getT3() != null ? jugador.getT3() : 0;
        int t2 = jugador.getT2() != null ? jugador.getT2() : 0;
        int tl = jugador.getTl() != null ? jugador.getTl() : 0;
        int fp = jugador.getFp() != null ? jugador.getFp() : 0;
        return (int) Math.round(pts * 1.2 + t3 * 3 + t2 * 2 + tl + fp * 1.5);
    }

    private double calcularRendimiento(JugadorLiga jugador) {
        int pts = jugador.getPts() != null ? jugador.getPts() : 0;
        int min = jugador.getMin() != null ? jugador.getMin() : 0;
        int tl = jugador.getTl() != null ? jugador.getTl() : 0;
        int t2 = jugador.getT2() != null ? jugador.getT2() : 0;
        int t3 = jugador.getT3() != null ? jugador.getT3() : 0;
        int fp = jugador.getFp() != null ? jugador.getFp() : 0;

        double rendimiento = 0.0;
        rendimiento += pts * 0.4;
        rendimiento += fp * 0.2;
        rendimiento += min * 0.15;
        rendimiento += (t2 + t3 + tl) * 0.25;
        return Math.min(rendimiento / 10.0, 10.0);
    }

    private BigDecimal calcularPrecioVenta(double rendimiento, double multiplicador) {
        BigDecimal precio = BigDecimal.valueOf(0.5 + rendimiento / 10 * 9.5)
                .multiply(BigDecimal.valueOf(1_000_000))
                .multiply(BigDecimal.valueOf(multiplicador));
        return precio.setScale(2, RoundingMode.HALF_UP);
    }

}
