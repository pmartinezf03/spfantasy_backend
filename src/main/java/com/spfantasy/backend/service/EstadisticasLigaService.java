package com.spfantasy.backend.service;

import com.spfantasy.backend.model.JugadorLiga;
import com.spfantasy.backend.dto.JugadorLigaDTO;
import com.spfantasy.backend.repository.JugadorLigaRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class EstadisticasLigaService {

    @Autowired
    private JugadorLigaRepository jugadorLigaRepository;

    private static final Pageable TOP5 = PageRequest.of(0, 5);

    public List<JugadorLigaDTO> topT3(Long ligaId) {
        return jugadorLigaRepository.findTopByLiga_IdOrderByT3Desc(ligaId, TOP5)
                .stream().map(JugadorLigaDTO::new).collect(Collectors.toList());
    }

    public List<JugadorLigaDTO> topFp(Long ligaId) {
        return jugadorLigaRepository.findTopByLiga_IdOrderByFpDesc(ligaId, TOP5)
                .stream().map(JugadorLigaDTO::new).collect(Collectors.toList());
    }

    public List<JugadorLigaDTO> topRendimiento(Long ligaId) {
        List<JugadorLiga> jugadores = jugadorLigaRepository.findByLiga_Id(ligaId);
        return jugadores.stream()
                .filter(j -> j.getMin() > 0)
                .sorted((a, b) -> Double.compare((b.getFp() / (double) b.getMin()), (a.getFp() / (double) a.getMin())))
                .limit(5)
                .map(JugadorLigaDTO::new)
                .collect(Collectors.toList());
    }

    public List<JugadorLigaDTO> topPrecio(Long ligaId) {
        return jugadorLigaRepository.findTopByLiga_IdOrderByPrecioVentaDesc(ligaId, TOP5)
                .stream().map(JugadorLigaDTO::new).collect(Collectors.toList());
    }

    public List<JugadorLigaDTO> topMinutos(Long ligaId) {
        return jugadorLigaRepository.findTopByLiga_IdOrderByMinDesc(ligaId, TOP5)
                .stream().map(JugadorLigaDTO::new).collect(Collectors.toList());
    }

    public List<JugadorLigaDTO> topTl(Long ligaId) {
        return jugadorLigaRepository.findTopByLiga_IdOrderByTlDesc(ligaId, TOP5)
                .stream().map(JugadorLigaDTO::new).collect(Collectors.toList());
    }

    public Map<String, Object> compararUsuarioVsMedia(Long ligaId, Long usuarioId) {
        List<JugadorLiga> todos = jugadorLigaRepository.findByLiga_Id(ligaId);
        List<JugadorLiga> mios = jugadorLigaRepository.findByLiga_IdAndPropietario_Id(ligaId, usuarioId);

        int totalLiga = todos.stream().mapToInt(JugadorLiga::getFp).sum();
        int totalMios = mios.stream().mapToInt(JugadorLiga::getFp).sum();
        double media = todos.isEmpty() ? 0 : totalLiga / (double) todos.size();

        return Map.of(
                "usuarioPuntos", totalMios,
                "mediaLiga", media);
    }

    public List<JugadorLigaDTO> jugadoresMasUtilizados(Long ligaId) {
        return jugadorLigaRepository.findTopByLiga_IdOrderByEsTitularTrueDesc(ligaId, TOP5)
                .stream().map(JugadorLigaDTO::new).collect(Collectors.toList());
    }
}
