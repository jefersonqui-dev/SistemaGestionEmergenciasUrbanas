package com.devsenior.jquiguantar.SGEU.model.resources;

import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergencia;

public class Recurso {
    private String tipo; // Ej: Camion de Bomberos
    private int id;
    private EstadoRecurso estado;
    private Emergencia emergenciaAsignada;

    // Constructor
    public Recurso(String tipo, int id) {
        this.tipo = tipo;
        this.id = id;
        this.estado = EstadoRecurso.DISPONIBLE;
        this.emergenciaAsignada = null;
    }

    // Getters y Setters
    public String getTipo() {
        return tipo;
    }

    public int getId() {
        return id;
    }

    public EstadoRecurso getEstado() {
        return estado;
    }

    public Emergencia getEmergenciaAsignada() {
        return emergenciaAsignada;
    }

    public void setEstado(EstadoRecurso estado) {
        this.estado = estado;
    }

    public void asignarEmergencia(Emergencia emergencia) {
        this.emergenciaAsignada = emergencia;
        this.estado = EstadoRecurso.OCUPADO; // Cambia el estado a OCUPADO al asignar una emergencia
    }

    public void liberar() {
        this.emergenciaAsignada = null; // Libera la emergencia asignada
        this.estado = EstadoRecurso.DISPONIBLE; // Cambia el estado a DISPONIBLE al liberar el recurso
    }

    @Override
    public String toString() {
        String asignado = (emergenciaAsignada != null) ? "Asignado a emergencia ID: " + emergenciaAsignada.getId()
                : "No asignado";
        return tipo + " [ID: " + id + ", Estado =  " + estado + asignado + "]";
    }

}