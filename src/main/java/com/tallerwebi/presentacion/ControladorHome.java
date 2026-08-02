package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.servicios.ServicioJuego;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.infraestructura.RepositorioSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ControladorHome {

    private final ServicioJuego servicioJuego;
    private final RepositorioSession sessionRepo;

    @Autowired
    public ControladorHome(ServicioJuego servicioJuego,
                           RepositorioSession sessionRepo) {
        this.servicioJuego = servicioJuego;
        this.sessionRepo   = sessionRepo;
    }

    @GetMapping("/home")
    public ModelAndView irAMiHome(HttpServletRequest request) {
        Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
        if (usuario == null) {
            // No hay usuario logueado, vamos al login
            return new ModelAndView("redirect:/login");
        }

        // Compruebo si ya existe una sesión/partida activa para este usuario
        boolean tieneSesionActiva = sessionRepo.findActive(usuario) != null;

        ModelMap model = new ModelMap();
        model.put("tieneSesionActiva", tieneSesionActiva);
        return new ModelAndView("home", model);
    }

    @GetMapping("/mazmorra")
    public ModelAndView irAMazmorra() {
        return new ModelAndView("mazmorra");
    }

    @GetMapping("/reglas")
    public ModelAndView irAreglas() {
        return new ModelAndView("reglas");
    }

}
