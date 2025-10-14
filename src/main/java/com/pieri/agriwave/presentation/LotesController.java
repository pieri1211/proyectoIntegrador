package com.pieri.agriwave.presentation;

import com.pieri.agriwave.persistence.entity.Lotes;
import com.pieri.agriwave.service.LotesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/lotes")
public class LotesController {

    private final LotesService lotesService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("lotes", lotesService.findAll());
        return "lotes/list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("lote", new Lotes());
        return "lotes/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("lote", lotesService.findById(id));
        return "lotes/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Lotes lote, RedirectAttributes ra) {
        lotesService.save(lote);
        ra.addFlashAttribute("ok", "Lote guardado.");
        return "redirect:/lotes";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        lotesService.deleteById(id);
        ra.addFlashAttribute("ok", "Lote eliminado.");
        return "redirect:/lotes";
    }
}
