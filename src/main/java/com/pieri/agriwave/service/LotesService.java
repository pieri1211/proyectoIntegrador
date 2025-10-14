package com.pieri.agriwave.service;

import com.pieri.agriwave.persistence.entity.Animal;
import com.pieri.agriwave.persistence.entity.Lotes;
import com.pieri.agriwave.persistence.repository.IAnimalRepository;
import com.pieri.agriwave.persistence.repository.ILotesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class LotesService {

    private final ILotesRepository lotesRepository;
    private final IAnimalRepository animalRepository; // para calcular ocupación por lote

    // LISTAR
    public List<Lotes> findAll() {
        return lotesRepository.findAll();
    }

    // OBTENER POR ID
    public Lotes findById(Long id) {
        return lotesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado: " + id));
    }

    // CREAR / ACTUALIZAR
    public Lotes save(Lotes lote) {
        return lotesRepository.save(lote);
    }

    // ELIMINAR
    public void deleteById(Long id) {
        if (!lotesRepository.existsById(id)) {
            throw new IllegalArgumentException("Lote no existe: " + id);
        }
        lotesRepository.deleteById(id);
    }

    // KPI: total de lotes
    public long count() {
        return lotesRepository.count();
    }

    // KPI: Ocupación por lote (cuenta animales agrupados)
    // Devuelve algo tipo: "Lote A: 12 • Lote B: 7 • Lote C: 0"
    public String ocupacionTexto() {
        List<Lotes> lotes = lotesRepository.findAll();
        List<Animal> animales = animalRepository.findAll();

        Map<Long, Long> conteo = animales.stream()
                .filter(a -> a.getLote() != null && a.getLote().getId() != null)
                .collect(Collectors.groupingBy(a -> a.getLote().getId(), Collectors.counting()));

        List<String> partes = new ArrayList<>();
        for (Lotes l : lotes) {
            long c = conteo.getOrDefault(l.getId(), 0L);
            // `l.lote` es el nombre según tu entidad actual
            String nombre = l.getLote() == null ? ("Lote " + l.getId()) : l.getLote();
            partes.add(nombre + ": " + c);
        }
        return partes.isEmpty() ? "-" : String.join(" • ", partes);
    }
}
