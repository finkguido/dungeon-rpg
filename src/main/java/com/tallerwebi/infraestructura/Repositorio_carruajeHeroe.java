package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.entidades.Carruaje;
import com.tallerwebi.dominio.entidades.CarruajeHeroe;
import com.tallerwebi.dominio.entidades.Heroe;

import java.util.List;

public interface Repositorio_carruajeHeroe {
    List<Heroe> getListaDeHeroes(Long id);

    CarruajeHeroe buscarRelacion(Carruaje carruajeBuscado, Heroe heroeBuscado);

    void quitarHeroeDeCarruaje(Carruaje carruajeBuscado, Heroe heroeBuscado);

    void removerRelacion(CarruajeHeroe carruajeHeroeBuscado);

    void agregarRelacion(Carruaje carruaje, Heroe heroe);

    void add(Carruaje carruaje, Heroe heroe);
    List<CarruajeHeroe> findByCarruaje(Carruaje carruaje);
    void delete(CarruajeHeroe relacion);
    List<Heroe> findHeroesByCarruaje(Carruaje carruaje);
}
