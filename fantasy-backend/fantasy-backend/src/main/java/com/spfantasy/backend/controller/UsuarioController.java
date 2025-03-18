package com.spfantasy.backend.controller;

import com.spfantasy.backend.model.Jugador;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.service.UsuarioService;
import com.spfantasy.backend.config.JwtUtil;
import com.spfantasy.backend.dto.JugadorDTO;
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
@CrossOrigin(origins = "http://localhost:4200")
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

            Map<String, Object> response = new HashMap<>();
            response.put("user", user);
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
                    .map(jugador -> new JugadorDTO(
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
            )).toList();

            List<JugadorDTO> suplentes = usuario.getPlantilla().stream()
                    .filter(j -> !j.getTitular())
                    .map(jugador -> new JugadorDTO(
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
            )).toList();

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
    public List<Usuario> getUsuarios() {
        return usuarioService.obtenerTodosLosUsuarios();
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
    public ResponseEntity<Map<String, Object>> comprarJugador(@PathVariable String username, @RequestBody Jugador jugador) {
        System.out.println("📩 Recibido en compra: " + jugador);

        boolean exito = usuarioService.comprarJugador(username, jugador);

        Map<String, Object> response = new HashMap<>();
        if (exito) {
            response.put("mensaje", "Jugador comprado exitosamente.");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } else {
            response.put("mensaje", "No se pudo comprar el jugador.");
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        }

    }

    @PostMapping("/{username}/vender")
    public ResponseEntity<Map<String, Object>> venderJugador(@PathVariable String username, @RequestBody Jugador jugador) {
        System.out.println("🎯 Recibiendo solicitud para vender el jugador: " + jugador.getNombre() + " (ID: " + jugador.getId() + ")");

        Map<String, Object> response = new HashMap<>();
        boolean exito = usuarioService.venderJugador(username, jugador);

        if (exito) {
            System.out.println("✅ Jugador vendido exitosamente: " + jugador.getNombre());
            response.put("mensaje", "Jugador vendido exitosamente.");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } else {
            System.out.println("❌ No se pudo vender el jugador: " + jugador.getNombre());
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
