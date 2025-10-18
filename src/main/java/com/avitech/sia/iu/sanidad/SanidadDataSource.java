package com.avitech.sia.iu.sanidad;

import com.avitech.sia.iu.sanidad.dto.AplicacionDTO;
import com.avitech.sia.iu.sanidad.dto.EventoDTO;

/**
 * Contrato de persistencia para el módulo Sanidad.
 * Más adelante podrás crear una implementación JDBC/MySQL que cumpla este contrato.
 */
public interface SanidadDataSource {

    /**
     * Persiste una aplicación de medicamento.
     * @param dto datos de la aplicación
     * @return id generado (si aplica) o -1 si no corresponde
     * @throws Exception si hay error de persistencia
     */
    long saveAplicacion(AplicacionDTO dto) throws Exception;

    /**
     * Persiste un evento sanitario.
     * @param dto datos del evento
     * @return id generado (si aplica) o -1 si no corresponde
     * @throws Exception si hay error de persistencia
     */
    long saveEvento(EventoDTO dto) throws Exception;

    /* ====== Hooks para futuras lecturas (opcional por ahora) ======
     * Puedes ir agregando métodos como:
     *   List<AplicacionResumen> listAplicaciones(Filtro f);
     *   List<EventoResumen> listEventos(Filtro f);
     *   KpiSanidad getKpis(LocalDate desde, LocalDate hasta);
     * De momento no son necesarios para compilar ni para los modales.
     */
}