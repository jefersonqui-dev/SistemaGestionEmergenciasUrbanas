package com.devsenior.jquiguantar.SGEU.model.services;

import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergency;
import com.devsenior.jquiguantar.SGEU.model.emergencies.Reply;
import com.devsenior.jquiguantar.SGEU.model.patterns.observer.EmergencyObserver;
import com.devsenior.jquiguantar.SGEU.model.resources.Resource;
// import com.devsenior.jquiguantar.SGEU.model.util.Location;
import java.util.ArrayList;
import java.util.List;

public abstract class EmergencyService implements Reply, EmergencyObserver {
    protected List<Resource> resources;
    protected boolean available;
    protected String name;

    public EmergencyService(String name) {
        this.name = name;
        this.resources = new ArrayList<>();
        this.available = true;
    }

    @Override
    public void notifyNewEmergency(Emergency emergency) {
        if (canAttend(emergency)) {
            attendEmergency(emergency);
        }
    }

    @Override
    public void updateEmergency(Emergency emergency) {
        if (emergency.isAtendida()) {
            releaseResources(emergency);
        }
    }

    @Override
    public boolean evaluateStatus() {
        return available && !resources.isEmpty();
    }

    public void addResource(Resource resource) {
        resources.add(resource);
    }

    public String getName() {
        return name;
    }

    public abstract boolean canAttend(Emergency emergency);
    protected abstract void releaseResources(Emergency emergency);
} 