package com.devsenior.jquiguantar.SGEU.model.patterns.singleton;

import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergencia;
import com.devsenior.jquiguantar.SGEU.model.services.BaseOperaciones; // Importar BaseOperaciones
import com.devsenior.jquiguantar.SGEU.model.resources.Recurso;
import com.devsenior.jquiguantar.SGEU.model.resources.Vehiculo; // Importar Vehiculo
import com.devsenior.jquiguantar.SGEU.model.util.Ubicacion;
import com.devsenior.jquiguantar.SGEU.model.config.BaseConfig; // Importar clases de config
import com.devsenior.jquiguantar.SGEU.model.config.RecursoConfig; // Importar RecursoConfig de config

// Importaciones para JSON (Jackson)
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.JsonSerializable.Base;
import com.fasterxml.jackson.core.type.TypeReference;

import com.devsenior.jquiguantar.SGEU.model.patterns.observer.Observer;
import com.devsenior.jquiguantar.SGEU.model.patterns.observer.Observable; // Importar Subject

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
// import java.util.Date;
import java.util.stream.Collectors; // Para usar streams

public class SistemaEmergencias implements Observable {
    private static SistemaEmergencias instance;

    // Colecciones Principales del sistema
    private List<Emergencia> emergenciasActivas;
    private List<BaseOperaciones> basesOperaciones;

    // Objero para la logica del mapa urbano(clase interna)
    private MapaUrbano mapa;
    private List<Observer> observers; // Lista de observadores

    // Constructor privado
    private SistemaEmergencias() {
        this.emergenciasActivas = new ArrayList<>();
        this.basesOperaciones = new ArrayList<>();
        this.mapa = new MapaUrbano(); // crear instancia de la clase interna

        inicializarSistemaDesdeJson("bases.json"); // Inicializar el sistema desde JSON
        addBasesAsObservers(); // Añadir las bases como observadores
    }

    public static synchronized SistemaEmergencias getInstance() {
        if (instance == null) {
            instance = new SistemaEmergencias();
        }
        return instance;
    }

