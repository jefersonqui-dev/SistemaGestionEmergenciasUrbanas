package com.devsenior.jquiguantar.SGEU.model.services;

import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergency;
import com.devsenior.jquiguantar.SGEU.model.emergencies.EmergencyType;
import com.devsenior.jquiguantar.SGEU.model.resources.Resource;
import com.devsenior.jquiguantar.SGEU.model.util.Location;
import java.util.List;
import java.util.stream.Collectors;

public class FireService extends EmergencyService {
    
    public FireService() {
        super("Servicio de Bomberos");
    }

    @Override
    public void attendEmergency(Emergency emergency) {
        if (canAttend(emergency)) {
            List<Resource> resourcesAssigned = addResources(emergency);
            resourcesAssigned.forEach(resource -> resource.setAvailable(false));
            emergency.setAtendida(true);
        }
    }

    @Override
    public List<Resource> addResources(Emergency emergency) {
        return resources.stream()
            .filter(Resource::isAvailable)
            .filter(resource -> resource.getType().equals("Camión Bombero"))
            .limit(emergency.getNivelGravedad().ordinal() + 1)
            .collect(Collectors.toList());
    }

    @Override
    public double calculateResponseTime(Location location) {
        // Implementación simplificada - en un caso real se calcularía la distancia
        return 5.0; // 5 minutos como ejemplo
    }

    @Override
    public void notifyStatusChange() {
        available = evaluateStatus();
    }

    @Override
    public boolean canAttend(Emergency emergency) {
        return emergency.getTipo() == EmergencyType.INCENDIO && 
               resources.stream().anyMatch(Resource::isAvailable);
    }

    @Override
    protected void releaseResources(Emergency emergency) {
        resources.forEach(resource -> resource.setAvailable(true));
    }
} 