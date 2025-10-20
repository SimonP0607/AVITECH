package com.avitech.sia.iu.sanidad;

import com.avitech.sia.iu.sanidad.dto.AplicacionDTO;
import com.avitech.sia.iu.sanidad.dto.EventoDTO;

import java.util.List;

public interface SanidadDataSource {

    // ---- catálogos (lecturas) ----
    List<String> findLotes();
    List<String> findMedicamentos();
    List<String> findViasAplicacion();
    List<String> findResponsables();
    List<String> findTiposEvento(); // para el modal de eventos

    // ---- escrituras ----
    long saveAplicacion(AplicacionDTO dto) throws Exception;
    long saveEvento(EventoDTO dto) throws Exception;
}
