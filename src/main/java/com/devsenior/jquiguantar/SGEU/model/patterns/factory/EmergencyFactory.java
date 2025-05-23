package com.devsenior.jquiguantar.SGEU.model.patterns.factory;

import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergency;
import com.devsenior.jquiguantar.SGEU.model.emergencies.EmergencyType;
import com.devsenior.jquiguantar.SGEU.model.emergencies.SeverityLevel;
import com.devsenior.jquiguantar.SGEU.model.util.Location;

public interface EmergencyFactory {
    Emergency createEmergency(EmergencyType type, SeverityLevel severity, Location location, double estimatedTime);
} 