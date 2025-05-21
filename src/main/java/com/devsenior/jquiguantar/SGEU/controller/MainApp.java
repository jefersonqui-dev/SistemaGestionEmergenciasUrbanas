package com.devsenior.jquiguantar.SGEU.controller;

// import com.devsenior.jquiguantar.SGEU.model.patterns.singleton.EmergencySistem;
import com.devsenior.jquiguantar.SGEU.view.ConsolaView;
import com.devsenior.jquiguantar.SGEU.model.util.Location;
import com.devsenior.jquiguantar.SGEU.model.emergencies.EmergencyType;
import com.devsenior.jquiguantar.SGEU.model.emergencies.SeverityLevel;
import com.devsenior.jquiguantar.SGEU.model.patterns.strategy.BasicTimeResponseStrategy;
import com.devsenior.jquiguantar.SGEU.model.patterns.strategy.TimeCalculation;
import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergency;
import com.devsenior.jquiguantar.SGEU.model.patterns.singleton.EmergencySistem;

public class MainApp {
    private static ConsolaView view;
    // private static EmergencySistem sistem;
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
                    break;
                case 3:
                    break;
                case 4:
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
        double timeResponse = timeCalculation.CalculateTime(type, location);
        long tiempoEstimado = Math.round(timeResponse);
        Emergency newEmergency = new Emergency(type, level, location, tiempoEstimado);
        sistem.registerEmergency(newEmergency);
    }
}
