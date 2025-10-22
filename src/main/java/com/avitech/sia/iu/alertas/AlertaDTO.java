package com.avitech.sia.iu.alertas;

public record AlertaDTO(
        String articulo,
        String categoria,
        String stockActual,   // puedes migrar a double/int cuando definas el esquema
        String stockMinimo,
        String porcentaje,
        String diasRestantes,
        String ubicacion,
        Criticidad criticidad
) {}
