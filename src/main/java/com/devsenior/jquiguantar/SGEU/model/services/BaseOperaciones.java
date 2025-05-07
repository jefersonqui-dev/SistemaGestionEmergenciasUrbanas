package com.devsenior.jquiguantar.SGEU.model.services;

import com.devsenior.jquiguantar.SGEU.model.util.Ubicacion;
import com.devsenior.jquiguantar.SGEU.model.resources.Recurso;
import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergencia;
import com.devsenior.jquiguantar.SGEU.model.interfaces.Responder;
import com.devsenior.jquiguantar.SGEU.model.resources.EstadoRecurso;
import com.devsenior.jquiguantar.SGEU.model.patterns.observer.Observer;
import com.devsenior.jquiguantar.SGEU.model.emergencies.TipoEmergencia;
import com.devsenior.jquiguantar.SGEU.model.patterns.singleton.SistemaEmergencias;

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
        
        // Crear un objeto que contenga toda la información de la notificación
        NotificacionEmergencia notificacion = new NotificacionEmergencia(
            getNombre(),
            getTipoServicioAsociado(),
            nuevaEmergencia.getTipo(),
            nuevaEmergencia.getUbicacion(),
            nuevaEmergencia.getId(),
            reacciona
        );
        
        // Notificar al sistema sobre la notificación
        SistemaEmergencias.getInstance().notificarActualizacion(notificacion);
    }

    // Clase interna para encapsular la información de la notificación
    public static class NotificacionEmergencia {
        private final String nombreBase;
        private final String tipoServicio;
        private final TipoEmergencia tipoEmergencia;
        private final Ubicacion ubicacion;
        private final int idEmergencia;
        private final boolean reacciona;

        public NotificacionEmergencia(String nombreBase, String tipoServicio, TipoEmergencia tipoEmergencia,
                                    Ubicacion ubicacion, int idEmergencia, boolean reacciona) {
            this.nombreBase = nombreBase;
            this.tipoServicio = tipoServicio;
            this.tipoEmergencia = tipoEmergencia;
            this.ubicacion = ubicacion;
            this.idEmergencia = idEmergencia;
            this.reacciona = reacciona;
        }

        // Getters
        public String getNombreBase() { return nombreBase; }
        public String getTipoServicio() { return tipoServicio; }
        public TipoEmergencia getTipoEmergencia() { return tipoEmergencia; }
        public Ubicacion getUbicacion() { return ubicacion; }
        public int getIdEmergencia() { return idEmergencia; }
        public boolean getReacciona() { return reacciona; }
    }

    @Override
    public String toString() {
        return "Base [ID= " + id + ", Nombre=" + nombre + ", Ubicacion=" + ubicacion.toString() + ", Tipo="
                + tipoServicioAsociado + ", Recursos=" + recursosEnBase.size() + "]";
    }

}
