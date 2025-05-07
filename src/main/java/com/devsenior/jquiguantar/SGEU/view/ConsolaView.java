package com.devsenior.jquiguantar.SGEU.view;
import com.devsenior.jquiguantar.SGEU.model.emergencies.Emergencia;
import com.devsenior.jquiguantar.SGEU.model.emergencies.NivelGravedad;
import com.devsenior.jquiguantar.SGEU.model.emergencies.TipoEmergencia;
import com.devsenior.jquiguantar.SGEU.model.resources.Recurso;
import com.devsenior.jquiguantar.SGEU.model.resources.Vehiculo;
import com.devsenior.jquiguantar.SGEU.model.resources.EstadoRecurso;
import com.devsenior.jquiguantar.SGEU.model.services.BaseOperaciones;
import com.devsenior.jquiguantar.SGEU.model.util.Ubicacion;


import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit; // Importar TimeUnit para mostrar tiempos en formato legible
import java.util.stream.Collectors;
import java.util.Scanner;
public class ConsolaView {
    private Scanner scanner;

    public ConsolaView() {
        this.scanner = new Scanner(System.in);
    }

    // --- Métodos para mostrar información ---

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    // Consolidar mensajes de error
    public void mostrarMensajeErrorEntrada(String tipo) {
        mostrarMensaje("Entrada no válida. Por favor, ingrese un " + tipo + ".");
    }

    // Menú principal simplificado
    public void mostrarMenuPrincipal() {
        System.out.println("\n===================================================");
        System.out.println("  Sistema de Gestión de Emergencias Urbanas");
        System.out.println("===================================================");
        System.out.println("1. Registrar nueva emergencia");
        System.out.println("2. Ver estado actual de emergencias"); // Incluirá activas y resueltas si quieres
        System.out.println("3. Ver estado actual de recursos");
        System.out.println("4. **Gestionar Emergencias Activas**"); // Este iniciará el flujo integrado (atender, asignar, repostar, etc.)
        System.out.println("5. Mostrar estadísticas del día"); // Fase 7
        System.out.println("6. Finalizar jornada"); // Opción para salir
        System.out.println("===================================================");
    }

     // Menú para gestionar emergencias activas
     public void mostrarMenuGestionEmergenciasActivas() {
         System.out.println("\n--- Gestión de Emergencias Activas ---");
         System.out.println("1. Atender próxima emergencia prioritaria (automático/manual)"); // Iniciar flujo de asignación
         System.out.println("2. Iniciar repostaje de vehículo"); // Opción de gestión de recursos
         System.out.println("3. Completar repostaje de vehículo"); // Opción de gestión de recursos
         System.out.println("4. Volver al menú principal");
         System.out.println("--------------------------------------");
     }

    // Simplificar encabezados y separadores
    public void mostrarEncabezado(String titulo) {
        mostrarMensaje("\n             ================= " + titulo + " =================");
    }

    public void mostrarEmergencias(List<Emergencia> emergencias) {
        if (emergencias == null || emergencias.isEmpty()) {
            mostrarMensaje("No hay emergencias registradas.");
            return;
        }
        mostrarEncabezado("Estado Actual de Emergencias");
        List<Emergencia> activas = emergencias.stream().filter(e -> !e.isAtendida()).collect(Collectors.toList());
        List<Emergencia> resueltas = emergencias.stream().filter(e -> e.isAtendida()).collect(Collectors.toList());

        if (!activas.isEmpty()) {
            mostrarMensaje("\n  Emergencias Activas:");
            mostrarMensaje("  -----------------------------------------------------------------------------");
            for (Emergencia emergencia : activas) {
                mostrarMensaje(String.format("  ID: %-3d | Tipo: %-25s | Gravedad: %-5s | Progreso: %.1f%%",
                        emergencia.getId(), emergencia.getTipo(), emergencia.getNivelGravedad(), emergencia.getProgresoAtencion()));
            }
        }

        if (!resueltas.isEmpty()) {
            mostrarMensaje("\n  Emergencias Resueltas:");
            mostrarMensaje("  ------------------------------------------------------------------------------------");
            for (Emergencia emergencia : resueltas) {
                long tiempoTotalAtencionMillis = emergencia.calcularTiempoTotalAtencionMillis();
                String tiempoAtencionStr = (tiempoTotalAtencionMillis != -1) ?
                        String.format("Tiempo de atención: %.2f minutos", tiempoTotalAtencionMillis / 60000.0) : "";
                mostrarMensaje(String.format("  ID: %-3d | Tipo: %-25s | Gravedad: %-5s | %s",
                        emergencia.getId(), emergencia.getTipo(), emergencia.getNivelGravedad(), tiempoAtencionStr));
            }
        }

        mostrarMensaje("  ============================================================================");
    }

