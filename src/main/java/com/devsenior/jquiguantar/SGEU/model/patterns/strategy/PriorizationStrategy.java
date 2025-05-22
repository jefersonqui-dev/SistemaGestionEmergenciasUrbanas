package com.devsenior.jquiguantar.SGEU.model.patterns.strategy;

import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergency;
import java.util.List;

public interface PriorizationStrategy {
    List<Emergency> priorizeEmergencies(List<Emergency> emergencies);
    double calculatePriority(Emergency emergency);
} 