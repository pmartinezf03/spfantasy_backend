package com.spfantasy.backend.service;

import com.spfantasy.backend.config.JwtUtil;
import com.spfantasy.backend.model.Jugador;
import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.model.Role;
import com.spfantasy.backend.repository.UsuarioRepository;
import com.spfantasy.backend.repository.JugadorRepository;
import java.util.List;
import java.util.Optional;
import java.util.Collections;
import java.math.BigDecimal;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JugadorRepository jugadorRepository; // Asegúrate de inyectar el repositorio de jugadores

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil; // Inyección del utilitario JWT

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

                // ✅ Marcar como disponible y liberar propietario
                jugadorRepository.liberarJugador(jugadorAEliminar.getId());

                System.out.println("✅ Jugador vendido y liberado correctamente.");
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
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name()))
        );
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

    /**
     * Método para extraer el username desde un token JWT.
     * @param token el token JWT sin el prefijo "Bearer "
     * @return el username del usuario autenticado
     */
    public String obtenerUsernameDesdeToken(String token) {
        return jwtUtil.extractUsername(token);
    }
    
    
}
