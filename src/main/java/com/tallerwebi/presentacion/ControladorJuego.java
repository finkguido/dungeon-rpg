package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.entidades.*;
import com.tallerwebi.dominio.servicios.ServicioJuego;
import com.tallerwebi.dominio.servicios.ServicioLogin;
import com.tallerwebi.infraestructura.RepositorioExpedition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
public class ControladorJuego {

    private final ServicioJuego servicioJuego;
    private final RepositorioExpedition expeditionRepo;
    private final ServicioLogin servicioLogin;

    @Autowired
    public ControladorJuego(ServicioJuego servicioJuego, RepositorioExpedition expeditionRepo, ServicioLogin servicioLogin) {
        this.servicioJuego = servicioJuego;
        this.expeditionRepo = expeditionRepo;
        this.servicioLogin = servicioLogin;
    }

    @GetMapping("/juego")
    public String mostrarJuego(HttpSession httpSession, Model model) {
        Usuario usuarioSesion = (Usuario) httpSession.getAttribute("usuario");
        if (usuarioSesion == null) return "redirect:/login";

        // ¡RECARGAR el usuario desde la base!
        Usuario usuario = servicioLogin.buscarUsuario(usuarioSesion.getId());
        // Opcional: actualizá el usuario en sesión por si lo vas a necesitar más tarde
        httpSession.setAttribute("usuario", usuario);

        GameSession session = servicioJuego.iniciarPartida(usuario);

        Expedition exp = expeditionRepo
                .findBySessionAndCompletedFalse(session)
                .orElseThrow(() -> new IllegalStateException("No hay expedición activa"));

        model.addAttribute("expNumber",    exp.getNumber());
        model.addAttribute("dungeonLevel", session.getNivel());
        model.addAttribute("usuario",      usuario);
        model.addAttribute("monstruos",    servicioJuego.getMonstruos(usuario));
        model.addAttribute("heroesDeSesion", servicioJuego.getHeroesDeSesion(usuario));
        model.addAttribute("items", servicioJuego.getItemsDeUsuario(usuario));

        return "juego";
    }


    @PostMapping("/juego/atacar")
    public String atacar(
            @RequestParam int heroOrden,
            @RequestParam int monsterOrden,
            HttpSession httpSession,
            RedirectAttributes ra
    ) {
        Usuario u = (Usuario) httpSession.getAttribute("usuario");
        String mensaje = servicioJuego.atacar(u, heroOrden, monsterOrden);
        ra.addFlashAttribute("mensaje", mensaje);
        return "redirect:/juego";
    }

    @PostMapping("/juego/defender")
    public String defender(
            @RequestParam int heroOrden,
            HttpSession httpSession,
            RedirectAttributes ra
    ) {
        Usuario u = (Usuario) httpSession.getAttribute("usuario");
        String mensaje = servicioJuego.defender(u, heroOrden);
        ra.addFlashAttribute("mensaje", mensaje);
        return "redirect:/juego";
    }

    @PostMapping("/juego/usarPocion")
    public String usarPocion(
            @RequestParam int heroOrden,
            HttpSession httpSession,
            RedirectAttributes ra
    ) {
        Usuario u = (Usuario) httpSession.getAttribute("usuario");
        String mensaje = servicioJuego.usarPocion(u, heroOrden);
        ra.addFlashAttribute("mensaje", mensaje);
        return "redirect:/juego";
    }

    @PostMapping("/juego/usarEspada")
    public String usarEspada(
            @RequestParam int heroOrden,
            HttpSession httpSession,
            RedirectAttributes ra
    ) {
        Usuario u = (Usuario) httpSession.getAttribute("usuario");
        String mensaje = servicioJuego.usarEspada(u, heroOrden);
        ra.addFlashAttribute("mensaje", mensaje);
        return "redirect:/juego";
    }


    @PostMapping("/juego/siguiente")
    public String siguienteMazmorra(
            HttpSession httpSession,
            RedirectAttributes ra
    ) {
        Usuario u = (Usuario) httpSession.getAttribute("usuario");
        servicioJuego.siguienteMazmorra(u);
        ra.addFlashAttribute("mensaje", "¡Nueva mazmorra generada!");
        return "redirect:/juego";
    }

    @PostMapping("/juego/terminarExpedicion")
    public String terminarExpedicion(HttpSession httpSession, RedirectAttributes ra) {
        Usuario u = (Usuario) httpSession.getAttribute("usuario");
        if (u == null) return "redirect:/login";

        servicioJuego.terminarExpedicion(u);
        ra.addFlashAttribute("mensaje", "¡Expedición completada! Has recibido 250 de oro.");
        ra.addFlashAttribute("oro", u.getOro());

        return "redirect:/home";
    }
}
