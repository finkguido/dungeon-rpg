/*
package com.tallerwebi.dominio;
import com.tallerwebi.dominio.entidades.*;
import com.tallerwebi.dominio.excepcion.ReclutaException;
import com.tallerwebi.dominio.interfaces.*;
import com.tallerwebi.dominio.servicios.Impl.ServicioReclutaImpl;
import com.tallerwebi.dominio.servicios.ServicioRecluta;
import com.tallerwebi.infraestructura.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


public class ServicioReclutaTest {

    private ServicioRecluta servicioReclutas;
    private RepositorioHeroe repositorioHeroe;
    private RepositorioCarruaje repositorioCarruaje;
    private RepositorioUsuario repositorioUsuario;
    private Repositorio_carruajeHeroe repositorio_carruajeHeroe;
    private Repositorio_usuarioHeroe repositorio_usuarioHeroe;

    private Usuario usuarioMock;
    private Carruaje carruajeMock;
    private Heroe heroe1;
    private Heroe heroe2;
    private List<Heroe> listaDeHeroesMock;
    private CarruajeHeroe carruajeHeroe1Mock;
    private UsuarioHeroe usuarioHeroe1Mock;

    @BeforeEach
    public void init() {

        repositorioCarruaje = Mockito.mock(RepositorioCarruajeImpl.class);
        repositorioHeroe = Mockito.mock(RepositorioHeroeImpl.class);
        repositorioUsuario = Mockito.mock(RepositorioUsuarioImpl.class);
        repositorio_carruajeHeroe = Mockito.mock(Repositorio_carruajeHeroeImpl.class);
        repositorio_usuarioHeroe = Mockito.mock(Repositorio_usuarioHeroeImpl.class);

        // Configuración inicial
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setEmail("admin@admin.com");

        carruajeMock = new Carruaje();
        carruajeMock.setId(1L);
        carruajeMock.setNivel(0);
        carruajeMock.setSemana(0);
        carruajeMock.setCantidadDeHeroesSemanales(2);
        carruajeMock.setUsuario(usuarioMock);

        carruajeHeroe1Mock = new CarruajeHeroe();
        carruajeHeroe1Mock.setId(1L);
        carruajeHeroe1Mock.setHeroe(heroe1);
        carruajeHeroe1Mock.setCarruaje(carruajeMock);

        usuarioHeroe1Mock = new UsuarioHeroe();
        usuarioHeroe1Mock.setId(1L);
        usuarioHeroe1Mock.setHeroe(heroe1);
        usuarioHeroe1Mock.setUsuario(usuarioMock);

        servicioReclutas = new ServicioReclutaImpl(
                repositorioCarruaje,repositorioHeroe,repositorioUsuario,
                repositorio_carruajeHeroe,repositorio_usuarioHeroe);

        heroe1 = new Heroe(1L, "Cruzado",1, 300, "/imagenes/cruzado.webp");
        heroe2 = new Heroe(2L, "Vestal",1, 200, "/imagenes/Vestal.webp");
        listaDeHeroesMock = List.of(heroe1, heroe2);

    }

    @Test
    public void queLanceExcepcionSiElCarruajeGuardadoTieneIdNulo() {
        // Configuración
        when(repositorioUsuario.buscarUsuarioPorId(usuarioMock.getId())).thenReturn(usuarioMock);
        when(repositorioCarruaje.buscarCarruajeAsignadoAUnUsuario(usuarioMock)).thenReturn(null);
        when(repositorioCarruaje.guardar(any(Carruaje.class))).thenReturn(new Carruaje(null,1,0,2)); // id es null

        // Ejecución y Verificación
        Exception exception = assertThrows(RuntimeException.class, () -> {
            servicioReclutas.asignarOActualizarUnCarrujeAUnUsuario(usuarioMock.getId());
        });
        assertThat(exception.getMessage(), equalTo("El carruaje tiene id nulo"));
    }

    @Test
    public void queLanceExcepcionSiElUsuarioNoExisteAlAsignarCarruaje() {
        // Configuración
        when(repositorioUsuario.buscarUsuarioPorId(999L)).thenReturn(null);

        // Ejecución y Verificación
        Exception exception = assertThrows(RuntimeException.class, () -> {
            servicioReclutas.asignarOActualizarUnCarrujeAUnUsuario(999L);
        });
        assertThat(exception.getMessage(), equalTo("No se encontro usuario"));
    }

    @Test
    public void queLanceExcepcionSiElCarruajeNoExisteAlObtenerHeroes() {
        // Configuración
        when(repositorioCarruaje.buscarCarruajePorId(carruajeMock.getId())).thenReturn(null);

        // Ejecución y Verificación
        Exception exception = assertThrows(RuntimeException.class, () -> {
            servicioReclutas.getHeroesDisponiblesEnCarruaje(carruajeMock);
        });
        assertThat(exception.getMessage(), equalTo("No se encontro el carruaje"));
    }

    @Test
    public void queNoFalleConCamposNulosEnCarruajeAlAsignar() {
        // Configuración
        Carruaje carruajeConNulos = new Carruaje(); // Campos por defecto null
        when(repositorioUsuario.buscarUsuarioPorId(usuarioMock.getId())).thenReturn(usuarioMock);
        when(repositorioCarruaje.buscarCarruajeAsignadoAUnUsuario(usuarioMock)).thenReturn(null);
        when(repositorioCarruaje.guardar(any(Carruaje.class))).thenReturn(carruajeMock); // Devuelve un carruaje válido
        when(repositorioHeroe.getListaDeHeroes()).thenReturn(listaDeHeroesMock);

        // Ejecución
        Carruaje carruaje = servicioReclutas.asignarOActualizarUnCarrujeAUnUsuario(usuarioMock.getId());

        // Verificación
        assertThat(carruaje, notNullValue());
        assertThat(carruaje.getNivel(), equalTo(0)); // Verifica que se seteen valores por defecto
        verify(repositorioCarruaje).guardar(any(Carruaje.class));
    }

    //Me quede en hacer mas test de carruaje y si no hay mas entonces hacer el siguiente
    //Ya hice dos test en repositorioCarruajeTest

    @Test
    public void queSePuedaAsignarUnCarruaje_AUnUsuarioNuevo() {

        // Configuración
        when(repositorioUsuario.buscarUsuarioPorId(usuarioMock.getId())).thenReturn(usuarioMock);
        when(repositorioCarruaje.buscarCarruajeAsignadoAUnUsuario(usuarioMock)).thenReturn(null);
        when(repositorioCarruaje.guardar(any(Carruaje.class))).thenReturn(carruajeMock);
        when(repositorioHeroe.getListaDeHeroes()).thenReturn(listaDeHeroesMock);

        // Ejecución
        Carruaje carruaje  = servicioReclutas.asignarOActualizarUnCarrujeAUnUsuario(usuarioMock.getId());

        // Verificación
        assertThat(carruaje,notNullValue());
        assertThat(carruaje.getNivel(),equalTo(0));
        verify(repositorioCarruaje).guardar(any(Carruaje.class));
        verify(repositorioCarruaje).asignarUsuarioAUnCarruje(any(Carruaje.class),any(Usuario.class));
    }

    @Test
    public void queSePuedaAsignarUnCarruajeCon2Heroes_AUnUsuarioNuevo() {

        // Configuración
        when(repositorioUsuario.buscarUsuarioPorId(usuarioMock.getId())).thenReturn(usuarioMock);
        when(repositorioCarruaje.buscarCarruajePorId(carruajeMock.getId())).thenReturn(carruajeMock);

        when(repositorioCarruaje.buscarCarruajeAsignadoAUnUsuario(usuarioMock)).thenReturn(null);
        when(repositorioCarruaje.guardar(any(Carruaje.class))).thenReturn(carruajeMock);
        when(repositorioHeroe.getListaDeHeroes()).thenReturn(listaDeHeroesMock);
        when(repositorio_carruajeHeroe.getListaDeHeroes(carruajeMock.getId())).thenReturn(listaDeHeroesMock);

        // Ejecución
        Carruaje carruaje  = servicioReclutas.asignarOActualizarUnCarrujeAUnUsuario(usuarioMock.getId());
        List<Heroe> listaObtenida = servicioReclutas.getHeroesDisponiblesEnCarruaje(carruaje);

        // Verificación
        assertThat(carruaje,notNullValue());
        assertThat(carruaje.getId(),equalTo(1L));
        assertThat(carruaje.getNivel(),equalTo(0));
        assertThat(listaObtenida.size(),equalTo(2));

        verify(repositorioCarruaje).guardar(any(Carruaje.class));
        verify(repositorioCarruaje).asignarUsuarioAUnCarruje(any(Carruaje.class),any(Usuario.class));
    }

    @Test
    public void queNoSePuedaAsignarUnCarruaje_AUnUsuarioQueYaTieneCarruaje() {

        // Configuración
        when(repositorioUsuario.buscarUsuarioPorId(usuarioMock.getId())).thenReturn(usuarioMock);
        when(repositorioCarruaje.buscarCarruajeAsignadoAUnUsuario(usuarioMock)).thenReturn(carruajeMock);

        // Ejecución
        servicioReclutas.asignarOActualizarUnCarrujeAUnUsuario(usuarioMock.getId());

        // Verificación
        verify(repositorioCarruaje,never()).guardar(any(Carruaje.class));
        verify(repositorioCarruaje,never()).asignarUsuarioAUnCarruje(any(Carruaje.class),any(Usuario.class));
    }

    @Test
    public void queSePuedaAgregarLaRelacionEntreUnCarruajeYUnHeroe() {
        // Configuración
        List<Heroe> heroesEnCarruaje = List.of(heroe1,heroe2);

        when(repositorioHeroe.buscarHeroePorId(heroe1.getId())).thenReturn(heroe1);
        when(repositorioCarruaje.buscarCarruajePorId(carruajeMock.getId())).thenReturn(carruajeMock);
        when(repositorio_carruajeHeroe.buscarRelacion(carruajeMock,heroe1)).thenReturn(null);

        // Ejecución
        servicioReclutas.setRelacionEntreCarruajeYHeroe(carruajeMock.getId(),heroe1.getId());

        // Verificación
        verify(repositorio_carruajeHeroe).buscarRelacion(carruajeMock,heroe1);
    }




    // -------------------- Tests para getHeroesDisponiblesEnCarruaje --------------------
    @Test
    public void queSePuedanObtenerLosHeroesDisponiblesEnElCarruaje() {
        // Configuración
        List<Heroe> heroesEnCarruaje = List.of(heroe1,heroe2);

        when(repositorioCarruaje.buscarCarruajePorId(carruajeMock.getId())).thenReturn(carruajeMock);
        when(repositorio_carruajeHeroe.getListaDeHeroes(carruajeMock.getId())).thenReturn(heroesEnCarruaje);

        // Ejecución
        List<Heroe> resultado = servicioReclutas.getHeroesDisponiblesEnCarruaje(carruajeMock);

        // Verificación
        assertThat(resultado, hasSize(2));
        assertThat(resultado, containsInAnyOrder(heroe1, heroe2));
    }

    @Test
    public void queSePuedanObtenerLosHeroesDisponiblesEnElUsuario() {
        // Configuración
        List<Heroe> heroesEnCarruaje = List.of(heroe1,heroe2);

        when(repositorioUsuario.buscarUsuarioPorId(usuarioMock.getId())).thenReturn(usuarioMock);
        when(repositorio_usuarioHeroe.getListaDeHeroes(usuarioMock.getId())).thenReturn(heroesEnCarruaje);

        // Ejecución
        List<Heroe> resultado = servicioReclutas.getHeroesDisponiblesEnUsuario(usuarioMock.getId());

        // Verificación
        assertThat(resultado.size(), equalTo(2));
        assertThat(resultado, containsInAnyOrder(heroe1, heroe2));
    }



    @Test
    public void queDevuelvaUnaExcepcion_SiNoHayHeroesEnElCarruaje() {
        // Configuración
        List<Heroe> heroesEnCarruaje = List.of();
        when(repositorioCarruaje.buscarCarruajePorId(carruajeMock.getId())).thenReturn(carruajeMock);
        when(repositorio_carruajeHeroe.getListaDeHeroes(carruajeMock.getId())).thenReturn(heroesEnCarruaje);

        // Ejecución y Verificación
        Exception exception = assertThrows(RuntimeException.class, () -> {
                                servicioReclutas.getHeroesDisponiblesEnCarruaje(carruajeMock);
                            });

        // Verificación
        assertThat(exception.getClass(), equalTo(ReclutaException.class));
        assertThat(exception.getMessage(),equalTo("No hay heroes disponibles por hoy"));
    }
    // -------------------- Tests para quitarUnHeroeDelCarruaje --------------------
    @Test
    public void queSePuedaQuitarUnHeroeDelCarruaje() {

        //Preparacion
        when(repositorioHeroe.buscarHeroePorId(heroe1.getId())).thenReturn(heroe1);
        when(repositorioCarruaje.buscarCarruajePorId(carruajeMock.getId())).thenReturn(carruajeMock);
        when(repositorio_carruajeHeroe.buscarRelacion(carruajeMock,heroe1)).thenReturn(carruajeHeroe1Mock);

        //Ejecucion
        servicioReclutas.quitarUnHeroeDelCarruaje(heroe1.getId(), carruajeMock.getId());

        //Verificacion
        verify(repositorio_carruajeHeroe).removerRelacion(carruajeHeroe1Mock);
    }
    @Test
    public void queLanceUnaExcepcion_AlQuitarHeroeInexistenteDelCarruaje() {
        // Configuración

        when(repositorioHeroe.buscarHeroePorId(heroe1.getId())).thenReturn(heroe1);
        when(repositorioCarruaje.buscarCarruajePorId(carruajeMock.getId())).thenReturn(carruajeMock);

        when(repositorio_carruajeHeroe.buscarRelacion(carruajeMock,heroe1)).thenReturn(null);

        // Ejecución y Verificación
        Exception e = assertThrows(RuntimeException.class, () -> {
            servicioReclutas.quitarUnHeroeDelCarruaje(heroe1.getId(), carruajeMock.getId());
        });

        // Verificar que no se modificó el carruaje
        assertThat(e.getMessage(),equalTo("No se encontro heroe en este carruaje"));
        verify(repositorio_carruajeHeroe,never()).removerRelacion(carruajeHeroe1Mock);
    }

    // -------------------- Tests para agregarUnHeroeAlUsuario --------------------
    @Test
    public void queSePuedaAgregarUnHeroeAlUsuario() {
        // Configuración

        when(repositorioHeroe.buscarHeroePorId(heroe1.getId())).thenReturn(heroe1);
        when(repositorioUsuario.buscarUsuarioPorId(usuarioMock.getId())).thenReturn(usuarioMock);
        when(repositorio_usuarioHeroe.buscarRelacion(usuarioMock,heroe1)).thenReturn(null);
        when(repositorio_usuarioHeroe.getListaDeHeroes(usuarioMock.getId())).thenReturn(List.of(heroe1));

        // Ejecución

        servicioReclutas.agregarUnHeroeAlUsuario(heroe1.getId(), usuarioMock);

        // Verificación
        verify(repositorio_usuarioHeroe).agregarRelacion(usuarioMock,heroe1);
        assertThat(servicioReclutas.getHeroesObtenidosPorElUsuario(usuarioMock), instanceOf(List.class));
        assertThat(servicioReclutas.getHeroesObtenidosPorElUsuario(usuarioMock).size(),equalTo(1));
    }

    @Test
    public void queLanceUnaExcepcion_AlAgregarHeroeQueYaTieneElUsuario() {

        // Configuración
        when(repositorioHeroe.buscarHeroePorId(heroe1.getId())).thenReturn(heroe1);
        when(repositorioUsuario.buscarUsuarioPorId(usuarioMock.getId())).thenReturn(usuarioMock);
        when(repositorio_usuarioHeroe.buscarRelacion(usuarioMock, heroe1)).thenReturn(usuarioHeroe1Mock);

        // Ejecución y Verificación
        Exception e = assertThrows(RuntimeException.class, () -> {
            servicioReclutas.agregarUnHeroeAlUsuario(heroe1.getId(), usuarioMock);
        });

        // Verificar
        assertThat(e.getMessage(), equalTo("Este heroe ya existe en el usuario"));
        verify(repositorio_usuarioHeroe, never()).agregarRelacion(usuarioMock, heroe1);
    }

    //
//    // -------------------- Tests para reclutarHeroeCompleto --------------------
    @Test
    public void queSePuedaRecluatarUnHeroeDelCarruaje() {
        // Configuración

        when(repositorioHeroe.buscarHeroePorId(heroe1.getId())).thenReturn(heroe1);
        when(repositorioUsuario.buscarUsuarioPorId(usuarioMock.getId())).thenReturn(usuarioMock);

        when(repositorioCarruaje.buscarCarruajePorId(carruajeMock.getId())).thenReturn(carruajeMock);

        when(repositorio_carruajeHeroe.buscarRelacion(carruajeMock,heroe1)).thenReturn(carruajeHeroe1Mock);
        when(repositorio_usuarioHeroe.buscarRelacion(usuarioMock,heroe1)).thenReturn(null);

        when(repositorio_carruajeHeroe.getListaDeHeroes(carruajeMock.getId())).thenReturn(List.of(heroe1));

        // Ejecución
        servicioReclutas.quitarUnHeroeDelCarruaje(heroe1.getId(), carruajeMock.getId());
        servicioReclutas.agregarUnHeroeAlUsuario(heroe1.getId(),usuarioMock);

        //Verificacion
        verify(repositorio_carruajeHeroe).removerRelacion(carruajeHeroe1Mock);
        verify(repositorio_usuarioHeroe).agregarRelacion(usuarioMock,heroe1);
        assertThat(servicioReclutas.getListaDeHeroesEnCarruaje(carruajeMock.getId()), instanceOf(List.class));
        assertThat(servicioReclutas.getListaDeHeroesEnCarruaje(carruajeMock.getId()).size(),equalTo(1));
    }


    // ─────────────────────────────────────────────────────────────────────────────
// Tests de nivel del carruaje y héroes semanales
// ─────────────────────────────────────────────────────────────────────────────

    @Test
    public void dadoNivel0_cuandoConsultoHeroesSemanales_entoncesDevuelve2() {
        Carruaje carruaje = new Carruaje();

        assertThat(carruaje.getNivel(), is(0));
        assertThat(carruaje.getCantidadDeHeroesSemanales(), is(2));
    }

    @Test
    public void dadoNivel1_cuandoConsultoHeroesSemanales_entoncesDevuelve3() {
        Carruaje carruaje = new Carruaje();
        carruaje.aumentarNivel(); // nivel = 1

        assertThat(carruaje.getNivel(), is(1));
        assertThat(carruaje.getCantidadDeHeroesSemanales(), is(3));
    }

    @Test
    public void dadoNivel2_cuandoConsultoHeroesSemanales_entoncesDevuelve4() {
        Carruaje carruaje = new Carruaje();
        carruaje.aumentarNivel(); // 1
        carruaje.aumentarNivel(); // 2

        assertThat(carruaje.getNivel(), is(2));
        assertThat(carruaje.getCantidadDeHeroesSemanales(), is(4));
    }

    @Test
    public void dadoNivel3_cuandoConsultoHeroesSemanales_entoncesDevuelve5() {
        Carruaje carruaje = new Carruaje();
        carruaje.aumentarNivel(); // 1
        carruaje.aumentarNivel(); // 2
        carruaje.aumentarNivel(); // 3

        assertThat(carruaje.getNivel(), is(3));
        assertThat(carruaje.getCantidadDeHeroesSemanales(), is(5));
    }

}
*/