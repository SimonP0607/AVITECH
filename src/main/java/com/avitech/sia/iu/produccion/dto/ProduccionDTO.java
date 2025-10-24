package com.avitech.sia.iu.produccion.dto;

import java.time.LocalDate;

public record ProduccionDTO(
        LocalDate fecha,
        String galpon,
        int produccionTotal,
        double pesoPromedio, // Mapeado a 'temperatura' en DB por falta de columna directa
        int mortalidad,
        int huevosB, // Mapeado a huevos_S en DB
        int huevosA, // Mapeado a huevos_M en DB
        int huevosAA, // Mapeado a huevos_L en DB
        int huevosAAA, // Se sumará a huevos_L en DB
        String responsable
) { }
