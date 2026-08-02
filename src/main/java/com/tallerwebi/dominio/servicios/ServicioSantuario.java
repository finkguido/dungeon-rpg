package com.tallerwebi.dominio.servicios;

import com.tallerwebi.dominio.entidades.Heroe;
import com.tallerwebi.dominio.entidades.Usuario;

import java.util.List;

public interface ServicioSantuario {
    List<Heroe> obtenerHeroesCurables(Usuario usuario);

    void curarHeroe(Usuario usuario, Long idHeroe);

    List<Heroe> obtenerHeroesUsuario(Usuario usuario);

    Object obtenerUsuarioActualizado(Long id);

    boolean puedeAccederASantuario(Usuario usuario);
}
