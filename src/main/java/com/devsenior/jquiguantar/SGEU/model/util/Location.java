package com.devsenior.jquiguantar.SGEU.model.util;

public class Location {
    private double latitude;
    private double longitud;

    public Location(double latitude, double longitud) {
        this.latitude = latitude;
        this.longitud = longitud;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitud() {
        return longitud;
    }

    public double distanciaKm(Location destino) {
        final double RADIO_TIERRA_KM = 6341.0;
        double dLat = Math.toRadians(destino.latitude - this.latitude);
        double dLon = Math.toRadians(destino.longitud - this.longitud);
        double lat1Rad = Math.toRadians(this.latitude);
        double lat2Rad = Math.toRadians(destino.latitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RADIO_TIERRA_KM * c;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitud);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Location other = (Location) obj;
        if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
            return false;
        if (Double.doubleToLongBits(longitud) != Double.doubleToLongBits(other.longitud))
            return false;
        return true;
    }

}
