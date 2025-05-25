package com.devsenior.jquiguantar.SGEU.model.emergencies;

import com.devsenior.jquiguantar.SGEU.model.util.Location;

public class Heist extends Emergency {
    public Heist(Location ubicacion, SeverityLevel nivelGravedad, long tiempoEstimado) {
        super(EmergencyType.ROBO, nivelGravedad, ubicacion, tiempoEstimado);
    }
}