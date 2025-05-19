package com.devsenior.jquiguantar.SGEU.model.config;

public class PredefinedLocation {
    private String id;
    private String nombre;
    private String description;
    private LocationSettings location;

    public PredefinedLocation() {

    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescription() {
        return description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(LocationSettings location) {
        this.location = location;
    }

    public LocationSettings getLocation() {
        return location;
    }
}
