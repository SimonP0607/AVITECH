package com.avitech.sia.iu.produccion;

import java.time.LocalDate;

/** Registro de producción (entrada de formulario). */
public record ProduccionDTO(
        LocalDate fecha,
        String    galpon,
        int       produccionTotal,
        double    pesoPromedio,
        int       mortalidad,
        int       huevosB,
        int       huevosA,
        int       huevosAA,
        int       huevosAAA
) {
    // Campos opcionales que puede llenar el datasource al listar
    public Integer aves() { return null; }                 // si tu consulta trae # de aves del galpón
    public Double  posturaPorcentual() { return null; }   // (producción/aves)*100 si aplica
}
