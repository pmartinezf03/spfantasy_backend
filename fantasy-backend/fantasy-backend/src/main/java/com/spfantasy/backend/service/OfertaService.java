package com.spfantasy.backend.service;

import com.spfantasy.backend.model.Jugador;
import com.spfantasy.backend.model.Oferta;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.repository.OfertaRepository;
import com.spfantasy.backend.repository.UsuarioRepository;
import com.spfantasy.backend.repository.JugadorRepository;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class OfertaService {

    private final OfertaRepository ofertaRepository;
    private final UsuarioRepository usuarioRepository;
    private final JugadorRepository jugadorRepository;

    public OfertaService(OfertaRepository ofertaRepository, UsuarioRepository usuarioRepository, JugadorRepository jugadorRepository) {
        this.ofertaRepository = ofertaRepository;
        this.usuarioRepository = usuarioRepository;
        this.jugadorRepository = jugadorRepository;
    }

    public Oferta crearOferta(Oferta oferta) {
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

    public void eliminarOferta(Long id) {
        ofertaRepository.deleteById(id);
    }

    @Transactional
    public void aceptarOferta(Long id) {
        Oferta oferta = ofertaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Oferta no encontrada"));

        Usuario comprador = oferta.getComprador();
        Usuario vendedor = oferta.getVendedor();
        Jugador jugador = oferta.getJugador();

        // Remover el jugador de la plantilla del vendedor
        vendedor.getPlantilla().remove(jugador);

        // Agregar el jugador a la plantilla del comprador
        comprador.getPlantilla().add(jugador);
        jugador.setPropietario(comprador);
        jugador.setDisponible(false);

        // Transferencia de dinero
        comprador.setDinero(comprador.getDinero().subtract(oferta.getMontoOferta()));
        vendedor.setDinero(vendedor.getDinero().add(oferta.getMontoOferta()));

        // Guardar cambios en BD
        usuarioRepository.save(vendedor);
        usuarioRepository.save(comprador);
        jugadorRepository.save(jugador);
        ofertaRepository.delete(oferta);
    }

    @Transactional
    public Oferta hacerContraoferta(Long ofertaId, BigDecimal nuevoMonto) {
        Oferta oferta = ofertaRepository.findById(ofertaId)
                .orElseThrow(() -> new RuntimeException("Oferta no encontrada"));

        oferta.setMontoOferta(nuevoMonto);
        oferta.setEstado(Oferta.EstadoOferta.CONTRAOFERTA);

        return ofertaRepository.save(oferta);
    }

}
