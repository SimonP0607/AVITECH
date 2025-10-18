package com.avitech.sia.iu.sanidad.dto;

import java.time.LocalDate;

/**
 * DTO que representa una aplicación de medicamento en el módulo Sanidad.
 * Usado para transportar datos desde el formulario al controlador principal o capa de persistencia.
 */
public class AplicacionDTO {
    private LocalDate fecha;
    private String loteGalpon;
    private String medicamento;
    private String dosis;
    private String via;
    private String responsable;
    private String observaciones;

    public AplicacionDTO(LocalDate fecha, String loteGalpon, String medicamento,
                         String dosis, String via, String responsable, String observaciones) {
        this.fecha = fecha;
        this.loteGalpon = loteGalpon;
        this.medicamento = medicamento;
        this.dosis = dosis;
        this.via = via;
        this.responsable = responsable;
        this.observaciones = observaciones;
    }

    // Getters y Setters
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getLoteGalpon() { return loteGalpon; }
    public void setLoteGalpon(String loteGalpon) { this.loteGalpon = loteGalpon; }

    public String getMedicamento() { return medicamento; }
    public void setMedicamento(String medicamento) { this.medicamento = medicamento; }

    public String getDosis() { return dosis; }
    public void setDosis(String dosis) { this.dosis = dosis; }

    public String getVia() { return via; }
    public void setVia(String via) { this.via = via; }

    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    @Override
    public String toString() {
        return String.format(
                "AplicacionDTO{fecha=%s, lote='%s', medicamento='%s', dosis='%s', via='%s', responsable='%s', observaciones='%s'}",
                fecha, loteGalpon, medicamento, dosis, via, responsable, observaciones);
    }
}
