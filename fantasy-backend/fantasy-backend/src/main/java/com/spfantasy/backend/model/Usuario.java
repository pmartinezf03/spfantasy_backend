package com.spfantasy.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.usuario;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal dinero = BigDecimal.valueOf(3000000);

    @ManyToMany
    @JoinTable(
            name = "plantilla_jugadores",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "jugador_id")
    )
    @JsonIgnore // 🔥 Evita la serialización infinita
    private List<Jugador> plantilla;

    @ManyToMany(mappedBy = "usuarios")
    private List<GrupoChat> grupos = new ArrayList<>();

    public List<GrupoChat> getGrupos() {
        return grupos;
    }

}
