package com.devsenior.jquiguantar.SGEU.model.patterns.singleton;

import com.devsenior.jquiguantar.SGEU.model.config.PredefinedLocation;
import com.devsenior.jquiguantar.SGEU.model.config.OperationalBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.devsenior.jquiguantar.SGEU.model.config.LocationSettings;
import com.devsenior.jquiguantar.SGEU.model.util.Location;
import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergency;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class EmergencySistem {
    private static EmergencySistem instance;

    private List<PredefinedLocation> referencePoints;
    private List<OperationalBase> operationalBases;
    private List<Emergency> emergencies;
    private static final String CONFIG_FILE = "/bases.json";

    private EmergencySistem() {
        this.referencePoints = new ArrayList<>();
        this.operationalBases = new ArrayList<>();
        this.emergencies = new ArrayList<>();
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
                    // LLenar los datos del punto desde el json
                    PredefinedLocation point = new PredefinedLocation();
                    point.setId(pointNode.get("id").asText());
                    point.setNombre(pointNode.get("nombre").asText());
                    point.setDescription(pointNode.get("descripcion").asText());
    
                    // Creamos y configuramos la Ubicacion
                    JsonNode locationNode = pointNode.get("ubicacion");
                    LocationSettings location = new LocationSettings();
                    location.setLatitude(locationNode.get("latitud").asDouble());
                    location.setLongitude(locationNode.get("longitud").asDouble());
                    
                    // asignamos la ubicacion al punto
                    point.setLocation(location);
                    referencePoints.add(point);
                }
            }

        //cargar bases operativas
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
                }
            }

            inputStream.close();
        } catch (IOException e) {
            System.err.println("Error al cargar los puntos de referencia: " + e.getMessage());
        }
    }
    public static EmergencySistem getInstance() {
        if (instance == null) {
            instance = new EmergencySistem();
        }
        return instance;
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
    
    
    
    
}
