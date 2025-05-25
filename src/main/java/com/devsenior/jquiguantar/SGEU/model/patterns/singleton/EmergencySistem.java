package com.devsenior.jquiguantar.SGEU.model.patterns.singleton;

import com.devsenior.jquiguantar.SGEU.model.config.PredefinedLocation;
import com.devsenior.jquiguantar.SGEU.model.config.OperationalBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.devsenior.jquiguantar.SGEU.model.config.LocationSettings;
import com.devsenior.jquiguantar.SGEU.model.util.Location;
import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergency;
import com.devsenior.jquiguantar.SGEU.model.emergencies.EmergencyType;
import com.devsenior.jquiguantar.SGEU.model.emergencies.SeverityLevel;
import com.devsenior.jquiguantar.SGEU.model.resourcess.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;


public class EmergencySistem {
    private static EmergencySistem instance;

    private List<PredefinedLocation> referencePoints;
    private List<OperationalBase> operationalBases;
    private List<Emergency> emergencies;
    private List<Resource> resources;
    private static final String CONFIG_FILE = "/bases.json";

    private EmergencySistem() {
        this.referencePoints = new ArrayList<>();
        this.operationalBases = new ArrayList<>();
        this.emergencies = new ArrayList<>();
        this.resources = new ArrayList<>();
        loadConfig();
    }

    private void loadConfig() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = getClass().getResourceAsStream(CONFIG_FILE);
            if (inputStream == null) {
                System.err.println("No se pudo encontrar el archivo de configuración: " + CONFIG_FILE);
                return;
            }
            
            JsonNode rootNode = mapper.readTree(inputStream);
            JsonNode PointsNode = rootNode.get("puntosReferencia");

            if (PointsNode != null) {
                for (JsonNode pointNode : PointsNode) {
                    PredefinedLocation point = new PredefinedLocation();
                    point.setId(pointNode.get("id").asText());
                    point.setNombre(pointNode.get("nombre").asText());
                    point.setDescription(pointNode.get("descripcion").asText());
    
                    JsonNode locationNode = pointNode.get("ubicacion");
                    LocationSettings location = new LocationSettings();
                    location.setLatitude(locationNode.get("latitud").asDouble());
                    location.setLongitude(locationNode.get("longitud").asDouble());
                    
                    point.setLocation(location);
                    referencePoints.add(point);
                }
            }

            JsonNode basesNode = rootNode.get("basesOperativas");
            if (basesNode != null) {
                for (JsonNode baseNode : basesNode) {
                    OperationalBase base = new OperationalBase();
                    base.setId(baseNode.get("id").asText());
                    base.setName(baseNode.get("nombre").asText());
                    base.setServiceType(baseNode.get("tipoServicio").asText());
                    
                    JsonNode locationNode = baseNode.get("ubicacion");
                    Location baseLocation = new Location(
                        locationNode.get("latitud").asDouble(),
                        locationNode.get("longitud").asDouble()
                    );
                    base.setLocation(baseLocation);
                    operationalBases.add(base);

                    // Cargar recursos de la base
                    JsonNode recursosNode = baseNode.get("recursosIniciales");
                    if (recursosNode != null) {
                        for (JsonNode recursoNode : recursosNode) {
                            int id = recursoNode.get("id").asInt();
                            String type = recursoNode.get("tipo").asText();
                            double consumptionByDistance = recursoNode.has("consumoPorDistancia") ?
                                recursoNode.get("consumoPorDistancia").asDouble() : 0.0;
                            
                            Resource resource = new Resource(id, type, base.getId(), consumptionByDistance);
                            resources.add(resource);
                        }
                    }
                }
            }

