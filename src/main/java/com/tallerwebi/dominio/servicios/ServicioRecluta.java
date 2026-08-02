package com.tallerwebi.dominio.servicios;

import com.tallerwebi.dominio.entidades.Carruaje;
import com.tallerwebi.dominio.entidades.Heroe;
import com.tallerwebi.dominio.entidades.Usuario;

import java.util.List;

public interface ServicioRecluta {

    List<Heroe> getListaDeHeroesEnCarruaje(Long usuarioId);

    List<Heroe> getHeroesDisponiblesEnCarruaje(Long usuarioId);

    List<Heroe> getHeroesObtenidosPorElUsuario(Usuario usuario);

    void quitarUnHeroeDelCarruaje(Long usuarioId, Long heroeId);

    void agregarUnHeroeAlUsuario(Long idHeroe, Usuario usuario);

    List<Heroe> getHeroesDisponiblesEnUsuario(Long idUsuario);

    void setRelacionEntreCarruajeYHeroe(Long idCarruaje, Long idHeroe);

    Carruaje asignarOActualizarUnCarrujeAUnUsuario(Long idUsuario);

    List<Heroe> getHeroesDisponiblesEnCarruaje(Carruaje carruaje);

    Usuario registrarUnUsuario(Usuario usuario);

    Usuario getUsuarioRegistradoPorId(Long idUsuario);

    Carruaje getCarruajeDelUsuarioPorId(Long idUsuario);

    Usuario getUsuarioPorId(Long idUsuario);

    Carruaje getCarruajeDelUsuarioId(Long idUsuario);

    List<Heroe> getHeroesEnCarruaje(Long usuarioId);

    void seleccionarHeroe(Long usuarioId, Long heroeId);

    void quitarHeroe(Long usuarioId, Long heroeId);

}
