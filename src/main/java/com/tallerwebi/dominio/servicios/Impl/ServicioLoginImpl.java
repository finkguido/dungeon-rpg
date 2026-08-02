package com.tallerwebi.dominio.servicios.impl;

import com.tallerwebi.config.PasswordHasher;
import com.tallerwebi.dominio.entidades.Inventario;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.servicios.ServicioLogin;
import com.tallerwebi.infraestructura.RepositorioInventario;
import com.tallerwebi.infraestructura.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service("servicioLogin")
@Transactional
public class ServicioLoginImpl implements ServicioLogin {

    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioInventario repositorioInventario;
    private final PasswordHasher passwordHasher;

    @Autowired
    public ServicioLoginImpl(RepositorioUsuario repositorioUsuario,
                             RepositorioInventario repositorioInventario,
                             PasswordHasher passwordHasher) {
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioInventario = repositorioInventario;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public Usuario consultarUsuario(String email, String password) {
        Usuario usuario = repositorioUsuario.buscar(email);
        if (usuario == null) {
            return null;
        }
        if (!passwordHasher.matches(password, usuario.getPassword())) {
            return null;
        }
        return usuario;
    }

    @Override
    public void registrar(Usuario usuario) throws UsuarioExistente {
        Usuario usuarioEncontrado = repositorioUsuario.buscar(usuario.getEmail());
        if (usuarioEncontrado != null) {
            throw new UsuarioExistente();
        }

        Inventario inventario = new Inventario();
        repositorioInventario.guardar(inventario);
        usuario.setInventario(inventario);
        usuario.setPassword(passwordHasher.hash(usuario.getPassword()));
        repositorioUsuario.guardar(usuario);
    }

    @Override
    public Usuario buscarUsuario(Long idUsuario) {
        return repositorioUsuario.buscarUsuarioPorId(idUsuario);
    }
}
