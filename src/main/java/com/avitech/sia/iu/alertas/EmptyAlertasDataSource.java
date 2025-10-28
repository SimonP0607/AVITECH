package com.avitech.sia.iu.alertas;

import java.util.Collections;
import java.util.List;

/**
 * Implementación vacía del DataSource de alertas.
 * Se usa mientras no hay conexión real a BD.
 */
public class EmptyAlertasDataSource implements AlertasDataSource {

    @Override
    public List<String> listCategorias() {
        return List.of("Alimento", "Medicamento", "Equipo", "Otro");
    }

    @Override
    public List<String> listUbicaciones() {
        return List.of("Almacén Principal", "Bodega A", "Bodega B");
    }

    @Override
    public List<AlertaDTO> searchAlertas(String texto, String categoria, Criticidad criticidad, String ubicacion) {
        return Collections.emptyList();
    }

    @Override
    public long countPorCriticidad(Criticidad criticidad) {
        return 0;
    }

    @Override
    public long countTotal() {
        return 0;
    }
}

