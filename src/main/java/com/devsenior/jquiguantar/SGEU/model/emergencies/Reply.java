package com.devsenior.jquiguantar.SGEU.model.emergencies;
// import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergency;
import com.devsenior.jquiguantar.SGEU.model.resources.Resource;
import java.util.List;
import com.devsenior.jquiguantar.SGEU.model.util.Location;
public interface Reply {
    void attendEmergency(Emergency emergency);
    boolean evaluateStatus();
    List<Resource> addResources(Emergency emergency);
    double calculateResponseTime(Location location);
    void notifyStatusChange();
    
}
