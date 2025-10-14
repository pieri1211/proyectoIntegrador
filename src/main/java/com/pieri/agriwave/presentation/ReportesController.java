package com.pieri.agriwave.presentation;

import com.pieri.agriwave.service.AnimalService;
import com.pieri.agriwave.service.FinanzasService;
import com.pieri.agriwave.service.LotesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reportes")
public class ReportesController {

    private final AnimalService animalService;
    private final FinanzasService finanzasService;
    private final LotesService lotesService;

    @GetMapping
    public String pagina(Model model){
        model.addAttribute("reporteResumenAnimales", animalService.count() + " animales");
        model.addAttribute("reporteBalance30d", finanzasService.balanceUltimos30Dias());
        model.addAttribute("reporteOcupacion", lotesService.ocupacionTexto());
        return "reportes";
    }

    @GetMapping("/reportes/dashboard")
    public String mostrarDashboard(Model model) {
        return "reportes";
    }

    @GetMapping("/export/csv")
    public void exportCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=agriwave-report.csv");
        try (PrintWriter out = response.getWriter()) {
            out.println("recurso,valor");
            out.println("animales," + animalService.count());
            out.println("balance_30d," + finanzasService.balanceUltimos30Dias());
            out.println("ocupacion," + lotesService.ocupacionTexto());
        }
    }

    @GetMapping(value = "/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportPdf() {
        // Placeholder simple; integra iText/PDFBox si quieres un PDF real
        byte[] fakePdf = "PDF no implementado aún".getBytes();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=agriwave-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(fakePdf);
    }
}
