package com.avitech.sia.iu.sanidad.dto;

import java.time.LocalDate;

public record EventoDTO(
        LocalDate fecha,
        String loteAfectado,
        String tipoEvento,
        String avesAfectadas,
        String descripcion,
        String accionesTomadas,
        String responsable
) { }
