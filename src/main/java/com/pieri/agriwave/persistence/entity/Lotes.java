package com.pieri.agriwave.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Lotes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String lote;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha_entrada;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha_salida;
    private String nota;
}

