package com.devsenior.jquiguantar.SGEU.model.emergencies;

import com.devsenior.jquiguantar.SGEU.model.util.Location;

public class Fire extends Emergency {
    public Fire(Location ubicacion, SeverityLevel nivelGravedad, long tiempoEstimado) {
        super(EmergencyType.INCENDIO, nivelGravedad, ubicacion, tiempoEstimado);
    }
}
