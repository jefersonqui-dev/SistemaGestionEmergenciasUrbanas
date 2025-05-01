package com.devsenior.jquiguantar.SGEU.model.emergencies;

import com.devsenior.jquiguantar.SGEU.model.util.Ubicacion;

public class Incendio extends Emergencia {
    public Incendio(Ubicacion ubicacion, NivelGravedad nivelGravedad, long tiempoRespuestaEstimado) {
        super(TipoEmergencia.INCENDIO, ubicacion, nivelGravedad, tiempoRespuestaEstimado);
    }
}
