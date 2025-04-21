package com.spfantasy.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.spfantasy.backend.config.JwtUtil;
import com.spfantasy.backend.dto.UsuarioDTO;
import com.spfantasy.backend.model.Jugador;
import com.spfantasy.backend.model.JugadorLiga;
import com.spfantasy.backend.model.Liga;
import com.spfantasy.backend.model.Role;
import com.spfantasy.backend.model.Transaccion;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.repository.JugadorLigaRepository;
import com.spfantasy.backend.repository.JugadorRepository;
import com.spfantasy.backend.repository.TransaccionRepository;
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
  private TransaccionService transaccionService;

  private TransaccionRepository transaccionRepository;

  public Usuario registrarUsuario(Usuario usuario) {
    usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
    usuario.setRole(Role.usuario);
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

  public boolean comprarJugador(String username, Jugador jugador) {
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
        long suplentes = usuario.getPlantilla().stream().filter(j -> !j.getTitular()).count();

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
        jugadorRepository.save(jugador); // ✅ Se guarda el cambio en la base de datos

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

    // 🔥 Registrar transacción (compra directa)
    Transaccion transaccion = new Transaccion();
    transaccion.setFecha(LocalDateTime.now());
    transaccion.setJugador(jugador);
    transaccion.setComprador(usuario);
    transaccion.setPrecio(jugador.getPrecioVenta().intValue());
    transaccion.setVendedor(null); // No hay vendedor porque es del mercado libre
    transaccion.setLiga(jugador.getLiga());

    transaccionRepository.save(transaccion);

    return true;
  }

  public boolean venderJugador(String username, Jugador jugador) {
    Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

    if (usuarioOpt.isPresent()) {
      Usuario usuario = usuarioOpt.get();
      Optional<Jugador> jugadorEnPlantilla = usuario.getPlantilla().stream()
          .filter(j -> j.getId().equals(jugador.getId()))
          .findFirst();

      if (jugadorEnPlantilla.isPresent()) {
        Jugador jugadorAEliminar = jugadorEnPlantilla.get();
        usuario.getPlantilla().remove(jugadorAEliminar);
        usuario.setDinero(usuario.getDinero().add(jugadorAEliminar.getPrecioVenta()));

        usuarioRepository.save(usuario);
        jugadorRepository.liberarJugador(jugadorAEliminar.getId());

        // ✅ REGISTRAR TRANSACCIÓN
        Transaccion transaccion = new Transaccion();
        transaccion.setFecha(LocalDateTime.now());
        transaccion.setPrecio(jugadorAEliminar.getPrecioVenta().intValue());
        transaccion.setJugador(null); // no es jugadorLiga, omítelo o añade lógica
        transaccion.setComprador(null); // no hay comprador porque es al mercado
        transaccion.setVendedor(usuario);
        transaccion.setLiga(null); // si quieres asignar liga puedes hacerlo también

        transaccionService.guardarTransaccion(transaccion);

        return true;
      }
    }

    return false;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Usuario usuario = usuarioRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

    return new org.springframework.security.core.userdetails.User(
        usuario.getUsername(),
        usuario.getPassword(),
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name())));
  }

  public boolean guardarPlantilla(String username, List<Long> titularesIds, List<Long> suplentesIds) {
    Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

    if (usuarioOpt.isPresent()) {
      Usuario usuario = usuarioOpt.get();
      List<Jugador> nuevaPlantilla = new ArrayList<>();

      // Verificar que los titulares sean exactamente 5
      if (titularesIds.size() != 5) {
        System.out.println("❌ Error: Se intentó guardar una plantilla sin 5 titulares.");
        return false;
      }

      // Asignar titulares
      for (Long id : titularesIds) {
        Optional<Jugador> jugadorOpt = jugadorRepository.findById(id);
        jugadorOpt.ifPresent(j -> {
          j.setTitular(true);
          nuevaPlantilla.add(j);
        });
      }

      // Asignar suplentes
      for (Long id : suplentesIds) {
        Optional<Jugador> jugadorOpt = jugadorRepository.findById(id);
        jugadorOpt.ifPresent(j -> {
          j.setTitular(false);
          nuevaPlantilla.add(j);
        });
      }

      usuario.setPlantilla(nuevaPlantilla);
      usuarioRepository.save(usuario);
      System.out.println("✅ Plantilla guardada para el usuario: " + username);
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
    return usuarioRepository.findById(id).orElse(null);
  }

  public Usuario buscarPorAlias(String alias) {
    return usuarioRepository.findByAlias(alias)
        .orElseThrow(() -> new RuntimeException("Alias no encontrado"));
  }

}
