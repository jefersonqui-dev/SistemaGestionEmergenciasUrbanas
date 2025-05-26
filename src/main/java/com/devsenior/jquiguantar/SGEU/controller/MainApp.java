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
import java.util.Timer;
import java.util.TimerTask;
import java.util.Scanner;

public class MainApp {
    private static ConsolaView view;
    private static TimeCalculation timeCalculation;
    private static EmergencySistem sistem;
    private static Timer timer;
    private static final long UPDATE_INTERVAL = 1000; // Actualizar cada segundo
    private static Scanner scanner;

    public static void main(String[] args) {
        view = new ConsolaView();
        timeCalculation = new BasicTimeResponseStrategy();
        sistem = EmergencySistem.getInstance();
        scanner = new Scanner(System.in);
        iniciarTemporizador();
        
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
                    showStatistics();
                    break;
                case 6:
                    detenerTemporizador();
                    break;
                default:
                    break;
            }
            // if (scanner.hasNextLine()) scanner.nextLine();
        } while (mainOption != 6);
    }

    private static void iniciarTemporizador() {
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sistem.actualizarEstadoEmergencias(1.0/60.0); // Actualizar cada segundo (1/60 de minuto)
            }
        }, 0, UPDATE_INTERVAL);
    }

    private static void detenerTemporizador() {
        if (timer != null) {
            timer.cancel();
        }
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
        while (true) {
            if (!sistem.hasActiveEmergencies()) {
                view.showNoActiveEmergencies();
                return;
            }

            List<Emergency> orderedEmergencies = sistem.getEmergencyOrdered();
            if (orderedEmergencies.isEmpty()) {
                view.showMessaje("No hay emergencias activas para atender.");
                return;
            }

            view.displayEmergencySummary(orderedEmergencies);
            view.mostrarOpcionesSeleccionEmergencia();
            
            int opcionSeleccion = view.solicitarOpcionSeleccionEmergencia();
            Emergency selectedEmergency;

            switch (opcionSeleccion) {
                case 1:
                    selectedEmergency = sistem.seleccionarEmergenciaPorPrioridad(orderedEmergencies);
                    break;
                case 2:
                    int numEmergencia = view.solicitarEmergenciaManual(orderedEmergencies);
                    selectedEmergency = sistem.seleccionarEmergenciaManual(orderedEmergencies, numEmergencia);
                    if (selectedEmergency == null) {
                        view.showMessaje("Número de emergencia no válido.");
                        view.waitForEnter("Presione Enter para continuar...");
                        continue;
                    }
                    break;
                case 3:
                    return;
                default:
                    view.showMessaje("Opción no válida");
                    view.waitForEnter("Presione Enter para continuar...");
                    continue;
            }

            List<Resource> available = sistem.getAvailableResourcesForEmergency(selectedEmergency);
            List<Resource> suggested = sistem.suggestResourcesForEmergency(selectedEmergency);
            
            if (available.isEmpty()) {
                view.mostrarMensajeNoHayRecursos();
                continue;
            }

            view.mostrarProcesoAsignacionRecursos(selectedEmergency, available, suggested);
            List<Resource> toAssign = view.solicitarRecursosParaAsignacion(available, suggested);
            
            boolean exito = sistem.procesarAsignacionRecursos(selectedEmergency, toAssign);
            view.mostrarResultadoAsignacion(selectedEmergency, toAssign, exito);
        }
    }

    private static void showStatistics() {
        view.mostrarEstadisticas();
    }
}
