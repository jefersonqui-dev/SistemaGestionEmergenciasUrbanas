package com.devsenior.jquiguantar.SGEU.view;

// import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.devsenior.jquiguantar.SGEU.model.config.PredefinedLocation;
import com.devsenior.jquiguantar.SGEU.model.emergencies.EmergencyType;
import com.devsenior.jquiguantar.SGEU.model.util.Location;
import com.devsenior.jquiguantar.SGEU.model.config.LocationSettings;
import com.devsenior.jquiguantar.SGEU.model.util.Utilities;
import com.devsenior.jquiguantar.SGEU.model.patterns.singleton.EmergencySistem;
import com.devsenior.jquiguantar.SGEU.model.emergencies.SeverityLevel;


public class ConsolaView {
    private Scanner scanner;
    private EmergencySistem sistem;

    public ConsolaView() {
        this.scanner = new Scanner(System.in);
        this.sistem = EmergencySistem.getInstance();
    }

    public void showMenu() {
        Utilities.printTitle("Sistea de Gestión de Emergencias Urbanas");
        System.out.println(
                "1. Registrar Nueva emergencia\n" +
                        "2. Ver Estado Actual de emergencia\n" +
                        "3. Ver Estado Actual de Recursos\n" +
                        "4. Gestionar Emergencias Activas\n" +
                        "5. Mostrar Estadisticas del dia\n" +
                        "6. Finalizar Jornada\n");
    }

    public EmergencyType requestEmergencyType() {
        Utilities.cleanConsole();
        Utilities.printTitle("Registrar Nueva Emergencia", "Tipo");
        // Usamos Stream API
        List<EmergencyType> types = Arrays.asList(EmergencyType.values());

        // Mostramos las opciones
        types.forEach(type -> {
            String option = String.format("%d. %s:",
                    types.indexOf(type) + 1,
                    type.getDescription());
            String detalle = String.format("   %s\n", type.getDetalle());
            showMessaje(option);
            showMessaje(detalle);
        });

        // Solicitamos al usuario y validamos la seleccion

        while (true) {
            try {
                int option = requestInteger("Ingrese el Tipo de Emergencia: ");
                if (option > 0 && option <= types.size()) {
                    return types.get(option - 1);
                } else {
                    showMessaje("Opcion No valida. Por favor Ingrese un numero entero entre 1 y " + types.size());
                }
            } catch (NumberFormatException e) {
                showMessaje("Por favor, Ingrese un Número Válido");
            }
        }

    }

    public Location requestLocation() {
        Utilities.cleanConsole();
        Utilities.printTitle("Registrar Nueva Emergencia ", "Ubicacion");
        List<PredefinedLocation> points = sistem.getReferencePoints();
        
        // Mostrar puntos de referencia
        for (int i = 0; i < points.size(); i++) {
            PredefinedLocation point = points.get(i);
            showMessaje(String.format("%d. %s - %s",
                    i + 1, point.getNombre(), point.getDescription()));
        }

        while(true) {
            try {
                int option = requestInteger("Ingrese el Lugar de la Emergencia(1-" + points.size() + "): ");
                
                if (option >= 1 && option <= points.size()) {
                    PredefinedLocation selectedPoint = points.get(option - 1);
                    LocationSettings locationSettings = selectedPoint.getLocation();
                    showMessaje(String.format("Ubicacion seleccionada: %s", selectedPoint.getNombre()));
                    showMessaje("Presione Enter para continuar...");
                    scanner.nextLine(); // Consumir la linea
                    Utilities.cleanConsole();
                    return new Location(locationSettings.getLatitude(), locationSettings.getLongitude());
                } else {
                    showMessaje("Error: Por favor ingrese un numero entre 1 y " + points.size());
                }
            } catch (Exception e) {
                showMessaje("Error: Por favor ingrese un numero valido");
            }
        }
    }

    public int requestInteger(String messaje) {
        System.out.print(messaje + " ");
        while (!scanner.hasNextInt()) {
            displayInputErrorMessaje("Numero Entero");
            scanner.next();
        }
        int number = scanner.nextInt();
        scanner.nextLine();

        return number;

    }

    public int requestOption() {
        System.out.print("Elija Una Opción: ");
        while (!scanner.hasNextInt()) {
            displayInputErrorMessaje("Numero");
            scanner.next();
        }
        int option = scanner.nextInt();
        scanner.nextLine();
        return option;
    }

    public void displayInputErrorMessaje(String tipo) {
        showMessaje("Entrada No Válida. Por favor, ingrese un " + tipo + ": ");
    }

    public void showMessaje(String messaje) {
        System.out.println(messaje);
    }

    public SeverityLevel requestSeverityLevel() {
        Utilities.cleanConsole();
        Utilities.printTitle("Registrar Nueva Emergencia", "Nivel de Gravedad");
        List<SeverityLevel> levels = Arrays.asList(SeverityLevel.values());
        //mostramos las opciones
        for(int i = 0; i < levels.size(); i++){
            SeverityLevel level = levels.get(i);
            showMessaje(String.format("%d. %s", i + 1, level.name()));
        }
        while(true){
            try {
                int option = requestInteger("Ingrese el Nivel de Gravedad (1-" + levels.size() + "): ");
                if (option >= 1 && option <= levels.size()) {
                    SeverityLevel selectedLevel = levels.get(option - 1);
                    showMessaje(String.format("Nivel de Gravedad Seleccionado: %s",selectedLevel.name()));
                    showMessaje("Presione Enter para continuar...");
                    scanner.nextLine();
                    Utilities.cleanConsole();
                    return selectedLevel;
                } else {
                    showMessaje("Error: Por favor ingrese un numero entre 1 y " + levels.size());
                }
            } catch (Exception e) {
                showMessaje("Error: Por favor ingrese un numero valido");
            }
        }

        
        
    }
}
