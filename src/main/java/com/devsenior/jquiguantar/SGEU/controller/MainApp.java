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
import com.devsenior.jquiguantar.SGEU.model.patterns.singleton.SistemaEmergencias.SugerenciaRecursos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit; // Para la simulación de tiempo

public class MainApp {

    private static SistemaEmergencias sistema;
    private static ConsolaView view;
    private static final long INTERVALO_SIMULACION_MILLIS = 2000; // Simular avance cada 2 segundos


    public static void main(String[] args) {
        sistema = SistemaEmergencias.getInstance();
        view = new ConsolaView();

        view.mostrarMensaje("Sistema de Gestión de Emergencias Urbanas iniciado.");

        int opcionPrincipal;
        do {
            // Simular avance del tiempo y progreso de emergencias activas antes de mostrar el menú (Paso 23)
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
                    verEstadoEmergencias(); // Mostrará activas y resueltas
                    break;
                case 3:
                    verEstadoRecursos();
                    break;
                case 4:
                    gestionarEmergenciasActivas(); // Iniciar el flujo integrado
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
                Thread.sleep(INTERVALO_SIMULACION_MILLIS / 2); // Pausa corta
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Llamar a esperarEnter después de cada acción del menú principal
            view.esperarEnter();

        } while (opcionPrincipal != 6); // La opción de salida ahora es 6

        view.cerrarScanner();
        view.mostrarMensaje("Sistema finalizado. ¡Gracias por usar el sistema!");
    }

    // --- Métodos para manejar las opciones del menú principal ---

    private static void registrarNuevaEmergencia() {
        view.mostrarMensaje("\n--- Registrar Nueva Emergencia ---");

        TipoEmergencia tipo = view.solicitarTipoEmergencia();
        Ubicacion ubicacion = view.solicitarUbicacion();
        NivelGravedad gravedad = view.solicitarNivelGravedad();
        long tiempoEstimado = view.solicitarTiempoRespuestaEstimado("minutos") * 60 * 1000; // Convertir minutos a milisegundos

        // Usar la Fábrica para crear la instancia correcta de Emergencia (Paso 13)
        Emergencia nuevaEmergencia = com.devsenior.jquiguantar.SGEU.model.patterns.factory.EmergenciaFactory.crearEmergencia(tipo, ubicacion, gravedad, tiempoEstimado);

        // Registrar la emergencia en el sistema (llama a SistemaEmergencias, que notifica observadores - Paso 14 y 15)
        sistema.registrarEmergencia(nuevaEmergencia);

        // Mensaje de confirmación amigable
        view.mostrarMensaje("--------------------------------------------------------");
        view.mostrarMensaje(String.format("¡Emergencia registrada exitosamente!\nTipo: %s\nUbicación: (Lat: %.2f, Long: %.2f)\nGravedad: %s\n",
                tipo, ubicacion.getLatitud(), ubicacion.getLongitud(), gravedad));
    }

    private static void verEstadoEmergencias() { // Mostrará todas, activas y resueltas
        List<Emergencia> emergencias = sistema.getEmergenciasActivas(); // getEmergenciasActivas ahora retorna todas registradas
        view.mostrarEmergencias(emergencias);
    }

    private static void verEstadoRecursos() {
        List<Recurso> recursos = sistema.getAllRecursos();
        view.mostrarRecursos(recursos);
    }

    // --- Nuevo método para gestionar emergencias activas (flujo integrado) ---
    private static void gestionarEmergenciasActivas() {
        int opcionGestion;
        do {
            // Mostrar emergencias activas no atendidas en este sub-menú
             List<Emergencia> emergenciasNoAtendidas = sistema.getEmergenciasNoAtendidas();
            if (!emergenciasNoAtendidas.isEmpty()) {
                 view.mostrarMensaje("\n--- Emergencias Pendientes de Asignación/Atención ---");
                  for (Emergencia emergencia : emergenciasNoAtendidas) {
                      view.mostrarMensaje("ID: " + emergencia.getId() + " - Tipo: " + emergencia.getTipo() + " - Gravedad: " + emergencia.getNivelGravedad() + " - Progreso: " + String.format("%.1f", emergencia.getProgresoAtencion()) + "%");
                  }
                 view.mostrarMensaje("---------------------------------------------------");
            } else {
                 view.mostrarMensaje("\nNo hay emergencias pendientes de asignación/atención.");
            }


            view.mostrarMenuGestionEmergenciasActivas(); // Mostrar el sub-menú de gestión
            opcionGestion = view.solicitarOpcion();

            switch (opcionGestion) {
                case 1:
                    atenderProximaEmergenciaFlujo(); // Iniciar el flujo de asignación (automático/manual)
                    break;
                case 2:
                    iniciarRepostajeFlujo(); // Flujo para iniciar repostaje
                    break;
                case 3:
                    completarRepostajeFlujo(); // Flujo para completar repostaje
                    break;
                case 4:
                    view.mostrarMensaje("Volviendo al menú principal...");
                    break;
                default:
                    mostrarMensajeErrorOpcion();
            }

             // Simular avance del tiempo después de una acción en este sub-menú
             simularAvanceTiempoYProgreso();


        } while (opcionGestion != 4);
    }


