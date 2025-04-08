package com.spfantasy.backend.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.spfantasy.backend.dto.OfertaDTO;
import com.spfantasy.backend.model.JugadorLiga;
import com.spfantasy.backend.model.Oferta;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.repository.JugadorLigaRepository;
import com.spfantasy.backend.service.OfertaService;
import com.spfantasy.backend.service.UsuarioService;

@RestController
@RequestMapping("/api/ofertas")
public class OfertaController {

    private static final Logger logger = LoggerFactory.getLogger(OfertaController.class);

    private final OfertaService ofertaService;
    private final UsuarioService usuarioService;
    private final JugadorLigaRepository jugadorLigaRepository;

    public OfertaController(OfertaService ofertaService, UsuarioService usuarioService,
            JugadorLigaRepository jugadorLigaRepository) {
        this.ofertaService = ofertaService;
        this.usuarioService = usuarioService;
        this.jugadorLigaRepository = jugadorLigaRepository;
    }

    @PostMapping
    public ResponseEntity<?> crearOferta(@RequestBody Oferta oferta, @RequestHeader("Authorization") String token) {
        logger.info("📥 Recibiendo oferta en el backend: {}", oferta);

        String username = usuarioService.obtenerUsernameDesdeToken(token.replace("Bearer ", ""));
        Usuario comprador = usuarioService.obtenerUsuarioPorUsername(username);

        // 🔍 Obtener jugador de la liga desde la BD
        JugadorLiga jugadorLiga = jugadorLigaRepository.findById(oferta.getJugador().getId())
                .orElseThrow(() -> new RuntimeException("JugadorLiga no encontrado"));

        Usuario propietario = jugadorLiga.getPropietario();

        logger.info("🟢 JugadorLiga encontrado: {} | Propietario: {}", jugadorLiga.getJugadorBase().getNombre(),
                propietario != null ? propietario.getUsername() : "Sin propietario");

        // ❌ Validaciones
        if (propietario == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El jugador no tiene propietario.");
        }

        if (oferta.getMontoOferta() == null || oferta.getMontoOferta().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El monto de la oferta debe ser mayor a 0.");
        }

        if (comprador.getDinero().compareTo(oferta.getMontoOferta()) < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("❌ No tienes suficiente dinero para esta oferta.");
        }

        // Validar misma liga
        if (comprador.getLiga() == null || propietario.getLiga() == null ||
                !comprador.getLiga().getId().equals(propietario.getLiga().getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Ambos usuarios deben estar en la misma liga para realizar una oferta.");
        }

        // ✅ Guardar oferta
        oferta.setComprador(comprador);
        oferta.setVendedor(propietario);
        oferta.setEstado(Oferta.EstadoOferta.PENDIENTE);
        oferta.setJugador(jugadorLiga);
        oferta.setLiga(comprador.getLiga());

        try {
            Oferta nuevaOferta = ofertaService.crearOferta(oferta);
            return ResponseEntity.ok(new OfertaDTO(nuevaOferta)); // ✅ DEVOLVEMOS SOLO LOS DATOS NECESARIOS
        } catch (Exception e) {
            logger.error("❌ Error al guardar la oferta", e);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al guardar la oferta.");
        }

    }

    @GetMapping("/vendedor/{vendedorId}")
    public ResponseEntity<List<OfertaDTO>> obtenerOfertasPorVendedor(
            @PathVariable Long vendedorId,
            @RequestParam Long ligaId) {
        List<Oferta> ofertas = ofertaService.obtenerOfertasPorVendedorYLiga(vendedorId, ligaId);
        List<OfertaDTO> ofertaDTOs = ofertas.stream().map(OfertaDTO::new).toList();
        return ResponseEntity.ok(ofertaDTOs);
    }

    @GetMapping("/comprador/{compradorId}")
    public ResponseEntity<List<OfertaDTO>> obtenerOfertasPorComprador(
            @PathVariable Long compradorId,
            @RequestParam Long ligaId) {
        List<Oferta> ofertas = ofertaService.obtenerOfertasPorCompradorYLiga(compradorId, ligaId);
        List<OfertaDTO> ofertaDTOs = ofertas.stream().map(OfertaDTO::new).toList();
        return ResponseEntity.ok(ofertaDTOs);
    }

    @PostMapping("/aceptar/{id}")
    public ResponseEntity<Map<String, String>> aceptarOferta(@PathVariable Long id) {
        ofertaService.aceptarOferta(id);
        return ResponseEntity.ok(Map.of("message", "Oferta aceptada y jugador transferido."));
    }

    @DeleteMapping("/rechazar/{id}")
    public ResponseEntity<Map<String, String>> rechazarOferta(@PathVariable Long id) {
        ofertaService.eliminarOferta(id);
        return ResponseEntity.ok(Map.of("message", "Oferta rechazada y eliminada."));
    }

    @PostMapping("/contraoferta/{id}")
    public ResponseEntity<Map<String, String>> hacerContraoferta(@PathVariable Long id,
            @RequestBody Oferta nuevaOferta) {
        Oferta ofertaExistente = ofertaService.obtenerOfertaPorId(id)
                .orElseThrow(() -> new RuntimeException("Oferta no encontrada"));

        Usuario compradorOriginal = ofertaExistente.getComprador();
        compradorOriginal.setDinero(compradorOriginal.getDinero().add(ofertaExistente.getMontoOferta()));
        usuarioService.usuarioRepository.save(compradorOriginal);

        nuevaOferta.setEstado(Oferta.EstadoOferta.CONTRAOFERTA);
        nuevaOferta.setVendedor(ofertaExistente.getComprador());
        nuevaOferta.setComprador(ofertaExistente.getVendedor());
        nuevaOferta.setJugador(ofertaExistente.getJugador());
        nuevaOferta.setLiga(ofertaExistente.getLiga());

        ofertaService.crearOferta(nuevaOferta);
        ofertaService.eliminarOferta(ofertaExistente.getId());

        return ResponseEntity.ok(Map.of("message", "Contraoferta enviada correctamente."));
    }

    @DeleteMapping("/{id}/retirar")
    public ResponseEntity<Map<String, String>> retirarOferta(@PathVariable Long id) {
        Optional<Oferta> ofertaOpt = ofertaService.obtenerOfertaPorId(id);

        if (ofertaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "❌ Oferta no encontrada."));
        }

