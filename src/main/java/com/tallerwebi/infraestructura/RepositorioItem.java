package com.tallerwebi.infraestructura;
import com.tallerwebi.dominio.entidades.Item;

import java.util.List;

public interface RepositorioItem {
    List<Item> obtenerTodosLosItems();
    Item buscarPorId(Long id);

    void eliminarItem(Item item);

    void guardarItem(Item item);

    List<Item> obtenerLosItemsByInventario(Long idInventario);

    List<Item> obtenerItemsSinInventario();
}
