package com.devsenior.jquiguantar.SGEU.model.patterns.factory;

import com.devsenior.jquiguantar.SGEU.model.util.Location;
import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergency;
import com.devsenior.jquiguantar.SGEU.model.emergencies.EmergencyType;
import com.devsenior.jquiguantar.SGEU.model.emergencies.SeverityLevel;

public class EmergencyFactory {
    public static Emergency createEmergency(EmergencyType type, SeverityLevel level, Location location, double timeResponse){
        switch (type) {
            case INCENDIO:
                return new Emergency(type, level, location, timeResponse);
            case ACCIDENTE_VEHICULAR:
                return new Emergency(type, level, location, timeResponse);
            case ROBO:
                return new Emergency(type, level, location, timeResponse);      
            default:
                throw new IllegalArgumentException("Tipo de Emergencia no soportado");
        }
    }
}
