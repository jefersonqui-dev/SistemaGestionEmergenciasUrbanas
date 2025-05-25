package com.devsenior.jquiguantar.SGEU.model.patterns.strategy;

import com.devsenior.jquiguantar.SGEU.model.util.Location;
import com.devsenior.jquiguantar.SGEU.model.emergencies.EmergencyType;

public interface TimeCalculation {
    double CalculateTime(EmergencyType type, Location emergencyLocation);
}
