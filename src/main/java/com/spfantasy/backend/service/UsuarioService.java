package com.spfantasy.backend.service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.spfantasy.backend.config.JwtUtil;
import com.spfantasy.backend.dto.CodigoRecompensaResponse;
import com.spfantasy.backend.dto.JugadorDTO;
import com.spfantasy.backend.dto.UsuarioConPlantillaDTO;
import com.spfantasy.backend.dto.UsuarioDTO;
import com.spfantasy.backend.model.JugadorLiga;
import com.spfantasy.backend.model.Liga;
import com.spfantasy.backend.model.Role;
import com.spfantasy.backend.model.Transaccion;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.repository.JugadorLigaRepository;
import com.spfantasy.backend.repository.JugadorRepository;
import com.spfantasy.backend.repository.TransaccionRepository;
import com.spfantasy.backend.repository.UsuarioLigaRepository;
import com.spfantasy.backend.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class UsuarioService implements UserDetailsService {

  @Autowired
  public UsuarioRepository usuarioRepository;

  @Autowired
  public JugadorRepository jugadorRepository;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @Autowired
  private JwtUtil jwtUtil; // Inyección del utilitario JWT

  @Autowired
  private JugadorLigaRepository jugadorLigaRepository;

  @Autowired
  private JugadorLigaService jugadorLigaService;

  @Autowired
  private TransaccionService transaccionService;

  @Autowired
  private UsuarioLigaRepository usuarioLigaRepository;

  @Autowired
  private TransaccionRepository transaccionRepository;

  public boolean usuarioPerteneceALiga(Long usuarioId, Long ligaId) {
    return usuarioLigaRepository.existsByUsuarioIdAndLigaId(usuarioId, ligaId);
  }

  public Usuario registrarUsuario(Usuario usuario) {
    usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
    usuario.setRole(Role.usuario);
    usuario.setNivel(1);
    usuario.setExperiencia(0);
    return usuarioRepository.save(usuario);
  }

  public Usuario obtenerUsuarioPorUsername(String username) {
    Optional<Usuario> usuarioOpt = usuarioRepository.findByUsernameWithPlantilla(username);
    if (usuarioOpt.isPresent()) {
      Usuario usuario = usuarioOpt.get();
      System.out.println("🔍 Jugadores en la plantilla del usuario " + username + ": " + usuario.getPlantilla());
      return usuario;
    }
    return null;
  }

  public List<Usuario> obtenerTodosLosUsuarios() {
    return usuarioRepository.findAll();
  }

  public void actualizarRol(Long userId, Role nuevoRol) {
    Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
    if (usuarioOpt.isPresent()) {
      Usuario usuario = usuarioOpt.get();
      usuario.setRole(nuevoRol);
      usuarioRepository.save(usuario);
    } else {
      throw new RuntimeException("Usuario no encontrado");
    }
  }

  public boolean comprarJugador(String username, JugadorLiga jugador) {
    Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
    if (usuarioOpt.isPresent()) {
      Usuario usuario = usuarioOpt.get();

      // Evitar compras duplicadas
      if (usuario.getPlantilla().contains(jugador)) {
        return false;
      }

      // Verificar si el usuario tiene suficiente dinero
      if (usuario.getDinero().compareTo(jugador.getPrecioVenta()) >= 0) {

        // Contar los suplentes en la plantilla
        long suplentes = usuario.getPlantilla().stream().filter(j -> !j.isEsTitular()).count();

        // Si el banquillo está lleno, cancelar la compra
        if (suplentes >= 5) {
          System.out.println("❌ Error: Se intentó comprar un jugador con el banquillo lleno.");
          return false;
        }

        // **Asignar el usuario como propietario**
        jugador.setPropietario(usuario); // ✅ Se asigna correctamente el propietario
        jugador.setTitular(false);
        jugador.setDisponible(false); // ✅ Ya no está en el mercado

        usuario.getPlantilla().add(jugador);
        usuario.setDinero(usuario.getDinero().subtract(jugador.getPrecioVenta()));

        usuarioRepository.save(usuario);
        jugadorLigaRepository.save(jugador);

        System.out.println("✅ Jugador comprado y asignado al usuario: " + usuario.getUsername());
        return true;
      }
    }
    return false;
  }

  @Transactional
  public boolean comprarJugadorDeLiga(String username, Long jugadorLigaId, Long ligaId) {
    Usuario usuario = usuarioRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    JugadorLiga jugador = jugadorLigaRepository
        .findById(jugadorLigaId)
        .orElseThrow(() -> new RuntimeException("Jugador no encontrado en la liga"));

    if (jugador.getPropietario() != null) {
      throw new RuntimeException("Jugador ya tiene propietario.");
    }

    if (usuario.getDinero().compareTo(jugador.getPrecioVenta()) < 0) {
      throw new RuntimeException("Dinero insuficiente para esta compra.");
    }

    jugador.setPropietario(usuario);
    jugador.setDisponible(false);
    jugador.setFechaAdquisicion(LocalDateTime.now());

    usuario.setDinero(usuario.getDinero().subtract(jugador.getPrecioVenta()));

    usuarioRepository.save(usuario);
    jugadorLigaRepository.save(jugador);
    aumentarExperiencia(usuario.getId(), 5);

    // 🔥 Registrar transacción (compra directa)
    Transaccion transaccion = new Transaccion();
    transaccion.setFecha(LocalDateTime.now());
    transaccion.setJugador(jugador);
    transaccion.setComprador(usuario);
    transaccion.setPrecio(jugador.getPrecioVenta().intValue());
    transaccion.setVendedor(null); // No hay vendedor porque es del mercado libre
    transaccion.setLiga(jugador.getLiga());

    transaccionRepository.save(transaccion);
    aumentarExperiencia(usuario.getId(), 5);

    return true;
  }

  public boolean venderJugador(String username, JugadorLiga jugador) {
    Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

    if (usuarioOpt.isPresent()) {
      Usuario usuario = usuarioOpt.get();

      Optional<JugadorLiga> jugadorEnPlantilla = usuario.getPlantilla().stream()
          .filter(j -> j.getJugadorBase().getId().equals(jugador.getId()))
          .findFirst();

      if (jugadorEnPlantilla.isPresent()) {
        JugadorLiga jugadorAEliminar = jugadorEnPlantilla.get();
        usuario.getPlantilla().remove(jugadorAEliminar);
        usuario.setDinero(usuario.getDinero().add(jugadorAEliminar.getPrecioVenta()));

        usuarioRepository.save(usuario);
        jugadorAEliminar.setDisponible(true);
        jugadorAEliminar.setPropietario(null);
        jugadorLigaRepository.save(jugadorAEliminar);

        // ✅ REGISTRAR TRANSACCIÓN
        Transaccion transaccion = new Transaccion();
        transaccion.setFecha(LocalDateTime.now());
        transaccion.setPrecio(jugadorAEliminar.getPrecioVenta().intValue());
        transaccion.setJugador(jugadorAEliminar); // ahora sí es del tipo correcto
        transaccion.setComprador(null); // no hay comprador porque es al mercado
        transaccion.setVendedor(usuario);
        transaccion.setLiga(jugadorAEliminar.getLiga());

        transaccionService.guardarTransaccion(transaccion);
        aumentarExperiencia(usuario.getId(), 3);

        return true;
      }
    }

    return false;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Usuario usuario = usuarioRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

    // ⚠️ Devuelve un UserDetails con contraseña vacía (solo para pruebas)
    return new org.springframework.security.core.userdetails.User(
        usuario.getUsername(),
        "", // <- contraseña vacía
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name())));
  }

  @Transactional
  public boolean guardarPlantilla(String username, List<Long> titularesIds, List<Long> suplentesIds) {
    Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

    if (usuarioOpt.isPresent()) {
      Usuario usuario = usuarioOpt.get();
      List<JugadorLiga> nuevaPlantilla = new ArrayList<>();

      if (titularesIds.size() != 5) {
        System.out.println("❌ Error: Se intentó guardar una plantilla sin 5 titulares.");
        return false;
      }

      // Asignar titulares
      for (Long id : titularesIds) {
        Optional<JugadorLiga> jugadorOpt = jugadorLigaRepository.findById(id);
        jugadorOpt.ifPresent(j -> {
          j.setEsTitular(true);
          nuevaPlantilla.add(j);
        });
      }

      // Asignar suplentes
      for (Long id : suplentesIds) {
        Optional<JugadorLiga> jugadorOpt = jugadorLigaRepository.findById(id);
        jugadorOpt.ifPresent(j -> {
          j.setEsTitular(false);
          nuevaPlantilla.add(j);
        });
      }

      usuario.setPlantilla(nuevaPlantilla);
      usuarioRepository.save(usuario);
      System.out.println("✅ Plantilla guardada para el usuario: " + username);
      aumentarExperiencia(usuario.getId(), 2);

      return true;
    }

    System.out.println("❌ Usuario no encontrado.");
    return false;
  }

  public void registrarTransaccion(JugadorLiga jugador, Usuario comprador, Usuario vendedor, Integer precio,
      Liga liga) {
    Transaccion t = new Transaccion();
    t.setJugador(jugador);
    t.setComprador(comprador);
    t.setVendedor(vendedor);
    t.setPrecio(precio);
    t.setLiga(liga);
    t.setFecha(LocalDateTime.now());
    transaccionRepository.save(t);
  }

  /**
   * Método para extraer el username desde un token JWT.
   *
   * @param token el token JWT sin el prefijo "Bearer "
   * @return el username del usuario autenticado
   */
  public String obtenerUsernameDesdeToken(String token) {
    return jwtUtil.extractUsername(token);
  }

  public List<UsuarioDTO> obtenerTodosComoDTO() {
    return usuarioRepository.findAll().stream()
        .map(UsuarioDTO::new) // ✅ Usa el constructor que recibe Usuario
        .toList();
  }

  public Usuario guardarUsuario(Usuario usuario) {
    return usuarioRepository.save(usuario);
  }

  public Usuario obtenerUsuarioPorId(Long id) {
    Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    System.out.println("🧾 [UsuarioService] Dinero actual: " + usuario.getDinero());
    System.out.println("🧾 [UsuarioService] Dinero pendiente: " + usuario.getDineroPendiente());

    return usuario;
  }

  public Usuario buscarPorAlias(String alias) {
    return usuarioRepository.findByAlias(alias)
        .orElseThrow(() -> new RuntimeException("Alias no encontrado"));
  }

  @Transactional
  public boolean venderJugadorDeLiga(String username, Long jugadorLigaId) {
    Usuario usuario = usuarioRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    JugadorLiga jugador = jugadorLigaRepository.findById(jugadorLigaId)
        .orElseThrow(() -> new RuntimeException("JugadorLiga no encontrado"));

    if (jugador.getPropietario() == null || !jugador.getPropietario().getId().equals(usuario.getId())) {
      throw new RuntimeException("Este jugador no te pertenece.");
    }

    // Liberar jugador
    jugador.setPropietario(null);
    jugador.setDisponible(true);
    jugador.setEsTitular(false);

    usuario.setDinero(usuario.getDinero().add(jugador.getPrecioVenta()));

    usuarioRepository.save(usuario);
    jugadorLigaRepository.save(jugador);

    // Registrar transacción
    Transaccion transaccion = new Transaccion();
    transaccion.setJugador(jugador);
    transaccion.setVendedor(usuario);
    transaccion.setComprador(null); // Venta al mercado
    transaccion.setPrecio(jugador.getPrecioVenta().intValue());
    transaccion.setLiga(jugador.getLiga());
    transaccion.setFecha(LocalDateTime.now());

    transaccionRepository.save(transaccion);
    aumentarExperiencia(usuario.getId(), 3); // ← aquí

    return true;
  }

  public Integer calcularPuntosDeTitulares(Usuario usuario) {
    if (usuario.getPlantilla() == null)
      return 0;

    return usuario.getPlantilla().stream()
        .filter(JugadorLiga::isEsTitular) // Solo titulares
        .mapToInt(j -> j.getPts() != null ? j.getPts() : 0) // Sumamos pts, si no hay, contamos 0
        .sum();
  }

  public Map<Long, String> obtenerPuntosSemanalesTitulares(Usuario usuario) {
    Map<Long, String> puntosSemanales = new HashMap<>();

    if (usuario.getPlantilla() == null)
      return puntosSemanales;

    for (JugadorLiga jugador : usuario.getPlantilla()) {
      if (jugador.isEsTitular()) {
        if (jugador.getPts() == null || jugador.getPts() == 0) {
          puntosSemanales.put(jugador.getId(), "Pendiente de Jugar");
        } else {
          puntosSemanales.put(jugador.getId(), jugador.getPts() + " pts");
        }
      }
    }

    return puntosSemanales;
  }

  @Transactional
  public void registrarLogin(Long usuarioId) {
    Usuario usuario = usuarioRepository.findById(usuarioId)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    LocalDateTime ahora = LocalDateTime.now();
    LocalDate hoy = ahora.toLocalDate();
    LocalDateTime ultimoLogin = usuario.getUltimoLogin();

    // Sumar login diario
    usuario.setLogins(usuario.getLogins() + 1);

    // Verificar racha
    if (ultimoLogin != null) {
      LocalDate anterior = ultimoLogin.toLocalDate();
      if (anterior.equals(hoy.minusDays(1))) {
        usuario.setRachaLogin(usuario.getRachaLogin() + 1);
      } else if (!anterior.equals(hoy)) {
        usuario.setRachaLogin(1); // reinicia si no fue ayer
      }
    } else {
      usuario.setRachaLogin(1); // primer login
    }

    // Contar días activos (solo si no ha logueado ya hoy)
    if (ultimoLogin == null || !ultimoLogin.toLocalDate().equals(hoy)) {
      usuario.setDiasActivo(usuario.getDiasActivo() + 1);
    }

    usuario.setUltimoLogin(ahora); // actualiza la fecha
    usuarioRepository.save(usuario);
  }

  public CodigoRecompensaResponse validarYAplicarCodigo(String username, String codigo) {
    CodigoRecompensaResponse respuesta = new CodigoRecompensaResponse();

    String urlLogin = "http://52.54.248.252:8069/auth/";
    String filters = "[[\"code\", \"=\", \"" + codigo + "\"], [\"used\", \"=\", false]]";
    String filtersCodificados = URLEncoder.encode(filters, StandardCharsets.UTF_8);
    String urlCodigos = "http://52.54.248.252:8069/api/codigo.recompensa/?filters=" + filtersCodificados;

    try {
      HttpClient client = HttpClient.newHttpClient();

      String loginJson = """
          {
            "params": {
              "login": "odoo@gmail.com",
              "password": "admin123",
              "db": "fantasybasket"
            }
          }
          """;

      HttpRequest loginRequest = HttpRequest.newBuilder()
          .uri(URI.create(urlLogin))
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(loginJson))
          .build();

      HttpResponse<String> loginResponse = client.send(loginRequest, HttpResponse.BodyHandlers.ofString());
      String cookie = loginResponse.headers().firstValue("Set-Cookie").orElseThrow();

      HttpRequest codigosRequest = HttpRequest.newBuilder()
          .uri(URI.create(urlCodigos))
          .header("Cookie", cookie)
          .build();

      HttpResponse<String> codigosResponse = client.send(codigosRequest, HttpResponse.BodyHandlers.ofString());
      JSONObject json = new JSONObject(codigosResponse.body());
      System.out.println("🔍 Respuesta de Odoo para código: " + json.toString(2));

      // Obtener resultados del JSON de Odoo
      JSONArray resultados = json.getJSONArray("result");

      // Buscar el código exacto en los resultados
      JSONObject codeInfo = null;
      for (int i = 0; i < resultados.length(); i++) {
        JSONObject item = resultados.getJSONObject(i);
        if (codigo.equalsIgnoreCase(item.getString("code"))) {
          codeInfo = item;
          break;
        }
      }

      if (codeInfo == null) {
        respuesta.setValido(false);
        respuesta.setMensaje("❌ Código no válido.");
        return respuesta;
      }

      if (codeInfo.getBoolean("used")) {
        respuesta.setValido(false);
        respuesta.setMensaje("⚠️ Código ya fue usado.");
        return respuesta;
      }

      String tipo = codeInfo.getString("reward_type");
      String valor = codeInfo.getString("reward_value");

      Usuario usuario = obtenerUsuarioPorUsername(username);
      if (usuario == null)
        throw new RuntimeException("Usuario no encontrado");

      switch (tipo) {
        case "vip":
          usuario.setVipHasta(LocalDateTime.now().plusMonths(1));
          respuesta.setMensaje("✅ ¡Acceso VIP activado por 1 mes!");
          break;
        case "coins":
          usuario.setDinero(usuario.getDinero().add(new BigDecimal(valor)));
          respuesta.setMensaje("✅ ¡Has recibido " + valor + " monedas!");
          break;
        default:
          respuesta.setMensaje("⚠️ Recompensa no reconocida.");
      }

      usuarioRepository.save(usuario);

      // Marcar como usado
      int id = codeInfo.getInt("id");
      String markUsedJson = """
          {
            "params": {
              "data": {
                "used": true
              }
            }
          }
          """;

      HttpRequest putRequest = HttpRequest.newBuilder()
          .uri(URI.create("http://52.54.248.252:8069/api/codigo.recompensa/" + id + "/"))
          .header("Content-Type", "application/json")
          .header("Cookie", cookie)
          .PUT(HttpRequest.BodyPublishers.ofString(markUsedJson))
          .build();

      client.send(putRequest, HttpResponse.BodyHandlers.ofString());

      respuesta.setValido(true);
      respuesta.setTipo(tipo);
      respuesta.setValor(valor);

      return respuesta;

    } catch (Exception e) {
      throw new RuntimeException("Error validando el código: " + e.getMessage());
    }
  }

  public void marcarTutorialVisto(Long usuarioId) {
    Usuario usuario = usuarioRepository.findById(usuarioId)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    usuario.setTutorialVisto(true);
    usuarioRepository.save(usuario);
  }

  public Usuario actualizarNivelUsuario(Long id) {
    Usuario usuario = usuarioRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    int experiencia = usuario.getExperiencia();
    int nivelActual = 1;
    int xpAcumulada = 0;

    // 🧠 Calculamos a qué nivel corresponde la experiencia actual
    while (experiencia >= xpAcumulada + (nivelActual * 10)) {
      xpAcumulada += nivelActual * 10;
      nivelActual++;
    }

    if (nivelActual != usuario.getNivel()) {
      System.out.println("🆙 Subiendo nivel del usuario de " + usuario.getNivel() + " a " + nivelActual);
      usuario.setNivel(nivelActual);
      usuarioRepository.save(usuario);
    } else {
      System.out.println("⚠️ Usuario ya está en su nivel correspondiente: " + nivelActual);
    }

    return usuario;
  }

  public Usuario actualizarNivelDesdeExperiencia(Usuario usuario) {
    int xp = usuario.getExperiencia();
    int nuevoNivel = 1;
    int xpAcumulada = 0;

    while (xp >= xpAcumulada + nuevoNivel * 10) {
      xpAcumulada += nuevoNivel * 10;
      nuevoNivel++;
    }

    usuario.setNivel(nuevoNivel);
    return usuario;
  }

  public int obtenerNivel(Long usuarioId) {
    Usuario usuario = usuarioRepository.findById(usuarioId)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    return usuario.getNivel();
  }

  public int obtenerExperiencia(Long usuarioId) {
    Usuario usuario = usuarioRepository.findById(usuarioId)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    return Optional.ofNullable(usuario.getExperiencia()).orElse(0);
  }

  public Usuario aumentarExperiencia(Long usuarioId, int puntos) {
    Usuario usuario = usuarioRepository.findById(usuarioId)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    int expAnterior = Optional.ofNullable(usuario.getExperiencia()).orElse(0);
    int nuevaXP = expAnterior + puntos;
    usuario.setExperiencia(nuevaXP);

    int nivelAnterior = usuario.getNivel();
    int nivelCalculado = calcularNivelDesdeExperiencia(nuevaXP);

    if (nivelCalculado > nivelAnterior) {
      System.out.println("🎉 ¡Usuario ha subido de nivel! " + nivelAnterior + " → " + nivelCalculado);
    } else {
      System.out.println("🧪 Usuario no sube de nivel. Nivel actual: " + nivelAnterior + " | XP total: " + nuevaXP);
    }

    usuario.setNivel(nivelCalculado);

    Usuario guardado = usuarioRepository.save(usuario);
    System.out.println("✅ Usuario actualizado. Nivel: " + guardado.getNivel() + ", XP: " + guardado.getExperiencia());

    return guardado;
  }

  public List<String> generarConsejosCoach(String username) {
    Usuario usuario = obtenerUsuarioPorUsername(username);
    List<String> consejos = new ArrayList<>();

    if (usuario == null || usuario.getPlantilla() == null) {
      consejos.add("❌ No se pudo cargar tu plantilla.");
      return consejos;
    }

    List<JugadorLiga> plantilla = usuario.getPlantilla();
    List<JugadorLiga> titulares = plantilla.stream()
        .filter(JugadorLiga::isEsTitular)
        .toList();

    List<JugadorLiga> suplentes = plantilla.stream()
        .filter(j -> !j.isEsTitular())
        .toList();

    // Dinero
    if (usuario.getDinero().compareTo(new BigDecimal("5000000")) > 0) {
      consejos.add("💰 Tienes más de 5 millones sin gastar. ¡Aprovecha el mercado!");
    }

    // Plantilla incompleta
    if (titulares.size() < 5) {
      consejos.add("📋 Tienes menos de 5 titulares. Alinea un equipo completo para puntuar.");
    }

    // Suplentes excesivos
    if (suplentes.size() > 5) {
      consejos.add("🔁 Tienes demasiados suplentes. Considera vender alguno para fichar mejores titulares.");
    }

    // VIP
    if (usuario.getRole().name().equalsIgnoreCase("VIP")) {
      consejos.add("🏆 Eres VIP. Recuerda usar el análisis exclusivo del scouting.");
    }

    // Bajo rendimiento
    boolean algunoConBajaMedia = titulares.stream()
        .anyMatch(j -> j.getFp() != null && j.getFp() < 10);
    if (algunoConBajaMedia) {
      consejos.add("⚠️ Algunos titulares tienen bajo rendimiento. Mira sus estadísticas y considera cambios.");
    }

    return consejos;
  }

  public int calcularNivelDesdeExperiencia(int experiencia) {
    int nivel = 1;
    int xpAcumulada = 0;

    System.out.println("🔍 Calculando nivel para XP total: " + experiencia);

    while (experiencia >= xpAcumulada + (nivel * 10)) {
      xpAcumulada += nivel * 10;
      nivel++;
    }

    System.out.println("📈 Nivel calculado: " + nivel + " (XP acumulada: " + xpAcumulada + ")");
    return nivel;
  }

}
