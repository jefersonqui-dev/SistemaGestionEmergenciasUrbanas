package com.devsenior.jquiguantar.SGEU.model.interfaces;
import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergency;
public interface Reply {
    void respondToEmergency(Emergency emergency);
    void assessStatus(Emergency emergency);
}
