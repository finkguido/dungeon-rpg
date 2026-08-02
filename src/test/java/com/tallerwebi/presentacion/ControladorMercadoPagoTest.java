package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.servicios.ServicioTienda;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ControladorMercadoPagoTest {

    ServicioTienda servicioTienda = mock(ServicioTienda.class);
    ControladorMercadoPago controlador = new ControladorMercadoPago(servicioTienda);

    @Test
    void testVistaComprarOro() {
        ControladorMercadoPago controlador = new ControladorMercadoPago(null);

        String mensajeEsperado = "Compra exitosa";
        ModelAndView mav = controlador.vistaComprarOro(mensajeEsperado);

        assertNotNull(mav);
        assertEquals("comprar-oro", mav.getViewName());
        assertEquals(mensajeEsperado, mav.getModel().get("mensaje"));
    }

    @Test
    void testCompraFallidaRedireccion() {
        ControladorMercadoPago controlador = new ControladorMercadoPago(null);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        ModelAndView mav = controlador.compraFallida(redirectAttributes);

        assertEquals("redirect:/comprar-oro", mav.getViewName());
        assertEquals("Su compra ha fallado.", redirectAttributes.getFlashAttributes().get("mensaje"));
    }

    @Test
    void testCompraExitosa() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setOro(0);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(usuario);

        RedirectAttributes redirect = new RedirectAttributesModelMap();

        ModelAndView mav = controlador.compraExitosa("PAQUETE_100", request, redirect);

        assertEquals("redirect:/comprar-oro", mav.getViewName());
        assertTrue(redirect.getFlashAttributes().containsKey("mensaje"));
        verify(servicioTienda).sumarOro("PAQUETE_100", usuario);
    }
}
