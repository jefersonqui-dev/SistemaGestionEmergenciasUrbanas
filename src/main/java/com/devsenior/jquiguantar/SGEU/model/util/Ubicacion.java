package com.devsenior.jquiguantar.SGEU.model.util;

public class Ubicacion {
    private double latitud;
    private double longitud;

    // creamos el constructor
    public Ubicacion(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public String toString() {
        return "Ubicacion{" +
                "latitud=" + latitud +
                ", longitud=" + longitud +
                '}';
    }

    // creamos el método equals y hash code para comparar ubicaciones
    // para saber si son el mimo punto geografico
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Ubicacion))
            return false;
        Ubicacion ubicacion = (Ubicacion) o;
        return Double.compare(ubicacion.latitud, latitud) == 0 && Double.compare(ubicacion.longitud, longitud) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitud);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitud);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

}
