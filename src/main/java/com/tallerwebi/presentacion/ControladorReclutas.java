package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.servicios.ServicioJuego;
import com.tallerwebi.dominio.servicios.ServicioRecluta;
import com.tallerwebi.dominio.entidades.Carruaje;
import com.tallerwebi.dominio.entidades.Heroe;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.ReclutaException;
import javassist.CtBehavior;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ControladorReclutas {

    private static final String VISTA_CARRUAJE             = "vista_carruaje";
    private static final String REDIRECT_LOGIN              = "redirect:/login";
    private static final String MODEL_USUARIO               = "usuario";
    private static final String MODEL_CARRUAJE              = "carruaje";
    private static final String MODEL_HEROES_DISPONIBLES    = "heroesDisponibles";
    private static final String MODEL_HEROES_EN_CARRUAJE    = "heroesEnCarruaje";

    private static final String VISTA_HEROES_OBTENIDOS      = "vista_heroes_obtenidos";
    private static final String MODEL_HEROES_OBTENIDOS      = "heroes_obtenidos";

    private final ServicioRecluta  servicioRecluta;
    private final ServicioJuego    servicioJuego;

    @Autowired
    public ControladorReclutas(ServicioRecluta servicioRecluta,
                               ServicioJuego servicioJuego) {
        this.servicioRecluta = servicioRecluta;
        this.servicioJuego   = servicioJuego;
    }

    @GetMapping("/carruaje")
    public ModelAndView mostrarCarruaje(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute(MODEL_USUARIO);
        if (usuario == null) {
            return new ModelAndView(REDIRECT_LOGIN);
        }

        // Aseguro que el usuario tenga carruaje y lo obtengo
        Carruaje carruaje = servicioRecluta
                .asignarOActualizarUnCarrujeAUnUsuario(usuario.getId());

        // Obtengo ambas listas
        List<Heroe> heroesDisponibles = servicioRecluta
                .getHeroesDisponiblesEnCarruaje(usuario.getId());
        List<Heroe> heroesEnCarruaje  = servicioRecluta
                .getListaDeHeroesEnCarruaje(usuario.getId());

        ModelMap model = new ModelMap();
        model.put(MODEL_USUARIO,            usuario);
        model.put(MODEL_CARRUAJE,           carruaje);
        model.put(MODEL_HEROES_DISPONIBLES, heroesDisponibles);
        model.put(MODEL_HEROES_EN_CARRUAJE, heroesEnCarruaje);

        return new ModelAndView(VISTA_CARRUAJE, model);
    }

    @PostMapping("/carruaje/seleccionar")
    public String seleccionarHeroe(
            @RequestParam Long usuarioId,
            @RequestParam Long heroeId,
            HttpSession session,
            RedirectAttributes redirectAttributes // <-- para mensajes flash
    ) {
        try {
            servicioRecluta.seleccionarHeroe(usuarioId, heroeId);

            Usuario usuarioActualizado = servicioRecluta.getUsuarioPorId(usuarioId);
            session.setAttribute("usuario", usuarioActualizado);

        } catch (ReclutaException ex) {

            redirectAttributes.addFlashAttribute("errorMsg", ex.getMessage());
        }
        return "redirect:/carruaje";
    }

    @PostMapping("/carruaje/quitar")
    public String quitarHeroe(
            @RequestParam Long usuarioId,
            @RequestParam Long heroeId,
            HttpSession session
    ) {
        servicioRecluta.quitarHeroe(usuarioId, heroeId);

        Usuario usuarioActualizado = servicioRecluta.getUsuarioPorId(usuarioId);
        session.setAttribute("usuario", usuarioActualizado);

        return "redirect:/carruaje";
    }

    @GetMapping("/reclutar/{id}")
    public ModelAndView reclutarHeroe(
            @PathVariable("id") Long idHeroe,
            HttpServletRequest request
    ) {
        HttpSession sessionHttp = request.getSession(false);
        Usuario usuario = obtenerUsuarioDesdeSesion(sessionHttp);
        if (usuario == null) {
            return new ModelAndView(REDIRECT_LOGIN);
        }

        Carruaje carruaje = servicioRecluta
                .asignarOActualizarUnCarrujeAUnUsuario(usuario.getId());

        servicioRecluta.quitarUnHeroeDelCarruaje(idHeroe, usuario.getId());
        servicioRecluta.agregarUnHeroeAlUsuario   (idHeroe, usuario);

        List<Heroe> heroesDespués = servicioRecluta
                .getHeroesDisponiblesEnCarruaje(usuario.getId());

        ModelMap model = new ModelMap();
        model.put(MODEL_USUARIO,            usuario);
        model.put(MODEL_CARRUAJE,           carruaje);
        model.put(MODEL_HEROES_EN_CARRUAJE, heroesDespués);

        return new ModelAndView(VISTA_CARRUAJE, model);
    }

    @GetMapping("/seleccion-heroes")
    public ModelAndView mostrarHeroesObtenidos(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute(MODEL_USUARIO);
        if (usuario == null) {
            return new ModelAndView(REDIRECT_LOGIN);
        }

        ModelMap model = new ModelMap();
        try {
            List<Heroe> obtenidos = servicioRecluta
                    .getHeroesObtenidosPorElUsuario(usuario);
            model.put(MODEL_HEROES_OBTENIDOS, obtenidos);
            model.put(MODEL_USUARIO,          usuario);
            return new ModelAndView(VISTA_HEROES_OBTENIDOS, model);

        } catch (ReclutaException e) {
            model.put(MODEL_USUARIO, usuario);
            model.put("mensaje1",    e.getMessage());
            return new ModelAndView(VISTA_HEROES_OBTENIDOS, model);
        }
    }

    @PostMapping("/seleccion-heroes/comenzar")
    public String comenzarExpedicion(HttpSession httpSession) {
        Usuario u = (Usuario) httpSession.getAttribute("usuario");
        if (u == null) {
            return "redirect:/login";
        }
        servicioJuego.iniciarPartida(u);
        return "redirect:/juego";
    }

    private Usuario obtenerUsuarioDesdeSesion(HttpSession session) {
        if (session == null) return null;
        Object obj = session.getAttribute(MODEL_USUARIO);
        return (obj instanceof Usuario) ? (Usuario) obj : null;
    }

    @GetMapping("/cerrar_sesion")
    public ModelAndView cerrarSesion(HttpSession session) {
        session.invalidate();
        return new ModelAndView(REDIRECT_LOGIN);
    }


}



