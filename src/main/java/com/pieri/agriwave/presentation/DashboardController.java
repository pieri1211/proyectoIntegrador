package com.pieri.agriwave.presentation;

import com.pieri.agriwave.service.AnimalService;
import com.pieri.agriwave.service.FinanzasService;
import com.pieri.agriwave.service.LotesService;
import com.pieri.agriwave.service.SaludService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final AnimalService animalService;
    private final FinanzasService finanzasService;
    private final LotesService lotesService;
    private final SaludService saludService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("animales", animalService.findAll());
        model.addAttribute("finanzas", finanzasService.findAll());
        model.addAttribute("lotes", lotesService.findAll());
        model.addAttribute("sanidades", saludService.findAll());

        model.addAttribute("reporteResumenAnimales", animalService.count() + " animales");
        model.addAttribute("reporteBalance30d", finanzasService.balanceUltimos30Dias());
        model.addAttribute("reporteOcupacion", lotesService.ocupacionTexto());
        return "index";
    }
}
