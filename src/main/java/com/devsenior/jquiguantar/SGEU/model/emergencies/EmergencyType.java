package com.devsenior.jquiguantar.SGEU.model.emergencies;

public enum EmergencyType {
    INCENDIO("Incendio", "Emergencia por fuego que requiere Bomberos"),
    ACCIDENTE_VEHICULAR("Accidente Vehicular", "Colisión o incidente en via publica"),
    ROBO("Robo", "Actividad delictiva que requiere presencia Policial");

    private final String description;
    private final String detalle;

    EmergencyType(String description, String detalle) {
        this.description = description;
        this.detalle = detalle;
    }

    public String getDescription() {
        return description;
    }

    public String getDetalle() {
        return detalle;
    }

    @Override
    public String toString() {
        return description;
    }
}
