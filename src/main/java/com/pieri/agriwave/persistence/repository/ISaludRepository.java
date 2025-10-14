package com.pieri.agriwave.persistence.repository;

import com.pieri.agriwave.persistence.entity.Salud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISaludRepository extends JpaRepository<Salud, Long> {
}
