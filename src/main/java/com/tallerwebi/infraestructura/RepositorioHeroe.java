package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.entidades.Heroe;

import java.util.List;

public interface RepositorioHeroe {
    Heroe buscarHeroePorId(Long id);

    Heroe guardar(Heroe heroe);

    List<Heroe> getListaDeHeroes();

    void modificar(Heroe heroe);
}
