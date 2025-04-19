package com.spfantasy.backend.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spfantasy.backend.dto.JugadorDTO;
import com.spfantasy.backend.model.Jugador;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.repository.JugadorRepository;
import com.spfantasy.backend.repository.UsuarioRepository;
import com.spfantasy.backend.service.JugadorService;

@RestController
@RequestMapping("/jugadores")
public class JugadorController {

    @Autowired
    private JugadorService jugadorService;

    @Autowired
    private JugadorRepository jugadorRepository; // ✅ Agregamos el repositorio de jugadores

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ✅ Nuevo endpoint para obtener solo los jugadores disponibles en el mercado
    @GetMapping("/mercado")
    public List<JugadorDTO> obtenerJugadoresDisponibles() {
        System.out.println("📤 Enviando lista de jugadores disponibles en el mercado.");
        return jugadorService.obtenerJugadoresDisponibles();
    }

    @GetMapping
    public List<JugadorDTO> obtenerJugadores() {
        return jugadorService.obtenerJugadores();
    }

    @PostMapping("/{username}/vender")
    public ResponseEntity<String> venderJugador(@PathVariable String username, @RequestBody JugadorDTO jugadorDTO) {
        // Convertir JugadorDTO a Jugador
        Jugador jugador = new Jugador();
        jugador.setId(jugadorDTO.getId());
        jugador.setNombre(jugadorDTO.getNombre());
        jugador.setPosicion(jugadorDTO.getPosicion());
        jugador.setPrecioVenta(BigDecimal.valueOf(jugadorDTO.getPrecioVenta()));
        jugador.setRendimiento(BigDecimal.valueOf(jugadorDTO.getRendimiento()));
        jugador.setPuntosTotales(jugadorDTO.getPuntosTotales());
        jugador.setFotoUrl(jugadorDTO.getFotoUrl());

        // Verificación de la plantilla del usuario
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            System.out.println("Usuario encontrado: " + usuario.getUsername());
            System.out.println("Jugadores en plantilla: " + usuario.getPlantilla());

            boolean jugadorEnPlantilla = usuario.getPlantilla().stream()
                    .anyMatch(j -> j.getId().equals(jugador.getId()));

            if (!jugadorEnPlantilla) {
                System.out.println("❌ El jugador no está en la plantilla del usuario.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El jugador no está en la plantilla del usuario");
            } else {
                System.out.println("✅ El jugador está en la plantilla y puede ser vendido.");
            }
        } else {
            System.out.println("❌ Usuario no encontrado: " + username);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario no encontrado");
        }

        // Llamar al servicio de venta
        boolean ventaExitosa = jugadorService.venderJugador(username, jugador);
        if (ventaExitosa) {
            return ResponseEntity.ok("Jugador vendido con éxito");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se pudo completar la venta.");
        }
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<List<JugadorDTO>> obtenerEstadisticas() {
        return ResponseEntity.ok(jugadorService.obtenerEstadisticasLiga());
    }
}
