package com.pieri.agriwave.persistence.repository;

import com.pieri.agriwave.persistence.entity.Finanzas;
import com.pieri.agriwave.persistence.entity.Lotes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILotesRepository extends JpaRepository <Lotes, Long> {
}
