package com.pieri.agriwave.service;

import com.pieri.agriwave.persistence.entity.Animal;
import com.pieri.agriwave.persistence.repository.IAnimalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AnimalService {

    private final IAnimalRepository animalRepository;

    // LISTAR
    public List<Animal> findAll() {
        return animalRepository.findAll();
    }

    // OBTENER POR ID
    public Animal findById(Long id) {
        return animalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Animal no encontrado: " + id));
    }

    // CREAR / ACTUALIZAR
    public Animal save(Animal animal) {
        return animalRepository.save(animal);
    }

    // ELIMINAR
    public void deleteById(Long id) {
        if (!animalRepository.existsById(id)) {
            throw new IllegalArgumentException("Animal no existe: " + id);
        }
        animalRepository.deleteById(id);
    }

    // KPI: total animales
    public long count() {
        return animalRepository.count();
    }
}
