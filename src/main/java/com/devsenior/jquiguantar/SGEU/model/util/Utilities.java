package com.devsenior.jquiguantar.SGEU.model.util;

import java.io.IOException;
import java.util.Scanner;

public class Utilities {
    public static void printTitle(String title, String option) {
        String separator = "-------------------------------------------------------------";
        System.out.println("\n" + separator);
        String mensaje;
        if (option != null && !option.isEmpty()) {
            mensaje = title + ": " + option;
        } else {
            mensaje = title;
        }

        // Centrar el mensaje (aproximadamente)
        int espacios = (separator.length() - mensaje.length()) / 2;
        String mensajeCentrado = " ".repeat(Math.max(0, espacios)) + mensaje
                + " ".repeat(Math.max(0, separator.length() - mensaje.length() - espacios));

        System.out.println(mensajeCentrado);
        System.out.println(separator);
    }

    public static void printTitle(String title) {
        printTitle(title, null);
    }

    public static void cleanConsole() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.println("\033[H\033[2J");
                System.out.flush();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al limpiar la consola" + e.getMessage());
        }
    }

    public static int getIntInput(Scanner scanner, String mensaje, int min, int max) {
        int input;
        do {
            System.out.print(mensaje);
            while (!scanner.hasNextInt()) {
                System.out.println("Por favor, ingrese un número válido.");
                System.out.print(mensaje);
                scanner.next();
            }
            input = scanner.nextInt();
            scanner.nextLine(); // Limpiar salto de línea
            if (input < min || input > max) {
                System.out.printf("Por favor, ingrese un número entre %d y %d.\n", min, max);
            }
        } while (input < min || input > max);
        return input;
    }

    public static String getStringInput(Scanner scanner, String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine();
    }

    public static void pressEnterToContinue(Scanner scanner) {
        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
}
