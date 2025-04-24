package com.spfantasy.backend.controller;

import com.spfantasy.backend.model.Oferta.EstadoOferta;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.spfantasy.backend.repository.OfertaRepository;

import com.spfantasy.backend.dto.OfertaDTO;
import com.spfantasy.backend.model.JugadorLiga;
import com.spfantasy.backend.model.Oferta;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.repository.JugadorLigaRepository;
import com.spfantasy.backend.service.OfertaService;
import com.spfantasy.backend.service.UsuarioService;

import com.spfantasy.backend.dto.ContraofertaDTO;

@RestController
@RequestMapping("/api/ofertas")
public class OfertaController {

  private static final Logger logger = LoggerFactory.getLogger(OfertaController.class);

  private final OfertaService ofertaService;
  private final UsuarioService usuarioService;
  private final JugadorLigaRepository jugadorLigaRepository;
  private final OfertaRepository ofertaRepository;
  private final SimpMessagingTemplate messagingTemplate;

  public OfertaController(
      OfertaService ofertaService,
      UsuarioService usuarioService,
      JugadorLigaRepository jugadorLigaRepository,
      OfertaRepository ofertaRepository,
      SimpMessagingTemplate messagingTemplate) {
    this.ofertaService = ofertaService;
    this.usuarioService = usuarioService;
    this.jugadorLigaRepository = jugadorLigaRepository;
    this.ofertaRepository = ofertaRepository;
    this.messagingTemplate = messagingTemplate;
  }

  @PostMapping
  public ResponseEntity<?> crearOferta(@RequestBody Oferta oferta, @RequestHeader("Authorization") String token) {
    logger.info("📥 Recibiendo oferta en el backend: {}", oferta);

    String username = usuarioService.obtenerUsernameDesdeToken(token.replace("Bearer ", ""));
    Usuario comprador = usuarioService.obtenerUsuarioPorUsername(username);

    JugadorLiga jugadorLiga = jugadorLigaRepository.findById(oferta.getJugador().getId())
        .orElseThrow(() -> new RuntimeException("JugadorLiga no encontrado"));

    Usuario propietario = jugadorLiga.getPropietario();

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

    // ✅ Validación de liga a través del jugadorLiga
    Long ligaId = jugadorLiga.getLiga().getId();

    if (!usuarioService.usuarioPerteneceALiga(comprador.getId(), ligaId) ||
        !usuarioService.usuarioPerteneceALiga(propietario.getId(), ligaId)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Ambos usuarios deben estar en la misma liga para realizar una oferta.");
    }

    oferta.setComprador(comprador);
    oferta.setVendedor(propietario);
    oferta.setEstado(Oferta.EstadoOferta.PENDIENTE);
    oferta.setJugador(jugadorLiga);
    oferta.setLiga(jugadorLiga.getLiga()); // ✅ Se asigna desde el jugador

    try {
      Oferta nuevaOferta = ofertaService.crearOferta(oferta);
      OfertaDTO dto = new OfertaDTO(nuevaOferta);

      // ✅ Notificar al vendedor en tiempo real
      messagingTemplate.convertAndSend("/chat/ofertas/" + nuevaOferta.getVendedor().getId(), dto);

      return ResponseEntity.ok(dto);
    } catch (Exception e) {
      logger.error("❌ Error al guardar la oferta", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al guardar la oferta.");
    }
  }

  @GetMapping("/vendedor/{vendedorId}")
  public ResponseEntity<List<OfertaDTO>> obtenerOfertasPorVendedor(
      @PathVariable Long vendedorId,
      @RequestParam Long ligaId) {
    List<Oferta> ofertas = ofertaService.obtenerOfertasPorVendedorYLiga(vendedorId, ligaId);
    return ResponseEntity.ok(ofertas.stream().map(OfertaDTO::new).toList());
  }

  @GetMapping("/comprador/{compradorId}")
  public ResponseEntity<List<OfertaDTO>> obtenerOfertasPorComprador(
      @PathVariable Long compradorId,
      @RequestParam Long ligaId) {
    List<Oferta> ofertas = ofertaService.obtenerOfertasPorCompradorYLiga(compradorId, ligaId);
    return ResponseEntity.ok(ofertas.stream().map(OfertaDTO::new).toList());
  }

  @PostMapping("/aceptar/{id}")
  public ResponseEntity<?> aceptarOferta(@PathVariable Long id) {
    try {
      Oferta oferta = ofertaRepository.findById(id)
          .orElseThrow(() -> new RuntimeException("❌ Oferta no encontrada."));

      if (oferta.getEstado() == EstadoOferta.CONTRAOFERTA) {
        ofertaService.aceptarContraoferta(oferta); // 🔥 NO devuelve boolean
        return ResponseEntity.ok().build(); // ✅ solo status 200, sin texto
      }

      ofertaService.aceptarOferta(id); // 🔥 NO devuelve boolean
      return ResponseEntity.ok().build(); // ✅ lo mismo
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", e.getMessage())); // Aquí sí está bien mandar JSON
    }
  }

  @DeleteMapping("/rechazar/{id}")
  public ResponseEntity<Map<String, String>> rechazarOferta(@PathVariable Long id) {
    ofertaService.eliminarOferta(id);
    return ResponseEntity.ok(Map.of("message", "Oferta rechazada y eliminada."));
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

    return ResponseEntity.ok(ofertaOpt.map(OfertaDTO::new).orElse(null));
  }

  @PostMapping("/contraoferta")
  public ResponseEntity<OfertaDTO> crearContraoferta(@RequestBody ContraofertaDTO dto) {
    Oferta ofertaOriginal = ofertaService.obtenerOfertaPorId(dto.getOfertaOriginalId())
        .orElseThrow(() -> new RuntimeException("❌ Oferta original no encontrada"));

    Oferta nuevaContraoferta = ofertaService.crearContraofertaDesdeOriginal(ofertaOriginal, dto.getMontoOferta());

    messagingTemplate.convertAndSend("/chat/ofertas/" + nuevaContraoferta.getVendedor().getId(),
        new OfertaDTO(nuevaContraoferta));

    return ResponseEntity.ok(new OfertaDTO(nuevaContraoferta));
  }

}
