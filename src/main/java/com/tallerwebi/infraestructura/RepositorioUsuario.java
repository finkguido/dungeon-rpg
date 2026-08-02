package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.entidades.Usuario;

import java.util.List;

public interface RepositorioUsuario {

    Usuario buscarUsuario(String email, String password);
    void guardar(Usuario usuario);
    Usuario buscar(String email);
    void modificar(Usuario usuario);

    Usuario buscarUsuarioPorId(Long idUsuario);

    List<Usuario> obtenerRankingJugadores();

}

