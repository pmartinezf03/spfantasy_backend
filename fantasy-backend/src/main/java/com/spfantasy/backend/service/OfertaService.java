package com.spfantasy.backend.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spfantasy.backend.model.JugadorLiga;
import com.spfantasy.backend.model.Oferta;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.repository.JugadorLigaRepository;
import com.spfantasy.backend.repository.OfertaRepository;
import com.spfantasy.backend.repository.UsuarioRepository;

@Service
public class OfertaService {

    private final OfertaRepository ofertaRepository;
    private final UsuarioRepository usuarioRepository;
    private final JugadorLigaRepository jugadorLigaRepository;

    public OfertaService(OfertaRepository ofertaRepository,
            UsuarioRepository usuarioRepository,
            JugadorLigaRepository jugadorLigaRepository) {
        this.ofertaRepository = ofertaRepository;
        this.usuarioRepository = usuarioRepository;
        this.jugadorLigaRepository = jugadorLigaRepository;
    }

    @Transactional
    public Oferta crearOferta(Oferta oferta) {
        Usuario comprador = usuarioRepository.findById(oferta.getComprador().getId())
                .orElseThrow(() -> new RuntimeException("Comprador no encontrado"));

        BigDecimal totalDisponible = comprador.getDinero().subtract(comprador.getDineroPendiente());

        if (totalDisponible.compareTo(oferta.getMontoOferta()) < 0) {
            throw new RuntimeException("No tienes suficiente dinero disponible para esta oferta.");
        }

        // No se descuenta, solo se reserva
        comprador.setDineroPendiente(comprador.getDineroPendiente().add(oferta.getMontoOferta()));
        usuarioRepository.save(comprador);

        return ofertaRepository.save(oferta);
    }

    public Optional<Oferta> obtenerOfertaPorId(Long id) {
        return ofertaRepository.findById(id);
    }

    public List<Oferta> obtenerOfertasPorVendedor(Long vendedorId) {
        return ofertaRepository.findByVendedorId(vendedorId);
    }

    public List<Oferta> obtenerOfertasPorComprador(Long compradorId) {
        return ofertaRepository.findByCompradorId(compradorId);
    }

    @Transactional
    public void eliminarOferta(Long id) {
        Oferta oferta = ofertaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Oferta no encontrada"));

        Usuario comprador = oferta.getComprador();
        comprador.setDineroPendiente(comprador.getDineroPendiente().subtract(oferta.getMontoOferta()));
        usuarioRepository.save(comprador);

        ofertaRepository.delete(oferta);
    }

    @Transactional
    public void aceptarOferta(Long id) {
        Oferta oferta = ofertaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Oferta no encontrada"));

        Usuario comprador = oferta.getComprador();
        Usuario vendedor = oferta.getVendedor();
        JugadorLiga jugador = oferta.getJugador();

        // Liberar dinero pendiente y restarlo de dinero real
        comprador.setDineroPendiente(comprador.getDineroPendiente().subtract(oferta.getMontoOferta()));
        comprador.setDinero(comprador.getDinero().subtract(oferta.getMontoOferta()));

        vendedor.setDinero(vendedor.getDinero().add(oferta.getMontoOferta()));

        jugador.setPropietario(comprador);
        jugador.setDisponible(false);

        usuarioRepository.save(comprador);
        usuarioRepository.save(vendedor);
        jugadorLigaRepository.save(jugador);
        ofertaRepository.delete(oferta);
    }

    @Transactional
    public Oferta crearContraoferta(Oferta contraoferta) {
        return ofertaRepository.save(contraoferta);
    }

    @Transactional
    public Oferta hacerContraoferta(Long ofertaId, BigDecimal nuevoMonto) {
        Oferta oferta = ofertaRepository.findById(ofertaId)
                .orElseThrow(() -> new RuntimeException("Oferta no encontrada"));

        oferta.setMontoOferta(nuevoMonto);
        oferta.setEstado(Oferta.EstadoOferta.CONTRAOFERTA);

        return ofertaRepository.save(oferta);
    }

    public boolean tieneOfertasNuevas(Long vendedorId) {
        return !ofertaRepository.findByVendedorIdAndLeidaPorVendedorFalse(vendedorId).isEmpty();
    }

    public List<Oferta> obtenerOfertasPorVendedorYLiga(Long vendedorId, Long ligaId) {
        return ofertaRepository.findByVendedor_IdAndLiga_Id(vendedorId, ligaId);
    }

    public List<Oferta> obtenerOfertasPorCompradorYLiga(Long compradorId, Long ligaId) {
        return ofertaRepository.findByComprador_IdAndLiga_Id(compradorId, ligaId);
    }

    @Transactional
    public void marcarOfertasComoLeidas(Long usuarioId) {
        List<Oferta> ofertas = ofertaRepository.findByVendedor_IdAndLeidaPorVendedorFalse(usuarioId);
        for (Oferta oferta : ofertas) {
            oferta.setLeidaPorVendedor(true);
        }
        ofertaRepository.saveAll(ofertas);
    }

    public Optional<Oferta> obtenerUltimaOferta(Long compradorId, Long jugadorLigaId, Long ligaId) {
        return ofertaRepository.findTopByCompradorIdAndJugadorLigaIdAndLigaIdOrderByTimestampDesc(
                compradorId, jugadorLigaId, ligaId);
    }

}
