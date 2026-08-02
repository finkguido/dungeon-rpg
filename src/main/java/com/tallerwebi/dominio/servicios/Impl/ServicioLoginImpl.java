package com.tallerwebi.dominio.servicios.Impl;

import com.tallerwebi.dominio.entidades.Inventario;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.infraestructura.RepositorioInventario;
import com.tallerwebi.infraestructura.RepositorioUsuario;
import com.tallerwebi.dominio.servicios.ServicioLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service("servicioLogin")
@Transactional
public class ServicioLoginImpl implements ServicioLogin {

    private RepositorioUsuario repositorioUsuario;

    @Autowired
    public ServicioLoginImpl(RepositorioUsuario repositorioUsuario){
        this.repositorioUsuario = repositorioUsuario;
    }

    @Autowired
    private RepositorioInventario repositorioInventario;

    @Override
    public Usuario consultarUsuario (String email, String password) {
        return repositorioUsuario.buscarUsuario(email, password);
    }

    @Override
    public void registrar(Usuario usuario) throws UsuarioExistente {
        Usuario usuarioEncontrado = repositorioUsuario.buscarUsuario(usuario.getEmail(), usuario.getPassword());
        if(usuarioEncontrado != null){
            throw new UsuarioExistente();
        }
        Inventario inventario = new Inventario();
        repositorioInventario.guardar(inventario);
        usuario.setInventario(inventario);
        repositorioUsuario.guardar(usuario);
    }
    @Override
    public Usuario buscarUsuario(Long idUsuario) {
        return repositorioUsuario.buscarUsuarioPorId(idUsuario);
    }

}

