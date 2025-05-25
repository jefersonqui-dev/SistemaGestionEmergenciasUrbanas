package com.devsenior.jquiguantar.SGEU.model.patterns.strategy;
import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergency;
import com.devsenior.jquiguantar.SGEU.model.config.OperationalBase;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PriorityByProximity implements PriorizationStrategy {
    @Override
    public List<Emergency> prioritize(List<Emergency> emergencies, List<OperationalBase> bases){
        OperationalBase base = bases.get(0);
        return emergencies.stream()
            .sorted(Comparator.comparing((Emergency e) -> e.getUbicacion().distanciaKm(base.getLocation())))
            .collect(Collectors.toList());
    }
}
