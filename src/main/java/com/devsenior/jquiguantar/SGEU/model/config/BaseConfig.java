package com.devsenior.jquiguantar.SGEU.model.config;

public class BaseConfig {
    private String id;
    private String nombre;
    private String tipoServicio;
    private LocationSettings ubicacion;

    // private List<ResourcesConfig> initialResources;
    public BaseConfig() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public LocationSettings getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(LocationSettings ubicacion) {
        this.ubicacion = ubicacion;
    }

}
