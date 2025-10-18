package com.avitech.sia.iu.sanidad;

import com.avitech.sia.iu.sanidad.dto.AplicacionDTO;
import com.avitech.sia.iu.sanidad.dto.EventoDTO;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementación temporal "en memoria" que NO escribe a BD.
 * Sirve para compilar y probar la UI. Más adelante la sustituyes por JDBC/MySQL.
 */
public class EmptySanidadDataSource implements SanidadDataSource {

    private static final AtomicLong SEQ = new AtomicLong(1);

    @Override
    public long saveAplicacion(AplicacionDTO dto) {
        long id = SEQ.getAndIncrement();
        System.out.println("[EmptySanidadDataSource] saveAplicacion -> id=" + id + " | " + dto);
        return id;
    }

    @Override
    public long saveEvento(EventoDTO dto) {
        long id = SEQ.getAndIncrement();
        System.out.println("[EmptySanidadDataSource] saveEvento -> id=" + id + " | " + dto);
        return id;
    }
}
