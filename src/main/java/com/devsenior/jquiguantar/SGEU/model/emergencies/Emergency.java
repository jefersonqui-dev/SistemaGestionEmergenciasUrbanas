package com.devsenior.jquiguantar.SGEU.model.emergencies;

import com.devsenior.jquiguantar.SGEU.model.util.Location;

public class Emergency {

    private EmergencyType tipo;
    private SeverityLevel nivelGravedad;
    private Location ubicacion;
    private double tiempoEstimado;
    private boolean atendida;

    public Emergency(EmergencyType tipo, SeverityLevel nivelGravedad, Location ubicacion, double tiempoEstimado) {
        this.tipo = tipo;
        this.nivelGravedad = nivelGravedad;
        this.ubicacion = ubicacion;
        this.tiempoEstimado = tiempoEstimado;
        this.atendida = false;
    }

    public EmergencyType getTipo() {
        return tipo;
    }

    public SeverityLevel getNivelGravedad() {
        return nivelGravedad;
    }

    public Location getUbicacion() {
        return ubicacion;
    }

    public double getTiempoEstimado() {
        return tiempoEstimado;
    }

    public boolean isAtendida() {
        return atendida;
    }

    public void setAtendida(boolean atendida) {
        this.atendida = atendida;
    }

    @Override
    public String toString() {
        return "Emergencia [tipo=" + tipo +
                ", nivelGravedad=" + nivelGravedad +
                ", ubicacion=" + ubicacion +
                ", tiempoEstimado=" + tiempoEstimado +
                ", atendida=" + atendida + "]";
    }

}
