package com.spfantasy.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "grupos_chat")
public class GrupoChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String descripcion;

    @Column(name = "password_grupo", nullable = true)
    private String passwordGrupo;

    @ManyToOne
    @JoinColumn(name = "creador_id", nullable = false)
    private Usuario creador; // Relación con el usuario que creó el grupo

    @ManyToMany
    @JoinTable(name = "usuarios_grupos", joinColumns = @JoinColumn(name = "grupo_id"), inverseJoinColumns = @JoinColumn(name = "usuario_id"))
    private Set<Usuario> usuarios = new HashSet<>(); // Usuarios dentro del grupo

}
