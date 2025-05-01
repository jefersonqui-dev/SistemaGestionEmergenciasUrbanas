package com.devsenior.jquiguantar.SGEU.model.emergencies;

import com.devsenior.jquiguantar.SGEU.model.util.Ubicacion;

public class AccidenteVehicular extends Emergencia {
    public AccidenteVehicular(Ubicacion ubicacion, NivelGravedad nivelGravedad, long tiempoRespuestaEstimado) {
        super(TipoEmergencia.ACCIDENTE_VEHICULAR, ubicacion, nivelGravedad, tiempoRespuestaEstimado);
    }
}
