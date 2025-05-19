package com.devsenior.jquiguantar.SGEU.model.util;

import java.io.IOException;

public class Utilities {
    public static void printTitle(String title, String option) {
        String separator = "=============================================================";
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
}
