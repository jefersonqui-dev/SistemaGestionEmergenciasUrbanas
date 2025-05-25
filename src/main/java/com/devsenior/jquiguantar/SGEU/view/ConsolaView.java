package com.devsenior.jquiguantar.SGEU.view;

// import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.ArrayList;

import com.devsenior.jquiguantar.SGEU.model.config.PredefinedLocation;
import com.devsenior.jquiguantar.SGEU.model.emergencies.EmergencyType;
import com.devsenior.jquiguantar.SGEU.model.util.Location;
import com.devsenior.jquiguantar.SGEU.model.config.LocationSettings;
import com.devsenior.jquiguantar.SGEU.model.util.Utilities;
import com.devsenior.jquiguantar.SGEU.model.patterns.singleton.EmergencySistem;
import com.devsenior.jquiguantar.SGEU.model.emergencies.SeverityLevel;
import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergency;
import com.devsenior.jquiguantar.SGEU.model.resourcess.Resource;
import com.devsenior.jquiguantar.SGEU.model.config.OperationalBase;

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
                    // showMessaje(String.format("Nivel de Gravedad Seleccionado: %s",selectedLevel.name()));
                    // showMessaje("Presione Enter para continuar...");
                    // scanner.nextLine();
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

    public void showEstimatedResponseTime(double timeResponse) {
        if (timeResponse < 0) {
            showMessaje("No hay bases operativas disponibles para este tipo de emergencia.");
        } else {
            int minutos = (int) timeResponse;
            int segundos = (int) ((timeResponse - minutos) * 60);
            showMessaje(String.format("Tiempo estimado de respuesta: %d minutos y %d segundos", minutos, segundos));
        }
        showMessaje("Presione Enter para continuar...");
        scanner.nextLine();
        Utilities.cleanConsole();
    }

    public void showTimeResponse(double timeResponse) {
        Utilities.printTitle("Registrar Nueva Emergencia", "Tiempo Estimado");
        if (timeResponse >= 0) {
            showMessaje(String.format("Tiempo estimado de respuesta: %.2f minutos", timeResponse));
        } else {
            showMessaje("No se pudo calcular el tiempo de respuesta");
        }
        showMessaje("Presione Enter para continuar...");
        scanner.nextLine();
        Utilities.cleanConsole();
    }
    public void displayActiveEmergencies(List<Emergency> emergencies){
        // Utilities.cleanConsole();
        Utilities.printTitle("Emergencias Activas");
        if(emergencies.isEmpty()){
            showMessaje("No hay emergencias activas en este momento.");
        }

        showMessaje("\nTotal de emergencias activas: " + emergencies.size() + "\n");
        for(int i = 0; i < emergencies.size(); i++){
            Emergency emergency = emergencies.get(i);
            showMessaje("Emergencia #" + (i + 1) + ":");
            showMessaje("Tipo: " + emergency.getTipo());
            showMessaje("Nivel de Gravedad: " + emergency.getNivelGravedad());
            
            // Buscar el nombre del lugar de la emergencia
            String locationName = sistem.getReferencePoints().stream()
                .filter(point -> point.getLocation().getLatitude() == emergency.getUbicacion().getLatitude() 
                    && point.getLocation().getLongitude() == emergency.getUbicacion().getLongitud())
                .map(PredefinedLocation::getNombre)
                .findFirst()
                .orElse("Ubicación no registrada");
            
            showMessaje("Ubicacion: " + locationName);
            showMessaje("Tiempo Estimado: " + String.format("%.2f", emergency.getTiempoEstimado()) + " minutos");
            showMessaje("--------------------------------");
        }
        showMessaje("Presione Enter para continuar...");
        scanner.nextLine();
        Utilities.cleanConsole();

    }
    public void showResources(List<Resource> resources) {
        Utilities.cleanConsole();
        showMessaje("  -------------------------------------------------------------------------------------------------------");
        System.out.println("                                         Estado Actual de Recursos");
        showMessaje("  -------------------------------------------------------------------------------------------------------");
        showMessaje("  ID   | Tipo                 | Cantidad | Estado     | Combustible | Base");
        showMessaje("  -------------------------------------------------------------------------------------------------------");

        if (resources.isEmpty()) {
            showMessaje("  No hay recursos disponibles en el sistema.");
        } else {
            resources.stream()
                    .collect(Collectors.groupingBy(Resource::getType))
                    .forEach((type, resourceList) -> {
                        for (Resource resource : resourceList) {
                            String baseInfo = "";
                            OperationalBase base = sistem.getOperationalBases().stream()
                                    .filter(b -> b.getId().equals(resource.getBaseOrigin()))
                                    .findFirst()
                                    .orElse(null);
                            
                            if (base != null) {
                                baseInfo = base.getName();
                            }

                            showMessaje(String.format("  %-4d | %-20s | %-8d | %-10s | %-11s | %s",
                                resource.getId(),
                                resource.getType(),
                                1, // Cantidad individual
                                resource.isAvailable() ? "Disponible" : "En uso",
                                String.format("%.1f%%", resource.getFuel()),
                                baseInfo));
                        }
                    });
        }

        showMessaje("  ------------------------------------------------------------------------------------------------------");
        // showMessaje("Total de recursos: " + resources.size());
        showMessaje("Presione Enter para continuar...");
        scanner.nextLine();
        Utilities.cleanConsole();
    }

    public void showEmergenciesOrdered(List<Emergency> emergencies) {
        Utilities.printTitle("Emergencias activas ordenadas por prioridad");
        for (int i = 0; i < emergencies.size(); i++) {
            Emergency e = emergencies.get(i);
            System.out.printf("%d. Tipo: %s | Gravedad: %s | Ubicación: %s\n",
                i + 1, e.getTipo(), e.getNivelGravedad(), obtenerNombreUbicacion(e));
        }
    }

    public void waitForEnter(String message) {
        System.out.println(message);
        scanner.nextLine();
    }

    public void showNoActiveEmergencies() {
        Utilities.cleanConsole();
        Utilities.printTitle("Gestión de Emergencias");
        showMessaje("No hay emergencias activas para atender.");
        waitForEnter("Presione Enter para continuar...");
    }

    public void showNoAvailableResources() {
        Utilities.cleanConsole();
        Utilities.printTitle("Gestión de Emergencias");
        showMessaje("No hay recursos disponibles para atender esta emergencia.");
        waitForEnter("Presione Enter para continuar...");
    }

    public void showNoSuggestedResources() {
        Utilities.cleanConsole();
        Utilities.printTitle("Gestión de Emergencias");
        showMessaje("No hay recursos sugeridos disponibles para este tipo de emergencia.");
        waitForEnter("Presione Enter para continuar...");
    }

    public void displayEmergenciesWithResources(List<Emergency> emergencies) {
        Utilities.cleanConsole();
        Utilities.printTitle("Emergencias con Recursos Disponibles");
        
        if (emergencies.isEmpty()) {
            showMessaje("No hay emergencias que puedan ser atendidas con los recursos disponibles.");
        } else {
            for (int i = 0; i < emergencies.size(); i++) {
                Emergency e = emergencies.get(i);
                showMessaje(String.format("%d. Tipo: %s | Gravedad: %s | Ubicación: %s",
                    i + 1, e.getTipo(), e.getNivelGravedad(), obtenerNombreUbicacion(e)));
            }
        }
    }

    public int requestEmergencyToAttend(int maxOptions) {
        return requestInteger("Seleccione el número de la emergencia a atender (1-" + maxOptions + "): ");
    }

    public void displayEmergencySummary(List<Emergency> emergencies) {
        Utilities.cleanConsole();
        Utilities.printTitle("Emergencias por Prioridad");
        
        if(emergencies.isEmpty()) {
            showMessaje("No hay emergencias activas en este momento.");
            return;
        }

        showMessaje("\nEmergencias Activas (Ordenadas por Prioridad):");
        showMessaje("----------------------------------------");
        for(int i = 0; i < emergencies.size(); i++) {
            Emergency emergency = emergencies.get(i);
            showMessaje(String.format("%d. %s - %s - %s",
                i + 1,
                emergency.getTipo(),
                emergency.getNivelGravedad(),
                obtenerNombreUbicacion(emergency)));
        }
        showMessaje("----------------------------------------");
    }

    public void showEmergencySummary(Emergency emergency) {
        Utilities.cleanConsole();
        Utilities.printTitle("Detalles de la Emergencia");
        showMessaje("Tipo: " + emergency.getTipo());
        showMessaje("Nivel de Gravedad: " + emergency.getNivelGravedad());
        showMessaje("Ubicación: " + obtenerNombreUbicacion(emergency));
        showMessaje("Tiempo Estimado: " + String.format("%.2f", emergency.getTiempoEstimado()) + " minutos");
        showMessaje("----------------------------------------");
    }

    public void showAvailableAndSuggestedResources(List<Resource> available, List<Resource> suggested) {
        Utilities.cleanConsole();
        Utilities.printTitle("Recursos Disponibles y Sugeridos");
        
        if (available.isEmpty()) {
            showMessaje("No hay recursos disponibles para este tipo de emergencia.");
            return;
        }

        showMessaje("\nRecursos Disponibles para " + getEmergencyTypeDescription(available.get(0).getType()) + ":");
        showMessaje("----------------------------------------");
        
        Map<String, List<Resource>> availableByType = available.stream()
            .collect(Collectors.groupingBy(Resource::getType));
        
        availableByType.forEach((type, resources) -> {
            showMessaje("\n" + type + ":");
            resources.forEach(r -> {
                String baseInfo = sistem.getOperationalBases().stream()
                    .filter(b -> b.getId().equals(r.getBaseOrigin()))
                    .map(OperationalBase::getName)
                    .findFirst()
                    .orElse("Base no encontrada");
                showMessaje(String.format("- ID: %d | Base: %s | Combustible: %.1f%%", 
                    r.getId(), baseInfo, r.getFuel()));
            });
        });

        if (!suggested.isEmpty()) {
            showMessaje("\nRecursos Sugeridos para esta Emergencia:");
            showMessaje("----------------------------------------");
            suggested.forEach(r -> {
                String baseInfo = sistem.getOperationalBases().stream()
                    .filter(b -> b.getId().equals(r.getBaseOrigin()))
                    .map(OperationalBase::getName)
                    .findFirst()
                    .orElse("Base no encontrada");
                showMessaje(String.format("- %s (ID: %d) ", 
                    r.getType(), r.getId()));
            });
        } else {
            showMessaje("\nNo hay recursos sugeridos para esta emergencia.");
        }
    }

    private String getEmergencyTypeDescription(String resourceType) {
        if (resourceType.contains("Bombero")) {
            return "Emergencia por Incendio";
        } else if (resourceType.contains("Paramédico") || resourceType.contains("Ambulancia")) {
            return "Emergencia por Accidente Vehicular";
        } else if (resourceType.contains("Policía") || resourceType.contains("Patrulla")) {
            return "Emergencia por Robo";
        }
        return "Emergencia";
    }

    public List<Resource> requestResourceSelection(List<Resource> available) {
        List<Resource> selected = new ArrayList<>();
        showMessaje("\nRecursos disponibles:");
        for (int i = 0; i < available.size(); i++) {
            Resource r = available.get(i);
            showMessaje(String.format("%d. %s", i + 1, r.getType()));
        }

        showMessaje("\nIngrese los números de los recursos a asignar (separados por coma): ");
        String[] selections = scanner.nextLine().split(",");
        
        for (String selection : selections) {
            try {
                int index = Integer.parseInt(selection.trim()) - 1;
                if (index >= 0 && index < available.size()) {
                    selected.add(available.get(index));
                }
            } catch (NumberFormatException e) {
                showMessaje("Entrada inválida: " + selection);
            }
        }
        
        return selected;
    }

    public boolean requestConfirmation(String message) {
        System.out.print(message);
        String input = scanner.nextLine().trim().toUpperCase();
        return input.equals("S");
    }

    public void showAssignmentSummary(Emergency emergency, List<Resource> assignedResources) {
        Utilities.cleanConsole();
        Utilities.printTitle("Resumen de Asignación");
        showMessaje("Emergencia atendida exitosamente:");
        showMessaje(String.format("Tipo: %s | Gravedad: %s | Ubicación: %s",
            emergency.getTipo(), emergency.getNivelGravedad(), obtenerNombreUbicacion(emergency)));
        showMessaje("\nRecursos asignados:");
        assignedResources.forEach(r -> {
            String baseInfo = sistem.getOperationalBases().stream()
                .filter(b -> b.getId().equals(r.getBaseOrigin()))
                .map(OperationalBase::getName)
                .findFirst()
                .orElse("Base no encontrada");
            showMessaje(String.format("- %s (ID: %d) | Base: %s", 
                r.getType(), r.getId(), baseInfo));
        });
        waitForEnter("Presione Enter para continuar...");
    }

    private String obtenerNombreUbicacion(Emergency e) {
        return sistem.getReferencePoints().stream()
            .filter(point -> point.getLocation().getLatitude() == e.getUbicacion().getLatitude()
                && point.getLocation().getLongitude() == e.getUbicacion().getLongitud())
            .map(PredefinedLocation::getNombre)
            .findFirst()
            .orElse("Ubicación no registrada");
    }

}
