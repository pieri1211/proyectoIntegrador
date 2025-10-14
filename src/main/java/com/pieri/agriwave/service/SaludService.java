package com.pieri.agriwave.service;

import com.pieri.agriwave.persistence.entity.Salud;
import com.pieri.agriwave.persistence.repository.ISaludRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SaludService {

    private final ISaludRepository saludRepository;

    // LISTAR
    public List<Salud> findAll() {
        return saludRepository.findAll();
    }

    // OBTENER POR ID
    public Salud findById(Long id) {
        return saludRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro sanitario no encontrado: " + id));
    }

    // CREAR / ACTUALIZAR
    public Salud save(Salud salud) {
        return saludRepository.save(salud);
    }

    // ELIMINAR
    public void deleteById(Long id) {
        if (!saludRepository.existsById(id)) {
            throw new IllegalArgumentException("Registro sanitario no existe: " + id);
        }
        saludRepository.deleteById(id);
    }

    // KPI simple: total de registros sanitarios
    public long count() {
        return saludRepository.count();
    }
}
