package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.entidades.Carruaje;

import java.util.List;

public interface RepositorioCarruaje {
    Carruaje buscarCarruajePorId(Long id);

    Carruaje guardar(Carruaje carruaje);

    Carruaje buscarCarruajeAsignadoAUnUsuario(Usuario usuarioBuscado);

    Carruaje asignarUsuarioAUnCarruje(Carruaje carruaje, Usuario usuarioBuscado);

    List<Carruaje> getListaDeCarruajes();
}
