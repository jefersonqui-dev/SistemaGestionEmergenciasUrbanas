package com.devsenior.jquiguantar.SGEU.model.emergencies;

import com.devsenior.jquiguantar.SGEU.model.util.Ubicacion;

public class Robo extends Emergencia {
    /**
     * Constructor de la clase Robo.
     * 
     * @param ubicacion               La ubicación del robo.
     * @param nivelGravedad           El nivel de gravedad del robo.
     * @param tiempoRespuestaEstimado El tiempo estimado de respuesta para el robo.
     */
    public Robo(Ubicacion ubicacion, NivelGravedad nivelGravedad, long tiempoRespuestaEstimado) {
        super(TipoEmergencia.ROBO, ubicacion, nivelGravedad, tiempoRespuestaEstimado);
    }

}
