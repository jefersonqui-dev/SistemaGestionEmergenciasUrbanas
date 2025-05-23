package com.devsenior.jquiguantar.SGEU.model.patterns.singleton;

import com.devsenior.jquiguantar.SGEU.model.config.PredefinedLocation;
import com.devsenior.jquiguantar.SGEU.model.config.OperationalBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.devsenior.jquiguantar.SGEU.model.config.LocationSettings;
import com.devsenior.jquiguantar.SGEU.model.util.Location;
import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergency;
import com.devsenior.jquiguantar.SGEU.model.resources.Resource;
import com.devsenior.jquiguantar.SGEU.model.patterns.observer.EmergencyObserver;
import com.devsenior.jquiguantar.SGEU.model.services.EmergencyService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class EmergencySistem {
    private static EmergencySistem instance;

    private List<PredefinedLocation> referencePoints;
    private List<OperationalBase> operationalBases;
    private List<Emergency> emergencies;
    private List<Resource> resources;
    private static final String CONFIG_FILE = "/bases.json";
    private List<EmergencyObserver> observers;

    private EmergencySistem() {
        this.referencePoints = new ArrayList<>();
        this.operationalBases = new ArrayList<>();
        this.emergencies = new ArrayList<>();
        this.resources = new ArrayList<>();
        this.observers = new ArrayList<>();
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

        System.out.println("Emergencia registrada exitosamente.");
        
    }

    public List<Emergency> getEmergencies(){
        return new ArrayList<>(emergencies);
    }
    public List<Emergency> getActiveEmergencies(){
        return emergencies.stream()
            .filter(emergency -> !emergency.isAtendida())
            .toList();
    }

    public void notifyServices(Emergency emergency) {
        for (EmergencyObserver observer : observers) {
            observer.notifyNewEmergency(emergency);
        }
    }

    public List<Resource> getAssignedResources(Emergency emergency) {
        List<Resource> assignedResources = new ArrayList<>();
        for (EmergencyObserver observer : observers) {
            if (observer instanceof EmergencyService) {
                EmergencyService service = (EmergencyService) observer;
                if (service.canAttend(emergency)) {
                    assignedResources.addAll(service.addResources(emergency));
                }
            }
        }
        return assignedResources;
    }

    public void addObserver(EmergencyObserver observer) {
        observers.add(observer);
    }
    
    
    
    
}
