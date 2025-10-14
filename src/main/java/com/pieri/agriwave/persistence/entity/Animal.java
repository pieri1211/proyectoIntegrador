package com.pieri.agriwave.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
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
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String raza;
    @ManyToOne
    @JoinColumn(name="lote_id")
    private Lotes lote;
    private String sexo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNac;
    private String observaciones;
}
