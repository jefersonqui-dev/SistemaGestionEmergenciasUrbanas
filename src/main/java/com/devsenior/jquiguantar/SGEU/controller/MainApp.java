package com.devsenior.jquiguantar.SGEU.controller;

// import com.devsenior.jquiguantar.SGEU.model.patterns.singleton.EmergencySistem;
import com.devsenior.jquiguantar.SGEU.view.ConsolaView;
import com.devsenior.jquiguantar.SGEU.model.util.Location;
import com.devsenior.jquiguantar.SGEU.model.emergencies.EmergencyType;
import com.devsenior.jquiguantar.SGEU.model.emergencies.SeverityLevel;
import com.devsenior.jquiguantar.SGEU.model.patterns.strategy.BasicTimeResponseStrategy;
import com.devsenior.jquiguantar.SGEU.model.patterns.strategy.PriorizationStrategy;
import com.devsenior.jquiguantar.SGEU.model.patterns.strategy.TimeCalculation;
import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergency;
import com.devsenior.jquiguantar.SGEU.model.patterns.singleton.EmergencySistem;
import com.devsenior.jquiguantar.SGEU.model.config.OperationalBase;
import com.devsenior.jquiguantar.SGEU.model.resourcess.Resource;
import com.devsenior.jquiguantar.SGEU.model.interfaces.Reply;
import com.devsenior.jquiguantar.SGEU.model.strategy.ReplyFirefighter;
import com.devsenior.jquiguantar.SGEU.model.patterns.strategy.PriorityByProximity;
import java.util.List;

public class MainApp {
    private static ConsolaView view;
    // private static EmergencySistem sistem;
    private static TimeCalculation timeCalculation;
    private static EmergencySistem sistem;
    private static PriorizationStrategy strategy;

    public static void main(String[] args) {
        view = new ConsolaView();
        timeCalculation = new BasicTimeResponseStrategy();
        sistem = EmergencySistem.getInstance();
        strategy = new PriorityByProximity();
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
        List<Emergency> active = sistem.getActiveEmergencies();
        List<OperationalBase> bases = sistem.getOperationalBases();
        List<Emergency> prioritize = strategy.prioritize(active, bases);

        Reply reply = new ReplyFirefighter();
        reply.respondToEmergency(prioritize.get(0));
    }
}
