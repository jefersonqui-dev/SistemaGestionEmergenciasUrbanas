package com.devsenior.jquiguantar.SGEU.controller;

import com.devsenior.jquiguantar.SGEU.model.patterns.singleton.SistemaEmergencias;
import com.devsenior.jquiguantar.SGEU.view.ConsolaView;
import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergencia;
import com.devsenior.jquiguantar.SGEU.model.emergencies.NivelGravedad;
import com.devsenior.jquiguantar.SGEU.model.emergencies.TipoEmergencia;
import com.devsenior.jquiguantar.SGEU.model.resources.Recurso;
import com.devsenior.jquiguantar.SGEU.model.resources.Vehiculo;
import com.devsenior.jquiguantar.SGEU.model.resources.EstadoRecurso;
import com.devsenior.jquiguantar.SGEU.model.services.BaseOperaciones;
import com.devsenior.jquiguantar.SGEU.model.util.Ubicacion;
// Removida la importación de SugerenciaRecursos si no se usa directamente aquí
// import com.devsenior.jquiguantar.SGEU.model.patterns.singleton.SistemaEmergencias.SugerenciaRecursos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit; // Para la simulación de tiempo

public class MainApp {

    private static SistemaEmergencias sistema;
    private static ConsolaView view;
    private static final long INTERVALO_SIMULACION_MILLIS = 2000; // Simular avance cada 2 segundos
    private static final String CONFIG_FILE = "bases.json"; // Nombre del archivo de configuración


