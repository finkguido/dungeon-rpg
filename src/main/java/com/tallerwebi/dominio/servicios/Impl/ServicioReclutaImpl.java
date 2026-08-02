package com.tallerwebi.dominio.servicios.Impl;

import com.tallerwebi.dominio.excepcion.ReclutaException;
import com.tallerwebi.dominio.entidades.*;
import com.tallerwebi.dominio.servicios.ServicioRecluta;
import com.tallerwebi.infraestructura.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServicioReclutaImpl implements ServicioRecluta {

    private final RepositorioCarruaje repositorioCarruaje;
    private final RepositorioHeroe repositorioHeroe;
    private final RepositorioUsuario repositorioUsuario;
    private final Repositorio_carruajeHeroe repositorio_carruajeHeroe;
    private final Repositorio_usuarioHeroe repositorio_usuarioHeroe;


    @Autowired
    public ServicioReclutaImpl(RepositorioCarruaje repositorioCarruaje,
                               RepositorioHeroe repositorioHeroe,
                               RepositorioUsuario repositorioUsuario,
                               Repositorio_carruajeHeroe repositorio_carruajeHeroe,
                               Repositorio_usuarioHeroe repositorio_usuarioHeroe) {
        this.repositorioCarruaje = repositorioCarruaje;
        this.repositorioHeroe = repositorioHeroe;
        this.repositorioUsuario = repositorioUsuario;
        this.repositorio_carruajeHeroe = repositorio_carruajeHeroe;
        this.repositorio_usuarioHeroe = repositorio_usuarioHeroe;

    }

    @Override
    public List<Heroe> getHeroesDisponiblesEnCarruaje(Carruaje carruaje) {
        Carruaje c = repositorioCarruaje
                .buscarCarruajePorId(carruaje.getId());
        if (c == null) {
            throw new ReclutaException("No se encontro el carruaje");
        }

        List<Heroe> escogidos =
                repositorio_carruajeHeroe.getListaDeHeroes(c.getId());

        if (escogidos.isEmpty()) {
            throw new ReclutaException("No hay heroes disponibles por hoy");
        }

        return escogidos;
    }

    @Override
    public Usuario registrarUnUsuario(Usuario usuario) {
        repositorioUsuario.guardar(usuario);
        return repositorioUsuario.buscarUsuarioPorId(usuario.getId());
    }

    @Override
    public Usuario getUsuarioRegistradoPorId(Long idUsuario) {
        return repositorioUsuario.buscarUsuarioPorId(idUsuario);
    }

    @Override
    public Carruaje getCarruajeDelUsuarioPorId(Long idUsuario) {
        Usuario usuario = repositorioUsuario.buscarUsuarioPorId(idUsuario);
        if (usuario == null) throw new RuntimeException("Usuario no encontrado");
        return repositorioCarruaje.buscarCarruajeAsignadoAUnUsuario(usuario);
    }

    @Override
    public Usuario getUsuarioPorId(Long idUsuario) {
        return repositorioUsuario.buscarUsuarioPorId(idUsuario);
    }

    @Override
    public Carruaje getCarruajeDelUsuarioId(Long idUsuario) {
        Usuario buscado = repositorioUsuario.buscarUsuarioPorId(idUsuario);
        if (buscado == null) throw new RuntimeException("Usuario no encontrado");
        return repositorioCarruaje.buscarCarruajeAsignadoAUnUsuario(buscado);
    }

    @Override
    public void quitarUnHeroeDelCarruaje(Long uid, Long hid) {
        Usuario u = repositorioUsuario.buscarUsuarioPorId(uid);
        Carruaje c = repositorioCarruaje.buscarCarruajeAsignadoAUnUsuario(u);
        Heroe h = repositorioHeroe.buscarHeroePorId(hid);
        CarruajeHeroe rel = repositorio_carruajeHeroe.buscarRelacion(c, h);
        if (rel != null) {
            repositorio_carruajeHeroe.removerRelacion(rel);
        }
    }

    @Override
    public void setRelacionEntreCarruajeYHeroe(Long idCarruaje, Long idHeroe) {

        Heroe heroeBuscado =  repositorioHeroe.buscarHeroePorId(idHeroe);

        Carruaje carruajeBuscado = repositorioCarruaje.buscarCarruajePorId(idCarruaje);

        if(heroeBuscado == null)  throw new RuntimeException("No se encontro heroe");
        if(carruajeBuscado == null) throw new RuntimeException("No se encontro carruaje");

        CarruajeHeroe carruajeHeroeBuscado  = repositorio_carruajeHeroe.buscarRelacion(carruajeBuscado,heroeBuscado);

        if(carruajeHeroeBuscado == null) {
            repositorio_carruajeHeroe.agregarRelacion(carruajeBuscado,heroeBuscado);
        }

        if(carruajeHeroeBuscado != null) throw new RuntimeException("Error, ya existe la relacion entre carruaje y heroe");

    }

    @Override
    public Carruaje asignarOActualizarUnCarrujeAUnUsuario(Long idUsuario) {

        Usuario usuarioBuscado = repositorioUsuario.buscarUsuarioPorId(idUsuario);
        if(usuarioBuscado == null) throw new RuntimeException("No se encontro usuario");

        Carruaje carruajeBus = repositorioCarruaje.buscarCarruajeAsignadoAUnUsuario(usuarioBuscado);

        if(carruajeBus == null) {
            Carruaje carruaje = repositorioCarruaje.guardar(new Carruaje(0,0,2));

            if (carruaje == null) throw new RuntimeException("No se guardo carruaje");
            if (carruaje.getId() == null) throw new RuntimeException("El carruaje tiene id nulo");

            List<Heroe> listaHeroesExistentes = repositorioHeroe.getListaDeHeroes();

            if (listaHeroesExistentes.isEmpty()) throw new RuntimeException("La lista esta vacia o nula");

            repositorioCarruaje.asignarUsuarioAUnCarruje(carruaje,usuarioBuscado);
            repositorio_carruajeHeroe.agregarRelacion(carruaje,listaHeroesExistentes.get(0));
            repositorio_carruajeHeroe.agregarRelacion(carruaje,listaHeroesExistentes.get(1));
            return carruaje;
        }

        return carruajeBus;
    }



    @Override
    public void agregarUnHeroeAlUsuario(Long idHeroe, Usuario usuario) {

        Heroe heroeBuscado =  repositorioHeroe.buscarHeroePorId(idHeroe);
        Usuario usuarioBuscado = repositorioUsuario.buscarUsuarioPorId(usuario.getId());

        if(heroeBuscado == null)  throw new RuntimeException("No se encontro heroe");

        if(usuarioBuscado == null) throw new RuntimeException("No se encontro usuario");

        UsuarioHeroe usuarioHeroeBus  = repositorio_usuarioHeroe.buscarRelacion(usuarioBuscado,heroeBuscado);

        if(usuarioHeroeBus == null) {
            repositorio_usuarioHeroe.agregarRelacion(usuarioBuscado,heroeBuscado);
        }
        if(usuarioHeroeBus != null) throw new RuntimeException("Este heroe ya existe en el usuario");
    }

    @Override
    public List<Heroe> getHeroesDisponiblesEnUsuario(Long idUsuario) {

        Usuario usuarioBus = repositorioUsuario.buscarUsuarioPorId(idUsuario);

        if(usuarioBus == null) throw new RuntimeException("No se encontro el Usuario");

        List<Heroe> listaDeHeroes = repositorio_usuarioHeroe.getListaDeHeroes(idUsuario);

        if(listaDeHeroes == null) throw new RuntimeException("La lista es nula");
        if(listaDeHeroes.isEmpty()) throw new RuntimeException("La lista no tiene heroes");

        return listaDeHeroes;
    }


    @Override
    public List<Heroe> getListaDeHeroesEnCarruaje(Long uid) {
        Usuario u = repositorioUsuario.buscarUsuarioPorId(uid);
        Carruaje c = repositorioCarruaje.buscarCarruajeAsignadoAUnUsuario(u);
        return repositorio_carruajeHeroe.getListaDeHeroes(c.getId());
    }


    @Override
    public List<Heroe> getHeroesObtenidosPorElUsuario(Usuario usuario) {

        Usuario usuarioBuscado = repositorioUsuario.buscarUsuarioPorId(usuario.getId());

        if(usuarioBuscado == null)  throw new RuntimeException("No se encontro el usuario");

        List<Heroe> listaDeHeroes = repositorio_usuarioHeroe.getListaDeHeroes(usuarioBuscado.getId());

        if(listaDeHeroes == null) {listaDeHeroes = new ArrayList<>();}

        if(listaDeHeroes.isEmpty()) throw new ReclutaException("Todavia no obtuviste ningun heroe");

        return listaDeHeroes;
    }

    @Override
    public List<Heroe> getHeroesDisponiblesEnCarruaje(Long uid) {
        Usuario u = repositorioUsuario.buscarUsuarioPorId(uid);
        Carruaje c = repositorioCarruaje.buscarCarruajeAsignadoAUnUsuario(u);
        List<Heroe> todos     = repositorioHeroe.getListaDeHeroes();
        List<Heroe> escogidos = repositorio_carruajeHeroe.getListaDeHeroes(c.getId());
        return todos.stream()
                .filter(h -> !escogidos.contains(h))
                .collect(Collectors.toList());
    }

    @Override
    public List<Heroe> getHeroesEnCarruaje(Long uid) {
        Usuario u = repositorioUsuario.buscarUsuarioPorId(uid);
        Carruaje c = repositorioCarruaje
                .buscarCarruajeAsignadoAUnUsuario(u);

        return repositorio_carruajeHeroe.getListaDeHeroes(c.getId());
    }

    @Override
    public void seleccionarHeroe(Long uid, Long hid) {
        Usuario u = repositorioUsuario.buscarUsuarioPorId(uid);
        Carruaje c = repositorioCarruaje.buscarCarruajeAsignadoAUnUsuario(u);
        Heroe h = repositorioHeroe.buscarHeroePorId(hid);

        if (u.getOro() < h.getPrecio()) {
            throw new ReclutaException("No tienes suficiente oro para reclutar este héroe.");
        }

        u.setOro(u.getOro() - h.getPrecio());
        repositorioUsuario.modificar(u);

        repositorio_carruajeHeroe.agregarRelacion(c, h);
    }


    @Override
    public void quitarHeroe(Long uid, Long hid) {
        Usuario u = repositorioUsuario.buscarUsuarioPorId(uid);
        Carruaje c = repositorioCarruaje.buscarCarruajeAsignadoAUnUsuario(u);
        Heroe h = repositorioHeroe.buscarHeroePorId(hid);

        u.setOro(u.getOro() + h.getPrecio());
        repositorioUsuario.modificar(u);

        CarruajeHeroe rel = repositorio_carruajeHeroe.buscarRelacion(c, h);
        if (rel != null) {
            repositorio_carruajeHeroe.removerRelacion(rel);
        }
    }


}

