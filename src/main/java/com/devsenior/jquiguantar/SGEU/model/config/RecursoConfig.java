package com.devsenior.jquiguantar.SGEU.model.config;

public class RecursoConfig {
    private String tipo;
    private int id;
    private double consumoPorDistancia;

    // getters
    public String getTipo() {
        return tipo;
    }

    public int getId() {
        return id;
    }

    public double getConsumoPorDistancia() {
        return consumoPorDistancia;
    }

    // setters
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setConsumoPorDistancia(double consumoPorDistancia) {
        this.consumoPorDistancia = consumoPorDistancia;
    }

    // constructor
    public RecursoConfig() {
    }
}
