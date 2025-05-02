package com.devsenior.jquiguantar.SGEU.model.patterns.factory;

import com.devsenior.jquiguantar.SGEU.model.emergencies.*;
import com.devsenior.jquiguantar.SGEU.model.util.Ubicacion;

public class EmergenciaFactory {
    // Metodo estatico para crear diferentes tipoos de Emergencias
    // Recibe los datos necesarios y retorna la instancia correcta
    public static Emergencia crearEmergencia(TipoEmergencia tipo, Ubicacion ubicacion, NivelGravedad nivelGravedad,
            long tiempoRespuestaEstimado) {
        switch (tipo) {
            case INCENDIO:
                return new Incendio(ubicacion, nivelGravedad, tiempoRespuestaEstimado);
            case ACCIDENTE_VEHICULAR:
                return new AccidenteVehicular(ubicacion, nivelGravedad, tiempoRespuestaEstimado);
            case ROBO:
                return new Robo(ubicacion, nivelGravedad, tiempoRespuestaEstimado);
            case OTROS:
                return new Emergencia(TipoEmergencia.OTROS, ubicacion, nivelGravedad, tiempoRespuestaEstimado);
            default:
                throw new IllegalArgumentException("Tipo de emergencia no soportado: " + tipo);
        }
    }
}
