package com.tallerwebi.presentacion;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.tallerwebi.config.MercadoPagoSettings;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.servicios.ServicioTienda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Controller
public class ControladorMercadoPago {

    private final ServicioTienda servicioTienda;

    @Autowired
    public ControladorMercadoPago(ServicioTienda servicioTienda) {
        this.servicioTienda = servicioTienda;
    }

    @GetMapping("/comprar-oro")
    public ModelAndView vistaComprarOro(@ModelAttribute("mensaje") String mensaje) {
        ModelMap model = new ModelMap();
        model.addAttribute("mensaje", mensaje);
        return new ModelAndView("comprar-oro", model);
    }

    @PostMapping("/crear-preferencia")
    public ResponseEntity<String> crearPreferencia(@RequestParam String paqueteOro,
                                                   @RequestParam Integer monto,
                                                   HttpServletRequest req) {
        try {
            MercadoPagoConfig.setAccessToken(MercadoPagoSettings.accessToken());

            String baseUrl = MercadoPagoSettings.appBaseUrl();
            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .id(paqueteOro)
                    .title("Compra de oro")
                    .unitPrice(BigDecimal.valueOf(monto))
                    .quantity(1)
                    .currencyId("ARS")
                    .build();

            List<PreferenceItemRequest> items = Collections.singletonList(item);

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(baseUrl + "/spring/compra-exitosa?paquete=" + paqueteOro)
                    .failure(baseUrl + "/spring/compra-fallida")
                    .pending(baseUrl + "/spring/home")
                    .build();

            PreferenceRequest reqPref = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrls)
                    .autoReturn("approved")
                    .externalReference(paqueteOro)
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference response = client.create(reqPref);
            return ResponseEntity.ok(response.getInitPoint());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Configuración de Mercado Pago incompleta");
        } catch (MPApiException | MPException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("/error");
        }
    }

    @GetMapping("/compra-exitosa")
    public ModelAndView compraExitosa(@RequestParam(name = "paquete") String paqueteOro,
                                      HttpServletRequest request,
                                      RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
        if (usuario == null) {
            return new ModelAndView("redirect:/login");
        }

        servicioTienda.sumarOro(paqueteOro, usuario);
        request.getSession().setAttribute("usuario", usuario);

        redirectAttributes.addFlashAttribute("mensaje", "Su compra ha sido exitosa. Se acreditaron sus monedas de oro");
        return new ModelAndView("redirect:/comprar-oro");
    }

    @GetMapping("/compra-fallida")
    public ModelAndView compraFallida(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("mensaje", "Su compra ha fallado.");
        return new ModelAndView("redirect:/comprar-oro");
    }
}
