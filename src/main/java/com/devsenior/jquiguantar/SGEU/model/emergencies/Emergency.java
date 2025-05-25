package com.devsenior.jquiguantar.SGEU.model.emergencies;

import com.devsenior.jquiguantar.SGEU.model.util.Location;
import com.devsenior.jquiguantar.SGEU.model.resourcess.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Emergency {

    private EmergencyType tipo;
    private SeverityLevel nivelGravedad;
    private Location ubicacion;
    private double tiempoEstimado;
    private boolean atendida;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaAtencion;
    private List<Resource> recursosAsignados;
    private double tiempoRestante;
    private EstadoEmergencia estado;

    public enum EstadoEmergencia {
        PENDIENTE,
        EN_ATENCION,
        ATENDIDA,
        CANCELADA
    }

    public Emergency(EmergencyType tipo, SeverityLevel nivelGravedad, Location ubicacion, double tiempoEstimado) {
        this.tipo = tipo;
        this.nivelGravedad = nivelGravedad;
        this.ubicacion = ubicacion;
        this.tiempoEstimado = tiempoEstimado;
        this.atendida = false;
        this.fechaRegistro = LocalDateTime.now();
        this.recursosAsignados = new ArrayList<>();
        this.tiempoRestante = tiempoEstimado;
        this.estado = EstadoEmergencia.PENDIENTE;
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
        if (atendida) {
            this.estado = EstadoEmergencia.ATENDIDA;
            this.fechaAtencion = LocalDateTime.now();
        }
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public LocalDateTime getFechaAtencion() {
        return fechaAtencion;
    }

    public List<Resource> getRecursosAsignados() {
        return recursosAsignados;
    }

    public void asignarRecursos(List<Resource> recursos) {
        this.recursosAsignados.addAll(recursos);
        this.estado = EstadoEmergencia.EN_ATENCION;
    }

    public void liberarRecursos() {
        for (Resource recurso : recursosAsignados) {
            recurso.setAvailable(true);
        }
        this.recursosAsignados.clear();
    }

    public double getTiempoRestante() {
        return tiempoRestante;
    }

    public void actualizarTiempoRestante(double tiempoTranscurrido) {
        this.tiempoRestante = Math.max(0, this.tiempoRestante - tiempoTranscurrido);
        if (this.tiempoRestante <= 0) {
            this.setAtendida(true);
        }
    }

    public EstadoEmergencia getEstado() {
        return estado;
    }

    public void setEstado(EstadoEmergencia estado) {
        this.estado = estado;
    }

    public double calcularConsumoCombustible() {
        return recursosAsignados.stream()
            .mapToDouble(r -> r.getConsumptionByDistance() * tiempoEstimado)
            .sum();
    }

    @Override
    public String toString() {
        return "Emergencia [tipo=" + tipo +
                ", nivelGravedad=" + nivelGravedad +
                ", ubicacion=" + ubicacion +
                ", tiempoEstimado=" + tiempoEstimado +
                ", tiempoRestante=" + tiempoRestante +
                ", estado=" + estado +
                ", atendida=" + atendida + "]";
    }

}
