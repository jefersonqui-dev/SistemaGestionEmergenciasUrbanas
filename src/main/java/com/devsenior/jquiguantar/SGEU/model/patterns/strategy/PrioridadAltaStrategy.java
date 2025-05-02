package com.devsenior.jquiguantar.SGEU.model.patterns.strategy;

import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergencia;
import com.devsenior.jquiguantar.SGEU.model.emergencies.NivelGravedad;

import java.util.List;

public class PrioridadAltaStrategy {
    @Override
    public Emergencia seleccionarSiguienteEmergencia(List<Emergencia> emergenciasActivas) {
        if (emergenciasActivas == null || emergenciasActivas.isEmpty()) {
            return null; // No hay emergencias activas
        }
        // Buscar emergencia con la gravedad mas alta
        Emergencia emergenciaPrioritaria = null;

        for (Emergencia emergencia : emergenciasActivas) {
            if (!emergencia.isAtendida()) {
                if (emergenciaPrioritaria == null) {
                    emergenciaPrioritaria = emergencia;
                } else {
                    // comparar niveles de gravedad
                    if (emergencia.getNivelGravedad() == NivelGravedad.ALTO
                            && emergenciaPrioritaria.getNivelGravedad() != NivelGravedad.ALTO) {
                        emergenciaPrioritaria = emergencia;
                    } else if (emergencia.getNivelGravedad() == NivelGravedad.MEDIO
                            && emergenciaPrioritaria.getNivelGravedad() == NivelGravedad.BAJO) {
                        emergenciaPrioritaria = emergencia;
                    }
                    // si ambos tienen la misma gravedad, o la nueva es menor, mantenemos la actual
                }
            }
        }
        return emergenciaPrioritaria; // Retorna la emergencia de mayor prioridad encontrada
    }
}
