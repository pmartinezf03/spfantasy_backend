package com.spfantasy.backend.controller;

import com.spfantasy.backend.model.Noticia;
import com.spfantasy.backend.service.NoticiaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/noticias")
@CrossOrigin(origins = "http://localhost:4200") // Permitir conexión con Angular
public class NoticiaController {  // Asegurar que el nombre coincide con el de la clase
    private final NoticiaService noticiaService;

    public NoticiaController(NoticiaService noticiaService) {
        this.noticiaService = noticiaService;
    }

    @GetMapping
    public List<Noticia> obtenerNoticias() {
        return noticiaService.obtenerNoticias();
    }

    @PostMapping
    public Noticia crearNoticia(@RequestBody Noticia noticia) {
        return noticiaService.crearNoticia(noticia);
    }
}
