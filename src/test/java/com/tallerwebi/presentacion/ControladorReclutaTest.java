/*
package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.entidades.Carruaje;
import com.tallerwebi.dominio.entidades.Heroe;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.ReclutaException;
import com.tallerwebi.dominio.servicios.Impl.ServicioReclutaImpl;
import com.tallerwebi.dominio.servicios.ServicioRecluta;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import org.junit.jupiter.api.BeforeEach;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class ControladorReclutaTest {

    private ControladorReclutas controladorReclutas;
    private ServicioRecluta servicioReclutas;
    private ServicioJuego   servicioJuego;
    private HttpServletRequest requestMock;
    private HttpSession sessionMock;
    private Carruaje carruajeMock;
    private Usuario usuarioMock;
    private Heroe heroeMock1;
    private Heroe heroeMock2;
    private List<Heroe> listaDeHeroesEnCarruajeMock;
    private List<Heroe> listaDeSoloUnHeroeEnCarruajeMock;
    private static final String REDIRECT_VISTA_CARRUAJE = "redirect:/carruaje";
    private static final String VISTA_CARRUAJE = "vista_carruaje";
    private static final String VISTA_HEROES_OBTENIDOS = "vista_heroes_obtenidos";

    @BeforeEach
    public void preparacion() {
        servicioReclutas = mock(ServicioReclutaImpl.class);
        servicioJuego   = mock(ServicioJuego.class);
        controladorReclutas
                = new ControladorReclutas(servicioReclutas, servicioJuego);

        requestMock = mock(HttpServletRequest.class);
        sessionMock = mock(HttpSession.class);

        usuarioMock = new Usuario(1L,"admin@admin.com");

        carruajeMock = new Carruaje(1L,0,0,0);
        carruajeMock.setUsuario(usuarioMock);

        heroeMock1 = new Heroe(1L,"Cruzado",1,300,"/imagenes/cruzado.webp");
        heroeMock2 = new Heroe(2L,"Vestal",1,200,"/imagenes/Vestal.webp");
        listaDeHeroesEnCarruajeMock =  List.of(heroeMock1, heroeMock2);
        listaDeSoloUnHeroeEnCarruajeMock =  List.of( heroeMock2);

        // Por defecto simulamos que la sesión tiene un carruaje asociado
//        when(servicioReclutas.getUsuarioPorId(1L)).thenReturn(usuarioMock);

        when(servicioReclutas.registrarUnUsuario(usuarioMock)).thenReturn(usuarioMock);
        when(servicioReclutas.asignarOActualizarUnCarrujeAUnUsuario(usuarioMock.getId())).thenReturn(carruajeMock);

        when(requestMock.getSession()).thenReturn(sessionMock);
        when(sessionMock.getAttribute("carruaje")).thenReturn(carruajeMock);
        when(sessionMock.getAttribute("usuario")).thenReturn(usuarioMock);

    }

    @Test
    public void queAlIngresarPorPrimeraVesAlCarruaje_SeLeAsigneUnCarruajeNuevoAlUsuario() {

        when(servicioReclutas.getCarruajeDelUsuarioPorId(usuarioMock.getId())).thenReturn(carruajeMock);

        ModelAndView mav = controladorReclutas.mostrarCarruaje((HttpSession) requestMock);

        assertThat(mav.getViewName(), is("vista_carruaje"));
        assertThat(mav.getModel().get("carruaje"), instanceOf(Carruaje.class));
        assertThat(mav.getModel().get("carruaje"), notNullValue());

        assertThat(mav.getModel().get("usuario"), instanceOf(Usuario.class));
        assertThat(mav.getModel().get("usuario"), notNullValue());

    }
    /*
    @Test
    public void queAlIngresarPorPrimeraVesAlCarruaje_SeMuestre2HeroesEnCarruaje() {

        when(servicioReclutas.getCarruajeDelUsuarioPorId(usuarioMock.getId())).thenReturn(carruajeMock);

        when(servicioReclutas.getHeroesDisponiblesEnCarruaje(carruajeMock)).thenReturn(listaDeHeroesEnCarruajeMock);
        when(servicioReclutas.getHeroesObtenidosPorElUsuario(usuarioMock)).thenReturn(List.of());

        ModelAndView mav = controladorReclutas.mostrarCarruaje((HttpSession) requestMock);

        assertThat(mav.getViewName(), is("vista_carruaje"));
        assertThat(mav.getModel().get("carruaje"), instanceOf(Carruaje.class));
        assertThat(mav.getModel().get("heroesEnCarruaje"), instanceOf(List.class));
        assertThat( (List<?>) mav.getModel().get("heroesEnCarruaje") , hasSize(2) );
        assertThat(mav.getModel().get("heroesEnCarruaje"), instanceOf(List.class));

        List<Heroe> heroesObt = (List<Heroe>) mav.getModel().get("heroesEnCarruaje");
        assertThat(heroesObt.size(), equalTo(2));
        assertThat(heroesObt.get(0), instanceOf(Heroe.class));
        assertThat(heroesObt.get(0).getNombre(), equalTo("Cruzado"));
        assertThat(heroesObt.get(0).getUrlImagen(), equalTo("/imagenes/cruzado.webp"));
    }

    @Test
    public void queSePuedaRecluatarUnHeroeEnCarruaje() {

        when(servicioReclutas.asignarOActualizarUnCarrujeAUnUsuario(usuarioMock.getId())).thenReturn(carruajeMock);
        when(servicioReclutas.getHeroesDisponiblesEnCarruaje(carruajeMock)).thenReturn(listaDeSoloUnHeroeEnCarruajeMock);

        ModelAndView mav = controladorReclutas.reclutarHeroe(heroeMock1.getId(), requestMock);

        assertThat(mav.getViewName(), equalTo(VISTA_CARRUAJE));
        assertThat(mav.getModel().get("carruaje"), instanceOf(Carruaje.class));
        assertThat(mav.getModel().get("heroesEnCarruaje"), instanceOf(List.class));

        List<Heroe> heroesObt = (List<Heroe>) mav.getModel().get("heroesEnCarruaje");
        assertThat(heroesObt.size(), equalTo(1));
        verify(servicioReclutas).quitarUnHeroeDelCarruaje(heroeMock1.getId(), carruajeMock.getId());
        verify(servicioReclutas).agregarUnHeroeAlUsuario(heroeMock1.getId(), usuarioMock);
        assertThat(heroesObt.get(0), instanceOf(Heroe.class));
    }

    @Test
    public void queAlReclutarElUltimoHeroe_queMeLanceUnMensajeDeQueNoHayHeroesDisponiblesEnCarruaje() {

        when(servicioReclutas.asignarOActualizarUnCarrujeAUnUsuario(usuarioMock.getId())).thenReturn(carruajeMock);
        when(servicioReclutas.getHeroesDisponiblesEnCarruaje(carruajeMock))
                .thenThrow(new ReclutaException("No hay heroes en carruaje"));

        ModelAndView mav = controladorReclutas.reclutarHeroe(heroeMock1.getId(), requestMock);

        assertThat(mav.getViewName(), equalTo(VISTA_CARRUAJE));
        assertThat(mav.getModel().get("carruaje"), instanceOf(Carruaje.class));
        assertThat(mav.getModelMap().get("mensaje1"), equalTo("No hay heroes en carruaje"));

    }

    @Test
    public void queSiNoTieneHeroesObtenidos_MuestreUnMensaje() {

        when(servicioReclutas.getHeroesObtenidosPorElUsuario(usuarioMock))
                .thenThrow(new ReclutaException("Todavia obtuviste ningun heroe"));

        ModelAndView mav = controladorReclutas.mostrarHeroesObtenidos((HttpSession) requestMock);

        assertThat(mav.getViewName(), equalTo(VISTA_HEROES_OBTENIDOS));
        assertThat(mav.getModelMap().get("mensaje1"), equalTo("Todavia obtuviste ningun heroe"));

    }

    @Test
    public void queSePuedaMostrarLosHeroesQueTieneElUsuario() {

        when(servicioReclutas.asignarOActualizarUnCarrujeAUnUsuario(usuarioMock.getId())).thenReturn(carruajeMock);
        when(servicioReclutas.getHeroesObtenidosPorElUsuario(usuarioMock)).thenReturn(listaDeHeroesEnCarruajeMock);

        ModelAndView mav = controladorReclutas.mostrarHeroesObtenidos((HttpSession) requestMock);

        assertThat(mav.getViewName(), equalTo(VISTA_HEROES_OBTENIDOS));


        List<Heroe> heroesObt = (List<Heroe>) mav.getModel().get("heroes_obtenidos");
        assertThat(heroesObt.size(), equalTo(2));

    }


//    @Test
//    public void queSePuedaReclutarUnHeroe() {
//
////        when(servicioReclutas.quitarUnHeroeDelCarruaje(h1.getId(),carruajeMock)).thenReturn(true);
////        when(servicioReclutas.agregarUnHeroeAlUsuario(h1.getId(),usuarioMock)).thenReturn(true);
//
//        when(servicioReclutas.getListaDeHeroesEnCarruaje(carruajeMock)).thenReturn(listaDeHeroesEnCarruajeMock);
//
//        ModelAndView mav = controladorReclutas.reclutarHeroe(h1.getId(),requestMock);
//
//
//        assertThat(mav.getViewName(), is("vista_carruaje"));
//        assertThat(mav.getModel().containsKey("carruaje"), is(true));
//        assertThat(mav.getModel().get("carruaje"), instanceOf(Carruaje.class));
//        assertThat(mav.getModel().get("heroesEnCarruaje"), instanceOf(List.class));
//        assertThat( (List<?>) mav.getModel().get("heroesEnCarruaje") , hasSize(1) );
//
//        List<Heroe> heroesObt = (List<Heroe>) mav.getModel().get("heroesEnCarruaje");
//        assertThat(heroesObt.size(), is(1));
//    }


    // ─────────────────────────────────────────────────────────────────────────────
    // 1) mostrarCarruaje: con carruaje en sesión, debe recuperar datos desde el servicio
    // ─────────────────────────────────────────────────────────────────────────────

//    @Test
//    public void mostrarCarruaje_conCarruajeEnSesion_debeMostrarTodosLosAtributos() {
//
//        // Preparación: stub del servicio para que devuelva valores concretos
//
//        when(carruajeMock.getId()).thenReturn(1L);
//        when(carruajeMock.getSemana()).thenReturn(0);
//        when(carruajeMock.getNivel()).thenReturn(0);
//        when(carruajeMock.getCantidadDeHeroesSemanales()).thenReturn(2);
//
//        when(servicioReclutas.getCantidadDeHeroesDisponiblesEnCarruaje(carruajeMock)).thenReturn(2);
//        when(servicioReclutas.getCantidadDeHeroesAsignadosAUnUsuario(usuarioMock)).thenReturn(0);
//        // Ejecución
//        ModelAndView mav = controladorReclutas.mostrarCarruaje(requestMock);
//
//        // Validación
//        assertThat(mav.getViewName(), is("vista_carruaje"));
//
//        // Verificamos que el modelo contiene exactamente los atributos esperados
//        assertThat(mav.getModel().get("semana"), is(0));
//        assertThat(mav.getModel().get("nivel"), is(0));
//        assertThat(mav.getModel().get("heroesDisponibles"), is(2));
//        assertThat(mav.getModel().get("heroesSemanales"), is(2));
//        assertThat(mav.getModel().get("heroesAsignados"), is(0));
//
//        // Nos aseguramos de que se llamó a cada metodo del servicio exactamente una vez
//        verify(servicioReclutas, times(1)).getCantidadDeHeroesDisponiblesEnCarruaje(carruajeMock);
//        verify(servicioReclutas, times(1)).getCantidadDeHeroesAsignadosAUnUsuario(usuarioMock);
//    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 4) reclutarHeroe: con carruaje en sesión, debe invocar al servicio y recargar modelo
    // ─────────────────────────────────────────────────────────────────────────────
//
//    @Test
//    public void reclutarHeroe_conCarruajeEnSesion_debeLlamarServicioYRefrescarModelo() {
//        // Preparación: stub para el servicio
//        // Asumimos que el metodo que procesa el reclutamiento no retorna nada (void),
//        // pero sí actualiza el estado interno en el servicio.
//        doNothing().when(servicioReclutas).reclutarSegunIdHeroe(1L);
//
//        // Después de reclutar, el controladorReclutas volverá a consultar al servicio para poblar el modelo:
//        when(servicioReclutas.getNivelSegunCarruaje(carruajeMock)).thenReturn(0);
//        when(servicioReclutas.getNumeroDeSemanaSegunCarruaje(carruajeMock)).thenReturn(0);
//        when(servicioReclutas.getCantidadDeHeroesDisponiblesEnCarruaje(carruajeMock)).thenReturn(1);
//        when(servicioReclutas.getCantidadDeHeroesSemanalesSegunCarruaje(carruajeMock)).thenReturn(2);
//        when(servicioReclutas.getCantidadDeHeroesEnUsuarioHeroeSegunCarruaje(carruajeMock)).thenReturn(1);
//
//        // Ejecución
//        ModelAndView mav = controladorReclutas.reclutarHeroe(1L, requestMock);
//
//        // Validación de interacción con el servicio
//        verify(servicioReclutas, times(1)).reclutarSegunIdHeroe(1L);
//        verify(servicioReclutas, times(1)).getNivelSegunCarruaje(carruajeMock);
//        verify(servicioReclutas, times(1)).getNumeroDeSemanaSegunCarruaje(carruajeMock);
//        verify(servicioReclutas, times(1)).getCantidadDeHeroesDisponiblesEnCarruaje(carruajeMock);
//        verify(servicioReclutas, times(1)).getCantidadDeHeroesSemanalesSegunCarruaje(carruajeMock);
//        verify(servicioReclutas, times(1)).getCantidadDeHeroesEnUsuarioHeroeSegunCarruaje(carruajeMock);
//
//        // El controladorReclutas debe volver a la vista "carruaje"
//        assertThat(mav.getViewName(), is("carruaje"));
//
//        // Verificamos que el modelo incluye las llaves esperadas
//        assertThat(mav.getModel().get("nivel"), is(0));
//        assertThat(mav.getModel().get("semana"), is(0));
//        assertThat(mav.getModel().get("heroesDisponibles"), is(1));
//        assertThat(mav.getModel().get("heroesSemanales"), is(2));
//        assertThat(mav.getModel().get("heroesAsignados"), is(1));
//    }
//
//    // ─────────────────────────────────────────────────────────────────────────────
//    // 5) reclutarHeroe: si el servicio lanza excepción, debe mostrar mensaje de error
//    // ─────────────────────────────────────────────────────────────────────────────
//
//    @Test
//    public void reclutarHeroe_conErrorEnServicio_debeAtraparExcepcionYMostrarErrorEnVista() {
//        // Preparación: el servicio arroja RuntimeException al intentar reclutar
//        doThrow(new RuntimeException("Error interno")).when(servicioReclutas).reclutarSegunIdHeroe(42L);
//
//        // Aun así, el controladorReclutas intentará volver a cargar los datos (aunque podrían omitirse si falla)
//        when(servicioReclutas.getNivelSegunCarruaje(carruajeMock)).thenReturn(0);
//        when(servicioReclutas.getNumeroDeSemanaSegunCarruaje(carruajeMock)).thenReturn(0);
//        when(servicioReclutas.getCantidadDeHeroesDisponiblesEnCarruaje(carruajeMock)).thenReturn(2);
//        when(servicioReclutas.getCantidadDeHeroesSemanalesSegunCarruaje(carruajeMock)).thenReturn(2);
//        when(servicioReclutas.getCantidadDeHeroesEnUsuarioHeroeSegunCarruaje(carruajeMock)).thenReturn(0);
//
//        // Ejecución
//        ModelAndView mav = controladorReclutas.reclutarHeroe(42L, requestMock);
//
//        // Validación: aun frente a excepción, el controladorReclutas devuelve la vista "carruaje"
//        assertThat(mav.getViewName(), is("carruaje"));
//
//        // Verificamos que se capturó el error en el modelo
//        assertThat(
//                mav.getModel().get("error").toString(),
//                equalToIgnoringCase("Error al reclutar héroe")
//        );
//
//        // También se recargan las demás propiedades para la vista
//        assertThat(mav.getModel().get("nivel"), is(0));
//        assertThat(mav.getModel().get("semana"), is(0));
//        assertThat(mav.getModel().get("heroesDisponibles"), is(2));
//        assertThat(mav.getModel().get("heroesSemanales"), is(2));
//        assertThat(mav.getModel().get("heroesAsignados"), is(0));
//
//        verify(servicioReclutas, times(1)).reclutarSegunIdHeroe(42L);
//    }
//
//    // ─────────────────────────────────────────────────────────────────────────────
//    // 6) aumentarNivel: sin carruaje en sesión, debe redirigir a login
//    // ─────────────────────────────────────────────────────────────────────────────
//
//    @Test
//    public void aumentarNivel_sinCarruajeEnSesion_debeRedirigirALogin() {
//        // Preparación: no ponemos carruaje en sesión
//        when(sessionMock.getAttribute("carruaje")).thenReturn(null);
//
//        // Ejecución
//        ModelAndView mav = controladorReclutas.aumentarNivel(requestMock);
//
//        // Validación
//        assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/login"));
//        verify(servicioReclutas, never()).aumentarNivel(any());
//    }
//
//    // ─────────────────────────────────────────────────────────────────────────────
//    // 7) aumentarNivel: con carruaje en sesión, debe invocar servicio y recargar modelo
//    // ─────────────────────────────────────────────────────────────────────────────
//
//    @Test
//    public void aumentarNivel_conCarruajeEnSesion_debeLlamarServicioYRefrescarModelo() {
//        // Preparación: el servicio no lanza excepción al aumentar nivel
//        doNothing().when(servicioReclutas).aumentarNivel(carruajeMock);
//
//        // Después de aumentar nivel, el controladorReclutas debe volver a consultar el estado actualizado
//        when(servicioReclutas.getNivelSegunCarruaje(carruajeMock)).thenReturn(2);
//        when(servicioReclutas.getNumeroDeSemanaSegunCarruaje(carruajeMock)).thenReturn(1);
//        when(servicioReclutas.getCantidadDeHeroesDisponiblesEnCarruaje(carruajeMock)).thenReturn(5);
//        when(servicioReclutas.getCantidadDeHeroesSemanalesSegunCarruaje(carruajeMock)).thenReturn(4);
//        when(servicioReclutas.getCantidadDeHeroesEnUsuarioHeroeSegunCarruaje(carruajeMock)).thenReturn(1);
//
//        // Ejecución
//        ModelAndView mav = controladorReclutas.aumentarNivel(requestMock);
//
//        // Validación de interacción
//        verify(servicioReclutas, times(1)).aumentarNivel(carruajeMock);
//        verify(servicioReclutas, times(1)).getNivelSegunCarruaje(carruajeMock);
//        verify(servicioReclutas, times(1)).getNumeroDeSemanaSegunCarruaje(carruajeMock);
//        verify(servicioReclutas, times(1)).getCantidadDeHeroesDisponiblesEnCarruaje(carruajeMock);
//        verify(servicioReclutas, times(1)).getCantidadDeHeroesSemanalesSegunCarruaje(carruajeMock);
//        verify(servicioReclutas, times(1)).getCantidadDeHeroesEnUsuarioHeroeSegunCarruaje(carruajeMock);
//
//        // La vista vuelve a ser "carruaje"
//        assertThat(mav.getViewName(), is("carruaje"));
//
//        // El modelo debe contener los valores actualizados
//        assertThat(mav.getModel().get("nivel"), is(2));
//        assertThat(mav.getModel().get("semana"), is(1));
//        assertThat(mav.getModel().get("heroesDisponibles"), is(5));
//        assertThat(mav.getModel().get("heroesSemanales"), is(4));
//        assertThat(mav.getModel().get("heroesAsignados"), is(1));
//    }
//
//    // ─────────────────────────────────────────────────────────────────────────────
//    // 8) pasarSemana: sin carruaje en sesión, debe redirigir a login
//    // ─────────────────────────────────────────────────────────────────────────────
//
//    @Test
//    public void pasarSemana_sinCarruajeEnSesion_debeRedirigirALogin() {
//        // Preparación: simulamos sesión sin carruaje
//        when(sessionMock.getAttribute("carruaje")).thenReturn(null);
//
//        // Ejecución
//        ModelAndView mav = controladorReclutas.pasarSemana(requestMock);
//
//        // Validación
//        assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/login"));
//        verify(servicioReclutas, never()).pasarSemana(any());
//    }
//
//    // ─────────────────────────────────────────────────────────────────────────────
//    // 9) pasarSemana: con carruaje en sesión, debe invocar servicio y recargar modelo
//    // ─────────────────────────────────────────────────────────────────────────────
//
//    @Test
//    public void pasarSemana_conCarruajeEnSesion_debeLlamarServicioYRefrescarModelo() {
//        // Preparación: el servicio no arroja excepción
//        doNothing().when(servicioReclutas).pasarSemana(carruajeMock);
//
//        // Luego de pasar semana, el controladorReclutas vuelve a preguntar por el estado
//        when(servicioReclutas.getNivelSegunCarruaje(carruajeMock)).thenReturn(1);
//        when(servicioReclutas.getNumeroDeSemanaSegunCarruaje(carruajeMock)).thenReturn(2);
//        when(servicioReclutas.getCantidadDeHeroesDisponiblesEnCarruaje(carruajeMock)).thenReturn(6);
//        when(servicioReclutas.getCantidadDeHeroesSemanalesSegunCarruaje(carruajeMock)).thenReturn(3);
//        when(servicioReclutas.getCantidadDeHeroesEnUsuarioHeroeSegunCarruaje(carruajeMock)).thenReturn(2);
//
//        // Ejecución
//        ModelAndView mav = controladorReclutas.pasarSemana(requestMock);
//
//        // Verificamos que el servicio fue invocado
//        verify(servicioReclutas, times(1)).pasarSemana(carruajeMock);
//        verify(servicioReclutas, times(1)).getNivelSegunCarruaje(carruajeMock);
//        verify(servicioReclutas, times(1)).getNumeroDeSemanaSegunCarruaje(carruajeMock);
//        verify(servicioReclutas, times(1)).getCantidadDeHeroesDisponiblesEnCarruaje(carruajeMock);
//        verify(servicioReclutas, times(1)).getCantidadDeHeroesSemanalesSegunCarruaje(carruajeMock);
//        verify(servicioReclutas, times(1)).getCantidadDeHeroesEnUsuarioHeroeSegunCarruaje(carruajeMock);
//
//        // El controladorReclutas debe devolver la vista "carruaje"
//        assertThat(mav.getViewName(), is("carruaje"));
//
//        // Verificamos que el modelo incluye los datos de la semana recién pasada
//        assertThat(mav.getModel().get("nivel"), is(1));
//        assertThat(mav.getModel().get("semana"), is(2));
//        assertThat(mav.getModel().get("heroesDisponibles"), is(6));
//        assertThat(mav.getModel().get("heroesSemanales"), is(3));
//        assertThat(mav.getModel().get("heroesAsignados"), is(2));
//    }
}
*/