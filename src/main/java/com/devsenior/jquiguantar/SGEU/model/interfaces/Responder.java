package com.devsenior.jquiguantar.SGEU.model.interfaces;

import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergencia;

public interface Responder {
    void atenderEmergencia(Emergencia emergencia);

    void evaluarEstado(Emergencia emergencia);
}
