package com.tallerwebi.dominio.servicios.Impl;

import com.tallerwebi.dominio.servicios.ServicioJuego;
import com.tallerwebi.dominio.entidades.*;

import com.tallerwebi.dominio.servicios.ServicioSanatorio;
import com.tallerwebi.infraestructura.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicioSanatorioImpl implements ServicioSanatorio {

    private final Repositorio_carruajeHeroe repositorioCarruajeHeroe;
    private final RepositorioHeroe repositorioHeroe;
    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioCarruaje repositorioCarruaje;
    private final RepositorioSession sessionRepo;
    private final RepositorioHeroSession shRepo;
    private final ServicioJuego servicioJuego;


    @Autowired
    public ServicioSanatorioImpl(
            Repositorio_carruajeHeroe repositorioCarruajeHeroe,
            RepositorioHeroe repositorioHeroe,
            RepositorioUsuario repositorioUsuario, RepositorioCarruaje repositorioCarruaje, RepositorioSession sessionRepo, RepositorioHeroSession shRepo, ServicioJuego servicioJuego
    ) {
        this.repositorioCarruajeHeroe = repositorioCarruajeHeroe;
        this.repositorioHeroe = repositorioHeroe;
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioCarruaje = repositorioCarruaje;
        this.sessionRepo = sessionRepo;
        this.shRepo = shRepo;
        this.servicioJuego = servicioJuego;
    }

    @Override
    public List<Heroe> obtenerHeroesCurables(Usuario usuario) {
        return obtenerHeroesUsuario(usuario).stream()
                .filter(h -> h.getVidaActual() < h.getMaxVida())
                .collect(Collectors.toList());
    }

    @Override
    public List<Heroe> obtenerHeroesUsuario(Usuario usuario) {

        Carruaje carruaje = repositorioCarruaje.buscarCarruajeAsignadoAUnUsuario(usuario);
        if (carruaje == null) {
            return new ArrayList<>();
        }
        return repositorioCarruajeHeroe.getListaDeHeroes(carruaje.getId());
    }

    @Override
    public void curarHeroe(Usuario usuario, Long idHeroe) {
        Usuario usuarioBD = repositorioUsuario.buscarUsuarioPorId(usuario.getId());
        Heroe heroe = repositorioHeroe.buscarHeroePorId(idHeroe);

        if (heroe == null || usuarioBD == null) {
            throw new RuntimeException("Usuario o héroe no encontrados");
        }
        if (heroe.getVidaActual() >= heroe.getMaxVida()) {
            throw new RuntimeException("El héroe ya tiene la vida al máximo");
        }
        if (usuarioBD.getOro() < 50) {
            throw new RuntimeException("No tienes suficiente oro para curar al héroe");
        }

        heroe.setVidaActual(heroe.getMaxVida());
        usuarioBD.setOro(usuarioBD.getOro() - 50);

        repositorioHeroe.modificar(heroe);
        repositorioUsuario.modificar(usuarioBD);

        // ------- NUEVO: Actualiza también SessionHero si hay sesión activa -------
        GameSession session = sessionRepo.findActive(usuarioBD);
        if (session != null) {
            // Busca el SessionHero correspondiente
            SessionHero sh = shRepo.findBySession(session).stream()
                    .filter(h -> h.getHero().getId().equals(heroe.getId()))
                    .findFirst()
                    .orElse(null);
            if (sh != null) {
                sh.setVidaActual(heroe.getMaxVida());
                shRepo.update(sh);
            }
        }
    }

    @Override
    public Usuario obtenerUsuarioActualizado(Long id) {
        return repositorioUsuario.buscarUsuarioPorId(id);
    }

    @Override
    public boolean puedeAccederASantuario(Usuario usuario) {

        GameSession session = servicioJuego.getSession(usuario);

        if (session == null) {

            return true;
        }

        int nivel = session.getNivel();

        if (nivel == 2 || nivel == 3) {
            return false;
        }

        if (nivel == 1) {
            List<SessionMonster> monstruos = servicioJuego.getMonstruos(usuario);
            boolean huboCombate = monstruos.stream()
                    .anyMatch(m -> m.getVidaActual() < m.getMonster().getVida());
            if (huboCombate) {
                return false;
            }
        }

        return true;
    }


}