        Oferta oferta = ofertaOpt.get();
        if (oferta.getEstado() == Oferta.EstadoOferta.ACEPTADA || oferta.getEstado() == Oferta.EstadoOferta.RECHAZADA) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "❌ No puedes retirar una oferta que ya fue procesada."));
        }

        ofertaService.eliminarOferta(id);
        return ResponseEntity.ok(Map.of("message", "✅ Oferta retirada correctamente."));
    }

    @GetMapping("/nuevas/{vendedorId}")
    public ResponseEntity<Map<String, Boolean>> tieneOfertasNuevas(@PathVariable Long vendedorId) {
        boolean tieneNuevas = ofertaService.tieneOfertasNuevas(vendedorId);
        return ResponseEntity.ok(Map.of("tieneOfertasNuevas", tieneNuevas));
    }

    @PostMapping("/marcar-leidas/{usuarioId}")
    public ResponseEntity<?> marcarOfertasComoLeidas(@PathVariable Long usuarioId) {
        try {
            ofertaService.marcarOfertasComoLeidas(usuarioId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al marcar ofertas como leídas");
        }
    }

    @GetMapping("/ultima-oferta/{compradorId}/{jugadorId}")
    public ResponseEntity<OfertaDTO> obtenerUltimaOferta(
            @PathVariable Long compradorId,
            @PathVariable Long jugadorId,
            @RequestParam Long ligaId) {

        Optional<Oferta> ofertaOpt = ofertaService.obtenerUltimaOferta(compradorId, jugadorId, ligaId);

        if (ofertaOpt.isPresent()) {
            return ResponseEntity.ok(new OfertaDTO(ofertaOpt.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
