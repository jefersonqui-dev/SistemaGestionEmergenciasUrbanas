package com.devsenior.jquiguantar.SGEU.model.patterns.singleton;

import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergencia;
import com.devsenior.jquiguantar.SGEU.model.services.BaseOperaciones; // Importar BaseOperaciones
import com.devsenior.jquiguantar.SGEU.model.resources.EstadoRecurso;
import com.devsenior.jquiguantar.SGEU.model.resources.Recurso;
import com.devsenior.jquiguantar.SGEU.model.resources.Vehiculo; // Importar Vehiculo
import com.devsenior.jquiguantar.SGEU.model.util.Ubicacion;
import com.devsenior.jquiguantar.SGEU.model.config.BaseConfig; // Importar clases de config
import com.devsenior.jquiguantar.SGEU.model.config.RecursoConfig; // Importar RecursoConfig de config

// Importaciones para JSON (Jackson)
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import com.devsenior.jquiguantar.SGEU.model.patterns.observer.Observer;
import com.devsenior.jquiguantar.SGEU.model.patterns.observer.Observable; // Importar Subject

import com.devsenior.jquiguantar.SGEU.model.patterns.strategy.PrioridadAltaStrategy;
import com.devsenior.jquiguantar.SGEU.model.patterns.strategy.PriorizacionStrategy;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.stream.Collectors; // Para usar streams
import java.util.IntSummaryStatistics; // Para estadísticas de ints/longs
import java.util.DoubleSummaryStatistics;

public class SistemaEmergencias implements Observable {
    private static SistemaEmergencias instance;

    // Colecciones Principales del sistema
    private List<Emergencia> emergenciasActivas;
    private List<BaseOperaciones> basesOperaciones;

    // Objeto para la lógica del mapa urbano (clase interna)
    private MapaUrbano mapa;
    private List<Observer> observers; // Lista de observadores
    private List<BaseOperaciones.NotificacionEmergencia> notificacionesPendientes; // Lista de notificaciones pendientes

    // Estrategia de priorización de emergencias
    private PriorizacionStrategy estrategiaPriorizacionActual;

    // Constructor privado
    private SistemaEmergencias() {
        this.emergenciasActivas = new ArrayList<>();
        this.basesOperaciones = new ArrayList<>();
        this.mapa = new MapaUrbano(); // crear instancia de la clase interna
        this.observers = new ArrayList<>();
        this.notificacionesPendientes = new ArrayList<>();
        this.estrategiaPriorizacionActual = new PrioridadAltaStrategy();

        // La inicialización y adición de observadores se moverá al Controlador
    }

    public static synchronized SistemaEmergencias getInstance() {
        if (instance == null) {
            instance = new SistemaEmergencias();
        }
        return instance;
    }

