package com.devsenior.jquiguantar.SGEU.model.emergencies;

import com.devsenior.jquiguantar.SGEU.model.util.Ubicacion;
import java.util.Date; //Usamos Date para los tiempos
//import java.util.concurrent.TimeUnit; //para calcular la diferencia de tiempos

public class Emergencia {
    // Atributos de la clase Emergencia
    private static int contadorId = 0; // Contador para asignar ID a cada emergencia
    private int id;
    private TipoEmergencia tipo;
    private Ubicacion ubicacion;
    private NivelGravedad nivelGravedad;
    private Date tiempoRegistro;
    private Date tiempoInicioAtencion;
    private long tiempoRespuestaEstimado; // en minutos
    private Date tiempoFinAtencion;
    private double progresoAtencion; // 0.0 a 100.0 %
    private boolean atendida;

    // Constructor
    public Emergencia(TipoEmergencia tipo, Ubicacion ubicacion, NivelGravedad nivelGravedad,
            long tiempoRespuestaEstimado) {
        this.id = ++contadorId; // Asignar ID único a la emergencia
        this.tipo = tipo;
        this.ubicacion = ubicacion;
        this.nivelGravedad = nivelGravedad;
        this.tiempoRegistro = new Date();
        this.tiempoRespuestaEstimado = tiempoRespuestaEstimado; // en minutos
        this.tiempoInicioAtencion = null;
        this.tiempoFinAtencion = null;
        this.progresoAtencion = 0.0;
        this.atendida = false;
    }

    // getters
    public int getId() {
        return id;
    }

    public TipoEmergencia getTipo() {
        return tipo;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public NivelGravedad getNivelGravedad() {
        return nivelGravedad;
    }

    public Date getTiempoRegistro() {
        return tiempoRegistro;
    }

    public Date getTiempoInicioAtencion() {
        return tiempoInicioAtencion;
    }

    public Date getTiempoFinAtencion() {
        return tiempoFinAtencion;
    }

    public long getTiempoRespuestaEstimado() {
        return tiempoRespuestaEstimado;
    }

    public double getProgresoAtencion() {
        return progresoAtencion;
    }

    public boolean isAtendida() {
        return atendida;
    }

    // setters (para atributos que se pueden modificar)

    public void setTiempoInicioAtencion(Date tiempoInicioAtencion) {
        this.tiempoInicioAtencion = tiempoInicioAtencion;
    }

    public void setTiempoFinAtencion(Date tiempoFinAtencion) {
        this.tiempoFinAtencion = tiempoFinAtencion;
        this.atendida = true; // Marcar como atendida al finalizar
        this.progresoAtencion = 100.0;
    }

    public void setProgresoAtencion(double progresoAtencion) {
        this.progresoAtencion = Math.min(100.0, progresoAtencion);
        if (this.progresoAtencion >= 100.0 && !this.atendida) {
            setTiempoFinAtencion(new Date()); // Si el progreso llega a 100%, se marca como atendida
        }
    }

    public void simularAvanceProgreso(double porcentajeAvance) {
        if (!this.atendida) {
            setProgresoAtencion(this.progresoAtencion + porcentajeAvance);
        }
    }

    public long calcularTiempoTotalAtencionMillis() {
        if (tiempoFinAtencion != null && tiempoInicioAtencion != null) {
            return tiempoFinAtencion.getTime() - tiempoInicioAtencion.getTime(); // en milisegundos
        } else {
            return -1; // Si no se ha iniciado o finalizado la atención, retornamos 0
        }
    }

    public long calcularTiempoRealRespuestaMillis() {
        if (tiempoRegistro != null && tiempoInicioAtencion != null) {
            return tiempoInicioAtencion.getTime() - tiempoRegistro.getTime(); // en milisegundos
        } else {
            return -1; // indica que aun no se ha atendido
        }
    }

    public long calcularTiempoDesdeRegistroMillis() {
        if (!atendida) {
            return new Date().getTime() - tiempoRegistro.getTime(); // en milisegundos
        } else {
            return calcularTiempoRealRespuestaMillis() + (tiempoFinAtencion.getTime() - tiempoInicioAtencion.getTime());
        }
    }

    @Override
    public String toString() {
        String estado = atendida ? "Resuelta" : "Pendiente";
        return "Emergencia [" +
                "  ID=" + id +
                ", Tipo=" + tipo +
                ", Ubicacion=" + ubicacion +
                ", Gravedad=" + nivelGravedad +
                ", Progreso=" + String.format("%.2f", progresoAtencion) + "%" +
                ", Estado=" + estado +
                ", Tiempo Estimado=" + tiempoRespuestaEstimado + " ms]";
    }

}
