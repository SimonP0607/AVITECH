package com.avitech.sia.iu.produccion;

/** KPIs formateados para pintar (el datasource decide formato o números crudos). */
public record ProduccionKpis(
        String produccionTotal,
        String posturaPromedio,
        String pesoPromedio,
        String mortalidadTotal
) {}
