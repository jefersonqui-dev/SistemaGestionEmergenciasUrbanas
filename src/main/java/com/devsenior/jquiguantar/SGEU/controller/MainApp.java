package com.devsenior.jquiguantar.SGEU.controller;

import com.devsenior.jquiguantar.SGEU.view.ConsolaView;
import com.devsenior.jquiguantar.SGEU.model.util.Location;
import com.devsenior.jquiguantar.SGEU.model.emergencies.EmergencyType;
import com.devsenior.jquiguantar.SGEU.model.emergencies.SeverityLevel;
import com.devsenior.jquiguantar.SGEU.model.patterns.strategy.BasicTimeResponseStrategy;
import com.devsenior.jquiguantar.SGEU.model.patterns.strategy.TimeCalculation;
import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergency;
import com.devsenior.jquiguantar.SGEU.model.patterns.singleton.EmergencySistem;
import com.devsenior.jquiguantar.SGEU.model.resourcess.Resource;
import java.util.List;

public class MainApp {
    private static ConsolaView view;
    private static TimeCalculation timeCalculation;
    private static EmergencySistem sistem;

    public static void main(String[] args) {
        view = new ConsolaView();
        timeCalculation = new BasicTimeResponseStrategy();
        sistem = EmergencySistem.getInstance();
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
        Emergency newEmergency = new Emergency(type, level, location, timeCalculation.CalculateTime(type, location));
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
        if (!sistem.hasActiveEmergencies()) {
            view.showNoActiveEmergencies();
            return;
        }

        // Mostrar emergencias ordenadas por prioridad
        List<Emergency> orderedEmergencies = sistem.getEmergencyOrdered();
        if (orderedEmergencies.isEmpty()) {
            view.showMessaje("No hay emergencias activas para atender.");
            return;
        }

        view.displayEmergencySummary(orderedEmergencies);
        view.waitForEnter("Presione Enter para continuar...");

        // Obtener la emergencia de mayor prioridad
        Emergency highestPriority = orderedEmergencies.get(0);
        if (highestPriority == null) {
            view.showMessaje("No hay emergencias activas para atender.");
            return;
        }

        // Mostrar resumen de la emergencia
        view.showEmergencySummary(highestPriority);

        // Obtener y mostrar recursos disponibles y sugeridos
        List<Resource> available = sistem.getAvailableResourcesForEmergency(highestPriority);
        List<Resource> suggested = sistem.suggestResourcesForEmergency(highestPriority);
        
        if (available.isEmpty()) {
            view.showMessaje("No hay recursos disponibles para atender esta emergencia.");
            return;
        }

        view.showAvailableAndSuggestedResources(available, suggested);

        // Solicitar confirmación para asignar recursos sugeridos
        boolean confirm = view.requestConfirmation("¿Desea asignar los recursos sugeridos? (S/N): ");
        List<Resource> toAssign;
        if (confirm) {
            toAssign = suggested;
        } else {
            toAssign = view.requestResourceSelection(available);
        }

        // Asignar recursos y mostrar resumen
        if (sistem.assignResourcesToEmergency(highestPriority, toAssign)) {
            view.showAssignmentSummary(highestPriority, toAssign);
        } else {
            view.showMessaje("No se pudieron asignar los recursos a la emergencia.");
        }
    }
}
