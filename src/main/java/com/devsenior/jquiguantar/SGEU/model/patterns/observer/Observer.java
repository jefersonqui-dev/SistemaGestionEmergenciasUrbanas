package com.devsenior.jquiguantar.SGEU.model.patterns.observer;

import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergencia;

public interface Observer {
    void update(Emergencia emergencia);
}
