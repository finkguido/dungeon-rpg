package com.tallerwebi.infraestructura;

import com.tallerwebi.config.HibernateConfig;
import com.tallerwebi.dominio.entidades.Inventario;
import com.tallerwebi.dominio.entidades.Item;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
        SpringWebTestConfig.class,
        HibernateTestConfig.class,
        HibernateConfig.class
})
public class RepositorioItemTest {

    @Autowired
    RepositorioItem repositorioItem;
    @Autowired
    RepositorioUsuario repositorioUsuario;
    @Autowired
    RepositorioInventario repositorioInventario;

    @Test
    public void queSeGuardeElItem() {
        Item item = new Item();
        repositorioItem.guardarItem(item);

        assertNotNull(item.getId());
    }

    @Test
    public void obtenerListaDeItemsPorInventario() {
        Inventario inventario = new Inventario();
        repositorioInventario.guardar(inventario);

        Usuario usuario = new Usuario();
        usuario.setOro(500);
        usuario.setEmail("test2@prueba.com");
        usuario.setPassword("1234");
        usuario.setInventario(inventario);

        repositorioUsuario.guardar(usuario);

        Item item1 = new Item();
        item1.setNombre("Espada");
        item1.setPrecio(150);
        item1.setInventario(inventario);

        Item item2 = new Item();
        item2.setNombre("Escudo");
        item2.setPrecio(120);
        item2.setInventario(inventario);

        repositorioItem.guardarItem(item1);
        repositorioItem.guardarItem(item2);

        List<Item> items = repositorioItem.obtenerLosItemsByInventario(inventario.getId());

        Map<Long, Item> itemsUnicos = new LinkedHashMap<>();
        for (Item i : items) {
            itemsUnicos.put(i.getId(), i);
        }
        List<Item> listaUnicos = new ArrayList<>(itemsUnicos.values());

        assertNotNull(listaUnicos);
        assertEquals(2, listaUnicos.size());
    }


    @Test
    public void buscarPorIdDevuelveElItemEsperado() {
        Inventario inventario = new Inventario();
        repositorioInventario.guardar(inventario);

        Item item = new Item();
        item.setNombre("Poción");
        item.setPrecio(100);
        item.setInventario(inventario);

        repositorioItem.guardarItem(item);

        assertNotNull(repositorioItem.buscarPorId(item.getId()));
        assertEquals("Poción", repositorioItem.buscarPorId(item.getId()).getNombre());
    }
}
