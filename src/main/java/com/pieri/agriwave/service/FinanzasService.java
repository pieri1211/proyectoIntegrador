package com.pieri.agriwave.service;

import com.pieri.agriwave.persistence.entity.Finanzas;
import com.pieri.agriwave.persistence.repository.IFinanzasRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FinanzasService {

    private final IFinanzasRepository finanzasRepository;

    // LISTAR
    public List<Finanzas> findAll() {
        return finanzasRepository.findAll();
    }

    // OBTENER POR ID
    public Finanzas findById(Long id) {
        return finanzasRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movimiento no encontrado: " + id));
    }

    // CREAR / ACTUALIZAR
    public Finanzas save(Finanzas finanza) {
        // Normalizamos el tipo por si llega en minúsculas
        if (finanza.getTipo() != null) {
            finanza.setTipo(finanza.getTipo().trim());
        }
        return finanzasRepository.save(finanza);
    }

    // ELIMINAR
    public void deleteById(Long id) {
        if (!finanzasRepository.existsById(id)) {
            throw new IllegalArgumentException("Movimiento no existe: " + id);
        }
        finanzasRepository.deleteById(id);
    }

    // KPI: total de registros
    public long count() {
        return finanzasRepository.count();
    }

    // KPI: Balance últimos 30 días (Ingresos - Egresos)
    // Devuelve un String listo para mostrarse (ej: "$1,234.56")
    public String balanceUltimos30Dias() {
        LocalDate desde = LocalDate.now().minusDays(30);
        double balance = finanzasRepository.findAll().stream()
                .filter(f -> f.getFecha() != null && !f.getFecha().isBefore(desde))
                .mapToDouble(f -> {
                    String t = f.getTipo() == null ? "" : f.getTipo().toLowerCase();
                    double monto = f.getMonto() == null ? 0.0 : f.getMonto();
                    if (t.contains("ingreso")) return monto;
                    if (t.contains("egreso")) return -monto;
                    // Si el tipo no coincide, lo tratamos neutro
                    return 0.0;
                })
                .sum();

        // Formato sencillo con separador de miles por comodidad visual
        return formatCurrency(balance);
    }

    // Formateo rápido sin Locale complejo
    private String formatCurrency(double value) {
        // Dos decimales, separador de miles básico
        String raw = String.format(java.util.Locale.US, "%,.2f", value);
        return "$" + raw;
    }
}
