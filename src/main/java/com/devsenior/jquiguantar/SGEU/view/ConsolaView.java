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
    public Scanner scanner;
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
    public void displayActiveEmergencies(List<Emergency> emergencies) {
        Utilities.cleanConsole();
        Utilities.printTitle("Emergencias Activas");

        // Primero verificar si hay recursos con combustible bajo
        if (sistem.hayRecursosConCombustibleBajo()) {
            mostrarNotificacionesCombustibleBajo();
            return;
        }

        // Verificar si hay recursos en recarga
        if (sistem.hayRecursosEnRecarga()) {
            mostrarEstadoRecarga();
        }

        if (emergencies.isEmpty()) {
            showMessaje("No hay emergencias activas en este momento.");
            return;
        }

        showMessaje("\nTotal de emergencias activas: " + emergencies.size() + "\n");
        
        for (int i = 0; i < emergencies.size(); i++) {
            Emergency emergency = emergencies.get(i);
            showMessaje("----------------------------------------");
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
            
            showMessaje("Ubicación: " + locationName);
            
            // Mostrar estado y progreso de la emergencia
            showMessaje("\nEstado: " + emergency.getEstado());
            if (emergency.getEstado() == Emergency.EstadoEmergencia.EN_ATENCION) {
                // Verificar si hay recursos con combustible bajo en esta emergencia
                boolean hayRecursosConCombustibleBajo = false;
                for (Resource recurso : emergency.getRecursosAsignados()) {
                    if (recurso.necesitaRecarga() && !recurso.isEnRecarga()) {
                        hayRecursosConCombustibleBajo = true;
                        break;
                    }
                }

                if (hayRecursosConCombustibleBajo) {
                    showMessaje("\n¡ATENCIÓN! Esta emergencia está pausada debido a combustible bajo.");
                    showMessaje("Se requiere recargar los recursos antes de continuar.");
                } else {
                    int minutos = (int) emergency.getTiempoRestante();
                    int segundos = (int) ((emergency.getTiempoRestante() - minutos) * 60);
                    showMessaje("Tiempo Restante: " + minutos + " minutos y " + segundos + " segundos");
                }
                
                // Mostrar recursos asignados
                if (!emergency.getRecursosAsignados().isEmpty()) {
                    showMessaje("\nRecursos Asignados:");
                    for (Resource recurso : emergency.getRecursosAsignados()) {
                        String estadoRecurso = recurso.isEnRecarga() ? 
                            String.format(" (En recarga: %.1f minutos restantes)", recurso.getTiempoRestanteRecarga()) :
                            "";
                        if (esVehiculo(recurso)) {
                            showMessaje(String.format("  - %s (ID: %d) - Combustible: %.1f%%%s", 
                                recurso.getType(), 
                                recurso.getId(), 
                                recurso.getFuel(),
                                estadoRecurso));
                        } else {
                            showMessaje(String.format("  - %s (ID: %d)%s", 
                                recurso.getType(), 
                                recurso.getId(),
                                estadoRecurso));
                        }
                    }
                }
            } else {
                showMessaje("Tiempo Estimado: " + String.format("%.2f", emergency.getTiempoEstimado()) + " minutos");
            }
            showMessaje("----------------------------------------\n");
        }
        
        showMessaje("\nOpciones:");
        showMessaje("1. Actualizar estado");
        showMessaje("2. Ir a la pantalla de recarga de combustible");
        showMessaje("3. Volver al menú principal");

        int opcion = Utilities.getIntInput(scanner, "Seleccione una opción (1-3): ", 1, 3);

        switch (opcion) {
            case 1:
                displayActiveEmergencies(emergencies);
                break;
            case 2:
                mostrarRecursosNecesitanRecarga();
                break;
            case 3:
                return;
        }
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

                            if (esVehiculo(resource)) {
                                showMessaje(String.format("  %-4d | %-20s | %-8d | %-10s | %-11s | %s",
                                    resource.getId(),
                                    resource.getType(),
                                    1, // Cantidad individual
                                    resource.isAvailable() ? "Disponible" : "En uso",
                                    String.format("%.1f%%", resource.getFuel()),
                                    baseInfo));
                            } else {
                                showMessaje(String.format("  %-4d | %-20s | %-8d | %-10s | %-11s | %s",
                                    resource.getId(),
                                    resource.getType(),
                                    1, // Cantidad individual
                                    resource.isAvailable() ? "Disponible" : "En uso",
                                    String.format("- %s (ID: %d)", resource.getType(), resource.getId()),
                                    baseInfo));
                            }
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
                if (esVehiculo(r)) {
                    showMessaje(String.format("- ID: %d | Base: %s | Combustible: %.1f%%", 
                        r.getId(), baseInfo, r.getFuel()));
                } else {
                    showMessaje(String.format("- %s (ID: %d)", r.getType(), r.getId()));
                }
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
                if (esVehiculo(r)) {
                    showMessaje(String.format("- %s (ID: %d) ", 
                        r.getType(), r.getId()));
                } else {
                    showMessaje(String.format("- %s (ID: %d)", r.getType(), r.getId()));
                }
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
            if (esVehiculo(r)) {
                showMessaje(String.format("- %s (ID: %d) | Base: %s", 
                    r.getType(), r.getId(), baseInfo));
            } else {
                showMessaje(String.format("- %s (ID: %d)", r.getType(), r.getId()));
            }
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

    public void mostrarEstadoSistema() {
        Utilities.cleanConsole();
        Utilities.printTitle("ESTADO DEL SISTEMA DE EMERGENCIAS");
        
        // Mostrar emergencias en atención
        List<Emergency> emergenciasEnAtencion = sistem.getEmergenciasEnAtencion();
        if (!emergenciasEnAtencion.isEmpty()) {
            showMessaje("\nEMERGENCIAS EN ATENCIÓN:");
            for (Emergency emergencia : emergenciasEnAtencion) {
                showMessaje("\nEmergencia #" + emergencia.getTipo() + 
                    "\nTipo: " + emergencia.getTipo() +
                    "\nNivel de Gravedad: " + emergencia.getNivelGravedad() +
                    "\nTiempo Restante: " + String.format("%.1f minutos", emergencia.getTiempoRestante()) +
                    "\nRecursos Asignados:");
                
                for (Resource recurso : emergencia.getRecursosAsignados()) {
                    if (esVehiculo(recurso)) {
                        showMessaje("  - " + recurso.getType() + 
                            " (ID: " + recurso.getId() + ")" +
                            " - Combustible: " + String.format("%.1f%%", recurso.getFuel()));
                    } else {
                        showMessaje("  - " + recurso.getType() + 
                            " (ID: " + recurso.getId() + ")");
                    }
                }
            }
        }

        // Mostrar emergencias pendientes
        List<Emergency> emergenciasPendientes = sistem.getEmergenciasPendientes();
        if (!emergenciasPendientes.isEmpty()) {
            showMessaje("\nEMERGENCIAS PENDIENTES:");
            for (Emergency emergencia : emergenciasPendientes) {
                showMessaje("\nEmergencia #" + emergencia.getTipo() + 
                    "\nTipo: " + emergencia.getTipo() +
                    "\nNivel de Gravedad: " + emergencia.getNivelGravedad() +
                    "\nTiempo Estimado: " + String.format("%.1f minutos", emergencia.getTiempoEstimado()));
            }
        }

        // Mostrar recursos que necesitan recarga
        List<Resource> recursosNecesitanRecarga = sistem.getRecursosNecesitanRecarga();
        if (!recursosNecesitanRecarga.isEmpty()) {
            showMessaje("\nRECURSOS QUE NECESITAN RECARGA:");
            for (Resource recurso : recursosNecesitanRecarga) {
                if (esVehiculo(recurso)) {
                    showMessaje("  - " + recurso.getType() + 
                        " (ID: " + recurso.getId() + ")" +
                        " - Combustible: " + String.format("%.1f%%", recurso.getFuel()));
                } else {
                    showMessaje("  - " + recurso.getType() + 
                        " (ID: " + recurso.getId() + ")");
                }
            }
        }

        showMessaje("\nPresione ENTER para continuar...");
        scanner.nextLine();
    }

    public void mostrarResumenEmergencia(Emergency emergencia) {
        Utilities.cleanConsole();
        Utilities.printTitle("RESUMEN DE EMERGENCIA");
        
        showMessaje("\nTipo: " + emergencia.getTipo());
        showMessaje("Nivel de Gravedad: " + emergencia.getNivelGravedad());
        showMessaje("Ubicación: " + emergencia.getUbicacion());
        showMessaje("Tiempo Estimado: " + String.format("%.1f minutos", emergencia.getTiempoEstimado()));
        
        if (emergencia.getEstado() == Emergency.EstadoEmergencia.EN_ATENCION) {
            showMessaje("\nEstado: EN ATENCIÓN");
            showMessaje("Tiempo Restante: " + String.format("%.1f minutos", emergencia.getTiempoRestante()));
            showMessaje("\nRecursos Asignados:");
            for (Resource recurso : emergencia.getRecursosAsignados()) {
                if (esVehiculo(recurso)) {
                    showMessaje("  - " + recurso.getType() + 
                        " (ID: " + recurso.getId() + ")" +
                        " - Combustible: " + String.format("%.1f%%", recurso.getFuel()));
                } else {
                    showMessaje("  - " + recurso.getType() + 
                        " (ID: " + recurso.getId() + ")");
                }
            }
        } else {
            showMessaje("\nEstado: " + emergencia.getEstado());
        }
        
        showMessaje("\nPresione ENTER para continuar...");
        scanner.nextLine();
    }

    public void mostrarRecursosDisponibles(List<Resource> recursos) {
        Utilities.cleanConsole();
        Utilities.printTitle("RECURSOS DISPONIBLES");
        
        if (recursos.isEmpty()) {
            showMessaje("\nNo hay recursos disponibles en este momento.");
        } else {
            Map<String, List<Resource>> recursosPorTipo = recursos.stream()
                .collect(Collectors.groupingBy(Resource::getType));
            
            for (Map.Entry<String, List<Resource>> entry : recursosPorTipo.entrySet()) {
                showMessaje("\n" + entry.getKey() + ":");
                for (Resource recurso : entry.getValue()) {
                    if (esVehiculo(recurso)) {
                        showMessaje("  - ID: " + recurso.getId() + 
                            " - Base: " + recurso.getBaseOrigin() +
                            " - Combustible: " + String.format("%.1f%%", recurso.getFuel()));
                    } else {
                        showMessaje("  - ID: " + recurso.getId() + 
                            " - Base: " + recurso.getBaseOrigin());
                    }
                }
            }
        }
        
        showMessaje("\nPresione ENTER para continuar...");
        scanner.nextLine();
    }

    public void mostrarTiempoRestante(Emergency emergencia) {
        Utilities.cleanConsole();
        Utilities.printTitle("MONITOREO DE EMERGENCIA EN ATENCIÓN");
        
        // Verificar si hay recursos con combustible bajo
        boolean hayRecursosConCombustibleBajo = false;
        for (Resource recurso : emergencia.getRecursosAsignados()) {
            if (recurso.necesitaRecarga() && !recurso.isEnRecarga()) {
                hayRecursosConCombustibleBajo = true;
                break;
            }
        }
        
        if (hayRecursosConCombustibleBajo) {
            showMessaje("\n¡ATENCIÓN! La emergencia está pausada debido a combustible bajo.");
            showMessaje("Se requiere recargar los recursos antes de continuar.");
            showMessaje("\nOpciones:");
            showMessaje("1. Ir a la pantalla de recarga de combustible");
            showMessaje("2. Volver al menú de emergencias");
            showMessaje("3. Volver al menú principal");
            
            int opcion = Utilities.getIntInput(scanner, "Seleccione una opción (1-3): ", 1, 3);
            
            switch (opcion) {
                case 1:
                    mostrarRecursosNecesitanRecarga();
                    break;
                case 2:
                case 3:
                    return;
            }
        } else {
            showMessaje("\nEmergencia: " + emergencia.getTipo());
            showMessaje("Nivel de Gravedad: " + emergencia.getNivelGravedad());
            showMessaje("Ubicación: " + obtenerNombreUbicacion(emergencia));
            
            int minutos = (int) emergencia.getTiempoRestante();
            int segundos = (int) ((emergencia.getTiempoRestante() - minutos) * 60);
            
            showMessaje("\nTiempo Restante: " + minutos + " minutos y " + segundos + " segundos");
            
            if (emergencia.getTiempoRestante() <= 5) {
                showMessaje("\n¡ATENCIÓN! La emergencia está por finalizar.");
            }
            
            showMessaje("\nRecursos Asignados:");
            for (Resource recurso : emergencia.getRecursosAsignados()) {
                if (esVehiculo(recurso)) {
                    showMessaje("  - " + recurso.getType() + 
                        " (ID: " + recurso.getId() + ")" +
                        " - Combustible: " + String.format("%.1f%%", recurso.getFuel()));
                } else {
                    showMessaje("  - " + recurso.getType() + 
                        " (ID: " + recurso.getId() + ")");
                }
            }
            
            showMessaje("\nOpciones:");
            showMessaje("1. Continuar monitoreando");
            showMessaje("2. Volver al menú de emergencias");
            showMessaje("3. Volver al menú principal");
        }
    }

    public void mostrarOpcionesSeleccionEmergencia() {
        showMessaje("\nOpciones de selección de emergencia:");
        showMessaje("1. Atender emergencia de mayor prioridad");
        showMessaje("2. Seleccionar emergencia manualmente");
        showMessaje("3. Volver al menú principal");
    }

    public int solicitarOpcionSeleccionEmergencia() {
        int opcion;
        while (true) {
            opcion = requestInteger("Seleccione una opción (1-3): ");
            if (opcion >= 1 && opcion <= 3) {
                return opcion;
            } else {
                showMessaje("Por favor, ingrese un número entre 1 y 3.");
                if (scanner.hasNextLine()) scanner.nextLine(); // Consumir línea pendiente
            }
        }
    }

    public int solicitarEmergenciaManual(List<Emergency> emergencias) {
        return requestInteger("Seleccione el número de la emergencia a atender (1-" + emergencias.size() + "): ");
    }

    public void mostrarRecursosNecesitanRecarga() {
        Utilities.cleanConsole();
        Utilities.printTitle("RECURSOS QUE NECESITAN RECARGA");

        List<Resource> recursosNecesitanRecarga = sistem.getRecursosNecesitanRecarga();
        List<Resource> recursosEnRecarga = sistem.getRecursosEnRecarga();

        if (recursosNecesitanRecarga.isEmpty() && recursosEnRecarga.isEmpty()) {
            showMessaje("No hay recursos que necesiten recarga en este momento.");
            return;
        }

        if (!recursosNecesitanRecarga.isEmpty()) {
            showMessaje("\nRecursos que necesitan recarga:");
            for (Resource recurso : recursosNecesitanRecarga) {
                if (esVehiculo(recurso)) {
                    showMessaje(String.format("- %s (Combustible: %.1f%%)", 
                        recurso.toString(), recurso.getFuel()));
                } else {
                    showMessaje(String.format("- %s", recurso.toString()));
                }
            }
        }

        if (!recursosEnRecarga.isEmpty()) {
            showMessaje("\nRecursos en proceso de recarga:");
            for (Resource recurso : recursosEnRecarga) {
                showMessaje(String.format("- %s", recurso.toString()));
            }
        }

        showMessaje("\nOpciones:");
        showMessaje("1. Iniciar recarga de todos los recursos necesitados");
        showMessaje("2. Seleccionar recursos específicos para recargar");
        showMessaje("3. Volver al menú principal");

        int opcion = Utilities.getIntInput(scanner, "Seleccione una opción (1-3): ", 1, 3);

        switch (opcion) {
            case 1:
                if (sistem.iniciarRecargaRecursos(recursosNecesitanRecarga)) {
                    showMessaje("\nRecarga iniciada para todos los recursos necesitados.");
                } else {
                    showMessaje("\nNo se pudo iniciar la recarga de los recursos.");
                }
                break;
            case 2:
                mostrarSeleccionRecargaRecursos(recursosNecesitanRecarga);
                break;
            case 3:
                return;
        }

        Utilities.pressEnterToContinue(scanner);
    }

    private void mostrarSeleccionRecargaRecursos(List<Resource> recursos) {
        if (recursos.isEmpty()) {
            showMessaje("No hay recursos disponibles para recargar.");
            return;
        }

        showMessaje("\nSeleccione los recursos a recargar (ingrese los números separados por comas):");
        for (int i = 0; i < recursos.size(); i++) {
            Resource recurso = recursos.get(i);
            showMessaje(String.format("%d. %s", i + 1, recurso.toString()));
        }

        String input = Utilities.getStringInput(scanner, "\nIngrese los números de los recursos (ej: 1,3,4): ");
        String[] seleccion = input.split(",");
        List<Resource> recursosSeleccionados = new ArrayList<>();

        for (String num : seleccion) {
            try {
                int index = Integer.parseInt(num.trim()) - 1;
                if (index >= 0 && index < recursos.size()) {
                    recursosSeleccionados.add(recursos.get(index));
                }
            } catch (NumberFormatException e) {
                // Ignorar entradas inválidas
            }
        }

        if (!recursosSeleccionados.isEmpty()) {
            if (sistem.iniciarRecargaRecursos(recursosSeleccionados)) {
                showMessaje("\nRecarga iniciada para los recursos seleccionados.");
            } else {
                showMessaje("\nNo se pudo iniciar la recarga de los recursos seleccionados.");
            }
        } else {
            showMessaje("\nNo se seleccionaron recursos válidos.");
        }
    }

    public void mostrarNotificacionesCombustibleBajo() {
        List<Resource> recursosConCombustibleBajo = sistem.getRecursosConCombustibleBajo();
        if (!recursosConCombustibleBajo.isEmpty()) {
            Utilities.cleanConsole();
            Utilities.printTitle("¡ALERTA! RECURSOS CON COMBUSTIBLE BAJO");
            
            showMessaje("\nLos siguientes recursos tienen el combustible por debajo del 20%:");
            for (Resource recurso : recursosConCombustibleBajo) {
                if (esVehiculo(recurso)) {
                    showMessaje(String.format("- %s (Combustible: %.1f%%)", 
                        recurso.toString(), recurso.getFuel()));
                } else {
                    showMessaje(String.format("- %s", recurso.toString()));
                }
            }
            
            showMessaje("\nSe recomienda recargar estos recursos antes de continuar con las emergencias.");
            showMessaje("\nOpciones:");
            showMessaje("1. Ir a la pantalla de recarga de combustible");
            showMessaje("2. Continuar sin recargar (no recomendado)");
            
            int opcion = Utilities.getIntInput(scanner, "Seleccione una opción (1-2): ", 1, 2);
            
            if (opcion == 1) {
                mostrarRecursosNecesitanRecarga();
            } else {
                sistem.limpiarNotificacionesCombustibleBajo();
            }
        }
    }

    public void mostrarEstadoRecarga() {
        List<Resource> recursosEnRecarga = sistem.getRecursosEnRecarga();
        if (!recursosEnRecarga.isEmpty()) {
            Utilities.cleanConsole();
            Utilities.printTitle("ESTADO DE RECARGA DE COMBUSTIBLE");
            
            showMessaje("\nRecursos en proceso de recarga:");
            for (Resource recurso : recursosEnRecarga) {
                showMessaje(String.format("- %s", recurso.toString()));
            }
            
            showMessaje("\nLa atención de emergencias está pausada hasta que se complete la recarga.");
            Utilities.pressEnterToContinue(scanner);
        }
    }

    private boolean esVehiculo(Resource recurso) {
        String tipo = recurso.getType();
        return tipo.contains("Camión") || tipo.contains("Ambulancia") || tipo.contains("Patrulla");
    }

    public void mostrarProcesoAsignacionRecursos(Emergency emergency, List<Resource> available, List<Resource> suggested) {
        showEmergencySummary(emergency);
        showAvailableAndSuggestedResources(available, suggested);
    }

    public List<Resource> solicitarRecursosParaAsignacion(List<Resource> available, List<Resource> suggested) {
        boolean confirm = requestConfirmation("¿Desea asignar los recursos sugeridos? (S/N): ");
        return confirm ? suggested : requestResourceSelection(available);
    }

    public void mostrarResultadoAsignacion(Emergency emergency, List<Resource> assignedResources, boolean exito) {
        if (exito) {
            showAssignmentSummary(emergency, assignedResources);
        } else {
            showMessaje("No se pudieron asignar los recursos a la emergencia.");
        }
        waitForEnter("Presione Enter para continuar...");
    }

    public void mostrarMensajeNoHayRecursos() {
        showMessaje("No hay recursos disponibles para atender esta emergencia.");
        waitForEnter("Presione Enter para continuar...");
    }

}