    public static void main(String[] args) {
        sistema = SistemaEmergencias.getInstance();
        view = new ConsolaView();

        view.mostrarMensaje("Sistema de Gestión de Emergencias Urbanas iniciado.");

        // Paso 1: Inicializar el sistema desde el archivo JSON (Ahora en el Controlador)
        view.mostrarMensaje("Cargando configuración del sistema desde " + CONFIG_FILE + "...");
        boolean inicializacionExitosa = sistema.inicializarSistemaDesdeJson(CONFIG_FILE);

        if (inicializacionExitosa) {
            view.mostrarMensaje("Configuración cargada exitosamente.");
            // Paso 2: Añadir las bases como observadores (Ahora en el Controlador)
            sistema.addBasesAsObservers();
            view.mostrarMensaje("Todas las bases de Operaciones registradas como observadores del sistema.");

            // Establecer estrategia de priorización inicial (ejemplo: Prioridad Alta)
            // Ahora el mensaje de confirmación se imprime aquí en el Controlador
            sistema.setEstrategiaPriorizacionActual(new com.devsenior.jquiguantar.SGEU.model.patterns.strategy.PrioridadAltaStrategy());
             view.mostrarMensaje("Estrategia de priorización inicial establecida: Prioridad Alta.");


        } else {
            view.mostrarMensaje("Error al cargar la configuración del sistema. No se puede continuar.");
            view.cerrarScanner();
            return; // Salir si la inicialización falla
        }


        int opcionPrincipal;
        do {
            // Simular avance del tiempo y progreso de emergencias activas antes de mostrar el menú (Paso 23)
            // La liberación de recursos al completar una emergencia también se gestiona aquí, llamando al Modelo
            simularAvanceTiempoYProgreso();

            // Mostrar el menú principal
            view.mostrarMenuPrincipal();

            // Solicitar y procesar la opción del usuario
            opcionPrincipal = view.solicitarOpcion();

            switch (opcionPrincipal) {
                case 1:
                    registrarNuevaEmergencia();
                    break;
                case 2:
                    verEstadoEmergencias();
                    break;
                case 3:
                    verEstadoRecursos();
                    break;
                case 4:
                    // La opción 4 ahora solo entra al sub-menú de gestión
                    gestionarEmergenciasActivas();
                    break;
                case 5:
                    mostrarEstadisticas(); // Fase 7
                    break;
                case 6:
                    finalizarJornada(); // Fase 7
                    break;
                default:
                    mostrarMensajeErrorOpcion();
            }

            // Pausa simulada para permitir la lectura de la salida y la simulación de tiempo
            try {
                Thread.sleep(INTERVALO_SIMULACION_MILLIS / 2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Llamar a esperarEnter después de cada acción del menú principal (excepto salir)
             if (opcionPrincipal != 6) {
                view.esperarEnter();
             }

        } while (opcionPrincipal != 6);

        view.cerrarScanner();
        view.mostrarMensaje("Sistema finalizado. ¡Gracias por usar el sistema!");
    }

    // --- Métodos para manejar las opciones del menú principal ---

    private static void registrarNuevaEmergencia() {
        view.mostrarMensaje("\n--- Registrar Nueva Emergencia ---");

        TipoEmergencia tipo = view.solicitarTipoEmergencia();
        Ubicacion ubicacion = view.solicitarUbicacion();
        NivelGravedad gravedad = view.solicitarNivelGravedad();
        // El tiempo estimado se guarda en milisegundos en el Modelo
        long tiempoEstimadoMillis = view.solicitarTiempoRespuestaEstimado("minutos") * 60 * 1000;

        Emergencia nuevaEmergencia = com.devsenior.jquiguantar.SGEU.model.patterns.factory.EmergenciaFactory.crearEmergencia(tipo, ubicacion, gravedad, tiempoEstimadoMillis);

        sistema.registrarEmergencia(nuevaEmergencia);

        // Notificar a los observadores (bases) DESPUÉS de registrar la emergencia
        view.mostrarMensaje("\nNotificando a las bases de operaciones sobre la nueva emergencia...");
        sistema.notifyObservers(nuevaEmergencia);

        // Obtener y mostrar las notificaciones pendientes
        List<BaseOperaciones.NotificacionEmergencia> notificaciones = sistema.getNotificacionesPendientes();
        if (!notificaciones.isEmpty()) {
            view.mostrarNotificacionesEmergencia(notificaciones);
        }

        // Mensaje de confirmación amigable
        //view.mostrarMensaje("\n--------------------------------------------------------");
        view.mostrarMensaje(String.format("¡Emergencia registrada exitosamente!\nID: %d\nTipo: %s\nUbicación: (Lat: %.2f, Long: %.2f)\nGravedad: %s\n",
                nuevaEmergencia.getId(), nuevaEmergencia.getTipo(), ubicacion.getLatitud(), ubicacion.getLongitud(), gravedad));
    }

    private static void verEstadoEmergencias() {
         // Ahora getAllEmergencias() retorna todas las registradas en el Modelo modificado
         // Nota: Tu método mostrarEmergencias en ConsolaView ya separa activas y resueltas.
         List<Emergencia> emergencias = sistema.getAllEmergencias();
         view.mostrarEmergencias(emergencias);
    }

    private static void verEstadoRecursos() {
        List<Recurso> recursos = sistema.getAllRecursos();
        view.mostrarRecursos(recursos);
    }

    // --- Método para gestionar emergencias activas (flujo integrado) ---
    private static void gestionarEmergenciasActivas() {
        int opcionGestion;
        do {
            // Mostrar emergencias activas no atendidas al entrar al sub-menú
            List<Emergencia> emergenciasNoAtendidas = sistema.getEmergenciasNoAtendidas();
            if (!emergenciasNoAtendidas.isEmpty()) {
                view.mostrarMensaje("\n--- Emergencias Pendientes de Asignación/Atención ---");
                for (Emergencia emergencia : emergenciasNoAtendidas) {
                    view.mostrarMensaje(String.format("ID: %d - Tipo: %s - Gravedad: %s - Progreso: %.1f%%",
                            emergencia.getId(), emergencia.getTipo(), emergencia.getNivelGravedad(), emergencia.getProgresoAtencion()));
                }
                view.mostrarMensaje("---------------------------------------------------");
            } else {
                 view.mostrarMensaje("\nNo hay emergencias pendientes de asignación/atención en este momento.");
            }

            // Mostrar el sub-menú de gestión
            view.mostrarMenuGestionEmergenciasActivas();
            opcionGestion = view.solicitarOpcion();

            switch (opcionGestion) {
                case 1:
                    iniciarFlujoAtencionEmergencia();
                    break;
                case 2:
                    iniciarRepostajeFlujo();
                    break;
                case 3:
                    completarRepostajeFlujo();
                    break;
                case 4:
                    view.mostrarMensaje("Volviendo al menú principal...");
                    break;
                default:
                    mostrarMensajeErrorOpcion();
            }

            // Simular avance del tiempo después de una acción en este sub-menú
            if (opcionGestion != 4) { // No simular avance si solo está volviendo
                 simularAvanceTiempoYProgreso();
                 view.esperarEnter(); // Pausa después de la acción y simulación en el sub-menú
            }


        } while (opcionGestion != 4);
    }

    // Nuevo método para gestionar el flujo de atención de emergencia (automático o manual)
    private static void iniciarFlujoAtencionEmergencia() {
        view.mostrarMensaje("\n--- Selección de Modo de Atención ---");
        String modoAtencion = view.solicitarModoAtencionEmergencia();

        if (modoAtencion.equals("a")) {
            atenderProximaEmergenciaAutomaticamente();
        } else if (modoAtencion.equals("m")) {
            atenderEmergenciaPorIdFlujo();
        } else {
            view.mostrarMensaje("Opción de modo de atención no válida. Volviendo al menú de gestión.");
        }
    }

    // --- Flujo de asignación automática y manual (llamado desde gestionarEmergenciasActivas) ---
    private static void atenderProximaEmergenciaAutomaticamente() {
        view.mostrarMensaje("\n================= Atender Próxima Emergencia Automáticamente =================");
        Emergencia emergenciaPrioritaria = sistema.getProximaEmergenciaAPriorizar();

        if (emergenciaPrioritaria == null) {
            view.mostrarMensaje("No hay emergencias pendientes de atención según la estrategia de priorización actual.");
            return;
        }
        
        if (emergenciaPrioritaria.isAtendida()) {
             view.mostrarMensaje("La emergencia prioritaria (ID: " + emergenciaPrioritaria.getId() + ") ya ha sido atendida o resuelta.");
             // Opcionalmente, buscar la siguiente no atendida si este fuera el caso.
             // Por ahora, simplemente informamos.
             return;
        }

        view.mostrarMensaje("Emergencia seleccionada automáticamente (según prioridad):");
        // Mostrar detalles de la emergencia seleccionada automáticamente
        // (similar a como se hace en atenderProximaEmergenciaFlujo)
        // Esto es importante para que el usuario sepa qué emergencia se está atendiendo.
         procesarAsignacionRecursosParaEmergencia(emergenciaPrioritaria);
    }

    // Renombrado y ajustado para atender por ID
    private static void atenderEmergenciaPorIdFlujo() {
        view.mostrarMensaje("\n================= Atender Emergencia por ID =================");

        // Obtener lista de emergencias no atendidas
        List<Emergencia> emergenciasNoAtendidas = sistema.getEmergenciasNoAtendidas();
        
        if (emergenciasNoAtendidas.isEmpty()) {
            view.mostrarMensaje("No hay emergencias pendientes de atención.");
            return;
        }

        // Mostrar lista de emergencias disponibles
        view.mostrarMensaje("\nEmergencias disponibles para atención:");
        for (Emergencia emergencia : emergenciasNoAtendidas) {
            view.mostrarMensaje(String.format("ID: %d - Tipo: %s - Gravedad: %s - Progreso: %.1f%%",
                emergencia.getId(), emergencia.getTipo(), emergencia.getNivelGravedad(), emergencia.getProgresoAtencion()));
        }

        // Solicitar ID de la emergencia a atender
        int idEmergencia = view.solicitarNumeroEntero("\nIngrese el ID de la emergencia a atender:");
        Emergencia emergenciaSeleccionada = sistema.getEmergenciaById(idEmergencia);

        if (emergenciaSeleccionada == null || emergenciaSeleccionada.isAtendida()) {
            view.mostrarMensaje("ID de emergencia no válido o la emergencia ya ha sido resuelta/atendida.");
            return;
        }

        // Llamar al método común para procesar la asignación de recursos
        procesarAsignacionRecursosParaEmergencia(emergenciaSeleccionada);
    }

    // Nuevo método refactorizado para procesar la asignación de recursos una vez seleccionada la emergencia
    private static void procesarAsignacionRecursosParaEmergencia(Emergencia emergenciaSeleccionada) {
        if (emergenciaSeleccionada == null) { // Doble check por si acaso
            view.mostrarMensaje("Error: No se ha seleccionado una emergencia válida.");
            return;
        }

        view.mostrarMensaje(String.format("\nProcesando Emergencia Seleccionada:\n  ID: %d\n  Tipo: %s\n  Ubicación: Latitud %.2f, Longitud %.2f\n  Gravedad: %s\n  Progreso: %.2f%%\n  Estado: %s\n  Tiempo Estimado: %d ms\n",
                emergenciaSeleccionada.getId(),
                emergenciaSeleccionada.getTipo(),
                emergenciaSeleccionada.getUbicacion().getLatitud(),
                emergenciaSeleccionada.getUbicacion().getLongitud(),
                emergenciaSeleccionada.getNivelGravedad(),
                emergenciaSeleccionada.getProgresoAtencion(),
                emergenciaSeleccionada.isAtendida() ? "Resuelta" : "Pendiente",
                emergenciaSeleccionada.getTiempoRespuestaEstimado()));
        view.mostrarMensaje("=============================================================");

        // Sugerir recursos automáticamente
        List<Recurso> recursosSugeridos = sistema.sugerirRecursosAutomaticos(emergenciaSeleccionada);

        if (!recursosSugeridos.isEmpty()) {
            view.mostrarMensajeSugerenciaRecursos(recursosSugeridos, null); // Asumimos que no hay base sugerida por ahora

            String confirmacion = view.solicitarConfirmacion("¿Desea asignar estos recursos sugeridos a Emergencia ID " + emergenciaSeleccionada.getId() + "? (s/n)");

            if (confirmacion.equals("s")) {
                SistemaEmergencias.AssignmentResult result = sistema.asignarRecursosAEmergencia(emergenciaSeleccionada, recursosSugeridos);
                for (String msg : result.getMessages()) {
                    view.mostrarMensaje(msg);
                }

                if (result.isOverallSuccess()) {
                    simularProgresoEmergencia(emergenciaSeleccionada);
                } else {
                    view.mostrarMensaje("La asignación automática de recursos no fue exitosa. Puede intentar la asignación manual si lo desea.");
                }
            } else {
                view.mostrarMensaje("Sugerencia automática declinada. Procediendo a asignación manual.");
                asignarRecursosManualmenteAEmergencia(emergenciaSeleccionada);
            }
        } else {
            view.mostrarMensaje("No se encontraron recursos disponibles para sugerir automáticamente para esta emergencia.");
            String intentarManual = view.solicitarConfirmacion("¿Desea intentar asignar recursos manualmente a Emergencia ID " + emergenciaSeleccionada.getId() + "? (s/n)");
            if (intentarManual.equals("s")) {
                asignarRecursosManualmenteAEmergencia(emergenciaSeleccionada);
            }
        }
    }

    // --- Flujos para iniciar y completar repostaje (llamados desde gestionarEmergenciasActivas) ---

    private static void iniciarRepostajeFlujo() {
        view.mostrarMensaje("\n--- Iniciar Repostaje de Vehículo ---");
        int idRecurso = view.solicitarIdRecurso(); // Usamos idRecurso para mayor claridad

        // Llamar al método del sistema para iniciar el repostaje y capturar el mensaje
        String mensajeResultado = sistema.iniciarRepostajeRecurso(idRecurso);

        // Mostrar el mensaje retornado por el Modelo
        view.mostrarMensaje(mensajeResultado);
    }

    private static void completarRepostajeFlujo() {
        view.mostrarMensaje("\n--- Completar Repostaje de Vehículo ---");
        int idRecurso = view.solicitarIdRecurso(); // Usamos idRecurso para mayor claridad

        // Llamar al método del sistema para completar el repostaje y capturar el mensaje
        String mensajeResultado = sistema.completarRepostajeRecurso(idRecurso);

        // Mostrar el mensaje retornado por el Modelo
        view.mostrarMensaje(mensajeResultado);
    }


    private static void mostrarEstadisticas() {
        view.mostrarMensaje("\n--- Estadísticas del Día ---");
        // Implementación de estadísticas usando métodos del SistemaEmergencias
        view.mostrarMensaje("Total de emergencias registradas: " + sistema.getTotalEmergenciasRegistradas());
        view.mostrarMensaje("Total de emergencias resueltas: " + sistema.getTotalEmergenciasResueltas());
        view.mostrarMensaje(String.format("Porcentaje de emergencias resueltas: %.2f%%", sistema.getPorcentajeEmergenciasResueltas()));

        double tiempoPromedioRespuesta = sistema.getTiempoPromedioRealRespuestaMillis();
         if (tiempoPromedioRespuesta > 0) { // Verificar si el promedio es mayor a 0 para considerar que hay datos válidos
             view.mostrarMensaje(String.format("Tiempo promedio real de respuesta: %.2f ms", tiempoPromedioRespuesta));
         } else {
             view.mostrarMensaje("Tiempo promedio real de respuesta: No hay datos suficientes (ninguna emergencia iniciada).");
         }

        double tiempoPromedioAtencion = sistema.getTiempoPromedioTotalAtencionMillis();
         if (tiempoPromedioAtencion > 0) { // Verificar si el promedio es mayor a 0 para considerar que hay datos válidos
              view.mostrarMensaje(String.format("Tiempo promedio total de atención: %.2f ms", tiempoPromedioAtencion));
         } else {
              view.mostrarMensaje("Tiempo promedio total de atención: No hay datos suficientes (ninguna emergencia completada).");
         }


        view.mostrarMensaje("Recursos Disponibles: " + sistema.getCantidadRecursosPorEstado(EstadoRecurso.DISPONIBLE));
        view.mostrarMensaje("Recursos Ocupados: " + sistema.getCantidadRecursosPorEstado(EstadoRecurso.OCUPADO));
        view.mostrarMensaje("Recursos Repostando: " + sistema.getCantidadRecursosPorEstado(EstadoRecurso.REPOSTANDO));
        view.mostrarMensaje("Recursos Fuera de Servicio: " + sistema.getCantidadRecursosPorEstado(EstadoRecurso.FUERA_DE_SERVICIO));

        double nivelPromedioCombustible = sistema.getNivelPromedioCombustibleVehiculos();
         if (!Double.isNaN(nivelPromedioCombustible)) { // Usar Double.isNaN para verificar si no hay vehículos
             view.mostrarMensaje(String.format("Nivel promedio de combustible de vehículos: %.2f%%", nivelPromedioCombustible));
         } else {
             view.mostrarMensaje("Nivel promedio de combustible de vehículos: No hay vehículos registrados.");
         }
    }

    private static void finalizarJornada() {
        view.mostrarMensaje("\n--- Finalizando Jornada ---");
        // Lógica de finalización (Fase 7 - Paso 29)
        // Mostrar estadísticas finales y estado de recursos
        mostrarEstadisticas(); // Reutilizamos la función de estadísticas para el resumen final

        view.mostrarMensaje("\nEstado final de los recursos:");
        verEstadoRecursos(); // Reutilizamos la función para mostrar el estado de todos los recursos

        // Simular guardado del estado (podría ser en un archivo, base de datos, etc.)
        view.mostrarMensaje("\nSimulando guardado del estado final del sistema...");
        // Aquí iría la lógica real de guardado si se implementara.

        view.mostrarMensaje("Jornada finalizada. El sistema se cerrará.");

        // La salida del programa ocurre en el bucle do-while principal cuando opcionPrincipal es 6
    }

    // Método para simular el avance del tiempo y el progreso de las emergencias (Paso 23)
    // Llamado en cada iteración del bucle principal del menú y del sub-menú de gestión.
    private static void simularAvanceTiempoYProgreso() {
        double avancePorCiclo = 5.0; // Porcentaje de avance por cada vez que se llama a este método

        List<Emergencia> emergenciasNoAtendidas = sistema.getEmergenciasNoAtendidas();
        if (!emergenciasNoAtendidas.isEmpty()) {
            // Eliminada la impresión opcional de log del Controlador
            for (Emergencia emergencia : emergenciasNoAtendidas) {
                // Solo avanza si se ha iniciado la atención y no está resuelta
                if (emergencia.getTiempoInicioAtencion() != null && !emergencia.isAtendida()) {
                    emergencia.simularAvanceProgreso(avancePorCiclo);
                    // Si la emergencia se completa en esta simulación
                    // Verificar también que el tiempo de fin se haya establecido (se establece en simularAvanceProgreso si llega a 100%)
                    if (emergencia.isAtendida()) {
                        view.mostrarMensaje("Emergencia ID " + emergencia.getId() + " completada y marcada como Resuelta.");
                        // Lógica para liberar recursos asociados a esta emergencia (llamando al Modelo)
                        // Capturar el resultado de la liberación y mostrar los mensajes
                        SistemaEmergencias.LiberationResult libResult = sistema.liberarRecursosDeEmergencia(emergencia);
                        for (String msg : libResult.getMessages()) {
                            view.mostrarMensaje(msg);
                        }
                    }
                }
            }
        }
    }


    // Consolidar mensajes de error
    private static void mostrarMensajeErrorOpcion() {
        view.mostrarMensaje("Opción no válida. Por favor, intente de nuevo.");
    }

    // Método auxiliar para simular el progreso de una emergencia después de asignar recursos
    // Este método podría ser más sofisticado para simular el tiempo que tarda en completarse
    private static void simularProgresoEmergencia(Emergencia emergencia) {
        // En una implementación real, esta simulación podría basarse en el tiempo real
        // transcurrido o un cálculo más complejo. Aquí, simplemente avanzamos el progreso
        // un poco para mostrar algún cambio.
        // La simulación principal en simularAvanceTiempoYProgreso continuará avanzando el progreso.
        view.mostrarMensaje("Iniciando simulación de progreso para Emergencia ID " + emergencia.getId() + "...");
         // Si deseas simular un avance inicial inmediato al asignar, podrías llamarlo aquí:
         // emergencia.simularAvanceProgreso(10.0); // Ejemplo: un 10% de avance inicial al asignar
         // view.mostrarMensaje(String.format("Avance inicial simulado. Progreso actual: %.1f%%", emergencia.getProgresoAtencion()));
    }

    // Nuevo método auxiliar para asignar recursos manualmente a una emergencia específica
    private static void asignarRecursosManualmenteAEmergencia(Emergencia emergencia) {
        view.mostrarMensaje("\n--- Asignación Manual para Emergencia ID " + emergencia.getId() + " ---");

        List<Recurso> recursosAAsignar = new ArrayList<>();
        String continuarAsignando;

        do {
            view.mostrarMensaje("\n--- Seleccionar Recurso ---");

            // Mostrar recursos DISPONIBLES (considerando estado y combustible) por tipo
            // IDEALMENTE: Implementar un método en SistemaEmergencias que filtre recursos disponibles
            // relevantes para el tipo de emergencia, o por base cercana, etc.
            // Por ahora, mostramos todos los disponibles, pero la lógica de selección del usuario es clave.
            // view.mostrarMensaje("Recursos Disponibles:"); // Este mensaje ya está en mostrarRecursosDisponiblesParaAsignacionManual
            List<Recurso> todosRecursosDisponibles = sistema.getAllRecursosDisponibles(); // Obtener todos los disponibles
            if (todosRecursosDisponibles.isEmpty()) {
                view.mostrarMensaje("No hay recursos disponibles para asignar.");
                break; // Salir del bucle de selección manual si no hay recursos disponibles
            }

            // Mostrar solo los recursos disponibles que no han sido ya seleccionados para esta asignación manual
             List<Recurso> recursosDisponiblesNoSeleccionados = new ArrayList<>(todosRecursosDisponibles);
             recursosDisponiblesNoSeleccionados.removeAll(recursosAAsignar);

             if(recursosDisponiblesNoSeleccionados.isEmpty() && !recursosAAsignar.isEmpty()){
                 view.mostrarMensaje("Todos los recursos disponibles han sido seleccionados para esta asignación manual.");
                 break; // Salir del bucle si todos los disponibles ya fueron seleccionados
             } else if (recursosDisponiblesNoSeleccionados.isEmpty() && recursosAAsignar.isEmpty()){
                  view.mostrarMensaje("No hay recursos disponibles para asignar.");
                  break; // Salir si no hay ningún recurso disponible
             }


            view.mostrarRecursosDisponiblesParaAsignacionManual(recursosDisponiblesNoSeleccionados); // Mostrar solo los no seleccionados


            int idRecursoSeleccionado = view.solicitarNumeroEntero("Ingrese el ID del recurso a asignar (o 0 para finalizar selección):");

            if (idRecursoSeleccionado == 0) {
                break; // El usuario quiere finalizar la selección manual
            }

            Recurso recursoParaAsignar = sistema.getRecursoById(idRecursoSeleccionado);

            if (recursoParaAsignar != null) {
                 // Verifica si el recurso ya fue seleccionado para evitar duplicados en la lista temporal
                 if (!recursosAAsignar.contains(recursoParaAsignar)) {
                    // Verifica si el recurso está disponible antes de añadirlo a la lista para intentar asignar
                    if (recursoParaAsignar.getEstado() == EstadoRecurso.DISPONIBLE) {
                         recursosAAsignar.add(recursoParaAsignar);
                         view.mostrarMensaje("Recurso " + recursoParaAsignar.getTipo() + " (ID: " + recursoParaAsignar.getId() + ") añadido a la lista de asignación manual.");
                    } else {
                         view.mostrarMensaje("Recurso con ID " + idRecursoSeleccionado + " no está DISPONIBLE. Estado actual: " + recursoParaAsignar.getEstado());
                    }
                 } else {
                     view.mostrarMensaje("Este recurso ya ha sido seleccionado para asignación manual.");
                 }
            } else {
                view.mostrarMensaje("Recurso con ID " + idRecursoSeleccionado + " no encontrado.");
            }

            // Continuar preguntando si desea seleccionar otro recurso si hay recursos disponibles que no se han seleccionado
             List<Recurso> recursosDisponiblesRestantes = new ArrayList<>(todosRecursosDisponibles);
             recursosDisponiblesRestantes.removeAll(recursosAAsignar);

             if (!recursosDisponiblesRestantes.isEmpty()) {
                continuarAsignando = view.solicitarConfirmacion("¿Desea seleccionar otro recurso para asignar a Emergencia ID " + emergencia.getId() + "?");
             } else {
                 continuarAsignando = "n"; // Ya no hay más recursos disponibles para seleccionar
                 // view.mostrarMensaje("No hay más recursos disponibles para añadir a la lista de asignación manual."); // Mensaje ya manejado arriba
             }


        } while (continuarAsignando.equals("s"));

        // Intentar asignar los recursos seleccionados manualmente (llama al Modelo)
        if (!recursosAAsignar.isEmpty()) {
            view.mostrarMensaje("\nIntentando asignar recursos seleccionados manualmente...");
            // Llamar al Modelo para asignar y capturar el resultado detallado (mensajes y éxito general)
            SistemaEmergencias.AssignmentResult result = sistema.asignarRecursosAEmergencia(emergencia, recursosAAsignar);

            // Mostrar los mensajes de resultado de la asignación usando la Vista
            for (String msg : result.getMessages()) {
                view.mostrarMensaje(msg);
            }

            // Si al menos un recurso fue asignado exitosamente, simular progreso
            if (result.isOverallSuccess()) {
                simularProgresoEmergencia(emergencia); // Nota: La simulación principal sigue en el bucle principal
            } else {
                 view.mostrarMensaje("La asignación manual de recursos no fue exitosa.");
            }

        } else {
            view.mostrarMensaje("No se seleccionaron recursos para asignación manual.");
        }
    }

}