    private void inicializarSistemaDesdeJson(String nombreArchivo) {
        ObjectMapper objectMapper = new ObjectMapper();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(nombreArchivo)) {
            if (is == null) {
                System.err.println("Error: Archivo de configuración JSON no encontrado en recursos: " + nombreArchivo);
                // Considera lanzar una RuntimeException o manejar este error de forma más
                // robusta
                return;
            }

            // Leer el array de objetos BaseConfig desde el JSON
            List<BaseConfig> basesConfig = objectMapper.readValue(is, new TypeReference<List<BaseConfig>>() {
            });

            // Recorrer los datos leídos y crear los objetos del modelo (BaseOperaciones,
            // Recurso)
            for (BaseConfig baseConf : basesConfig) {
                // Crear la Ubicacion para la base usando las coordenadas del JSON
                Ubicacion ubicacionBase = new Ubicacion(baseConf.getUbicacion().getLatitud(),
                        baseConf.getUbicacion().getLongitud());

                // Crear la instancia de BaseOperaciones
                BaseOperaciones nuevaBase = new BaseOperaciones(baseConf.getId(), baseConf.getNombre(), ubicacionBase,
                        baseConf.getTipoServicio());

                // Crear y añadir recursos a la base desde la configuración del JSON
                if (baseConf.getRecursosIniciales() != null) {
                    for (RecursoConfig recursoConf : baseConf.getRecursosIniciales()) {
                        Recurso nuevoRecurso;
                        // Decidir si crear un Vehiculo o un Recurso genérico basado en el tipo
                        // O podrías tener un atributo en RecursoConfig para indicar si es vehículo
                        if (recursoConf.getTipo().toLowerCase().contains("camion") ||
                                recursoConf.getTipo().toLowerCase().contains("ambulancia") ||
                                recursoConf.getTipo().toLowerCase().contains("patrulla") ||
                                recursoConf.getTipo().toLowerCase().contains("vehiculo")) { // Asumiendo que estos tipos
                                                                                            // son vehículos
                            nuevoRecurso = new Vehiculo(recursoConf.getTipo(), recursoConf.getId(),
                                    recursoConf.getConsumoPorDistancia(), ubicacionBase);
                        } else {
                            nuevoRecurso = new Recurso(recursoConf.getTipo(), recursoConf.getId());
                        }
                        nuevaBase.addRecurso(nuevoRecurso);
                    }
                }

                // Añadir la base creada a la lista global de bases
                this.basesOperaciones.add(nuevaBase);
                System.out.println(
                        "Base cargada: " + nuevaBase.getNombre() + " (" + nuevaBase.getTipoServicioAsociado() + ")");
            }

            System.out.println(
                    "Sistema de emergencias inicializado con " + this.basesOperaciones.size() + " bases desde JSON.");

        } catch (IOException e) {
            e.printStackTrace(); // Imprimir el stack trace del error
            System.err.println("Error al leer o parsear el archivo JSON de bases: " + e.getMessage());
            // En un sistema real, podrías salir o cargar una configuración por defecto
        }
    }

    private void addBasesAsObservers() {
        for (BaseOperaciones base : this.basesOperaciones) {
            this.addObserver(base); // Añadir cada base como observador del sistema
        }
        System.out.println("Todas las bases de Operaciones registradas como observadores.");
    }

    // Método para añadir una emergencia al sistema
    public void registrarEmergencia(Emergencia emergencia) {
        this.emergenciasActivas.add(emergencia);
        System.out.println("Emergencia registrada: " + emergencia.getTipo() + " en "
                + emergencia.getUbicacion() + ", (ID: " + emergencia.getId() + ")");
    }

    @Override
    public void addObserver(Observer o) {
        if (o != null && !observers.contains(o)) {
            this.observers.add(o);
        }
    }

    @Override
    public void removeObserver(Observer o) {
        if (o != null) {
            this.observers.remove(o);
        }
    }

    @Override
    public void notifyObservers(Object event) {
        // Notificamos a cada observador sobre el evento (la nueva emergencia)
        if (event instanceof Emergencia) {
            Emergencia nuevEmergencia = (Emergencia) event;
            System.out.println("Notificamos a los observadores sobre una nueva emergencia...");
            for (Observer observer : this.observers) {
                observer.update(nuevEmergencia); // Llamamos al método update de cada observador
            }
        }
    }

    public List<Emergencia> getEmergenciasActivas() {
        return emergenciasActivas;
    }

    public List<BaseOperaciones> getBasesOperaciones() {
        return basesOperaciones;
    }

    // Metodo para obtener todos los recursos de todas las bases
    public List<Recurso> getAllRecursosDisponibles() {
        return this.basesOperaciones.stream()
                .flatMap(base -> base.getRecursosDisponibles().stream())
                .collect(Collectors.toList());
    }

    // metodo para obtener recursos disponibles por tipo en todas las bases
    public List<Recurso> getAllRecursosDisponiblesPorTipo(String tipoRecurso) {
        return getAllRecursosDisponibles().stream()
                .filter(r -> r.getTipo().equalsIgnoreCase(tipoRecurso))
                .collect(Collectors.toList());
    }

    public BaseOperaciones encontrarBaseMasCercana(Emergencia emergencia, String tipoServicioRequerido) {
        List<BaseOperaciones> basesRelevantes = this.basesOperaciones.stream()
                .filter(base -> base.getTipoServicioAsociado().equalsIgnoreCase(tipoServicioRequerido))
                .collect(Collectors.toList());

        BaseOperaciones baseMasCercana = null;
        double menorDistancia = Double.MAX_VALUE;
        for (BaseOperaciones base : basesRelevantes) {
            double distancia = this.mapa.calcularDistancia(emergencia.getUbicacion(), base.getUbicacion());
            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                baseMasCercana = base;
            }
        }
        return baseMasCercana;
    }

    private class MapaUrbano {
        // Metodo para calcular la distancia geografica (usando haversine para
        // coordenadas)
        // Retorna la distancia en kilometros
        public double calcularDistancia(Ubicacion u1, Ubicacion u2) {
            double lat1 = u1.getLatitud();
            double lon1 = u1.getLongitud();
            double lat2 = u2.getLatitud();
            double lon2 = u2.getLongitud();

            final int R = 6371; // Radio de la Tierra en km
            double latDist = lat2 - lat1;
            double lonDist = lon2 - lon1;

            double a = Math.sin(latDist / 2) * Math.sin(latDist / 2) +
                    Math.cos(lat1) * Math.cos(lat2) *
                            Math.sin(lonDist / 2) * Math.sin(lonDist / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            return R * c; // Distancia en km
        }
    }
}
