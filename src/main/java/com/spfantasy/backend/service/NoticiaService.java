package com.spfantasy.backend.service;

import com.spfantasy.backend.model.Noticia;
import com.spfantasy.backend.repository.NoticiaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticiaService {
    private final NoticiaRepository noticiaRepository;

    public NoticiaService(NoticiaRepository noticiaRepository) {
        this.noticiaRepository = noticiaRepository;
    }

    public List<Noticia> obtenerNoticias() {
        return noticiaRepository.findAll();
    }

    public Noticia crearNoticia(Noticia noticia) {
        return noticiaRepository.save(noticia);
    }
}
