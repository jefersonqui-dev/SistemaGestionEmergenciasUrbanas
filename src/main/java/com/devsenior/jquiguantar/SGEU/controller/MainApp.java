package com.devsenior.jquiguantar.SGEU.controller;

// import com.devsenior.jquiguantar.SGEU.model.patterns.singleton.EmergencySistem;
import com.devsenior.jquiguantar.SGEU.view.ConsolaView;
import com.devsenior.jquiguantar.SGEU.model.util.Location;
import com.devsenior.jquiguantar.SGEU.model.emergencies.EmergencyType;
import com.devsenior.jquiguantar.SGEU.model.emergencies.SeverityLevel;
import com.devsenior.jquiguantar.SGEU.model.patterns.strategy.BasicTimeResponseStrategy;
import com.devsenior.jquiguantar.SGEU.model.patterns.strategy.TimeCalculation;
import com.devsenior.jquiguantar.SGEU.model.resources.Resource;
import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergency;
import com.devsenior.jquiguantar.SGEU.model.patterns.singleton.EmergencySistem;
import com.devsenior.jquiguantar.SGEU.model.patterns.factory.EmergencyFactory;
import com.devsenior.jquiguantar.SGEU.model.patterns.factory.ConcreteEmergencyFactory;
import java.util.List;
import com.devsenior.jquiguantar.SGEU.model.patterns.strategy.PriorizationStrategy;
import com.devsenior.jquiguantar.SGEU.model.patterns.strategy.SeverityBasedPriorization;
import java.util.Scanner;

public class MainApp {
    private static ConsolaView view;
    private static TimeCalculation timeCalculation;
    private static EmergencySistem sistem;
    private static EmergencyFactory emergencyFactory;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        view = new ConsolaView();
        timeCalculation = new BasicTimeResponseStrategy();
        sistem = EmergencySistem.getInstance();
        emergencyFactory = new ConcreteEmergencyFactory();
        int mainOption;
        do {
            view.showMenu();
            mainOption = view.requestOption();
            switch (mainOption) {
                case 1:
                    registerNewEmergency();
                    break;
                case 2:
                    showEmergenciesActive();
                    break;
                case 3:
                    showResourcesStatus();
                    break;
                case 4:
                    handleEmergencies();
                    break;
                case 5:
                    break;
                case 6:
                    break;

                default:
                    break;
            }
        } while (mainOption != 6);
    }

    private static void registerNewEmergency() {
        EmergencyType type = view.requestEmergencyType();
        Location location = view.requestLocation();
        SeverityLevel level = view.requestSeverityLevel();
        double estimatedTime = timeCalculation.CalculateTime(type, location);
        Emergency newEmergency = emergencyFactory.createEmergency(type, level, location, estimatedTime);
        sistem.registerEmergency(newEmergency);
    }
    private static void showEmergenciesActive() {
        List<Emergency> activeEmergencies = sistem.getActiveEmergencies();
        view.displayActiveEmergencies(activeEmergencies);
    }
    private static void showResourcesStatus() {
        List<Resource> resources = sistem.getAllResources();
        view.showResources(resources);
    }
    private static void handleEmergencies() {
        List<Emergency> activeEmergencies = sistem.getActiveEmergencies();
        
        if (activeEmergencies.isEmpty()) {
            view.showMessage("No hay emergencias activas para atender.");
            return;
        }

        // Crear e implementar la estrategia de priorización
        PriorizationStrategy priorizationStrategy = new SeverityBasedPriorization();
        List<Emergency> prioritizedEmergencies = priorizationStrategy.priorizeEmergencies(activeEmergencies);

        // Procesar cada emergencia según su prioridad
        for (Emergency emergency : prioritizedEmergencies) {
            if (!emergency.isAtendida()) {
                // Notificar a los servicios correspondientes
                sistem.notifyServices(emergency);
                
                // Mostrar información de la emergencia siendo atendida
                view.showMessage("\nAtendiendo emergencia: " + emergency.toString());
                
                // Obtener y asignar recursos
                List<Resource> assignedResources = sistem.getAssignedResources(emergency);
                
                // Iniciar operación para cada recurso asignado
                for (Resource resource : assignedResources) {
                    resource.startOperation(emergency.getTiempoEstimado());
                }
                
                // Mostrar recursos asignados
                view.showAssignedResources(assignedResources);
                
                // Simular progreso de la operación
                simulateOperationProgress(assignedResources, emergency.getTiempoEstimado());
                
                // Marcar emergencia como atendida
                emergency.setAtendida(true);
                
                // Mostrar resumen final
                view.showMessage("\nEmergencia atendida exitosamente.");
                view.showMessage("Presione Enter para continuar...");
                scanner.nextLine();
            }
        }
    }

    private static void simulateOperationProgress(List<Resource> resources, double totalTime) {
        double timeElapsed = 0;
        double timeStep = 1.0; // 1 minuto por paso
        int updateInterval = 5; // Actualizar cada 5 minutos simulados

        view.showMessage("\nIniciando atención de emergencia...");
        view.showMessage("Tiempo estimado: " + totalTime + " minutos");
        view.showMessage("Simulando progreso...\n");

        // Mostrar estado inicial
        view.showMessage("Estado inicial de los recursos:");
        view.showResources(sistem.getAllResources());
        view.showMessage("\nPresione Enter para comenzar la simulación...");
        scanner.nextLine();

        while (timeElapsed < totalTime) {
            // Actualizar progreso de cada recurso
            for (Resource resource : resources) {
                resource.updateProgress(timeStep);
            }

            // Mostrar estado actual solo cada cierto intervalo
            if (timeElapsed % updateInterval == 0 || timeElapsed == 0) {
                view.showMessage(String.format("\nTiempo transcurrido: %.0f minutos", timeElapsed));
                view.showMessage("Estado actual de los recursos:");
                view.showResources(sistem.getAllResources());
                
                // Solo pedir Enter en los intervalos de actualización
                if (timeElapsed > 0) {
                    view.showMessage("\nPresione Enter para continuar...");
                    scanner.nextLine();
                }
            }
            
            // Esperar un momento para simular el paso del tiempo
            try {
                Thread.sleep(500); // Medio segundo de espera
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            timeElapsed += timeStep;
        }

        // Mostrar estado final
        view.showMessage("\nOperación completada!");
        view.showMessage("Estado final de los recursos:");
        view.showResources(sistem.getAllResources());
    }
}
