package com.devsenior.jquiguantar.SGEU.model.patterns.strategy;

import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergency;
// import com.devsenior.jquiguantar.SGEU.model.emergencies.SeverityLevel;
import java.util.List;
import java.util.Comparator;

public class SeverityBasedPriorization implements PriorizationStrategy {
    @Override
    public List<Emergency> priorizeEmergencies(List<Emergency> emergencies) {
        return emergencies.stream()
            .sorted(Comparator.comparing(this::calculatePriority).reversed())
            .toList();
    }

    @Override
    public double calculatePriority(Emergency emergency) {
        return switch (emergency.getNivelGravedad()) {
            case ALTO -> 3.0; //mayor prioridad
            case MEDIO -> 2.0;
            case BAJO -> 1.0;
        };
    }
} 