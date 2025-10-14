package com.pieri.agriwave.persistence.repository;

import com.pieri.agriwave.persistence.entity.Finanzas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFinanzasRepository extends JpaRepository <Finanzas, Long> {
}
