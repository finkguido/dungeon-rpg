package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.entidades.Heroe;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.entidades.UsuarioHeroe;

import java.util.List;

public interface Repositorio_usuarioHeroe {
    UsuarioHeroe buscarRelacion(Usuario usuarioBuscado, Heroe heroeBuscado);

    void agregarRelacion(Usuario usuarioBuscado, Heroe heroeBuscado);

    List<Heroe> getListaDeHeroes(Long idUsuario);
}
