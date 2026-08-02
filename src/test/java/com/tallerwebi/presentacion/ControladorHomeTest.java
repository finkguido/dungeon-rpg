package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.servicios.ServicioJuego;
import com.tallerwebi.dominio.entidades.GameSession;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.infraestructura.RepositorioSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ControladorHomeTest {

    private ServicioJuego servicioJuegoMock;
    private RepositorioSession sessionRepoMock;
    private ControladorHome controladorHome;
    private HttpServletRequest requestMock;
    private HttpSession sessionMock;

    @BeforeEach
    public void setUp() {
        servicioJuegoMock = mock(ServicioJuego.class);
        sessionRepoMock = mock(RepositorioSession.class);
        controladorHome = new ControladorHome(servicioJuegoMock, sessionRepoMock);

        requestMock = mock(HttpServletRequest.class);
        sessionMock = mock(HttpSession.class);
    }

    @Test
    public void irAMiHome_usuarioNoLogueado_redireccionaAlLogin() {
        when(requestMock.getSession()).thenReturn(sessionMock);
        when(sessionMock.getAttribute("usuario")).thenReturn(null);

        ModelAndView mav = controladorHome.irAMiHome(requestMock);

        assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/login"));
    }

    @Test
    public void irAMiHome_usuarioConSesionActiva_retornaHomeConSesionActivaTrue() {
        Usuario usuario = new Usuario();
        when(requestMock.getSession()).thenReturn(sessionMock);
        when(sessionMock.getAttribute("usuario")).thenReturn(usuario);
        when(sessionRepoMock.findActive(usuario)).thenReturn(new GameSession());

        ModelAndView mav = controladorHome.irAMiHome(requestMock);

        assertThat(mav.getViewName(), equalToIgnoringCase("home"));
        ModelMap model = (ModelMap) mav.getModel();
        assertTrue((Boolean) model.get("tieneSesionActiva"));
    }

    @Test
    public void irAMiHome_usuarioSinSesionActiva_retornaHomeConSesionActivaFalse() {
        Usuario usuario = new Usuario();
        when(requestMock.getSession()).thenReturn(sessionMock);
        when(sessionMock.getAttribute("usuario")).thenReturn(usuario);
        when(sessionRepoMock.findActive(usuario)).thenReturn(null); // sin sesión activa

        ModelAndView mav = controladorHome.irAMiHome(requestMock);

        assertThat(mav.getViewName(), equalToIgnoringCase("home"));
        ModelMap model = (ModelMap) mav.getModel();
        assertFalse((Boolean) model.get("tieneSesionActiva"));
    }

    @Test
    public void irAMazmorra_retornaVistaMazmorra() {
        ModelAndView mav = controladorHome.irAMazmorra();
        assertThat(mav.getViewName(), equalToIgnoringCase("mazmorra"));
    }

    @Test
    public void irAreglas_retornaVistaReglas() {
        ModelAndView mav = controladorHome.irAreglas();
        assertThat(mav.getViewName(), equalToIgnoringCase("reglas"));
    }
}
