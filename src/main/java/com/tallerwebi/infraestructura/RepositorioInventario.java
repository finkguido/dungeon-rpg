package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.entidades.Inventario;

public interface RepositorioInventario {
    void guardar(Inventario inventario);

    Inventario buscarPorId(Long inventarioId);
}
