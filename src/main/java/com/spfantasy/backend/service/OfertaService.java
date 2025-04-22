package com.spfantasy.backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.spfantasy.backend.model.Oferta.EstadoOferta;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spfantasy.backend.model.JugadorLiga;
import com.spfantasy.backend.model.Oferta;
import com.spfantasy.backend.model.Transaccion;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.repository.JugadorLigaRepository;
import com.spfantasy.backend.repository.OfertaRepository;
import com.spfantasy.backend.repository.UsuarioRepository;
import com.spfantasy.backend.repository.TransaccionRepository;

@Service
public class OfertaService {

    private final OfertaRepository ofertaRepository;
    private final UsuarioRepository usuarioRepository;
    private final JugadorLigaRepository jugadorLigaRepository;
    private final TransaccionRepository transaccionRepository;

    public OfertaService(OfertaRepository ofertaRepository,
            UsuarioRepository usuarioRepository,
            JugadorLigaRepository jugadorLigaRepository,
            TransaccionRepository transaccionRepository) {
        this.ofertaRepository = ofertaRepository;
        this.usuarioRepository = usuarioRepository;
        this.jugadorLigaRepository = jugadorLigaRepository;
        this.transaccionRepository = transaccionRepository;

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

        // ✅ En lugar de borrar, actualiza el estado
        oferta.setEstado(Oferta.EstadoOferta.RECHAZADA); // o RETIRADA si prefieres

        usuarioRepository.save(comprador);
        ofertaRepository.save(oferta);
    }

    @Transactional
    public void aceptarOferta(Long id) {
        Oferta oferta = ofertaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Oferta no encontrada"));

        Usuario comprador = oferta.getComprador();
        Usuario vendedor = oferta.getVendedor();
        JugadorLiga jugador = oferta.getJugador();

        BigDecimal monto = oferta.getMontoOferta();

        // El dinero ya fue bloqueado, solo liberamos el pendiente
        comprador.setDineroPendiente(comprador.getDineroPendiente().subtract(monto));

        // Realizamos la transferencia al vendedor
        vendedor.setDinero(vendedor.getDinero().add(monto));

        jugador.setPropietario(comprador);
        jugador.setDisponible(false);
        jugador.setFechaAdquisicion(LocalDateTime.now());
        oferta.setEstado(Oferta.EstadoOferta.ACEPTADA);
        oferta.setTimestamp(LocalDateTime.now());

        usuarioRepository.save(comprador);
        usuarioRepository.save(vendedor);
        jugadorLigaRepository.save(jugador);
        ofertaRepository.save(oferta);

        // Registrar transacción
        Transaccion transaccion = new Transaccion();
        transaccion.setJugador(jugador);
        transaccion.setComprador(comprador);
        transaccion.setVendedor(vendedor);
        transaccion.setPrecio(monto.intValue());
        transaccion.setFecha(LocalDateTime.now());
        transaccion.setLiga(oferta.getLiga());

        transaccionRepository.save(transaccion);
    }

    @Transactional
    public void aceptarContraoferta(Oferta oferta) {
        Usuario nuevoComprador = oferta.getVendedor(); // El que acepta la contraoferta
        Usuario antiguoPropietario = oferta.getComprador(); // El que había hecho la oferta original
        JugadorLiga jugador = oferta.getJugador();

        if (!jugador.getPropietario().getId().equals(antiguoPropietario.getId())) {
            throw new RuntimeException("El jugador ya no pertenece al ofertante original.");
        }

        BigDecimal monto = oferta.getMontoOferta();

        if (nuevoComprador.getDinero().compareTo(monto) < 0) {
            throw new RuntimeException("Dinero insuficiente para aceptar la contraoferta.");
        }

        // 💰 Transferencia de dinero (no se toca dineroPendiente)
        nuevoComprador.setDinero(nuevoComprador.getDinero().subtract(monto));
        antiguoPropietario.setDinero(antiguoPropietario.getDinero().add(monto));

        // ⚽ Transferencia del jugador
        jugador.setPropietario(nuevoComprador);
        jugador.setEsTitular(false);
        jugador.setDisponible(false);
        jugador.setFechaAdquisicion(LocalDateTime.now());

        // 📦 Actualizar estado de la oferta
        oferta.setEstado(Oferta.EstadoOferta.ACEPTADA);
        oferta.setTimestamp(LocalDateTime.now());

        // 💾 Guardar cambios
        usuarioRepository.save(nuevoComprador);
        usuarioRepository.save(antiguoPropietario);
        jugadorLigaRepository.save(jugador);
        ofertaRepository.save(oferta);

        // 🧾 Registrar transacción
        Transaccion transaccion = new Transaccion();
        transaccion.setJugador(jugador);
        transaccion.setComprador(nuevoComprador);
        transaccion.setVendedor(antiguoPropietario);
        transaccion.setPrecio(monto.intValue());
        transaccion.setFecha(LocalDateTime.now());
        transaccion.setLiga(oferta.getLiga());

        transaccionRepository.save(transaccion);
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