     public void mostrarRecursos(List<Recurso> recursos) {
         if (recursos == null || recursos.isEmpty()) {
             mostrarMensaje("No hay recursos registrados en el sistema.");
             return;
         }
         mostrarEncabezado("Estado Actual de Recursos");
         mostrarMensaje("  --------------------------------------------------------------------------------------");
         mostrarMensaje("  ID   | Tipo                 | Cantidad | Estado     | Combustible | Base (Lat, Long)");
         mostrarMensaje("  --------------------------------------------------------------------------------------");

         recursos.stream()
                 .collect(Collectors.groupingBy(Recurso::getTipo))
                 .forEach((tipo, listaRecursos) -> {
                     int cantidad = listaRecursos.size();
                     for (Recurso r : listaRecursos) {
                         String baseInfo = "";
                         String combustibleInfo = "";
                         if (r instanceof Vehiculo) {
                             Vehiculo v = (Vehiculo) r;
                             baseInfo = String.format("(%.2f, %.2f)", v.getUbicacionBase().getLatitud(), v.getUbicacionBase().getLongitud());
                             combustibleInfo = String.format("%.1f%%", v.getNivelCombustible());
                         }
                         mostrarMensaje(String.format("  %-4d | %-20s | %-8d | %-10s | %-11s | %s",
                                 r.getId(), r.getTipo(), cantidad, r.getEstado(), combustibleInfo, baseInfo));
                     }
                 });

         mostrarMensaje("  =====================================================================================");
     }


     public void mostrarRecursosDisponiblesParaAsignacionManual(List<Recurso> recursos) {
         if (recursos == null || recursos.isEmpty()) {
             mostrarMensaje("No hay recursos disponibles del tipo solicitado o en general que puedan ser asignados.");
             return;
         }
         mostrarMensaje("\n--- Recursos Disponibles para Asignación Manual ---");
         mostrarMensaje("ID | Tipo | Estado | Combustible | Base");
         mostrarMensaje("----------------------------------------");
         for (Recurso r : recursos) {
             String baseInfo = "";
             if (r instanceof Vehiculo) {
                 Vehiculo v = (Vehiculo) r;
                 baseInfo = " - Base: " + v.getUbicacionBase().toString();
             }
             mostrarMensaje(String.format("%-3d | %-10s | %-10s | %-10s | %s", r.getId(), r.getTipo(), r.getEstado(), (r instanceof Vehiculo ? String.format("%.1f", ((Vehiculo)r).getNivelCombustible()) + "%" : ""), baseInfo));
         }
         mostrarMensaje("----------------------------------------");
     }

     public void mostrarMensajeSugerenciaRecursos(List<Recurso> sugerencias, BaseOperaciones baseSugerida) {
          if (sugerencias == null || sugerencias.isEmpty()) {
              mostrarMensaje("No se encontraron recursos disponibles para sugerir.");
              if (baseSugerida != null) {
                  mostrarMensaje("Base más cercana encontrada: " + baseSugerida.getNombre() + " (" + baseSugerida.getTipoServicioAsociado() + ")");
              } else {
                  mostrarMensaje("No se encontró una base cercana para sugerir.");
              }
              return;
          }

          if (baseSugerida != null) {
              mostrarMensaje("\nRecursos sugeridos desde " + baseSugerida.getNombre() + ":");
          } else {
              mostrarMensaje("\nRecursos sugeridos:");
          }
          for (Recurso r : sugerencias) {
              mostrarMensaje("- " + r.getTipo() + " (ID: " + r.getId() + ")");
          }
     }

    public void mostrarNotificacionesEmergencia(List<BaseOperaciones.NotificacionEmergencia> notificaciones) {
        if (notificaciones == null || notificaciones.isEmpty()) {
            return;
        }

        StringBuilder mensaje = new StringBuilder();
        String separador = "------------------------------------------------------------------------------------";
        
        mensaje.append("\n").append(separador).append("\n");
        mensaje.append("                  NOTIFICACIONES DE EMERGENCIA\n");
        mensaje.append(separador).append("\n");
        
        for (BaseOperaciones.NotificacionEmergencia notif : notificaciones) {
            // Información principal de la notificación
            mensaje.append("Base Notificada: ").append(notif.getNombreBase()).append("\n");
            mensaje.append("Servicio: ").append(notif.getTipoServicio()).append("\n");
            mensaje.append("Tipo de Emergencia: ").append(notif.getTipoEmergencia()).append("\n");
            mensaje.append("Ubicación: (").append(String.format("%.2f, %.2f", 
                notif.getUbicacion().getLatitud(), 
                notif.getUbicacion().getLongitud())).append(")\n");
            mensaje.append("ID Emergencia: ").append(notif.getIdEmergencia()).append("\n");
            
            // Si la base reacciona, mostrar el mensaje
            if (notif.getReacciona()) {
                mensaje.append("-> ").append(notif.getNombreBase())
                      .append(" ha sido notificada y está evaluando la emergencia.\n");
            }
            
            mensaje.append(separador).append("\n");
        }
        
        mostrarMensaje(mensaje.toString());
    }



