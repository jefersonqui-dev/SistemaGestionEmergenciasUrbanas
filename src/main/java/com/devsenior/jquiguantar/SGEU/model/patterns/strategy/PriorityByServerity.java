package com.devsenior.jquiguantar.SGEU.model.patterns.strategy;

import com.devsenior.jquiguantar.SGEU.model.config.OperationalBase;
import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergency;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PriorityByServerity implements PriorizationStrategy {
    @Override
    public List<Emergency> prioritize(List<Emergency> emergencies, List<OperationalBase> bases){
        return emergencies.stream()
            .sorted(Comparator.comparing(Emergency::getNivelGravedad).reversed())
            .collect(Collectors.toList());
    }
}
