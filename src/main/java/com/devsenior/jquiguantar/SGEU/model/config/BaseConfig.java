package com.devsenior.jquiguantar.SGEU.model.config;

import java.util.List;

public class BaseConfig {
    private String id;
    private String nombre;
    private String tipoServicio;
    private UbicacionConfig ubicacion;
    private List<RecursoConfig> recursosIniciales;

    // Getters
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public UbicacionConfig getUbicacion() {
        return ubicacion;
    }

    public List<RecursoConfig> getRecursosIniciales() {
        return recursosIniciales;
    }

    // constructor
    public BaseConfig() {
    }
}
