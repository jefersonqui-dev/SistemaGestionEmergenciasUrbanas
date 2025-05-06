package com.devsenior.jquiguantar.SGEU.model.patterns.strategy;

import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergencia;

import java.util.List;

public interface PriorizacionStrategy {
    Emergencia seleccionarSiguienteEmergencia(List<Emergencia> emergenciasActivas);
}
