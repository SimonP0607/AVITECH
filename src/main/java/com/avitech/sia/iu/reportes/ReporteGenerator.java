package com.avitech.sia.iu.reportes;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Interfaz para generadores de reportes
 */
public interface ReporteGenerator {
    /**
     * Genera un reporte basado en la configuración proporcionada
     * @param config Configuración del reporte
     * @param datos Datos a incluir en el reporte
     * @return Archivo generado
     * @throws IOException Si hay error al generar el archivo
     */
    File generarReporte(ReporteConfig config, List<? extends Object> datos) throws IOException;

    /**
     * Verifica si este generador soporta el formato especificado
     */
    boolean soportaFormato(FormatoReporte formato);
}

