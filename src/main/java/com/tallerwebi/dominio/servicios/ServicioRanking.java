package com.tallerwebi.dominio.servicios;

import com.tallerwebi.dominio.entidades.Usuario;
import java.util.List;

public interface ServicioRanking {
    List<Usuario> obtenerRanking();
}