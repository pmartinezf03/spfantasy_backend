package com.spfantasy.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spfantasy.backend.model.Equipo;
import com.spfantasy.backend.repository.EquipoRepository;

@Service
public class EquipoService {
    @Autowired
    private EquipoRepository equipoRepository;

    public List<Equipo> obtenerEquipos() {
        return equipoRepository.findAll();
    }

    public Equipo guardarEquipo(Equipo equipo) {
        return equipoRepository.save(equipo);
    }

    public void eliminarEquipo(Long id) {
        equipoRepository.deleteById(id);
    }
}
