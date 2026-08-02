package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.servicios.ServicioRanking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControladorRanking {

    private final ServicioRanking servicioRanking;

    @Autowired
    public ControladorRanking(ServicioRanking servicioRanking) {
        this.servicioRanking = servicioRanking;
    }

    @GetMapping("/ranking")
    public String mostrarRanking(Model model) {
        model.addAttribute("ranking", servicioRanking.obtenerRanking());
        return "ranking";
    }
}