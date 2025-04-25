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
import com.spfantasy.backend.model.JugadorLiga;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.repository.JugadorRepository;
import com.spfantasy.backend.repository.UsuarioRepository;
import com.spfantasy.backend.service.JugadorService;

import com.spfantasy.backend.service.JugadorStatsFetcherService;
import com.spfantasy.backend.service.UsuarioService;

@RestController
@RequestMapping("/api/jugadores")
public class JugadorController {

    @Autowired
    private JugadorService jugadorService;

    @Autowired
    private JugadorStatsFetcherService statsFetcherService;

    @Autowired
    private JugadorRepository jugadorRepository; // ✅ Agregamos el repositorio de jugadores

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

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

        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        Optional<JugadorLiga> jugadorOpt = usuario.getPlantilla().stream()
                .filter(j -> j.getId().equals(jugadorDTO.getId()))
                .findFirst();

        if (jugadorOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El jugador no está en la plantilla del usuario");
        }

        JugadorLiga jugadorLiga = jugadorOpt.get(); // ✅ ahora sí existe la variable

        boolean ventaExitosa = usuarioService.venderJugador(username, jugadorLiga);

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

    @PostMapping("/recalcular-estadisticas")
    public ResponseEntity<String> recalcularEstadisticas() {
        // 🧪 Generar nuevos datos simulados primero
        statsFetcherService.actualizarDatosSimuladosParaTodosLosJugadores();

        // 🔁 Luego recalcular rendimiento y precio con esos datos
        jugadorService.recalcularEstadisticasParaTodos();

        return ResponseEntity.ok("✅ Estadísticas simuladas y recalculadas correctamente.");
    }

}
