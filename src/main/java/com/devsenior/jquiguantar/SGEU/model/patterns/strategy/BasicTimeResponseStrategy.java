package com.devsenior.jquiguantar.SGEU.model.patterns.strategy;

import com.devsenior.jquiguantar.SGEU.model.patterns.singleton.EmergencySistem;
import com.devsenior.jquiguantar.SGEU.model.util.Location;
import com.devsenior.jquiguantar.SGEU.model.util.Utilities;
import com.devsenior.jquiguantar.SGEU.model.emergencies.EmergencyType;
import com.devsenior.jquiguantar.SGEU.model.config.OperationalBase;
import com.devsenior.jquiguantar.SGEU.model.config.PredefinedLocation;
import java.util.OptionalDouble;
import java.util.Scanner;

public class BasicTimeResponseStrategy implements TimeCalculation {
    private EmergencySistem emergencySistem;
    private Scanner scanner;

    public BasicTimeResponseStrategy() {
        this.emergencySistem = EmergencySistem.getInstance();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public double CalculateTime(EmergencyType type, Location emergencyLocation) {
        Utilities.printTitle("Resumen del Incidente");
        System.out.println("Tipo de emergencia: " + type);
        
        // Buscar el nombre del lugar de la emergencia
        String locationName = emergencySistem.getReferencePoints().stream()
            .filter(point -> point.getLocation().getLatitude() == emergencyLocation.getLatitude() 
                && point.getLocation().getLongitude() == emergencyLocation.getLongitud())
            .map(PredefinedLocation::getNombre)
            .findFirst()
            .orElse("Ubicación no registrada");
            
        System.out.println("Ubicación: " + locationName + 
            " (Lat=" + String.format("%.6f", emergencyLocation.getLatitude()) + 
            ", Lon=" + String.format("%.6f", emergencyLocation.getLongitud()) + ")");

        String serviceTypeNeeded = mapEmergencyTypeToServiceType(type);
        System.out.println("Tipo de servicio requerido: " + serviceTypeNeeded);

        if (serviceTypeNeeded.isEmpty()) {
            System.err.println("Tipo de emergencia no mapeado");
            return -1.0;
        }

        System.out.println("\nBases operativas disponibles para " + serviceTypeNeeded + ":");
        emergencySistem.getOperationalBases().stream()
            .filter(base -> base.getServiceType().equals(serviceTypeNeeded))
            .forEach(base -> {
                System.out.println("- Base: " + base.getName());
                System.out.println("  Ubicación: Lat=" + String.format("%.6f", base.getLocation().getLatitude()) + 
                    ", Lon=" + String.format("%.6f", base.getLocation().getLongitud()));
                System.out.println("  Velocidad promedio: " + base.getAverageSpeed() + " km/h");
            });

        OptionalDouble minTime = emergencySistem.getOperationalBases().stream()
            .filter(base -> base.getServiceType().equals(serviceTypeNeeded))
            .mapToDouble(base -> calculateTimeResponse(base, emergencyLocation))
            .min();

        double result = minTime.orElse(-1.0);
        System.out.println("\nTiempo mínimo de respuesta calculado: " + String.format("%.2f", result) + " minutos");
        System.out.println("\nPresione Enter para confirmar Registro de Emergencia...");
        scanner.nextLine();
        Utilities.cleanConsole();
        
        return result;
    }

    private double calculateTimeResponse(OperationalBase base, Location emergencyLocation) {
        // Calculamos la distancia usando el método existente de Location
        double distance = base.getLocation().distanciaKm(emergencyLocation);
        System.out.println("- Cálculo para base " + base.getName() + ":");
        System.out.println("  Distancia calculada: " + String.format("%.2f", distance) + " km");
        
        // Usamos la velocidad promedio configurada para cada tipo de servicio
        double averageSpeed = base.getAverageSpeed();
        System.out.println("  Velocidad promedio: " + averageSpeed + " km/h");
        
        // Tiempo en horas = distancia / velocidad
        double timeInHours = distance / averageSpeed;
        // System.out.println("  Tiempo en horas: " + String.format("%.2f", timeInHours) + " h");
        
        // Convertimos a minutos
        double timeInMinutes = timeInHours * 60;
        // System.out.println("  Tiempo en minutos: " + String.format("%.2f", timeInMinutes) + " min");
        
        return timeInMinutes;
    }

    private String mapEmergencyTypeToServiceType(EmergencyType emergencyType) {
        switch (emergencyType) {
            case INCENDIO:
                return "BOMBEROS";
            case ACCIDENTE_VEHICULAR:
                return "AMBULANCIA";
            case ROBO:
                return "POLICIA";
            default:
                return "";
        }
    }
}