    // --- Flujo de asignación automática y manual (llamado desde gestionarEmergenciasActivas) ---
    private static void atenderProximaEmergenciaFlujo() {
         view.mostrarMensaje("\n================= Atender Próxima Emergencia =================");

         // 1. Obtener la próxima emergencia a priorizar (Paso 18 - parte)
        Emergencia emergenciaAPriorizar = sistema.getProximaEmergenciaAPriorizar();

        if (emergenciaAPriorizar == null) {
            view.mostrarMensaje("No hay emergencias pendientes según la estrategia de priorización.");
            return;
        }

        view.mostrarMensaje(String.format("Emergencia prioritaria seleccionada:\n  ID: %d\n  Tipo: %s\n  Ubicación: Latitud %.2f, Longitud %.2f\n  Gravedad: %s\n  Progreso: %.2f%%\n  Estado: %s\n  Tiempo Estimado: %d ms\n",
            emergenciaAPriorizar.getId(),
            emergenciaAPriorizar.getTipo(),
            emergenciaAPriorizar.getUbicacion().getLatitud(),
            emergenciaAPriorizar.getUbicacion().getLongitud(),
            emergenciaAPriorizar.getNivelGravedad(),
            emergenciaAPriorizar.getProgresoAtencion(),
            emergenciaAPriorizar.isAtendida() ? "Resuelta" : "Pendiente",
            emergenciaAPriorizar.getTiempoRespuestaEstimado()));
        view.mostrarMensaje("=============================================================");

        // 2. Sugerir recursos automáticamente (Paso 18)
        List<Recurso> recursosSugeridos = sistema.sugerirRecursosAutomaticos(emergenciaAPriorizar);

        if (!recursosSugeridos.isEmpty()) {
            view.mostrarMensajeSugerenciaRecursos(recursosSugeridos, null);

            // 3. Solicitar confirmación: sugerido o manual (Paso 18 y 19 - parte de la interacción)
            String confirmacion = view.solicitarConfirmacion("¿Desea asignar estos recursos sugeridos a Emergencia ID " + emergenciaAPriorizar.getId() + "? (s/n)");

            if (confirmacion.equals("s")) {
                // Asignar los recursos sugeridos (llama al Modelo - Paso 18 y 19)
                sistema.asignarRecursosAEmergencia(emergenciaAPriorizar, recursosSugeridos);
            } else {
                view.mostrarMensaje("Sugerencia automática declinada.");
                // Ir al flujo de asignación manual para esta emergencia
                asignarRecursosManualmenteAEmergencia(emergenciaAPriorizar); // Nuevo método para asignar manualmente a una emergencia específica
            }
        } else {
             view.mostrarMensaje("No se encontraron recursos disponibles para sugerir para esta emergencia en la base cercana.");
             // Opcional: Preguntar si quiere intentar asignación manual de todos modos
             String intentarManual = view.solicitarConfirmacion("¿Desea intentar asignar recursos manualmente a Emergencia ID " + emergenciaAPriorizar.getId() + "?");
             if (intentarManual.equals("s")) {
                 asignarRecursosManualmenteAEmergencia(emergenciaAPriorizar);
             }
        }
    }

