package com.devsenior.jquiguantar.SGEU.model.services;

import com.devsenior.jquiguantar.SGEU.model.util.Ubicacion;
import com.devsenior.jquiguantar.SGEU.model.resources.Recurso;
import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergencia;
import com.devsenior.jquiguantar.SGEU.model.interfaces.Responder;
import com.devsenior.jquiguantar.SGEU.model.resources.EstadoRecurso;
import com.devsenior.jquiguantar.SGEU.model.patterns.observer.Observer;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

//Esta base de operaciones implementa Responder para representar la respueta desde esa base
public class BaseOperaciones implements Responder, Observer {
    private String id;
    private String nombre;
    private Ubicacion ubicacion;
    private String tipoServicioAsociado; // ej: Bomberos, Ambulancia, Policia
    private List<Recurso> recursosEnBase; // lista de recursos que tiene la base de operaciones

    // Constructor
    public BaseOperaciones(String id, String nombre, Ubicacion ubicacion, String tipoServicioAsociado) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.tipoServicioAsociado = tipoServicioAsociado;
        this.recursosEnBase = new ArrayList<>();
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public String getTipoServicioAsociado() {
        return tipoServicioAsociado;
    }

    public List<Recurso> getRecursosEnBase() {
        return recursosEnBase;
    }

    /**
     * Añade recursos a esta base, usado durante la inicializacion desde JSON
     * 
     * @param recurso el recurso a agregar
     */
    public void addRecurso(Recurso recurso) {
        recursosEnBase.add(recurso);
    }

    /**
     * Devuelve una lista de recursos en esta base que estan disponibles
     * (DISPONIBLE) para ser asignados a una emergencia
     * 
     * @return lista de recursos disponibles
     */
    public List<Recurso> getRecursosDisponibles() {
        return this.getRecursosEnBase().stream()
                .filter(r -> r.getEstado() == EstadoRecurso.DISPONIBLE)
                .collect(Collectors.toList());
    }

    /**
     * Devuelve una lista de recursos en esta base que estan disponibles
     * (DISPONIBLE) y coinciden con el tipo de recurso especificado
     * 
     * @param tipoRecurso el tipo de recurso a buscar
     * @return lista de recursos disponibles del tipo especificado
     */
    public List<Recurso> getRecursosDisponiblesPorTipo(String tipoRecurso) {
        return getRecursosDisponibles().stream()
                .filter(r -> r.getTipo().equalsIgnoreCase(tipoRecurso))
                .collect(Collectors.toList());
    }

    @Override
    public void atenderEmergencia(Emergencia emergencia) {
        System.out.println(getTipoServicioAsociado() + " desde " + getNombre() + " evaluando estado de emergencia: "
                + emergencia.getId());

    }

    @Override
    public void evaluarEstado(Emergencia emergencia) {
        System.out.println(getTipoServicioAsociado() + " desde " + getNombre() + " evaluando estado de emergencia: "
                + emergencia.getId());
    }

    // Implementacion del metodo de la interfaz Observer
    @Override
    public void update(Emergencia nuevaEmergencia) {
        System.out.println(getNombre() + " (" + getTipoServicioAsociado() + ") notificada de nueva emergencia: "
                + nuevaEmergencia.getTipo() + " en " + nuevaEmergencia.getUbicacion() + " (ID: "
                + nuevaEmergencia.getId() + ")");
        boolean reacciona = false;
        // Una base de bomberos reacciona a incendios, una base de policia a robos, una
        // ambulancia a accidentes
        switch (getTipoServicioAsociado()) {
            case "BOMBEROS":
                reacciona = (nuevaEmergencia
                        .getTipo() == com.devsenior.jquiguantar.SGEU.model.emergencies.TipoEmergencia.INCENDIO);
                break;
            case "AMBULANCIA":
                reacciona = nuevaEmergencia
                        .getTipo() == com.devsenior.jquiguantar.SGEU.model.emergencies.TipoEmergencia.ACCIDENTE_VEHICULAR;
                break;
            case "POLICIA":
                reacciona = nuevaEmergencia
                        .getTipo() == com.devsenior.jquiguantar.SGEU.model.emergencies.TipoEmergencia.ROBO;
                break;
        }
        if (reacciona) {
            System.out.println(" -> " + getNombre() + "Considera esta Emergencia");
        }
    }

    @Override
    public String toString() {
        return "Base [ID= " + id + ", Nombre=" + nombre + ", Ubicacion=" + ubicacion.toString() + ", Tipo="
                + tipoServicioAsociado + ", Recursos=" + recursosEnBase.size() + "]";
    }

}
