package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.entidades.Heroe;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.servicios.ServicioSanatorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ControladorSantuario {

    private final ServicioSanatorio servicioSanatorio;

    @Autowired
    public ControladorSantuario(ServicioSanatorio servicioSanatorio) {
        this.servicioSanatorio = servicioSanatorio;
    }

    @GetMapping("/santuario")
    public String verSantuario(Model model, HttpSession session, RedirectAttributes ra) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        if (!servicioSanatorio.puedeAccederASantuario(usuario)) {
            ra.addFlashAttribute("error", "Expedición activa. Termina esta expedición para poder curar tus héroes.");
            return "redirect:/home";
        }

        List<Heroe> heroes = servicioSanatorio.obtenerHeroesUsuario(usuario);
        model.addAttribute("usuario", usuario);
        model.addAttribute("heroes", heroes);
        return "santuario";
    }


    @PostMapping("/santuario/curar")
    public String curarHeroe(@RequestParam Long idHeroe, HttpSession session, RedirectAttributes ra) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        try {
            servicioSanatorio.curarHeroe(usuario, idHeroe);
            ra.addFlashAttribute("mensaje", "¡Héroe curado con éxito!");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        // Actualizar usuario en sesión por si cambió el oro
        session.setAttribute("usuario", servicioSanatorio.obtenerUsuarioActualizado(usuario.getId()));
        return "redirect:/santuario";
    }
}

