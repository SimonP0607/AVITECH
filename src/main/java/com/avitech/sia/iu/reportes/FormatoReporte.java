package com.avitech.sia.iu.reportes;

/**
 * Formato de exportación del reporte
 */
public enum FormatoReporte {
    PDF("PDF", ".pdf"),
    EXCEL("Excel", ".xlsx");

    private final String nombre;
    private final String extension;

    FormatoReporte(String nombre, String extension) {
        this.nombre = nombre;
        this.extension = extension;
    }

    public String getNombre() { return nombre; }
    public String getExtension() { return extension; }
}

