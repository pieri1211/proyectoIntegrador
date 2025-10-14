package com.pieri.agriwave.presentation;

import com.pieri.agriwave.persistence.entity.Animal;
import com.pieri.agriwave.service.AnimalService;
import com.pieri.agriwave.service.LotesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/animales")
public class AnimalController {

    private final AnimalService animalService;
    private final LotesService lotesService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("animales", animalService.findAll());
        return "animales/list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("animal", new Animal());
        model.addAttribute("lotes", lotesService.findAll());
        return "animales/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("animal", animalService.findById(id));
        model.addAttribute("lotes", lotesService.findAll());
        return "animales/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Animal animal, RedirectAttributes ra) {
        animalService.save(animal);
        ra.addFlashAttribute("ok", "Animal guardado correctamente.");
        return "redirect:/animales";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        animalService.deleteById(id);
        ra.addFlashAttribute("ok", "Animal eliminado.");
        return "redirect:/animales";
    }
}
