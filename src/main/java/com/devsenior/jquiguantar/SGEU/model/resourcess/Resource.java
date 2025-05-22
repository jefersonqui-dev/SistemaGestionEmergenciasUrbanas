package com.devsenior.jquiguantar.SGEU.model.resourcess;

public class Resource {
    private int id;
    private String type;
    private String baseOrigin;
    private boolean available;
    private double fuel;
    private double consumptionByDistance;


    public Resource(int id, String type, String baseOrigin, double consumptionByDistance){
        this.id = id;
        this.type = type;
        this.baseOrigin = baseOrigin;
        this.available = true;
        this.consumptionByDistance = consumptionByDistance;
    }
    //getters y setters
    public int getId(){return id;}
    public String getType(){return type;}
    public String getBaseOrigin(){return baseOrigin;}
    public boolean isAvailable(){return available;}
    public double getFuel(){return fuel;}
    public double getConsumpionByDistance(){return consumptionByDistance;}


    public void setAvailable(boolean available){this.available = available;}
    public void setFuel(double fuel){this.fuel = fuel;}

    
}