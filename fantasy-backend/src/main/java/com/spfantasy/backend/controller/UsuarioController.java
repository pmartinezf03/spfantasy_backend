package com.spfantasy.backend.controller;

import com.spfantasy.backend.model.Jugador;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.service.UsuarioService;
import com.spfantasy.backend.config.JwtUtil;
import com.spfantasy.backend.dto.JugadorDTO;
import com.spfantasy.backend.dto.LoginResponseDTO;
import com.spfantasy.backend.dto.UsuarioDTO;
import com.spfantasy.backend.model.Role;
import com.spfantasy.backend.repository.UsuarioRepository;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/registro")
    public Usuario registrarUsuario(@RequestBody Usuario usuario) {
        return usuarioService.registrarUsuario(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody Usuario usuario) {
        Usuario user = usuarioService.obtenerUsuarioPorUsername(usuario.getUsername());

        if (user != null && passwordEncoder.matches(usuario.getPassword(), user.getPassword())) {
            String token = jwtUtil.generateToken(user.getUsername());

            LoginResponseDTO loginResponse = new LoginResponseDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole().name(),
                    token
            );

            Map<String, Object> response = new HashMap<>();
            response.put("user", loginResponse);
            response.put("token", token);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }

    }

    @GetMapping("/{username}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable String username) {
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username);

        if (usuario != null) {
            List<JugadorDTO> titulares = usuario.getPlantilla().stream()
                    .filter(Jugador::getTitular)
                    .map(jugador -> {
                        JugadorDTO dto = new JugadorDTO(
                                jugador.getId(),
                                jugador.getNombre(),
                                jugador.getPosicion(),
                                jugador.getPrecioVenta().doubleValue(),
                                jugador.getRendimiento().doubleValue(),
                                jugador.getPuntosTotales(),
                                jugador.getEquipo(),
                                jugador.getFotoUrl(),
                                jugador.getPts(),
                                jugador.getMin(),
                                jugador.getTl(),
                                jugador.getT2(),
                                jugador.getT3(),
                                jugador.getFp(),
                                jugador.getPropietario()
                        );
                        dto.setEsTitular(true);
                        return dto;
                    }).toList();

            List<JugadorDTO> suplentes = usuario.getPlantilla().stream()
                    .filter(j -> !j.getTitular())
                    .map(jugador -> {
                        JugadorDTO dto = new JugadorDTO(
                                jugador.getId(),
                                jugador.getNombre(),
                                jugador.getPosicion(),
                                jugador.getPrecioVenta().doubleValue(),
                                jugador.getRendimiento().doubleValue(),
                                jugador.getPuntosTotales(),
                                jugador.getEquipo(),
                                jugador.getFotoUrl(),
                                jugador.getPts(),
                                jugador.getMin(),
                                jugador.getTl(),
                                jugador.getT2(),
                                jugador.getT3(),
                                jugador.getFp(),
                                jugador.getPropietario()
                        );
                        dto.setEsTitular(false);
                        return dto;
                    }).toList();

            Map<String, Object> response = new HashMap<>();
            response.put("username", usuario.getUsername());
            response.put("email", usuario.getEmail());
            response.put("dinero", usuario.getDinero());
            response.put("role", usuario.getRole());
            response.put("titulares", titulares);
            response.put("suplentes", suplentes);

            System.out.println("📤 Enviando plantilla guardada del usuario al frontend: " + response);
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<UsuarioDTO> getUsuarios() {
        return usuarioService.obtenerTodosComoDTO();
    }

    @PutMapping("/{id}/rol")
    public ResponseEntity<String> actualizarRol(@PathVariable Long id, @RequestParam String nuevoRol) {
        try {
            Role role = Role.valueOf(nuevoRol.toUpperCase());
            usuarioService.actualizarRol(id, role);
            return ResponseEntity.ok("Rol actualizado correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: El rol especificado no es válido.");
        }
    }

    @PostMapping("/{username}/comprar")
    public ResponseEntity<Map<String, Object>> comprarJugador(@PathVariable String username, @RequestBody Jugador jugadorRequest) {
        System.out.println("📩 Recibido ID del jugador para compra: " + jugadorRequest.getId());

        Optional<Jugador> jugadorOpt = usuarioService.jugadorRepository.findById(jugadorRequest.getId());
        if (jugadorOpt.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Jugador no encontrado.");
            response.put("status", "error");
            System.out.println("❌ Jugador no encontrado con ID: " + jugadorRequest.getId());
            return ResponseEntity.badRequest().body(response);
        }

        Jugador jugador = jugadorOpt.get();
        System.out.println("🎯 Intentando comprar el jugador: " + jugador.getNombre() + " | Precio: " + jugador.getPrecioVenta());

        boolean exito = usuarioService.comprarJugador(username, jugador);
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username);

        Map<String, Object> response = new HashMap<>();
        if (exito && usuario != null) {
            response.put("mensaje", "Jugador comprado exitosamente.");
            response.put("status", "success");
            response.put("dinero", usuario.getDinero());
            System.out.println("✅ Compra exitosa: Jugador " + jugador.getNombre() + " comprado por " + username);
            return ResponseEntity.ok(response);
        } else {
            response.put("mensaje", "No se pudo comprar el jugador. Revisa dinero disponible o capacidad de plantilla.");
            response.put("status", "error");
            System.out.println("❌ Error: No se pudo comprar el jugador. Posibles causas: dinero insuficiente, jugador ya comprado o banquillo lleno.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{username}/vender")
    public ResponseEntity<Map<String, Object>> venderJugador(@PathVariable String username, @RequestBody Jugador jugador) {
        System.out.println("🎯 Recibiendo solicitud para vender el jugador: " + jugador.getNombre() + " (ID: " + jugador.getId() + ")");

        boolean exito = usuarioService.venderJugador(username, jugador);
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username);

        Map<String, Object> response = new HashMap<>();
        if (exito && usuario != null) {
            response.put("mensaje", "Jugador vendido exitosamente.");
            response.put("status", "success");
            response.put("dinero", usuario.getDinero());  // ✅ Dinero actualizado
            return ResponseEntity.ok(response);
        } else {
            response.put("mensaje", "No se pudo vender el jugador.");
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{username}/guardar-plantilla")
    public ResponseEntity<Map<String, Object>> guardarPlantilla(@PathVariable String username, @RequestBody Map<String, List<Long>> plantilla) {
        boolean exito = usuarioService.guardarPlantilla(username, plantilla.get("titulares"), plantilla.get("suplentes"));

        Map<String, Object> response = new HashMap<>();
        if (exito) {
            response.put("mensaje", "Plantilla guardada correctamente.");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } else {
            response.put("mensaje", "No se pudo guardar la plantilla.");
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{id}/dinero")
    public ResponseEntity<BigDecimal> obtenerDineroUsuario(@PathVariable Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isPresent()) {
            return ResponseEntity.ok(usuarioOpt.get().getDinero());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BigDecimal.ZERO);
        }
    }

}
