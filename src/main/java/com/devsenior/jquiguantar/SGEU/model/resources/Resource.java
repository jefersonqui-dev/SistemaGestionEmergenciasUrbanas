package com.devsenior.jquiguantar.SGEU.model.resources;

public class Resource {
    private int id;
    private String type;
    private String baseOrigin;
    private boolean available;
    private double fuel;
    private double consumptionByDistance;
    private double progress;
    private double totalTimeRequired;
    private boolean inProgress;

    public Resource(int id, String type, String baseOrigin, double consumptionByDistance) {
        this.id = id;
        this.type = type;
        this.baseOrigin = baseOrigin;
        this.available = true;
        this.consumptionByDistance = consumptionByDistance;
        this.fuel = 100.0; // Inicialmente con tanque lleno
        this.progress = 0.0;
        this.totalTimeRequired = 0.0;
        this.inProgress = false;
    }

    // Getters
    public int getId() { return id; }
    public String getType() { return type; }
    public String getBaseOrigin() { return baseOrigin; }
    public boolean isAvailable() { return available; }
    public double getFuel() { return fuel; }
    public double getConsumpionByDistance() { return consumptionByDistance; }
    public double getProgress() { return progress; }
    public double getTotalTimeRequired() { return totalTimeRequired; }
    public boolean isInProgress() { return inProgress; }

    // Setters
    public void setAvailable(boolean available) { this.available = available; }
    public void setFuel(double fuel) { this.fuel = Math.min(100.0, Math.max(0.0, fuel)); }
    
    public void startOperation(double totalTimeRequired) {
        this.totalTimeRequired = totalTimeRequired;
        this.progress = 0.0;
        this.inProgress = true;
        this.available = false;
    }

    public void updateProgress(double timeElapsed) {
        if (inProgress) {
            // Calcular el porcentaje de progreso basado en el tiempo transcurrido
            double progressIncrement = (timeElapsed / totalTimeRequired) * 100.0;
            this.progress = Math.min(100.0, this.progress + progressIncrement);
            
            // Actualizar el combustible
            updateFuel(timeElapsed);
            
            // Verificar si la operación está completa
            if (this.progress >= 100.0) {
                completeOperation();
            }
        }
    }

    private void updateFuel(double timeElapsed) {
        // Calcular el consumo de combustible por hora
        double hoursElapsed = timeElapsed / 60.0;
        double fuelConsumed = hoursElapsed * consumptionByDistance;
        setFuel(this.fuel - fuelConsumed);
    }

    public void completeOperation() {
        this.inProgress = false;
        this.progress = 100.0;
        this.available = true;
    }

    public String getStatus() {
        if (inProgress) {
            return String.format("En operación (%.1f%%)", progress);
        } else if (!available) {
            return "Asignado";
        } else {
            return "Disponible";
        }
    }
}