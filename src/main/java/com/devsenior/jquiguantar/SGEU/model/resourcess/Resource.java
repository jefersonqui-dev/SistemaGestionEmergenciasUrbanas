package com.devsenior.jquiguantar.SGEU.model.resourcess;

public class Resource {
    private int id;
    private String type;
    private String baseOrigin;
    private boolean available;
    private double fuel;
    private double consumptionByDistance;
    private double consumoBase; // Consumo base por minuto
    private double consumoPorDistancia; // Consumo por unidad de distancia
    private boolean enRecarga;
    private double tiempoRecarga; // Tiempo total para recargar (en minutos)
    private double tiempoRestanteRecarga; // Tiempo restante de recarga

    public Resource(int id, String type, String baseOrigin, double consumptionByDistance) {
        this.id = id;
        this.type = type;
        this.baseOrigin = baseOrigin;
        this.available = true;
        this.fuel = 100.0; // Inicialmente con tanque lleno
        this.consumptionByDistance = consumptionByDistance;
        this.consumoPorDistancia = consumptionByDistance;
        this.consumoBase = consumptionByDistance * 0.1; // 10% del consumo por distancia como consumo base
        this.enRecarga = false;
        this.tiempoRecarga = 1.0; // Cambiado de 5.0 a 1.0 minutos
        this.tiempoRestanteRecarga = 0.0;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getBaseOrigin() {
        return baseOrigin;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public double getFuel() {
        return fuel;
    }

    public void setFuel(double fuel) {
        this.fuel = Math.min(100.0, Math.max(0.0, fuel));
    }

    public double getConsumptionByDistance() {
        return consumptionByDistance;
    }

    public void consumirCombustible(double tiempo, double distancia) {
        // Consumo base por tiempo
        double consumoBase = this.consumoBase * tiempo;
        
        // Consumo por distancia recorrida
        double consumoDistancia = this.consumoPorDistancia * distancia;
        
        // Consumo total
        double consumoTotal = consumoBase + consumoDistancia;
        
        // Actualizar combustible
        this.fuel = Math.max(0.0, this.fuel - consumoTotal);
    }

    // Método para compatibilidad con el código existente
    public void consumirCombustible(double tiempo) {
        consumirCombustible(tiempo, 0.0);
    }

    public boolean necesitaRecarga() {
        return fuel < 20.0; // Necesita recarga si tiene menos del 20%
    }

    public void recargarCombustible() {
        this.fuel = 100.0;
    }

    public boolean isEnRecarga() {
        return enRecarga;
    }

    public void iniciarRecarga() {
        if (!enRecarga && fuel < 100.0) {
            enRecarga = true;
            tiempoRestanteRecarga = tiempoRecarga * ((100.0 - fuel) / 100.0);
        }
    }

    public void actualizarRecarga(double tiempoTranscurrido) {
        if (enRecarga) {
            tiempoRestanteRecarga -= tiempoTranscurrido;
            if (tiempoRestanteRecarga <= 0) {
                recargarCombustible();
                enRecarga = false;
                tiempoRestanteRecarga = 0.0;
            } else {
                // Calcular el porcentaje de recarga basado en el tiempo transcurrido
                double porcentajeRecarga = (tiempoTranscurrido / tiempoRecarga) * 100.0;
                fuel = Math.min(100.0, fuel + porcentajeRecarga);
            }
        }
    }

    public double getTiempoRestanteRecarga() {
        return tiempoRestanteRecarga;
    }

    public String getEstadoRecarga() {
        if (!enRecarga) {
            return "No está en recarga";
        }
        return String.format("En recarga: %.1f minutos restantes", tiempoRestanteRecarga);
    }

    @Override
    public String toString() {
        return "Resource [id=" + id +
                ", type=" + type +
                ", baseOrigin=" + baseOrigin +
                ", available=" + available +
                ", fuel=" + String.format("%.1f%%", fuel) +
                (enRecarga ? ", " + getEstadoRecarga() : "") + "]";
    }
}