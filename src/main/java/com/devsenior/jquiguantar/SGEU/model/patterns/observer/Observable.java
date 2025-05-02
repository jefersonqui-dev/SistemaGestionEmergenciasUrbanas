package com.devsenior.jquiguantar.SGEU.model.patterns.observer;

//Interfaz para el observable (quien notifica a otros)
public interface Observable {
    void addObserver(Observer o); // Agregar un observador

    void removeObserver(Observer o); // Remover un observador

    void notifyObservers(Object event); // Notificar a todos los observadores
}
