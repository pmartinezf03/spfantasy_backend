package com.spfantasy.backend.util;

import com.spfantasy.backend.model.Jugador;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class JugadorEstadisticasCalculator {

    public static void calcularEstadisticas(Jugador jugador) {
        if (jugador == null)
            return;

        // Variables base
        int pts = jugador.getPts() != null ? jugador.getPts() : 0;
        int min = jugador.getMin() != null ? jugador.getMin() : 0;
        int tl = jugador.getTl() != null ? jugador.getTl() : 0;
        int t2 = jugador.getT2() != null ? jugador.getT2() : 0;
        int t3 = jugador.getT3() != null ? jugador.getT3() : 0;
        int fp = jugador.getFp() != null ? jugador.getFp() : 0;

        // Cálculo de rendimiento (escala 0-10)
        double rendimiento = 0.0;
        rendimiento += pts * 0.4;
        rendimiento += fp * 0.2;
        rendimiento += min * 0.15;
        rendimiento += (t2 + t3 + tl) * 0.25;
        rendimiento = Math.min(rendimiento / 10.0, 10.0); // normalizar

        // Cálculo de puntos fantasy (más agresivo)
        int puntosFantasy = (int) Math.round(pts * 1.2 + t3 * 3 + t2 * 2 + tl + fp * 1.5);

        // Cálculo de precio (escala de 500K a 10M según rendimiento)
        BigDecimal precio = BigDecimal.valueOf(0.5 + rendimiento / 10 * 9.5).setScale(2, RoundingMode.HALF_UP); // 0.5M
                                                                                                                // - 10M
        precio = precio.multiply(BigDecimal.valueOf(1_000_000));

        // Seteo en el jugador
        jugador.setRendimiento(BigDecimal.valueOf(rendimiento).setScale(2, RoundingMode.HALF_UP));
        jugador.setPuntosTotales(puntosFantasy);
        jugador.setPrecioVenta(precio);
    }
}
