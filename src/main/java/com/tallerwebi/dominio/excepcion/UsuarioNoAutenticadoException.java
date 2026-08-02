package com.tallerwebi.dominio.excepcion;

public class UsuarioNoAutenticadoException extends RuntimeException {
    public UsuarioNoAutenticadoException() {
        super("Usuario no autenticado");
    }
}