    // Modificado para retornar boolean e indicar fallo sin imprimir error
    public boolean inicializarSistemaDesdeJson(String nombreArchivo) {
        ObjectMapper objectMapper = new ObjectMapper();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(nombreArchivo)) {
            if (is == null) {
                // El Controlador manejará la impresión del error
                return false;
            }

            List<BaseConfig> basesConfig = objectMapper.readValue(is, new TypeReference<List<BaseConfig>>() {
            });

            for (BaseConfig baseConf : basesConfig) {
                Ubicacion ubicacionBase = new Ubicacion(baseConf.getUbicacion().getLatitud(),
                        baseConf.getUbicacion().getLongitud());

                BaseOperaciones nuevaBase = new BaseOperaciones(baseConf.getId(), baseConf.getNombre(), ubicacionBase,
                        baseConf.getTipoServicio());

                if (baseConf.getRecursosIniciales() != null) {
                    for (RecursoConfig recursoConf : baseConf.getRecursosIniciales()) {
                        Recurso nuevoRecurso;
                        if (recursoConf.getTipo().toLowerCase().contains("camion") ||
                                recursoConf.getTipo().toLowerCase().contains("ambulancia") ||
                                recursoConf.getTipo().toLowerCase().contains("patrulla") ||
                                recursoConf.getTipo().toLowerCase().contains("vehiculo")) {
                            nuevoRecurso = new Vehiculo(recursoConf.getTipo(), recursoConf.getId(),
                                    recursoConf.getConsumoPorDistancia(), ubicacionBase);
                        } else {
                            nuevoRecurso = new Recurso(recursoConf.getTipo(), recursoConf.getId());
                        }
                        nuevaBase.addRecurso(nuevoRecurso);
                    }
                }
                this.basesOperaciones.add(nuevaBase);
                // Eliminada la impresión "Base cargada..."
            }
            // Eliminada la impresión "Sistema inicializado..."
            return true; // Indica inicialización exitosa

        } catch (IOException e) {
            e.printStackTrace(); // Considera manejar esto de forma más robusta en el Controlador
            // Eliminada la impresión de error
            return false; // Indica fallo en la inicialización
        }
    }

    // Eliminada la impresión de confirmación
    public void addBasesAsObservers() {
        for (BaseOperaciones base : this.basesOperaciones) {
            this.addObserver(base);
        }
    }

    // Eliminada la impresión de confirmación
    public void setEstrategiaPriorizacionActual(PriorizacionStrategy estrategiaPriorizacionActual) {
        this.estrategiaPriorizacionActual = estrategiaPriorizacionActual;
    }

    public Emergencia getProximaEmergenciaAPriorizar() {
        return this.estrategiaPriorizacionActual.seleccionarSiguienteEmergencia(this.emergenciasActivas);
    }

    public void registrarEmergencia(Emergencia emergencia) {
        this.emergenciasActivas.add(emergencia);
        // La notificación a observadores se hará en el Controlador después de registrar
        // this.notifyObservers(emergencia); // Esto se llama desde el Controlador
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

    // Eliminada la impresión previa a la notificación
    @Override
    public void notifyObservers(Object event) {
        if (event instanceof Emergencia) {
            Emergencia nuevaEmergencia = (Emergencia) event;
            for (Observer observer : this.observers) {
                observer.update(nuevaEmergencia);
            }
        }
    }

    // Clase interna para encapsular el resultado de la asignación de recursos
    public static class AssignmentResult {
        private final boolean overallSuccess; // Indica si al menos un recurso fue asignado con éxito
        private final List<String> messages; // Mensajes detallados sobre cada intento de asignación

        public AssignmentResult(boolean overallSuccess, List<String> messages) {
            this.overallSuccess = overallSuccess;
            this.messages = messages;
        }

        public boolean isOverallSuccess() {
            return overallSuccess;
        }

        public List<String> getMessages() {
            return messages;
        }
    }

    // Modificado para retornar un AssignmentResult con mensajes y estado general
    public AssignmentResult asignarRecursosAEmergencia(Emergencia emergencia, List<Recurso> recursosAAsignar) {
        List<String> messages = new ArrayList<>();
        boolean overallSuccess = false; // Por defecto, asumimos que no se asigna ningún recurso exitosamente

        if (emergencia == null || recursosAAsignar == null || recursosAAsignar.isEmpty()) {
            messages.add("Error: No se especificó la emergencia o los recursos a asignar.");
            return new AssignmentResult(false, messages);
        }

        messages.add("Intentando asignar " + recursosAAsignar.size() + " recursos a Emergencia ID "
                + emergencia.getId() + "...");

        if (emergencia.getTiempoInicioAtencion() == null) {
            emergencia.setTiempoInicioAtencion(new Date());
            messages.add("Inicio de atención registrado para Emergencia ID " + emergencia.getId());
        }

        List<Recurso> recursosRealmenteAsignados = new ArrayList<>();
        // double distanciaAEmergencia = 0; // Eliminado: se calculará individualmente para cada vehículo

        // // Calcular distancia solo si hay vehículos para asignar // Lógica anterior eliminada
        // if (!recursosAAsignar.isEmpty() && recursosAAsignar.get(0) instanceof Vehiculo) {
        //      Ubicacion origen = ((Vehiculo) recursosAAsignar.get(0)).getUbicacionBase(); 
        //      distanciaAEmergencia = this.mapa.calcularDistancia(origen, emergencia.getUbicacion());
        //      if (distanciaAEmergencia <= 0) { ... }
        // }


        for (Recurso recurso : recursosAAsignar) {
            if (recurso.getEstado() == EstadoRecurso.DISPONIBLE) {
                boolean puedeAsignarse = true;
                String mensajeRecurso = recurso.getTipo() + " (ID: " + recurso.getId() + "): ";

                if (recurso instanceof Vehiculo) {
                    Vehiculo vehiculo = (Vehiculo) recurso;
                    double distanciaParaEsteVehiculo = 0;
                    Ubicacion origenVehiculo = vehiculo.getUbicacionBase(); // Asume que Vehiculo tiene este método
                    Ubicacion destinoEmergencia = emergencia.getUbicacion(); // Asume que Emergencia tiene este método

                    if (origenVehiculo == null || destinoEmergencia == null) {
                        messages.add(mensajeRecurso + "No asignado - Ubicación de origen del vehículo o destino de emergencia no disponible.");
                        puedeAsignarse = false;
                    } else {
                        distanciaParaEsteVehiculo = this.mapa.calcularDistancia(origenVehiculo, destinoEmergencia);

                        // Validar distancia calculada
                        if (distanciaParaEsteVehiculo < 0) { // Distancia no puede ser negativa
                            messages.add(mensajeRecurso + "No asignado - Cálculo de distancia inválido (" + String.format("%.2f", distanciaParaEsteVehiculo) + " km).");
                            puedeAsignarse = false;
                        } else if (distanciaParaEsteVehiculo == 0) {
                            messages.add(mensajeRecurso + "Vehículo ya se encuentra en la ubicación de la emergencia o la distancia es cero. No se consumirá combustible por desplazamiento.");
                            // No se impide la asignación, pero el consumo por distancia será cero.
                        }

                        if (puedeAsignarse) { // Solo proceder si la distancia es válida
                            if (!vehiculo.tieneSuficienteCombustible(distanciaParaEsteVehiculo)) { // Asume que Vehiculo tiene este método
                                messages.add(mensajeRecurso + "No asignado - Combustible insuficiente para el viaje estimado de "
                                        + String.format("%.2f", distanciaParaEsteVehiculo) + " km.");
                                puedeAsignarse = false;
                            } else {
                                // moverA debe consumir el combustible y actualizar el estado del vehículo
                                vehiculo.moverA(destinoEmergencia, distanciaParaEsteVehiculo); // Asume que Vehiculo tiene este método
                                messages.add(mensajeRecurso + "Asignado y simulando viaje a emergencia ID " + emergencia.getId() + " ("
                                        + String.format("%.2f", distanciaParaEsteVehiculo) + " km). Combustible restante: " + String.format("%.1f", vehiculo.getNivelCombustible()) + "%.D");
                                 // El mensaje ya indica que fue asignado, se confirmará abajo si todo fue bien.
                            }
                        }
                    }
                }

                if (puedeAsignarse) {
                    recurso.asignarEmergencia(emergencia);
                    recursosRealmenteAsignados.add(recurso);
                    // Se mueve el mensaje de "Asignado exitosamente" más abajo para que sea general
                    // messages.add(mensajeRecurso + "Asignado exitosamente."); 
                    overallSuccess = true; // Al menos un recurso fue asignado
                }

            } else {
                messages.add(recurso.getTipo() + " (ID: " + recurso.getId() + "): No asignado - Estado actual: " + recurso.getEstado() + " (no está disponible).");
            }
        }

        if (!recursosRealmenteAsignados.isEmpty()) {
            messages.add("Asignación de recursos completada para Emergencia ID " + emergencia.getId()
                    + ". Recursos asignados: " + recursosRealmenteAsignados.size());
            for(Recurso rAsignado : recursosRealmenteAsignados) {
                messages.add("  - " + rAsignado.getTipo() + " (ID: " + rAsignado.getId() + ") asignado.");
            }
        } else if (overallSuccess) {
            // Este caso es por si un recurso no vehicular fue asignado pero ningún vehículo, o si hubo errores con vehículos
            // pero otros recursos sí. El mensaje de arriba es más general si hubo asignaciones.
             messages.add("Algunos recursos fueron procesados para Emergencia ID " + emergencia.getId() + ", pero no todos pudieron ser asignados.");
        } else {
            messages.add("No se pudo asignar ningún recurso válido a Emergencia ID " + emergencia.getId());
        }

        return new AssignmentResult(overallSuccess, messages);
    }


    // METODO AUXILIAR PARA ENCONTRAR UN RECURSO POR ID, UTIL PARA ASIGNACION MANUAL
    public Recurso getRecursoById(int idRecurso) {
        return this.basesOperaciones.stream()
                .flatMap(base -> base.getRecursosEnBase().stream())
                .filter(r -> r.getId() == idRecurso)
                .findFirst()
                .orElse(null);
    }

    // Modificado para retornar un String con el resultado del repostaje
    public String iniciarRepostajeRecurso(int idRecurso) {
        Recurso recurso = getRecursoById(idRecurso);
        if (recurso instanceof Vehiculo) {
            Vehiculo vehiculo = (Vehiculo) recurso;
            if (vehiculo.getEstado() == EstadoRecurso.DISPONIBLE) {
                vehiculo.iniciarRepostaje(); // Este método en Vehiculo ahora debe manejar su propia impresión o retornar un mensaje
                return "Solicitud de inicio de repostaje para " + vehiculo.getTipo() + " (ID: " + vehiculo.getId() + ") procesada.";
            } else {
                return "No se puede iniciar repostaje para " + vehiculo.getTipo() + " (ID: " + vehiculo.getId() + "). Estado actual: " + vehiculo.getEstado() + ".";
            }
        } else {
            return "El Recurso con ID " + idRecurso + " no es un vehículo y no requiere repostaje.";
        }
    }

    // Modificado para retornar un String con el resultado del repostaje
    public String completarRepostajeRecurso(int idRecurso) {
        Recurso recurso = getRecursoById(idRecurso);
        if (recurso instanceof Vehiculo) {
            Vehiculo vehiculo = (Vehiculo) recurso;
            if (vehiculo.getEstado() == EstadoRecurso.REPOSTANDO) {
                vehiculo.completarRepostaje(); // Este método en Vehiculo ahora debe manejar su propia impresión o retornar un mensaje
                return "Solicitud de completado de repostaje para " + vehiculo.getTipo() + " (ID: " + vehiculo.getId() + ") procesada.";
            } else {
                return "No se puede completar repostaje para " + vehiculo.getTipo() + " (ID: " + vehiculo.getId() + "). Estado actual: " + vehiculo.getEstado() + ". No estaba repostando.";
            }
        } else {
            return "El Recurso con ID " + idRecurso + " no es un vehículo.";
        }
    }


    private class MapaUrbano {
        public double calcularDistancia(Ubicacion u1, Ubicacion u2) {
            if (u1 == null || u2 == null) {
                // Eliminada la impresión de error. El llamador debe validar las ubicaciones.
                return 0.0; // Podrías considerar lanzar una IllegalArgumentException aquí.
            }

            double lat1 = Math.toRadians(u1.getLatitud()); // Convertir a radianes
            double lon1 = Math.toRadians(u1.getLongitud()); // Convertir a radianes
            double lat2 = Math.toRadians(u2.getLatitud()); // Convertir a radianes
            double lon2 = Math.toRadians(u2.getLongitud()); // Convertir a radianes


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


    //Metodos para calcular estadisticas

    // Total de emergencias registradas
    public int getTotalEmergenciasRegistradas() {
        return emergenciasActivas.size(); // Si emergenciasActivas guarda todas las registradas
    }

    // Total de emergencias resueltas
    public long getTotalEmergenciasResueltas() {
        return emergenciasActivas.stream()
                .filter(Emergencia::isAtendida)
                .count();
    }

    // Tiempo promedio de respuesta (desde registro hasta inicio de atención)
    public double getTiempoPromedioRealRespuestaMillis() {
        IntSummaryStatistics stats = emergenciasActivas.stream()
                .filter(e -> e.calcularTiempoRealRespuestaMillis() != -1)
                .collect(Collectors.summarizingInt(e -> (int) e.calcularTiempoRealRespuestaMillis()));

        return stats.getAverage();
    }

    // Tiempo promedio total de atención (desde inicio hasta fin)
    public double getTiempoPromedioTotalAtencionMillis() {
        IntSummaryStatistics stats = emergenciasActivas.stream()
                .filter(e -> e.calcularTiempoTotalAtencionMillis() != -1)
                .collect(Collectors.summarizingInt(e -> (int) e.calcularTiempoTotalAtencionMillis()));

        return stats.getAverage();
    }

    // Porcentaje de emergencias resueltas
    public double getPorcentajeEmergenciasResueltas() {
        int total = getTotalEmergenciasRegistradas();
        if (total == 0)
            return 0.0;
        return (double) getTotalEmergenciasResueltas() / total * 100.0;
    }

    // Número de recursos por estado (ej. Disponibles, Ocupados, Repostando)
    public long getCantidadRecursosPorEstado(EstadoRecurso estado) {
        return basesOperaciones.stream()
                .flatMap(base -> base.getRecursosEnBase().stream())
                .filter(r -> r.getEstado() == estado)
                .count();
    }

    // Nivel promedio de combustible de los vehículos
    public double getNivelPromedioCombustibleVehiculos() {
        DoubleSummaryStatistics stats = basesOperaciones.stream()
                .flatMap(base -> base.getRecursosEnBase().stream())
                .filter(r -> r instanceof Vehiculo)
                .mapToDouble(r -> ((Vehiculo) r).getNivelCombustible())
                .summaryStatistics();

        return stats.getAverage();
    }

    public double calcularDistancia(Ubicacion u1, Ubicacion u2) {
        return this.mapa.calcularDistancia(u1, u2);
    }

    public Emergencia getEmergenciaById(int id) {
        return emergenciasActivas.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);
    }

     // Añadido getter para obtener las emergencias registradas (activas + resueltas)
     // asumiendo que 'emergenciasActivas' en realidad guarda todas las registradas
     public List<Emergencia> getAllEmergencias() {
         return this.emergenciasActivas;
     }

    // Método para obtener todos los recursos del sistema
    public List<Recurso> getAllRecursos() {
        return this.basesOperaciones.stream()
                .flatMap(base -> base.getRecursosEnBase().stream())
                .collect(Collectors.toList());
    }

    // Método para obtener recursos disponibles
    public List<Recurso> getAllRecursosDisponibles() {
        return this.basesOperaciones.stream()
                .flatMap(base -> base.getRecursosEnBase().stream())
                .filter(r -> r.getEstado() == EstadoRecurso.DISPONIBLE)
                .collect(Collectors.toList());
    }

    // Método para obtener emergencias no atendidas
    public List<Emergencia> getEmergenciasNoAtendidas() {
        return this.emergenciasActivas.stream()
                .filter(e -> !e.isAtendida())
                .collect(Collectors.toList());
    }

    // Método para sugerir recursos automáticamente
    public List<Recurso> sugerirRecursosAutomaticos(Emergencia emergencia) {
        // Por ahora, simplemente retornamos todos los recursos disponibles
        // En una implementación más sofisticada, podríamos filtrar por tipo de emergencia,
        // distancia a la emergencia, etc.
        return getAllRecursosDisponibles();
    }

    // Clase interna para encapsular el resultado de la liberación de recursos
    public static class LiberationResult {
        private final boolean success;
        private final List<String> messages;

        public LiberationResult(boolean success, List<String> messages) {
            this.success = success;
            this.messages = messages;
        }

        public boolean isSuccess() {
            return success;
        }

        public List<String> getMessages() {
            return messages;
        }
    }

    // Método para liberar recursos de una emergencia
    public LiberationResult liberarRecursosDeEmergencia(Emergencia emergencia) {
        List<String> messages = new ArrayList<>();
        boolean success = false;

        if (emergencia == null) {
            messages.add("Error: No se especificó la emergencia.");
            return new LiberationResult(false, messages);
        }

        List<Recurso> recursosALiberar = this.basesOperaciones.stream()
                .flatMap(base -> base.getRecursosEnBase().stream())
                .filter(r -> r.getEmergenciaAsignada() != null && r.getEmergenciaAsignada().getId() == emergencia.getId())
                .collect(Collectors.toList());

        if (recursosALiberar.isEmpty()) {
            messages.add("No se encontraron recursos asignados a la emergencia ID " + emergencia.getId());
            return new LiberationResult(false, messages);
        }

        for (Recurso recurso : recursosALiberar) {
            recurso.liberar();
            messages.add("Recurso " + recurso.getTipo() + " (ID: " + recurso.getId() + ") liberado exitosamente.");
            success = true;
        }

        messages.add("Liberación de recursos completada para Emergencia ID " + emergencia.getId());
        return new LiberationResult(success, messages);
    }

    public void notificarActualizacion(BaseOperaciones.NotificacionEmergencia notificacion) {
        this.notificacionesPendientes.add(notificacion);
    }

    public List<BaseOperaciones.NotificacionEmergencia> getNotificacionesPendientes() {
        List<BaseOperaciones.NotificacionEmergencia> notificaciones = new ArrayList<>(this.notificacionesPendientes);
        this.notificacionesPendientes.clear(); // Limpiar las notificaciones después de obtenerlas
        return notificaciones;
    }
}