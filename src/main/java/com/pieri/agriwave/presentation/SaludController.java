package com.pieri.agriwave.presentation;

import com.pieri.agriwave.persistence.entity.Salud;
import com.pieri.agriwave.service.AnimalService;
import com.pieri.agriwave.service.SaludService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/sanidad")
public class SaludController {

    private final SaludService saludService;
    private final AnimalService animalService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("sanidades", saludService.findAll());
        return "sanidad/list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("salud", new Salud());
        model.addAttribute("animales", animalService.findAll());
        return "sanidad/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("salud", saludService.findById(id));
        model.addAttribute("animales", animalService.findAll());
        return "sanidad/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Salud salud, RedirectAttributes ra) {
        saludService.save(salud);
        ra.addFlashAttribute("ok", "Registro sanitario guardado.");
        return "redirect:/sanidad";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        saludService.deleteById(id);
        ra.addFlashAttribute("ok", "Registro sanitario eliminado.");
        return "redirect:/sanidad";
    }
}