    // Nuevo método auxiliar para asignar recursos manualmente a una emergencia específica
     private static void asignarRecursosManualmenteAEmergencia(Emergencia emergencia) {
          view.mostrarMensaje("\n--- Asignación Manual para Emergencia ID " + emergencia.getId() + " ---");

         List<Recurso> recursosAAsignar = new ArrayList<>();
         String continuarAsignando;

         do {
             view.mostrarMensaje("\n--- Seleccionar Recurso ---");

              // Mostrar recursos DISPONIBLES (considerando estado y combustible) por tipo
              // Necesitamos saber los tipos de recursos relevantes para esta emergencia.
              // Simplificamos mostrando todos los recursos disponibles y dejando que el usuario elija.
             view.mostrarMensaje("Recursos Disponibles:");
             List<Recurso> todosRecursosDisponibles = sistema.getAllRecursosDisponiblesPorTipo("cualquier_tipo_simulado");
              if (todosRecursosDisponibles.isEmpty()) {
                 view.mostrarMensaje("No hay recursos disponibles para asignar.");
                 break; // Salir del bucle de asignación manual
             }

             view.mostrarRecursosDisponiblesParaAsignacionManual(todosRecursosDisponibles);


             int idRecursoSeleccionado = view.solicitarNumeroEntero("Ingrese el ID del recurso a asignar (o 0 para finalizar):");

             if (idRecursoSeleccionado == 0) {
                 break; // El usuario quiere finalizar la asignación manual
             }

             Recurso recursoParaAsignar = sistema.getRecursoById(idRecursoSeleccionado);

             if (recursoParaAsignar != null) {
                  // La lógica de asignación en sistema.asignarRecursosAEmergencia ya hace la verificación final (estado y combustible).
                  // Aquí solo añadimos el recurso a la lista para intentar asignarlo después.
                  if (!recursosAAsignar.contains(recursoParaAsignar)) {
                      recursosAAsignar.add(recursoParaAsignar);
                      view.mostrarMensaje("Recurso " + recursoParaAsignar.getTipo() + " (ID: " + recursoParaAsignar.getId() + ") añadido a la lista de asignación manual.");
                  } else {
                      view.mostrarMensaje("Este recurso ya ha sido seleccionado para asignación manual.");
                  }

             } else {
                 view.mostrarMensaje("Recurso con ID " + idRecursoSeleccionado + " no encontrado.");
             }

             continuarAsignando = view.solicitarConfirmacion("¿Desea seleccionar otro recurso para asignar a Emergencia ID " + emergencia.getId() + "?");

         } while (continuarAsignando.equals("s"));

         // Intentar asignar los recursos seleccionados manualmente (llama al Modelo - Paso 19)
         if (!recursosAAsignar.isEmpty()) {
              sistema.asignarRecursosAEmergencia(emergencia, recursosAAsignar);
         } else {
             view.mostrarMensaje("No se seleccionaron recursos para asignación manual.");
         }
     }


     // --- Flujos para iniciar y completar repostaje (llamados desde gestionarEmergenciasActivas) ---

     private static void iniciarRepostajeFlujo() {
         view.mostrarMensaje("\n--- Iniciar Repostaje de Vehículo ---");
         int idVehiculo = view.solicitarIdRecurso(); // Usa el método general para solicitar ID

         // Llamar al método del sistema para iniciar el repostaje (Paso 22)
         boolean iniciado = sistema.iniciarRepostajeRecurso(idVehiculo);

         if (iniciado) {
             view.mostrarMensaje("Solicitud de inicio de repostaje procesada.");
         } // El método del sistema ya imprime mensajes de éxito/error
     }

     private static void completarRepostajeFlujo() {
          view.mostrarMensaje("\n--- Completar Repostaje de Vehículo ---");
         int idVehiculo = view.solicitarIdRecurso(); // Usa el método general para solicitar ID

         // Llamar al método del sistema para completar el repostaje (Paso 22)
         boolean completado = sistema.completarRepostajeRecurso(idVehiculo);

         if (completado) {
             view.mostrarMensaje("Solicitud de completado de repostaje procesada.");
         } // El método del sistema ya imprime mensajes de éxito/error
     }


    private static void mostrarEstadisticas() {
        view.mostrarMensaje("\n--- Estadísticas del Día ---");
        // Lógica de estadísticas (Fase 7 - Paso 28)
        // Llamar a métodos en SistemaEmergencias para obtener estadísticas
        // y mostrarlas usando la Vista.
         view.mostrarMensaje("Funcionalidad de estadísticas aún no implementada.");
    }

    private static void finalizarJornada() {
        view.mostrarMensaje("\n--- Finalizando Jornada ---");
        // Lógica de finalización (Fase 7 - Paso 29)
        // Guardar registro del día, etc.
         view.mostrarMensaje("Funcionalidad de finalización de jornada aún no implementada.");
    }

