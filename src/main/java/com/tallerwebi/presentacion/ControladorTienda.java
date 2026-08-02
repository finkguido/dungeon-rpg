package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.entidades.Item;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.servicios.ServicioLogin;
import com.tallerwebi.dominio.servicios.ServicioTienda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class ControladorTienda {



    private ServicioTienda servicioTienda;
    private ServicioLogin servicioLogin;

    @Autowired
    public ControladorTienda(ServicioTienda servicioTienda, ServicioLogin servicioLogin) {
        this.servicioTienda = servicioTienda;
        this.servicioLogin = servicioLogin;
    }



    @PostMapping("/tienda/comprar")
    public String comprar(@RequestParam Long itemId, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");

        //ModelAndView mav = new ModelAndView("tienda");
        ModelMap model = new ModelMap();
        model.addAttribute("usuario",usuario);

        List<Item> itemsDeTienda = servicioTienda.obtenerItemsDeTienda();
        model.addAttribute("items", itemsDeTienda);

        try {
            servicioTienda.comprar(usuario, itemId);
            redirectAttributes.addFlashAttribute("mensaje", "Compra Exitosa!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());

        }

        Usuario usuarioActualizado = servicioLogin.buscarUsuario(usuario.getId());
        request.getSession().setAttribute("usuario", usuarioActualizado);

        return "redirect:/ver-tienda";

    }


    @GetMapping("/ver-tienda")
    public ModelAndView verTienda(HttpServletRequest request, @ModelAttribute("mensaje") String mensaje) {
        ModelMap model = new ModelMap();
        Usuario usuarioSesion = (Usuario) request.getSession().getAttribute("usuario");
        Usuario usuario = servicioLogin.buscarUsuario(usuarioSesion.getId());
        List<Item> items = servicioTienda.obtenerItemsDeTienda();

        List<Item> itemsDeInventario = servicioTienda.obtenerItemsPorInventario(usuario.getInventario().getId());

        // Agrupa por nombre
        Set<String> nombresUnicos = new HashSet<>();
        Map<String, Integer> inventarioAgrupado = new LinkedHashMap<>();
        for (Item item : itemsDeInventario) {
            // Solo sumo si ese id no lo vi antes
            String key = item.getNombre() + "-" + item.getId();
            if (!nombresUnicos.contains(key)) {
                inventarioAgrupado.merge(item.getNombre(), 1, Integer::sum);
                nombresUnicos.add(key);
            }
        }


        model.addAttribute("usuario", usuario);
        model.addAttribute("items", items);
        model.addAttribute("itemsDeInventario", itemsDeInventario);
        model.addAttribute("inventarioAgrupado", inventarioAgrupado);
        model.addAttribute("mensaje", mensaje == null || mensaje.isEmpty() ? "Bienvenido a la tienda!" : mensaje);

        return new ModelAndView("tienda", model);
    }

}