    // --- Métodos para solicitar entrada al usuario ---

    public int solicitarOpcion() {
        System.out.print("Ingrese su opción: ");
        while (!scanner.hasNextInt()) {
            mostrarMensajeErrorEntrada("número");
            scanner.next();
        }
        int opcion = scanner.nextInt();
        scanner.nextLine(); // Consumir el resto de la línea después del número
        return opcion;
    }

    public int solicitarNumeroEntero(String mensaje) {
        System.out.print(mensaje + " ");
        while (!scanner.hasNextInt()) {
            mostrarMensajeErrorEntrada("número entero");
            scanner.next();
        }
        int numero = scanner.nextInt();
        scanner.nextLine(); // Consumir el resto de la línea
        return numero;
    }

    public String solicitarTexto(String mensaje) {
        System.out.print(mensaje + " ");
        return scanner.nextLine();
    }

    public Ubicacion solicitarUbicacion() {
        mostrarMensaje("Ingrese la ubicación (Latitud y Longitud):");
        double latitud = 0;
        double longitud = 0;
        boolean entradaValida = false;
        while(!entradaValida) {
            try {
                System.out.print("Latitud: ");
                latitud = scanner.nextDouble();
                System.out.print("Longitud: ");
                longitud = scanner.nextDouble();
                entradaValida = true;
            } catch (InputMismatchException e) {
                mostrarMensaje("Entrada no válida. Por favor, ingrese números para la latitud y longitud.");
                scanner.next(); // Consumir la entrada no válida
            } finally {
                scanner.nextLine(); // Consumir el resto de la línea, incluyendo el newline después de los números
            }
        }
        return new Ubicacion(latitud, longitud);
    }


    public TipoEmergencia solicitarTipoEmergencia() {
       //1 mostrarMensaje("Seleccione el tipo de emergencia:");
        TipoEmergencia[] tipos = TipoEmergencia.values();
        for (int i = 0; i < tipos.length; i++) {
            mostrarMensaje((i + 1) + ". " + tipos[i]);
        }
        int opcionTipo = solicitarNumeroEntero("Ingrese el tipo de emergencia:");
        if (opcionTipo > 0 && opcionTipo <= tipos.length) {
            return tipos[opcionTipo - 1];
        } else {
            mostrarMensaje("Opción de tipo de emergencia no válida. Seleccionando OTROS por defecto.");
            return TipoEmergencia.OTROS; 
        }
    }

    public NivelGravedad solicitarNivelGravedad() {
        mostrarMensaje("  ---  Nivel de gravedad  --- ");
        NivelGravedad[] niveles = NivelGravedad.values();
         for (int i = 0; i < niveles.length; i++) {
            mostrarMensaje((i + 1) + ". " + niveles[i]);
        }
        int opcionNivel = solicitarNumeroEntero("Seleccione el nivel de gravedad:");
        if (opcionNivel > 0 && opcionNivel <= niveles.length) {
            return niveles[opcionNivel - 1];
        } else {
            mostrarMensaje("Opción de nivel de gravedad no válida. Seleccionando BAJO por defecto.");
            return NivelGravedad.BAJO; // O manejar error
        }
    }

     public long solicitarTiempoRespuestaEstimado(String unidad) {
         return (long) solicitarNumeroEntero("Ingrese tiempo estimado de respuesta inicial en " + unidad + ":");
     }


    public String solicitarConfirmacion(String mensaje) {
        System.out.print(mensaje + " (s/n): ");
        return scanner.nextLine().trim().toLowerCase();
    }

     public int solicitarIdEmergencia(List<Emergencia> emergenciasDisponibles) {
         mostrarMensaje("Ingrese el ID de la emergencia:");
         int id = solicitarNumeroEntero(""); // Usa el método existente
         // TODO: Validar si el ID existe en la lista de emergenciasDisponibles
         return id;
     }

     public int solicitarIdRecurso() {
         mostrarMensaje("Ingrese el ID del recurso:");
         return solicitarNumeroEntero(""); // Usa el método existente
     }


    public void cerrarScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }

    // Método para esperar a que el usuario presione Enter
    public void esperarEnter() {
        mostrarMensaje("Presione Enter para continuar...");
        scanner.nextLine(); // Esperar a que el usuario presione Enter
    }
}