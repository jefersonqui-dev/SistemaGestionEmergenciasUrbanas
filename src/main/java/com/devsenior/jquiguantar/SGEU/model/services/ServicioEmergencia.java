package com.devsenior.jquiguantar.SGEU.model.services;

import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergencia;
import com.devsenior.jquiguantar.SGEU.model.interfaces.Responder;

public abstract class ServicioEmergencia implements Responder {
    private String tipo; // Ej: "Bomberos", "Ambulancia", "Policía"

    public ServicioEmergencia(String tipo) {
        this.tipo = tipo;
    }

    // Getter
    public String getTipo() {
        return tipo;
    }

    // Métodos de la interfaz Responder (se implementarán en clases concretas o
    // bases)
    @Override
    public abstract void atenderEmergencia(Emergencia emergencia);

    @Override
    public abstract void evaluarEstado(Emergencia emergencia);

    @Override
    public String toString() {
        return "Servicio: " + tipo;
    }
}