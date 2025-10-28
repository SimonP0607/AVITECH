package com.avitech.sia.iu.reportes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuración de un reporte a generar
 */
public class ReporteConfig {
    private TipoReporte tipo;
    private FormatoReporte formato;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private Map<String, String> filtros;
    private boolean vistaPrevia;

    public ReporteConfig(TipoReporte tipo, FormatoReporte formato) {
        this.tipo = tipo;
        this.formato = formato;
        this.filtros = new HashMap<>();
        this.vistaPrevia = false;
    }

    // Getters y Setters
    public TipoReporte getTipo() { return tipo; }
    public void setTipo(TipoReporte tipo) { this.tipo = tipo; }

    public FormatoReporte getFormato() { return formato; }
    public void setFormato(FormatoReporte formato) { this.formato = formato; }

    public LocalDate getFechaDesde() { return fechaDesde; }
    public void setFechaDesde(LocalDate fechaDesde) { this.fechaDesde = fechaDesde; }

    public LocalDate getFechaHasta() { return fechaHasta; }
    public void setFechaHasta(LocalDate fechaHasta) { this.fechaHasta = fechaHasta; }

    public Map<String, String> getFiltros() { return filtros; }
    public void setFiltros(Map<String, String> filtros) { this.filtros = filtros; }

    public void addFiltro(String clave, String valor) {
        if (valor != null && !valor.isEmpty()) {
            filtros.put(clave, valor);
        }
    }

    public boolean isVistaPrevia() { return vistaPrevia; }
    public void setVistaPrevia(boolean vistaPrevia) { this.vistaPrevia = vistaPrevia; }
}

