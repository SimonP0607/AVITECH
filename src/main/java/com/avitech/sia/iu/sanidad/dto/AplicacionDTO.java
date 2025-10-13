package com.avitech.sia.iu.sanidad.dto;

import java.time.LocalDate;

public record AplicacionDTO(
        LocalDate fecha,
        String loteGalpon,
        String medicamento,
        String dosis,
        String viaAplicacion,
        String responsable,
        String observaciones
) { }
