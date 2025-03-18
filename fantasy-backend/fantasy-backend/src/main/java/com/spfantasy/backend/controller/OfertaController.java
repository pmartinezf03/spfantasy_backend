package com.spfantasy.backend.controller;

import com.spfantasy.backend.model.Jugador;
import com.spfantasy.backend.model.Oferta;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.repository.JugadorRepository;
import com.spfantasy.backend.service.OfertaService;
import com.spfantasy.backend.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/ofertas")
public class OfertaController {

    private static final Logger logger = LoggerFactory.getLogger(OfertaController.class);

    private final OfertaService ofertaService;
    private final UsuarioService usuarioService;
    private final JugadorRepository jugadorRepository;

    public OfertaController(OfertaService ofertaService, UsuarioService usuarioService, JugadorRepository jugadorRepository) {
        this.ofertaService = ofertaService;
        this.usuarioService = usuarioService;
        this.jugadorRepository = jugadorRepository;
    }

    @PostMapping
    public ResponseEntity<?> crearOferta(@RequestBody Oferta oferta, @RequestHeader("Authorization") String token) {
        logger.info("📥 Recibiendo oferta en el backend: {}", oferta);

        String username = usuarioService.obtenerUsernameDesdeToken(token.replace("Bearer ", ""));
        Usuario comprador = usuarioService.obtenerUsuarioPorUsername(username);

        // 🔍 Obtener el jugador desde la BD con su propietario
        Jugador jugador = jugadorRepository.findById(oferta.getJugador().getId())
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        logger.info("🟢 Jugador encontrado: {} | Propietario en BD: {}", jugador.getNombre(), jugador.getPropietario());

        // 🔴 Validaciones para evitar datos incorrectos
        if (jugador.getPropietario() == null) {
            logger.error("❌ Error: El jugador no tiene propietario.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El jugador no tiene propietario.");
        }

        if (oferta.getMontoOferta() == null || oferta.getMontoOferta().compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("❌ Error: Monto de oferta inválido: {}", oferta.getMontoOferta());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El monto de la oferta debe ser mayor a 0.");
        }

        // 🔴 Nueva validación: el comprador debe tener suficiente dinero
        if (comprador.getDinero().compareTo(oferta.getMontoOferta()) < 0) {
            logger.error("❌ Error: El comprador no tiene suficientes fondos. Disponible: {}, Necesario: {}",
                    comprador.getDinero(), oferta.getMontoOferta());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ No tienes suficiente dinero para realizar esta oferta.");
        }

        oferta.setComprador(comprador);
        oferta.setVendedor(jugador.getPropietario());
        oferta.setEstado(Oferta.EstadoOferta.PENDIENTE);
        oferta.setJugador(jugador);

        try {
            Oferta nuevaOferta = ofertaService.crearOferta(oferta);
            logger.info("✅ Oferta creada con éxito: {}", nuevaOferta);
            return ResponseEntity.ok(nuevaOferta);
        } catch (Exception e) {
            logger.error("❌ Error al guardar la oferta en la base de datos.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al guardar la oferta.");
        }
    }

    @GetMapping("/vendedor/{vendedorId}")
    public ResponseEntity<List<Oferta>> obtenerOfertasPorVendedor(@PathVariable Long vendedorId) {
        return ResponseEntity.ok(ofertaService.obtenerOfertasPorVendedor(vendedorId));
    }

    @GetMapping("/comprador/{compradorId}")
    public ResponseEntity<List<Oferta>> obtenerOfertasPorComprador(@PathVariable Long compradorId) {
        return ResponseEntity.ok(ofertaService.obtenerOfertasPorComprador(compradorId));
    }

    @PostMapping("/aceptar/{id}")
    public ResponseEntity<Map<String, String>> aceptarOferta(@PathVariable Long id) {
        ofertaService.aceptarOferta(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Oferta aceptada y jugador transferido.");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/rechazar/{id}")
    public ResponseEntity<Map<String, String>> rechazarOferta(@PathVariable Long id) {
        ofertaService.eliminarOferta(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Oferta rechazada y eliminada.");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/contraoferta/{id}")
    public ResponseEntity<Map<String, String>> hacerContraoferta(@PathVariable Long id, @RequestBody Oferta nuevaOferta) {
        Oferta ofertaExistente = ofertaService.obtenerOfertaPorId(id)
                .orElseThrow(() -> new RuntimeException("Oferta no encontrada"));

        // Crear la nueva contraoferta
        nuevaOferta.setEstado(Oferta.EstadoOferta.CONTRAOFERTA);
        nuevaOferta.setVendedor(ofertaExistente.getComprador()); // Ahora el vendedor es quien hizo la oferta
        nuevaOferta.setComprador(ofertaExistente.getVendedor()); // El comprador ahora es el que la recibe
        ofertaService.crearOferta(nuevaOferta);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Contraoferta enviada correctamente.");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/retirar")
    public ResponseEntity<Map<String, String>> retirarOferta(@PathVariable Long id) {
        Optional<Oferta> ofertaOpt = ofertaService.obtenerOfertaPorId(id);

        if (ofertaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "❌ Oferta no encontrada."));
        }

        Oferta oferta = ofertaOpt.get();

        // Verificar si la oferta ya fue aceptada o rechazada
        if (oferta.getEstado() == Oferta.EstadoOferta.ACEPTADA || oferta.getEstado() == Oferta.EstadoOferta.RECHAZADA) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "❌ No puedes retirar una oferta que ya fue procesada."));
        }

        ofertaService.eliminarOferta(id);
        return ResponseEntity.ok(Map.of("message", "✅ Oferta retirada correctamente."));
    }

}
