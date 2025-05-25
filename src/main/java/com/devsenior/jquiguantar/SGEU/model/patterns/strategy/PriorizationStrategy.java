package com.devsenior.jquiguantar.SGEU.model.patterns.strategy;
import com.devsenior.jquiguantar.SGEU.model.config.OperationalBase;
import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergency;
import java.util.List;

public interface PriorizationStrategy {
    List<Emergency> prioritize(List<Emergency> emergencias, List<OperationalBase> bases);
}
