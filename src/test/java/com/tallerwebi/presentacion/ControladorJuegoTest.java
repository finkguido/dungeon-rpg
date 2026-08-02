package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.entidades.*;
import com.tallerwebi.dominio.servicios.ServicioJuego;
import com.tallerwebi.dominio.servicios.ServicioLogin;
import com.tallerwebi.infraestructura.RepositorioExpedition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.Mockito.*;

public class ControladorJuegoTest {

    private ServicioJuego servicioJuegoMock;
    private RepositorioExpedition expeditionRepoMock;
    private HttpSession sessionMock;
    private Model modelMock;
    private RedirectAttributes redirectAttributesMock;
    private ServicioLogin servicioLoginMock;

    private Usuario usuario;
    private ControladorJuego controladorJuego;

    @BeforeEach
    public void setUp() {
        servicioJuegoMock = mock(ServicioJuego.class);
        expeditionRepoMock = mock(RepositorioExpedition.class);
        sessionMock = mock(HttpSession.class);
        modelMock = mock(Model.class);
        redirectAttributesMock = mock(RedirectAttributes.class);
        servicioLoginMock = mock(ServicioLogin.class);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setOro(500);
        usuario.setEmail("user@taller.com");

        controladorJuego = new ControladorJuego(servicioJuegoMock, expeditionRepoMock, servicioLoginMock);
    }

    @Test
    public void mostrarJuego_deberiaRedireccionarAlLoginSiUsuarioEsNull() {
        when(sessionMock.getAttribute("usuario")).thenReturn(null);
        String view = controladorJuego.mostrarJuego(sessionMock, modelMock);
        assertThat(view, equalToIgnoringCase("redirect:/login"));
    }

   @Test
    public void mostrarJuego_deberiaAgregarDatosYRetornarVistaJuego() {
        when(sessionMock.getAttribute("usuario")).thenReturn(usuario);
       when(servicioLoginMock.buscarUsuario(usuario.getId())).thenReturn(usuario);

        GameSession gameSession = new GameSession();
        Expedition expedition = new Expedition();
        expedition.setNumber(2);
        expedition.setCompleted(false);

        when(servicioJuegoMock.iniciarPartida(usuario)).thenReturn(gameSession);
        when(expeditionRepoMock.findBySessionAndCompletedFalse(gameSession)).thenReturn(Optional.of(expedition));
       when(servicioJuegoMock.getMonstruos(usuario)).thenReturn(Arrays.asList(new SessionMonster()));
       when(servicioJuegoMock.getHeroesDeSesion(usuario)).thenReturn(Arrays.asList(new SessionHero()));


       String view = controladorJuego.mostrarJuego(sessionMock, modelMock);

        assertThat(view, equalToIgnoringCase("juego"));
        verify(modelMock).addAttribute(eq("expNumber"), eq(expedition.getNumber()));
        verify(modelMock).addAttribute(eq("dungeonLevel"), eq(gameSession.getNivel()));
        verify(modelMock).addAttribute(eq("usuario"), eq(usuario));
        verify(modelMock).addAttribute(eq("monstruos"), anyList());
        verify(modelMock).addAttribute(eq("heroesDeSesion"), anyList());
    }

    @Test
    public void atacar_deberiaAgregarMensajeYRedireccionarAJuego() {
        when(sessionMock.getAttribute("usuario")).thenReturn(usuario);
        when(servicioJuegoMock.atacar(any(), anyInt(), anyInt())).thenReturn("Mensaje de ataque");

        String view = controladorJuego.atacar(0, 1, sessionMock, redirectAttributesMock);

        assertThat(view, equalToIgnoringCase("redirect:/juego"));
        verify(redirectAttributesMock).addFlashAttribute(eq("mensaje"), eq("Mensaje de ataque"));
    }

    @Test
    public void defender_deberiaAgregarMensajeYRedireccionarAJuego() {
        when(sessionMock.getAttribute("usuario")).thenReturn(usuario);
        when(servicioJuegoMock.defender(any(), anyInt())).thenReturn("Mensaje defender");

        String view = controladorJuego.defender(1, sessionMock, redirectAttributesMock);

        assertThat(view, equalToIgnoringCase("redirect:/juego"));
        verify(redirectAttributesMock).addFlashAttribute(eq("mensaje"), eq("Mensaje defender"));
    }

    @Test
    public void usarPocion_deberiaAgregarMensajeYRedireccionarAJuego() {
        when(sessionMock.getAttribute("usuario")).thenReturn(usuario);
        when(servicioJuegoMock.usarPocion(any(), anyInt())).thenReturn("Curación exitosa");

        String view = controladorJuego.usarPocion(2, sessionMock, redirectAttributesMock);

        assertThat(view, equalToIgnoringCase("redirect:/juego"));
        verify(redirectAttributesMock).addFlashAttribute(eq("mensaje"), eq("Curación exitosa"));
    }

    @Test
    public void siguienteMazmorra_deberiaLlamarServicioYRedireccionarAJuego() {
        when(sessionMock.getAttribute("usuario")).thenReturn(usuario);

        String view = controladorJuego.siguienteMazmorra(sessionMock, redirectAttributesMock);

        assertThat(view, equalToIgnoringCase("redirect:/juego"));
        verify(servicioJuegoMock).siguienteMazmorra(usuario);
        verify(redirectAttributesMock).addFlashAttribute(eq("mensaje"), eq("¡Nueva mazmorra generada!"));
    }

    @Test
    public void terminarExpedicion_deberiaRedireccionarAlLoginSiUsuarioEsNull() {
        when(sessionMock.getAttribute("usuario")).thenReturn(null);

        String view = controladorJuego.terminarExpedicion(sessionMock, redirectAttributesMock);

        assertThat(view, equalToIgnoringCase("redirect:/login"));
    }

    @Test
    public void terminarExpedicion_deberiaTerminarExpedicionYRedireccionarAHome() {
        when(sessionMock.getAttribute("usuario")).thenReturn(usuario);

        String view = controladorJuego.terminarExpedicion(sessionMock, redirectAttributesMock);

        assertThat(view, equalToIgnoringCase("redirect:/home"));
        verify(servicioJuegoMock).terminarExpedicion(usuario);
        verify(redirectAttributesMock).addFlashAttribute(eq("mensaje"), eq("¡Expedición completada! Has recibido 250 de oro."));
        verify(redirectAttributesMock).addFlashAttribute(eq("oro"), eq(usuario.getOro()));
    }
}
