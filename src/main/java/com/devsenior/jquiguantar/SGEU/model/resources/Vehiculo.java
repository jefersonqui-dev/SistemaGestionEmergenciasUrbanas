package com.devsenior.jquiguantar.SGEU.model.resources;

import com.devsenior.jquiguantar.SGEU.model.util.Ubicacion;

public class Vehiculo extends Recurso {
    private double nivelCombustible;
    private double consumoPorDistancia;
    private Ubicacion ubicacionActual;
    private Ubicacion ubicacionBase;

    public Vehiculo(String tipo, int id, double consumoPorDistancia, Ubicacion ubicacionBase) {
        super(tipo, id);// llama al constructor de la clase padre Recurso
        this.nivelCombustible = 100.0; // Tanque lleno al inicio
        this.consumoPorDistancia = consumoPorDistancia; // Litros por km
        this.ubicacionActual = ubicacionBase; // Inicialmente en la base
        this.ubicacionBase = ubicacionBase;
    }

    // getters
    public double getNivelCombustible() {
        return nivelCombustible;
    }

    public double getConsumoPorDistancia() {
        return consumoPorDistancia;
    }

    public Ubicacion getUbicacionActual() {
        return ubicacionActual;
    }

    public Ubicacion getUbicacionBase() {
        return ubicacionBase;
    }

    /**
     * Gasta combustible en base a la distancia recorrida por el vehiculo. El
     * gasto se calcula como la distancia recorrida multiplicada por el consumo
     * por distancia del vehiculo. Si el nivel de combustible es menor que el
     * gasto, se establece en cero.
     * 
     * @param distanciaRecorrida la distancia recorrida por el vehiculo
     * @see #getNivelCombustible()
     * @see #getConsumoPorDistancia()
     */
    public void gastarCombustible(double distanciaRecorrida) {
        double gasto = distanciaRecorrida * consumoPorDistancia;
        this.nivelCombustible = Math.max(0, nivelCombustible - gasto); // No bajar de cero
        // sout para ver si se esta gastando combustible
        System.out.println(getTipo() + " (ID: " + getId() + ") gastó " + String.format("%.2f", gasto)
                + " litros de combustible. Nivel actual: " + String.format("%.1f", nivelCombustible) + "%");
    }

    public void iniciarRepostaje() {
        setEstado(EstadoRecurso.REPOSTANDO);
        System.out.println(getTipo() + " (ID: " + getId() + ") Iniciando repostaje en " + ubicacionActual);
    }

    public void completarRepostaje() {
        this.nivelCombustible = 100.0; // Tanque lleno al finalizar
        setEstado(EstadoRecurso.DISPONIBLE);
        System.out.println(getTipo() + " (ID: " + getId() + ") Repostaje completado.Nivel de combustible: "
                + String.format("%.1f", nivelCombustible) + "%");
    }

    // Metodo para verificar si tiene suficiente combistible para un Viaje estimado
    public boolean tieneSuficienteCombustible(double distanciaEstimada) {
        return this.nivelCombustible >= (distanciaEstimada * consumoPorDistancia * 1.1); // Devuelve true si hay
                                                                                         // suficiente combustible
    }

    /**
     * Mueve el vehiculo a una ubicación dada, gastando combustible en base a la
     * distancia recorrida. El gasto se calcula como la distancia recorrida
     * multiplicada por el consumo por distancia del vehiculo. Si el nivel de
     * combustible es menor que el gasto, se establece en cero.
     * 
     * @param destino            la ubicación a la que se quiere mover el vehiculo
     * @param distanciaCalculada la distancia entre la ubicación actual y el destino
     * @see #gastarCombustible(double)
     */
    public void moverA(Ubicacion destino, double distanciaCalculada) {
        gastarCombustible(distanciaCalculada);
        this.ubicacionActual = destino; // Actualiza la ubicación
    }

    @Override
    public String toString() {
        return super.toString() + "(Combustible: " + String.format("%.1f", nivelCombustible) + "%,  Base: "
                + ubicacionBase + ")";
    }

}
