package com.turngo.turngo.exceptions;

public class TurnoNoDisponibleException extends RuntimeException {
    public TurnoNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}