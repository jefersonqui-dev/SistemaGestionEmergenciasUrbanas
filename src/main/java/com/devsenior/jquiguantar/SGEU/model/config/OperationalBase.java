package com.devsenior.jquiguantar.SGEU.model.config;
import com.devsenior.jquiguantar.SGEU.model.util.Location;

public class OperationalBase {
    private String id;
    private String name;
    private String serviceType;
    private Location location;
    private double averageSpeed; //km/h

    public OperationalBase(){
        switch (serviceType) {
            case "BOMBEROS":
                this.averageSpeed = 60.0;
                break;
            case "AMBULANCIA":
                this.averageSpeed = 80.0;
                break;
            case "POLICIA":
                this.averageSpeed = 50.0;
                break;
            default:
                this.averageSpeed = 50.0;
                break;
        }
    }
    //Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getServiceType() {
        return serviceType;
    }

    public Location getLocation() {
        return location;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }
    //Setters
    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
    public void setLocation(Location location) {
        this.location = location;
    }
    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }
    public double calculateTimeResponse(Location emergencyLocation){
        double distance = this.location.distanciaKm(emergencyLocation);
        return (distance / this.averageSpeed) * 60;
    }
    
}
