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
//import com.fasterxml.jackson.databind.JsonSerializable.Base;
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

    // Objero para la logica del mapa urbano(clase interna)
    private MapaUrbano mapa;
    private List<Observer> observers; // Lista de observadores

    // Estrategia de priorización de emergencias
    private PriorizacionStrategy estrategiaPriorizacionActual;

    // Constructor privado
    private SistemaEmergencias() {
        this.emergenciasActivas = new ArrayList<>();
        this.basesOperaciones = new ArrayList<>();
        this.mapa = new MapaUrbano(); // crear instancia de la clase interna
        this.observers = new ArrayList<>();
        this.estrategiaPriorizacionActual = new PrioridadAltaStrategy();

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

    // Metodo para cambiar la estrategia de priorizacion en tiempo de ejecucion
    public void setEstrategiaPriorizacionActual(PriorizacionStrategy estrategiPriorizacionActual) {
        this.estrategiaPriorizacionActual = estrategiPriorizacionActual;
        System.out.println(
                "Estrategia de priorización actualizada a: " + estrategiPriorizacionActual.getClass().getSimpleName());
    }

    // Metodo para obtener la proxima emergencia a atender segun la estrategia
    // actual
    public Emergencia getProximaEmergenciaAPriorizar() {
        return this.estrategiaPriorizacionActual.seleccionarSiguienteEmergencia(this.emergenciasActivas);
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

    public List<Emergencia> getEmergenciasNoAtendidas() {
        return emergenciasActivas.stream()
                .filter(e -> !e.isAtendida())
                .collect(Collectors.toList());
    }

    public List<BaseOperaciones> getBasesOperaciones() {
        return basesOperaciones;
    }

    public List<Recurso> getAllRecursos() {
        return this.basesOperaciones.stream()
                .flatMap(base -> base.getRecursosEnBase().stream())
                .collect(Collectors.toList());
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

    public static class SugerenciaRecursos {
        private final List<Recurso> recursosSugeridos;
        private final BaseOperaciones baseSugerida;

        public SugerenciaRecursos(List<Recurso> recursosSugeridos, BaseOperaciones baseSugerida) {
            this.recursosSugeridos = recursosSugeridos;
            this.baseSugerida = baseSugerida;
        }

        public List<Recurso> getRecursosSugeridos() {
            return recursosSugeridos;
        }

        public BaseOperaciones getBaseSugerida() {
            return baseSugerida;
        }
    }

    public List<Recurso> sugerirRecursosAutomaticos(Emergencia emergencia) {
        List<Recurso> sugerencias = new ArrayList<>();

        // Determinar qué tipo(s) de servicio y recursos necesita esta emergencia
        String tipoServicioRequerido = null;
        String tipoRecursoPrincipal; // Ej. Camión, Ambulancia, Patrulla
        String tipoPersonalAdicional;

        switch (emergencia.getTipo()) {
            case INCENDIO:
                tipoServicioRequerido = "BOMBEROS";
                tipoRecursoPrincipal = "Camión de Bomberos"; // Asumimos nombres exactos del JSON
                tipoPersonalAdicional = "Personal Bombero"; // Asumimos nombres exactos del JSON
                break;
            case ACCIDENTE_VEHICULAR:
                tipoServicioRequerido = "AMBULANCIA";
                tipoRecursoPrincipal = "Ambulancia"; // Asumimos nombres exactos del JSON
                tipoPersonalAdicional = "Personal Paramédico"; // Asumimos nombres exactos del JSON
                break;
            case ROBO:
                tipoServicioRequerido = "POLICIA";
                tipoRecursoPrincipal = "Patrulla Policial"; // Asumimos nombres exactos del JSON
                tipoPersonalAdicional = "Oficial de Policía";
                break;
            default:
                // Para otros tipos, puede ser más genérico o no sugerir automáticamente
                System.out.println("No hay sugerencia automática de recursos para este tipo de emergencia.");
                return sugerencias; // Lista vacía
        }

        // Encontrar la base más cercana de ese tipo de servicio (Paso 17)
        BaseOperaciones baseMasCercana = encontrarBaseMasCercana(emergencia, tipoServicioRequerido);

        if (baseMasCercana != null) {
            System.out.println("Base sugerida: " + baseMasCercana.getNombre() + " ("
                    + baseMasCercana.getTipoServicioAsociado() + ")");

            // Calcular distancia a la base sugerida para verificar combustible
            double distanciaALaBase = this.mapa.calcularDistancia(emergencia.getUbicacion(),
                    baseMasCercana.getUbicacion());

            // Obtener recursos disponibles y con suficiente combustible de esa base (Paso
            // 20)
            List<Recurso> recursosDisponiblesEnBase = baseMasCercana.getRecursosDisponibles();

            // Sugerir un recurso principal (ej. un camión, una ambulancia, una patrulla) si
            // está disponible y con combustible
            Recurso recursoPrincipalSugerido = recursosDisponiblesEnBase.stream()
                    .filter(r -> r.getTipo().equalsIgnoreCase(tipoRecursoPrincipal))
                    .filter(r -> {
                        if (r instanceof Vehiculo) {
                            Vehiculo v = (Vehiculo) r;
                            // Verificar si tiene suficiente combustible para ir de su base a la emergencia
                            return v.tieneSuficienteCombustible(distanciaALaBase);
                        }
                        return true; // Recursos no vehiculares siempre tienen suficiente "combustible"
                    })
                    .findFirst() // Sugerir solo el primero disponible de ese tipo
                    .orElse(null);

            if (recursoPrincipalSugerido != null) {
                sugerencias.add(recursoPrincipalSugerido);
                System.out.println("Recurso principal sugerido: " + recursoPrincipalSugerido);

                // Opcional: Sugerir personal adicional de la misma base si está disponible
                if (tipoPersonalAdicional != null) {

                    List<Recurso> personalSugerido = recursosDisponiblesEnBase.stream()
                            .filter(r -> r.getTipo().toLowerCase().contains("personal")
                                    || r.getTipo().equalsIgnoreCase(tipoPersonalAdicional)) // Tipos de personal/oficial
                            .limit(2) // Sugerir un máximo de 2 unidades de personal, por ejemplo
                            .collect(Collectors.toList());

                    sugerencias.addAll(personalSugerido);
                    if (!personalSugerido.isEmpty()) {
                        System.out.println("Personal sugerido adicional: " + personalSugerido.size() + " unidades.");
                    }
                }
            } else {
                System.out.println("No se encontró recurso principal disponible (" + tipoRecursoPrincipal
                        + ") en la base sugerida con suficiente combustible.");
                // Podrías buscar en otras bases si la principal no tiene
            }

        } else {
            System.out.println("No se encontró una base de operaciones relevante (" + tipoServicioRequerido
                    + ") para esta emergencia.");
        }

        return sugerencias; // Retorna la lista de recursos sugeridos (puede estar vacía)
    }

    public boolean asignarRecursosAEmergencia(Emergencia emergencia, List<Recurso> recursosAAsignar) {
        if (emergencia == null || recursosAAsignar == null || recursosAAsignar.isEmpty()) {
            System.out.println("Error: No se especificó la emergencia o los recursos a asignar.");
            return false;
        }

        boolean asignacionExitosa = true;
        System.out.println("Intentando asignar " + recursosAAsignar.size() + " recursos a Emergencia ID "
                + emergencia.getId() + "...");

        // La primera vez que se asignan recursos, marca el inicio de la atención real
        if (emergencia.getTiempoInicioAtencion() == null) {
            emergencia.setTiempoInicioAtencion(new Date());
            System.out.println("Inicio de atención registrado para Emergencia ID " + emergencia.getId());
        }

        // Verificar disponibilidad y combustible antes de asignar (re-verificación)
        // (Paso 20)
        List<Recurso> recursosRealmenteAsignados = new ArrayList<>();
        double distanciaAEmergencia = this.mapa.calcularDistancia(
                // Asumimos que los recursos inician desde su base de operaciones
                recursosAAsignar.get(0) instanceof Vehiculo ? ((Vehiculo) recursosAAsignar.get(0)).getUbicacionBase()
                        : null, // Origen: base del primer vehículo (simplificación)
                emergencia.getUbicacion() // Destino: ubicación de la emergencia
        );
        if (distanciaAEmergencia <= 0 && recursosAAsignar.get(0) instanceof Vehiculo) {
            // Si la distancia es 0 o negativa y es un vehículo, algo anda mal con el
            // cálculo o la ubicación
            System.err.println("Advertencia: Distancia calculada para la asignación no válida (" + distanciaAEmergencia
                    + " km). No se simulará gasto de combustible para este viaje.");
            distanciaAEmergencia = 0; // Neutralizar gasto de combustible si la distancia no es válida
        } else if (!(recursosAAsignar.get(0) instanceof Vehiculo)) {
            distanciaAEmergencia = 0; // No hay gasto de combustible para recursos no vehiculares
        }

        for (Recurso recurso : recursosAAsignar) {
            // En una implementación real, buscarías el recurso en la lista global de
            // recursos
            // Para simplificar, asumimos que los recursos en la lista recursosAAsignar ya
            // son referencias válidas
            // y verificamos su estado y combustible en el momento de la asignación
            if (recurso.getEstado() == EstadoRecurso.DISPONIBLE) {
                boolean puedeAsignarse = true;
                if (recurso instanceof Vehiculo) {
                    Vehiculo vehiculo = (Vehiculo) recurso;
                    // Verificar combustible para el viaje de ida a la emergencia
                    if (!vehiculo.tieneSuficienteCombustible(distanciaAEmergencia)) {
                        System.out.println("Recurso no asignado: " + vehiculo
                                + " - Combustible insuficiente para el viaje estimado ("
                                + String.format("%.2f", distanciaAEmergencia) + " km).");
                        puedeAsignarse = false;
                        asignacionExitosa = false; // Si al menos uno falla, la asignación general no es 100% exitosa
                    } else {
                        // Simular el viaje y gasto de combustible (Paso 21)
                        vehiculo.moverA(emergencia.getUbicacion(), distanciaAEmergencia);
                        System.out.println("Simulando viaje de " + vehiculo.getTipo() + " (ID: " + vehiculo.getId()
                                + ") a emergencia ID " + emergencia.getId() + " ("
                                + String.format("%.2f", distanciaAEmergencia) + " km).");
                    }
                }

                if (puedeAsignarse) {
                    recurso.asignarEmergencia(emergencia); // Marcar como ocupado y asignar la emergencia
                    recursosRealmenteAsignados.add(recurso);
                    System.out.println("Recurso asignado: " + recurso);
                }

            } else {
                System.out.println("Recurso no asignado: " + recurso + " - No está disponible.");
                asignacionExitosa = false; // Si al menos uno falla, la asignación general no es 100% exitosa
            }
        }

        if (recursosRealmenteAsignados.isEmpty()) {
            System.out.println("No se pudo asignar ningún recurso válido a Emergencia ID " + emergencia.getId());
            return false; // Ningún recurso fue asignado
        } else {
            // Opcional: Asociar los recursos asignados a la emergencia de alguna manera en
            // la clase Emergencia
            // emergencia.addRecursosAsignados(recursosRealmenteAsignados); // Necesitarías
            // un método addRecursosAsignados en Emergencia
            System.out.println("Asignación de recursos completada para Emergencia ID " + emergencia.getId()
                    + ". Recursos asignados: " + recursosRealmenteAsignados.size());
            return asignacionExitosa; // Retorna si TODOS los recursos solicitados fueron asignados exitosamente
        }
    }

    // METODO AUXILIAR PARA ENCONTRAR UN RECURSO POR ID, UTIL PARA ASIGNACION MANUAL
    public Recurso getRecursoById(int idRecurso) {
        return this.basesOperaciones.stream()
                .flatMap(base -> base.getRecursosEnBase().stream())
                .filter(r -> r.getId() == idRecurso)
                .findFirst()
                .orElse(null);
    }

    public boolean iniciarRepostajeRecurso(int idRecurso) {
        Recurso recurso = getRecursoById(idRecurso);
        if (recurso instanceof Vehiculo) {
            Vehiculo vehiculo = (Vehiculo) recurso;
            if (vehiculo.getEstado() == EstadoRecurso.DISPONIBLE) {
                vehiculo.iniciarRepostaje();
                return true;
            } else {
                System.out.println("No se puede iniciar repostaje para " + vehiculo.getTipo() + " (ID: "
                        + vehiculo.getId() + "). Estado actual: " + vehiculo.getEstado());
                return false;
            }
        } else {
            System.out.println("El Recurso con ID " + idRecurso + " no es un vehículo y no requiere repostaje.");
            return false;
        }
    }

    public boolean completarRepostajeRecurso(int idRecurso) {
        Recurso recurso = getRecursoById(idRecurso);
        if (recurso instanceof Vehiculo) {
            Vehiculo vehiculo = (Vehiculo) recurso;
            if (vehiculo.getEstado() == EstadoRecurso.REPOSTANDO) {
                vehiculo.completarRepostaje();
                return true;
            } else {
                System.out.println("No se puede completar repostaje para " + vehiculo.getTipo() + " (ID: "
                        + vehiculo.getId() + "). Estado actual: " + vehiculo.getEstado() + ". No estaba repostando.");
                return false;
            }
        } else {
            System.out.println("El Recurso con ID " + idRecurso + " no es un vehículo.");
            return false;
        }
    }

    private class MapaUrbano {
        // Metodo para calcular la distancia geografica (usando haversine para coordenadas)
        // Retorna la distancia en kilometros
        public double calcularDistancia(Ubicacion u1, Ubicacion u2) {
            if (u1 == null || u2 == null) {
                System.err.println("Error: Una o ambas ubicaciones son nulas. No se puede calcular la distancia.");
                return 0.0; // O lanzar una excepción si es más apropiado
            }

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
            .filter(e -> e.calcularTiempoRealRespuestaMillis() != -1) // Usar el método correcto de Emergencia
            .collect(Collectors.summarizingInt(e -> (int) e.calcularTiempoRealRespuestaMillis()));

        return stats.getAverage(); // Retorna el promedio en milisegundos
    }

    // Tiempo promedio total de atención (desde inicio hasta fin)
    public double getTiempoPromedioTotalAtencionMillis() {
         IntSummaryStatistics stats = emergenciasActivas.stream()
            .filter(e -> e.calcularTiempoTotalAtencionMillis() != -1) // Filtrar las que tienen tiempo de atención calculado
            .collect(Collectors.summarizingInt(e -> (int) e.calcularTiempoTotalAtencionMillis()));

        return stats.getAverage(); // Retorna el promedio en milisegundos
    }

     // Porcentaje de emergencias resueltas
     public double getPorcentajeEmergenciasResueltas() {
         int total = getTotalEmergenciasRegistradas();
         if (total == 0) return 0.0;
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
             .summaryStatistics(); // Cambiar collect por summaryStatistics

         return stats.getAverage(); // Retorna el promedio del nivel de combustible
     }

    public double calcularDistancia(Ubicacion u1, Ubicacion u2) {
        return this.mapa.calcularDistancia(u1, u2);
    }
}
