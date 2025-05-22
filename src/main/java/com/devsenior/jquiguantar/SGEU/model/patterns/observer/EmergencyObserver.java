package com.devsenior.jquiguantar.SGEU.model.patterns.observer;

import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergency;

public interface EmergencyObserver {
    void updateEmergency(Emergency emergency);
    void notifyNewEmergency(Emergency emergency);
} 