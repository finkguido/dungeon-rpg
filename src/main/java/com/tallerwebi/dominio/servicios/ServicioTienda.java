package com.tallerwebi.dominio.servicios;

import com.tallerwebi.dominio.entidades.Inventario;
import com.tallerwebi.dominio.entidades.Item;
import com.tallerwebi.dominio.entidades.Usuario;

import java.util.List;

public interface ServicioTienda {
    void comprar(Usuario usuario, Long id2);
    List<Item> obtenerItemsPorInventario(Long idInventario);

    List<Item> obtenerItemsDeUsuario(Usuario usuario);

    List<Item> obtenerTodosLosItems();

    List<Item> obtenerItemsDeTienda();
    //void sumarOro(Integer monto, Usuario usuario);

    void sumarOro(String paqueteOro, Usuario usuario);
}
