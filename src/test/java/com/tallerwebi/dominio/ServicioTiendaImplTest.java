package com.tallerwebi.dominio;

import com.tallerwebi.dominio.entidades.Inventario;
import com.tallerwebi.dominio.entidades.Item;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.infraestructura.RepositorioItem;
import com.tallerwebi.infraestructura.RepositorioUsuario;
import com.tallerwebi.dominio.servicios.Impl.ServicioTiendaImpl;
import com.tallerwebi.infraestructura.RepositorioInventario;
import com.tallerwebi.dominio.servicios.ServicioTienda;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ServicioTiendaImplTest {


    RepositorioItem repositorioItem = mock(RepositorioItem.class);
    RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
    RepositorioInventario repositorioInventario = mock(RepositorioInventario.class);
    ServicioTienda servicioTienda = new ServicioTiendaImpl(repositorioItem, repositorioUsuario, repositorioInventario);



    @Test
    public void siElUsuarioNoExisteAlComprarLanzarExcepcion() {

        Usuario usuarioSesion = new Usuario();
        usuarioSesion.setId(1L);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setOro(50);

        Item item = new Item();
        item.setPrecio(100);
        item.setId(1L);

       // when(repositorioUsuario.buscarUsuarioPorId(1L)).thenReturn(usuario); --> retorna nulo al mockearlo si esto esta comentado, el servicio lo toma y lanza la excepcion
        when(repositorioItem.buscarPorId(1L)).thenReturn(item);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            servicioTienda.comprar(usuarioSesion,item.getId());
        });

        assertThat(exception.getMessage(), equalTo("Usuario no encontrado"));
    }

    @Test
    public void siElItemNoExisteAlComprarLanzarExcepcion() {

        Usuario usuarioSesion = new Usuario();
        usuarioSesion.setId(1L);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setOro(50);

        Item item = new Item();
        item.setPrecio(100);
        item.setId(1L);

        when(repositorioUsuario.buscarUsuarioPorId(1L)).thenReturn(usuario);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            servicioTienda.comprar(usuarioSesion,item.getId());
        });

        assertThat(exception.getMessage(), equalTo("Item no encontrado"));
    }



    @Test
    public void siElUsuarioNoTieneOroSuficienteLanzarExcepcion() {

        Usuario usuarioSesion = new Usuario();
        usuarioSesion.setId(1L);

        Usuario usuarioDB = new Usuario();
        usuarioDB.setId(1L);
        usuarioDB.setOro(50);

        Item item = new Item();
        item.setPrecio(100);
        item.setId(1L);

        when(repositorioUsuario.buscarUsuarioPorId(1L)).thenReturn(usuarioDB);
        when(repositorioItem.buscarPorId(1L)).thenReturn(item);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            servicioTienda.comprar(usuarioSesion ,item.getId());
        });

        assertThat(exception.getMessage(), equalTo("No tenes suficiente oro!"));
    }

    @Test
    public void siElUsuarioTieneOroSuficienteComprarElItem() {
        Usuario usuarioSesion = new Usuario();
        usuarioSesion.setId(1L);

        Usuario usuarioDB = new Usuario();
        usuarioDB.setId(1L);
        usuarioDB.setOro(150);

        Inventario inventario = new Inventario();
        usuarioDB.setInventario(inventario);

        Item item = new Item();
        item.setId(1L);
        item.setNombre("Poción");
        item.setPrecio(100);

        when(repositorioUsuario.buscarUsuarioPorId(1L)).thenReturn(usuarioDB);
        when(repositorioItem.buscarPorId(1L)).thenReturn(item);

        servicioTienda.comprar(usuarioSesion, item.getId());

        assertThat(usuarioDB.getOro(), equalTo(50));

        ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);
        verify(repositorioItem).guardarItem(itemCaptor.capture());
        Item itemGuardado = itemCaptor.getValue();
        assertThat(itemGuardado.getNombre(), equalTo("Poción"));
        assertThat(itemGuardado.getPrecio(), equalTo(100));
        assertThat(itemGuardado.getInventario(), equalTo(inventario));
    }

}
