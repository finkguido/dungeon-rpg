package com.tallerwebi.dominio.servicios.Impl;

import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.infraestructura.RepositorioUsuario;
import com.tallerwebi.dominio.servicios.ServicioRanking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioRankingImpl implements ServicioRanking {

    private final RepositorioUsuario usuarioRepo;

    @Autowired
    public ServicioRankingImpl(RepositorioUsuario usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    @Override
    public List<Usuario> obtenerRanking() {
        return usuarioRepo.obtenerRankingJugadores();
    }
}