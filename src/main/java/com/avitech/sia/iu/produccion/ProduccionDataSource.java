package com.avitech.sia.iu.produccion;

import java.util.List;

public interface ProduccionDataSource {
    // Catálogos / lecturas
    List<String> findGalpones();

    // KPIs
    ProduccionKpis getKpis(); // si luego quieres por fecha/galpón, agrega parámetros

    // Tabla diaria
    List<ProduccionDTO> listarDiario(String galponOrTodos);

    // Escritura
    long saveRegistro(ProduccionDTO dto) throws Exception;
}