            inputStream.close();
        } catch (IOException e) {
            System.err.println("Error al cargar la configuración: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static EmergencySistem getInstance() {
        if (instance == null) {
            instance = new EmergencySistem();
        }
        return instance;
    }
    public List<Resource> getAllResources(){
        return new ArrayList<>(resources);
    }
    public Resource getResourceById(int id){
        return resources.stream()
            .filter(r -> r.getId() == id)
            .findFirst()
            .orElse(null);
    }

    public List<PredefinedLocation> getReferencePoints() {
        return referencePoints;
    }

    public List<OperationalBase> getOperationalBases() {
        return operationalBases;
    }

    public void registerEmergency(Emergency emergency){
        if (emergency != null) {
            emergencies.add(emergency);
        }
    }

    public List<Emergency> getEmergencies(){
        return new ArrayList<>(emergencies);
    }
    public List<Emergency> getActiveEmergencies(){
        return emergencies.stream()
            .filter(emergency -> !emergency.isAtendida())
            .toList();
    }
    public List<Emergency> getEmergencyOrdered(){
        return emergencies.stream()
            .filter(emergency -> !emergency.isAtendida())
            .sorted(Comparator.comparing(Emergency::getNivelGravedad).reversed())
            .collect(Collectors.toList());
    }
    public List<Resource> getAvailableResourcesForEmergency(Emergency emergency){
        return resources.stream()
            .filter(Resource::isAvailable)
            .filter(r -> esCompatible(r, emergency))
            .collect(Collectors.toList());
    }
    public List<Resource> suggestResourcesForEmergency(Emergency emergency){
        List<Resource> sugeridos = new ArrayList<Resource>();
        
        switch(emergency.getTipo()) {
            case INCENDIO:
                if(emergency.getNivelGravedad() == SeverityLevel.ALTO) {
                    List<Resource> camiones = getAvailableResourcesByType("Camión de Bomberos",2);
                    List<Resource> bomberos = getAvailableResourcesByType("Personal Bombero", 3);
                    sugeridos.addAll(camiones);
                    sugeridos.addAll(bomberos);
                } else if(emergency.getNivelGravedad() == SeverityLevel.MEDIO) {
                    List<Resource> camiones = getAvailableResourcesByType("Camión de Bomberos",1);
                    List<Resource> bomberos = getAvailableResourcesByType("Personal Bombero", 2);
                    sugeridos.addAll(camiones);
                    sugeridos.addAll(bomberos);
                } else {
                    List<Resource> bomberos = getAvailableResourcesByType("Personal Bombero", 1);
                    sugeridos.addAll(bomberos);
                }
                break;

            case ACCIDENTE_VEHICULAR:
                if(emergency.getNivelGravedad() == SeverityLevel.ALTO) {
                    List<Resource> ambulancias = getAvailableResourcesByType("Ambulancia",1);
                    List<Resource> paramedicos = getAvailableResourcesByType("Personal Paramédico", 2);
                    sugeridos.addAll(ambulancias);
                    sugeridos.addAll(paramedicos);
                } else if(emergency.getNivelGravedad() == SeverityLevel.MEDIO) {
                    List<Resource> ambulancias = getAvailableResourcesByType("Ambulancia",1);
                    List<Resource> paramedicos = getAvailableResourcesByType("Personal Paramédico", 1);
                    sugeridos.addAll(ambulancias);
                    sugeridos.addAll(paramedicos);
                } else {
                    List<Resource> paramedicos = getAvailableResourcesByType("Personal Paramédico", 1);
                    sugeridos.addAll(paramedicos);
                }
                break;

            case ROBO:
                if(emergency.getNivelGravedad() == SeverityLevel.ALTO) {
                    List<Resource> patrullas = getAvailableResourcesByType("Patrulla Policial",1);
                    List<Resource> oficiales = getAvailableResourcesByType("Oficial de Policía", 1);
                    sugeridos.addAll(patrullas);
                    sugeridos.addAll(oficiales);
                } else if(emergency.getNivelGravedad() == SeverityLevel.MEDIO) {
                    List<Resource> patrullas = getAvailableResourcesByType("Patrulla Policial",1);
                    sugeridos.addAll(patrullas);
                } else {
                    List<Resource> oficiales = getAvailableResourcesByType("Oficial de Policía", 1);
                    sugeridos.addAll(oficiales);
                }
                break;
        }
        
        return sugeridos;
    }
    public List<Resource> getAvailableResourcesByType(String tipo, int cantidad){
        return resources.stream()
            .filter(r -> r.isAvailable() && r.getType().equalsIgnoreCase(tipo))
            .limit(cantidad)
            .collect(Collectors.toList());
    }
    public boolean assignResourcesToEmergency(Emergency emergency, List<Resource> resourcesToAssign){
        if(resourcesToAssign == null || resourcesToAssign.isEmpty()) {
            return false;
        }

        // Verificar que todos los recursos estén disponibles
        boolean todosDisponibles = resourcesToAssign.stream()
            .allMatch(Resource::isAvailable);
            
        if (!todosDisponibles) {
            return false;
        }

        // Asignar los recursos
        for (Resource r : resourcesToAssign) {
            r.setAvailable(false);
        }
        emergency.setAtendida(true);
        return true;
    }
    
    private boolean esCompatible(Resource resource, Emergency emergency) {
        String resourceType = resource.getType();
        switch (emergency.getTipo()) {
            case INCENDIO:
                return resourceType.equals("Camión de Bomberos") || resourceType.equals("Personal Bombero");
            case ACCIDENTE_VEHICULAR:
                return resourceType.equals("Ambulancia") || resourceType.equals("Personal Paramédico");
            case ROBO:
                return resourceType.equals("Patrulla Policial") || resourceType.equals("Oficial de Policía");
            default:
                return false;
        }
    }

    public boolean hasActiveEmergencies() {
        return !emergencies.stream()
            .filter(emergency -> !emergency.isAtendida())
            .toList()
            .isEmpty();
    }

    public boolean hasAvailableResourcesForEmergency(Emergency emergency) {
        return !getAvailableResourcesForEmergency(emergency).isEmpty();
    }

    public boolean hasSuggestedResourcesForEmergency(Emergency emergency) {
        return !suggestResourcesForEmergency(emergency).isEmpty();
    }

    public List<Emergency> getEmergenciesWithAvailableResources() {
        return getEmergencyOrdered().stream()
            .filter(emergency -> {
                List<Resource> available = getAvailableResourcesForEmergency(emergency);
                List<Resource> suggested = suggestResourcesForEmergency(emergency);
                return !available.isEmpty() && !suggested.isEmpty();
            })
            .collect(Collectors.toList());
    }

    public Emergency getHighestPriorityEmergency() {
        return getEmergencyOrdered().stream()
            .findFirst()
            .orElse(null);
    }

    public List<Emergency> getAlternativeEmergencies() {
        Emergency highestPriority = getHighestPriorityEmergency();
        if (highestPriority == null) {
            return new ArrayList<>();
        }

        // Si hay recursos para la emergencia de mayor prioridad, retornar lista vacía
        if (hasAvailableResourcesForEmergency(highestPriority) && hasSuggestedResourcesForEmergency(highestPriority)) {
            return new ArrayList<>();
        }

        // Si no hay recursos para la de mayor prioridad, retornar todas las que sí tienen recursos
        return getEmergencyOrdered().stream()
            .filter(emergency -> hasAvailableResourcesForEmergency(emergency) && hasSuggestedResourcesForEmergency(emergency))
            .collect(Collectors.toList());
    }
}
