package com.spfantasy.backend.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.spfantasy.backend.config.JwtUtil;
import com.spfantasy.backend.dto.CodigoRecompensaResponse;
import com.spfantasy.backend.dto.JugadorDTO;
import com.spfantasy.backend.dto.JugadorLigaDTO;
import com.spfantasy.backend.dto.LoginResponseDTO;
import com.spfantasy.backend.dto.UsuarioConPlantillaDTO;
import com.spfantasy.backend.dto.UsuarioDTO;
import com.spfantasy.backend.dto.UsuarioNivelDTO;
import com.spfantasy.backend.model.JugadorLiga;
import com.spfantasy.backend.model.Role;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.repository.JugadorLigaRepository;
import com.spfantasy.backend.repository.UsuarioRepository;
import com.spfantasy.backend.service.UsuarioLigaService;
import com.spfantasy.backend.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioLigaService usuarioLigaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JugadorLigaRepository jugadorLigaRepository;

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
                    token);

            // ✅ Añade esta línea:
            loginResponse.setRachasFelicitadas(user.getRachasFelicitadas());

            Map<String, Object> response = new HashMap<>();
            response.put("user", loginResponse);
            response.put("token", token);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }
    }

    @PostMapping("/{username}/vender-jugador-liga/{jugadorLigaId}")
    public ResponseEntity<Map<String, Object>> venderJugadorDeLiga(
            @PathVariable String username,
            @PathVariable Long jugadorLigaId) {

        Map<String, Object> response = new HashMap<>();

        try {
            boolean exito = usuarioService.venderJugadorDeLiga(username, jugadorLigaId);

            if (exito) {
                response.put("mensaje", "Jugador vendido correctamente.");
                response.put("status", "success");
                return ResponseEntity.ok(response);
            } else {
                response.put("mensaje", "❌ No se pudo vender el jugador.");
                response.put("status", "error");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("mensaje", "❌ Error: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<UsuarioConPlantillaDTO> obtenerUsuario(@PathVariable String username) {
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        List<JugadorDTO> titulares = usuario.getPlantilla().stream()
                .filter(JugadorLiga::isEsTitular)
                .map(jugador -> new JugadorDTO(
                        jugador.getId(),
                        jugador.getJugadorBase().getNombre(),
                        jugador.getJugadorBase().getPosicion(),
                        jugador.getPrecioVenta().doubleValue(),
                        jugador.getRendimiento().doubleValue(),
                        jugador.getPuntosTotales(),
                        jugador.getJugadorBase().getEquipo(), // <-- se espera Equipo, no String
                        jugador.getFotoUrl(),
                        jugador.getPts(),
                        jugador.getMin(),
                        jugador.getTl(),
                        jugador.getT2(),
                        jugador.getT3(),
                        jugador.getFp(),
                        jugador.getPropietario()))
                .toList();

        List<JugadorDTO> suplentes = usuario.getPlantilla().stream()
                .filter(j -> !j.isEsTitular())
                .map(jugador -> {
                    JugadorDTO dto = new JugadorDTO(
                            jugador.getId(),
                            jugador.getJugadorBase().getNombre(),
                            jugador.getJugadorBase().getPosicion(),
                            jugador.getPrecioVenta().doubleValue(),
                            jugador.getRendimiento() != null ? jugador.getRendimiento().doubleValue() : 0.0,
                            jugador.getPuntosTotales(),
                            jugador.getJugadorBase().getEquipo(),
                            jugador.getFotoUrl(),
                            jugador.getPts(),
                            jugador.getMin(),
                            jugador.getTl(),
                            jugador.getT2(),
                            jugador.getT3(),
                            jugador.getFp(),
                            jugador.getPropietario());
                    dto.setEsTitular(false);
                    return dto;
                })

                .toList();

        UsuarioConPlantillaDTO dto = new UsuarioConPlantillaDTO();
        dto.setUsername(usuario.getUsername());
        dto.setEmail(usuario.getEmail());
        dto.setAlias(usuario.getAlias());
        dto.setDinero(usuario.getDinero());
        dto.setRole(usuario.getRole().toString());
        dto.setTitulares(titulares);
        dto.setSuplentes(suplentes);

        System.out.println(" Enviando plantilla del usuario al frontend: " + dto);
        return ResponseEntity.ok(dto);
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
    public ResponseEntity<Map<String, Object>> comprarJugador(@PathVariable String username,
            @RequestBody JugadorLiga jugadorRequest) {
        System.out.println(" Recibido ID del jugador para compra: " + jugadorRequest.getId());

        Optional<JugadorLiga> jugadorOpt = jugadorLigaRepository.findById(jugadorRequest.getId());
        if (jugadorOpt.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Jugador no encontrado.");
            response.put("status", "error");
            System.out.println("❌ Jugador no encontrado con ID: " + jugadorRequest.getId());
            return ResponseEntity.badRequest().body(response);
        }

        JugadorLiga jugador = jugadorOpt.get();
        System.out.println(
                " Intentando comprar el jugador: " + jugador.getNombre() + " | Precio: " + jugador.getPrecioVenta());

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
            response.put("mensaje",
                    "No se pudo comprar el jugador. Revisa dinero disponible o capacidad de plantilla.");
            response.put("status", "error");
            System.out.println(
                    "❌ Error: No se pudo comprar el jugador. Posibles causas: dinero insuficiente, jugador ya comprado o banquillo lleno.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{username}/vender")
    public ResponseEntity<Map<String, Object>> venderJugador(@PathVariable String username,
            @RequestBody JugadorLiga jugador) {

        System.out.println(" Recibiendo solicitud para vender el jugador: " + jugador.getNombre() + " (ID: "
                + jugador.getId() + ")");

        // ✅ Convertir Jugador → JugadorLiga
        JugadorLiga jugadorLiga = jugadorLigaRepository.findById(jugador.getId())
                .orElseThrow(() -> new RuntimeException("JugadorLiga no encontrado"));

        // ✅ Llamar al servicio con el tipo correcto
        boolean exito = usuarioService.venderJugador(username, jugadorLiga);
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username);

        Map<String, Object> response = new HashMap<>();
        if (exito && usuario != null) {
            response.put("mensaje", "Jugador vendido exitosamente.");
            response.put("status", "success");
            response.put("dinero", usuario.getDinero()); // ✅ Dinero actualizado
            return ResponseEntity.ok(response);
        } else {
            response.put("mensaje", "No se pudo vender el jugador.");
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{username}/guardar-plantilla")
    public ResponseEntity<Map<String, Object>> guardarPlantilla(@PathVariable String username,
            @RequestBody Map<String, List<Long>> plantilla) {
        boolean exito = usuarioService.guardarPlantilla(username, plantilla.get("titulares"),
                plantilla.get("suplentes"));

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

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerUsuarioPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
        if (usuario != null) {
            UsuarioDTO dto = new UsuarioDTO(usuario);
            // Aquí calculamos el nivel dinámicamente si no está almacenado
            dto.setNivel(usuario.getNivel()); // Si getNivel() lo calcula dinámicamente o lo tienes guardado
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/alias/{alias}")
    public Usuario buscarPorAlias(@PathVariable String alias) {
        return usuarioService.buscarPorAlias(alias);
    }

    @PostMapping("/{username}/comprar-liga")
    public ResponseEntity<Map<String, Object>> comprarJugadorDeLiga(
            @PathVariable String username,
            @RequestParam Long jugadorLigaId,
            @RequestParam Long ligaId) {

        System.out.println(" Compra directa de jugadorLigaId=" + jugadorLigaId + " en ligaId=" + ligaId);

        Map<String, Object> response = new HashMap<>();
        try {
            boolean exito = usuarioService.comprarJugadorDeLiga(username, jugadorLigaId, ligaId);
            Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username);

            if (exito && usuario != null) {
                response.put("mensaje", "Compra directa exitosa.");
                response.put("status", "success");
                response.put("dinero", usuario.getDinero());
                return ResponseEntity.ok(response);
            } else {
                response.put("mensaje", "No se pudo realizar la compra.");
                response.put("status", "error");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            System.err.println("❌ Error en compra directa: " + e.getMessage());
            response.put("mensaje", e.getMessage());
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{username}/puntos-semana")
    public ResponseEntity<Map<Long, String>> obtenerPuntosSemanales(@PathVariable String username) {
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        Map<Long, String> puntos = usuarioService.obtenerPuntosSemanalesTitulares(usuario);
        return ResponseEntity.ok(puntos);
    }

    @PutMapping("/{id}/hacer-vip")
    public ResponseEntity<UsuarioDTO> hacerVip(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setVipHasta(LocalDateTime.now().plusMonths(1));
        usuarioRepository.save(usuario);

        return ResponseEntity.ok(new UsuarioDTO(usuario)); // ✅ devolvemos DTO limpio
    }

    @PostMapping("/{id}/avatar")
    public ResponseEntity<?> subirAvatar(
            @PathVariable Long id,
            @RequestParam("avatar") MultipartFile file) {
        try {
            System.out.println("️ Recibiendo avatar para usuario ID: " + id);
            System.out.println(" Nombre del archivo recibido: " + file.getOriginalFilename());
            System.out.println(" Tamaño del archivo: " + file.getSize());

            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("El archivo está vacío");
            }

            Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
            if (optionalUsuario.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }

            Usuario usuario = optionalUsuario.get();
            usuario.setAvatarBytes(file.getBytes());
            usuario.setAvatarUrl(null); // limpiamos si hay ruta antigua

            usuarioRepository.save(usuario);

            return ResponseEntity.ok("Avatar guardado correctamente");

        } catch (Exception e) {
            e.printStackTrace(); // imprime detalles del error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar avatar: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/avatar")
    public ResponseEntity<byte[]> obtenerAvatar(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        byte[] datos = usuario.getAvatarBytes();

        if (datos == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .body(datos);
    }

    @PutMapping("/{id}/registrar-login")
    public ResponseEntity<Void> registrarLogin(@PathVariable Long id) {
        usuarioService.registrarLogin(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/estadisticas/{id}/login")
    public ResponseEntity<Void> registrarLoginEstadisticas(@PathVariable Long id) {
        usuarioService.registrarLogin(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/experiencia")
    public ResponseEntity<UsuarioDTO> aumentarExperiencia(@PathVariable Long id,
            @RequestBody Map<String, Integer> body) {
        int puntos = body.getOrDefault("puntos", 0);
        Usuario usuarioActualizado = usuarioService.aumentarExperiencia(id, puntos);
        UsuarioDTO dto = new UsuarioDTO(usuarioActualizado);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/nivel")
    public ResponseEntity<Integer> obtenerNivelUsuario(@PathVariable Long id) {
        int nivel = usuarioService.obtenerNivel(id);
        return ResponseEntity.ok(nivel);
    }

    @GetMapping("/{id}/experiencia")
    public ResponseEntity<Integer> obtenerExperienciaUsuario(@PathVariable Long id) {
        int experiencia = usuarioService.obtenerExperiencia(id);
        return ResponseEntity.ok(experiencia);
    }

    @PutMapping("/{id}/nivel")
    public ResponseEntity<Usuario> actualizarNivel(@PathVariable Long id) {
        Usuario usuario = usuarioService.actualizarNivelUsuario(id);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/{id}/tutorial/visto")
    public ResponseEntity<?> marcarTutorialVisto(@PathVariable Long id) {
        usuarioService.marcarTutorialVisto(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{username}/canjear-codigo")
    public ResponseEntity<?> canjearCodigo(
            @PathVariable String username,
            @RequestBody Map<String, String> body) {

        String codigoIngresado = body.get("codigo");

        try {
            CodigoRecompensaResponse respuesta = usuarioService.validarYAplicarCodigo(username, codigoIngresado);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{username}/consejos")
    public ResponseEntity<List<String>> obtenerConsejosCoach(@PathVariable String username) {
        List<String> consejos = usuarioService.generarConsejosCoach(username);
        return ResponseEntity.ok(consejos);
    }

    @GetMapping("/{id}/sobres-bienvenida")
    public ResponseEntity<List<JugadorLigaDTO>> getSobresBienvenida(@PathVariable Long id) {
        List<JugadorLiga> jugadores = usuarioLigaService.obtenerJugadoresRepartidosHoy(id);

        if (jugadores.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // o 403
        }

        List<JugadorLigaDTO> sobres = jugadores.stream()
                .map(JugadorLigaDTO::new)
                .toList();

        return ResponseEntity.ok(sobres);
    }

    @PutMapping("/{usuarioId}/ligas/{ligaId}/marcar-sobres-mostrados")
    public ResponseEntity<Void> marcarSobresMostrados(@PathVariable Long usuarioId, @PathVariable Long ligaId) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (usuarioOpt.isEmpty()) {
            System.out.println("❌ Usuario no encontrado");
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = usuarioOpt.get();
        System.out.println(" Marcando sobres como mostrados para usuario ID: " + usuarioId);

        // Actualizamos la propiedad haVistoSobres
        usuario.setHaVistoSobres(true);
        usuarioRepository.save(usuario);

        System.out.println("✅ Estado de haVistoSobres después de actualizar: " + usuario.getHaVistoSobres());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/nivel-detallado")
    public ResponseEntity<UsuarioNivelDTO> obtenerNivelDetallado(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ResponseEntity.ok(new UsuarioNivelDTO(usuario));
    }

    @PutMapping("/{id}/felicitacion-nivel")
    public ResponseEntity<Void> registrarNivelFelicitado(@PathVariable Long id, @RequestParam int nivel) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Primero, actualiza el nivel real en base a la experiencia
        usuario = usuarioService.actualizarNivelUsuario(id);

        // Luego, verifica si corresponde actualizar el nivel felicitado
        if (usuario.getNivelFelicitado() == null || usuario.getNivelFelicitado() < usuario.getNivel()) {
            usuario.setNivelFelicitado(usuario.getNivel());
            usuarioRepository.save(usuario);

            System.out.println("✅ Usuario felicitado por nivel: " + usuario.getNivel());
        } else {
            System.out.println("️ Nivel ya felicitado anteriormente: " + usuario.getNivelFelicitado());
        }

        System.out.println(" Nivel actual: " + usuario.getNivel() +
                " | Felicitado: " + usuario.getNivelFelicitado() +
                " | XP total: " + usuario.getExperiencia());

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/racha-felicitada")
    public ResponseEntity<Void> registrarRachaFelicitada(@PathVariable Long id, @RequestParam int racha) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getRachasFelicitadas() == null) {
            usuario.setRachasFelicitadas(new ArrayList<>());
        }

        if (!usuario.getRachasFelicitadas().contains(racha)) {
            usuario.getRachasFelicitadas().add(racha);
            usuarioRepository.save(usuario);
            System.out.println("✅ Racha " + racha + " registrada para usuario ID " + id);
        } else {
            System.out.println("️ Racha " + racha + " ya estaba registrada");
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/sumar-experiencia")
    public ResponseEntity<UsuarioDTO> sumarExperiencia(
            @PathVariable Long id,
            @RequestParam int puntos) {

        Usuario usuarioActualizado = usuarioService.aumentarExperiencia(id, puntos);
        return ResponseEntity.ok(new UsuarioDTO(usuarioActualizado));
    }

}