    // Método para simular el avance del tiempo y el progreso de las emergencias (Paso 23)
    // Llamado en cada iteración del bucle principal del menú y del sub-menú de gestión.
     private static void simularAvanceTiempoYProgreso() {
         // Simulación simple: avanzar el progreso de cada emergencia activa un pequeño porcentaje en cada ciclo
         double avancePorCiclo = 5.0; // Porcentaje de avance por cada vez que se llama a este método

         List<Emergencia> emergenciasNoAtendidas = sistema.getEmergenciasNoAtendidas(); // Obtener solo las que no están 100%
         if (!emergenciasNoAtendidas.isEmpty()) {
              // view.mostrarMensaje("\nSimulando avance de progreso de emergencias activas..."); // Opcional: log
              for (Emergencia emergencia : emergenciasNoAtendidas) {
                  if (emergencia.getTiempoInicioAtencion() != null) { // Solo avanza si se ha iniciado la atención
                       emergencia.simularAvanceProgreso(avancePorCiclo);
                       // Si la emergencia se completa en esta simulación
                       if (emergencia.isAtendida()) {
                           view.mostrarMensaje("Emergencia ID " + emergencia.getId() + " marcada como Resuelta.");
                           // Lógica para liberar recursos asociados a esta emergencia
                           liberarRecursosDeEmergencia(emergencia); // <-- Llamar a la lógica de liberación
                       }
                  }
              }
         }
     }

     // Lógica para liberar recursos de una emergencia cuando se resuelve (Implementado en MainApp/Controlador por ahora)
     private static void liberarRecursosDeEmergencia(Emergencia emergencia) {
         view.mostrarMensaje("Liberando recursos asignados a Emergencia ID " + emergencia.getId() + "...");
         // Recorrer todos los recursos del sistema y liberar los que están asignados a esta emergencia
         for (BaseOperaciones base : sistema.getBasesOperaciones()) { // Recorrer todas las bases
             for (Recurso recurso : base.getRecursosEnBase()) { // Recorrer los recursos de cada base
                 if (recurso.getEmergenciaAsignada() != null && recurso.getEmergenciaAsignada().getId() == emergencia.getId()) {
                     recurso.liberar(); // Marcar como disponible y desasignar
                     view.mostrarMensaje("  Recurso liberado: " + recurso.getTipo() + " (ID: " + recurso.getId() + ")");

                     // Si es vehículo, mover su ubicacionActual de vuelta a su base y simular gasto de regreso (Paso 21)
                     if (recurso instanceof Vehiculo) {
                          Vehiculo v = (Vehiculo) recurso;
                          // Calcular distancia de la ubicación actual (la emergencia) a la base
                           double distanciaRegreso = sistema.calcularDistancia(v.getUbicacionActual(), v.getUbicacionBase());
                           if (distanciaRegreso > 0) { // Solo simular viaje si hay distancia
                                view.mostrarMensaje("  Simulando viaje de regreso a base para " + v.getTipo() + " (ID: " + v.getId() + ") (" + String.format("%.2f", distanciaRegreso) + " km).");
                                v.moverA(v.getUbicacionBase(), distanciaRegreso); // Simular viaje de regreso y gasto de combustible
                           } else {
                               // Si la distancia es 0 o negativa, ya está en la base (o error en cálculo)
                               v.moverA(v.getUbicacionBase(), 0); // Asegurar que la ubicación actual sea la base
                           }


                           // Opcional: Si el combustible es bajo después del viaje de regreso, iniciar repostaje automáticamente (Paso 22)
                            if (v.getNivelCombustible() < 20) { // Umbral de combustible bajo (ejemplo: 20%)
                                view.mostrarMensaje("  Combustible bajo (" + String.format("%.1f", v.getNivelCombustible()) + "%) en " + v.getTipo() + " (ID: " + v.getId() + "). Iniciando repostaje automático.");
                                sistema.iniciarRepostajeRecurso(v.getId()); // Llamar al método del sistema
                            } else {
                                // Si el combustible es suficiente, simplemente vuelve a estar disponible
                                 view.mostrarMensaje("  " + v.getTipo() + " (ID: " + v.getId() + ") regresó a base. Combustible restante: " + String.format("%.1f", v.getNivelCombustible()) + "%.");
                            }
                     }
                 }
             }
         }
         // Opcional: Mover la emergencia a una lista de emergencias resueltas si ya no se quiere ver en "activas"
         // sistema.marcarEmergenciaComoResuelta(emergencia); // Necesitarías un método en SistemaEmergencias para esto
     }

    // Consolidar mensajes de error
    private static void mostrarMensajeErrorOpcion() {
        view.mostrarMensaje("Opción no válida. Por favor, intente de nuevo.");
    }
}