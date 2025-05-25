package com.devsenior.jquiguantar.SGEU.model.emergencies;

import com.devsenior.jquiguantar.SGEU.model.util.Location;

public class CarAccident extends Emergency {
    public CarAccident(Location ubicacion, SeverityLevel gravedad, long tiempoEstimado) {
        super(EmergencyType.ACCIDENTE_VEHICULAR, gravedad, ubicacion, tiempoEstimado);
    }
}
