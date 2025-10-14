package com.pieri.agriwave.presentation;

import com.pieri.agriwave.persistence.entity.Finanzas;
import com.pieri.agriwave.service.FinanzasService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/finanzas")
public class FinanzasController {

    private final FinanzasService finanzasService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("finanzas", finanzasService.findAll());
        return "finanzas/list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("finanza", new Finanzas());
        return "finanzas/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("finanza", finanzasService.findById(id));
        return "finanzas/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Finanzas finanza, RedirectAttributes ra) {
        finanzasService.save(finanza);
        ra.addFlashAttribute("ok", "Movimiento guardado.");
        return "redirect:/finanzas";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        finanzasService.deleteById(id);
        ra.addFlashAttribute("ok", "Movimiento eliminado.");
        return "redirect:/finanzas";
    }
}
