package com.avitech.sia.iu.sanidad.dto;

import java.time.LocalDate;

/**
 * DTO que representa un evento sanitario en el módulo Sanidad.
 * Permite transportar los datos del formulario al controlador principal o capa de base de datos.
 */
public class EventoDTO {
    private LocalDate fecha;
    private String loteGalpon;
    private String tipoEvento;
    private int avesAfectadas;
    private String descripcion;
    private String acciones;
    private String responsable;

    public EventoDTO(LocalDate fecha, String loteGalpon, String tipoEvento,
                     int avesAfectadas, String descripcion, String acciones, String responsable) {
        this.fecha = fecha;
        this.loteGalpon = loteGalpon;
        this.tipoEvento = tipoEvento;
        this.avesAfectadas = avesAfectadas;
        this.descripcion = descripcion;
        this.acciones = acciones;
        this.responsable = responsable;
    }

    // Getters y Setters
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getLoteGalpon() { return loteGalpon; }
    public void setLoteGalpon(String loteGalpon) { this.loteGalpon = loteGalpon; }

    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }

    public int getAvesAfectadas() { return avesAfectadas; }
    public void setAvesAfectadas(int avesAfectadas) { this.avesAfectadas = avesAfectadas; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getAcciones() { return acciones; }
    public void setAcciones(String acciones) { this.acciones = acciones; }

    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }

    @Override
    public String toString() {
        return String.format(
                "EventoDTO{fecha=%s, lote='%s', tipo='%s', avesAfectadas=%d, descripcion='%s', acciones='%s', responsable='%s'}",
                fecha, loteGalpon, tipoEvento, avesAfectadas, descripcion, acciones, responsable);
    }
}
