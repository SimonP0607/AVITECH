package com.avitech.sia.iu.alertas;

import java.time.LocalDateTime;
import java.util.List;

public interface AlertasDataSource {
    // Catálogos
    List<String> listCategorias();
    List<String> listUbicaciones();

    // Consulta principal (server-side cuando conectes BD)
    List<AlertaDTO> searchAlertas(String texto, String categoria, Criticidad criticidad, String ubicacion);

    // KPIs
    long countPorCriticidad(Criticidad criticidad);
    long countTotal();

    // Marca de tiempo (opcional)
    default LocalDateTime lastUpdatedAt() { return LocalDateTime.now(); }
}
