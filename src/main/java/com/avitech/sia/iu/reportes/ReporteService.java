package com.avitech.sia.iu.reportes;

import javafx.concurrent.Task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para gestionar la generación de reportes
 */
public class ReporteService {

    private final Map<FormatoReporte, ReporteGenerator> generators;

    public ReporteService() {
        this.generators = new HashMap<>();
        this.generators.put(FormatoReporte.PDF, new PDFReporteGenerator());
        this.generators.put(FormatoReporte.EXCEL, new ExcelReporteGenerator());
    }

    /**
     * Genera un reporte de forma asíncrona
     * @param config Configuración del reporte
     * @return Task que genera el archivo
     */
    public Task<File> generarReporteAsync(ReporteConfig config) {
        return new Task<>() {
            @Override
            protected File call() throws Exception {
                updateMessage("Preparando datos...");
                updateProgress(0, 100);

                // Obtener datos según el tipo de reporte
                List<Object> datos = obtenerDatos(config);

                updateMessage("Generando reporte...");
                updateProgress(50, 100);

                // Obtener el generador apropiado
                ReporteGenerator generator = generators.get(config.getFormato());
                if (generator == null) {
                    throw new IllegalArgumentException("Formato no soportado: " + config.getFormato());
                }

                // Generar el reporte
                File archivo = generator.generarReporte(config, datos);

                updateMessage("Reporte generado exitosamente");
                updateProgress(100, 100);

                return archivo;
            }
        };
    }

    /**
     * Genera un reporte de forma síncrona
     */
    public File generarReporte(ReporteConfig config) throws IOException {
        List<Object> datos = obtenerDatos(config);
        ReporteGenerator generator = generators.get(config.getFormato());

        if (generator == null) {
            throw new IllegalArgumentException("Formato no soportado: " + config.getFormato());
        }

        return generator.generarReporte(config, datos);
    }

    /**
     * Obtiene los datos de la base de datos según el tipo de reporte.
     * Por ahora retorna datos de ejemplo. Cuando se conecte la BD,
     * aquí se harán las consultas reales.
     */
    private List<Object> obtenerDatos(ReporteConfig config) {
        // TODO: Conectar con la base de datos real
        // Este es un placeholder que retorna lista vacía para usar datos de ejemplo
        // Cuando se implemente la BD, aquí se ejecutarán las consultas SQL

        // Ejemplo de cómo se usará:
        /*
        switch (config.getTipo()) {
            case STOCK_ACTUAL -> return stockDAO.obtenerStockActual(config.getFiltros());
            case REGISTRO_ARTICULO -> return movimientosDAO.obtenerMovimientos(
                config.getFiltros().get("articulo"),
                config.getFechaDesde(),
                config.getFechaHasta()
            );
            case RECIBOS_INSUMOS -> return comprasDAO.obtenerRecibos(
                config.getFechaDesde(),
                config.getFechaHasta()
            );
            // ... más casos
        }
        */

        return new ArrayList<>(); // Retorna vacío para usar datos de ejemplo
    }

    /**
     * Interfaz para futura implementación de DataSource de reportes
     * Permite inyectar acceso a la base de datos
     */
    public interface ReportesDataSource {
        List<Map<String, Object>> obtenerStockActual(Map<String, String> filtros);
        List<Map<String, Object>> obtenerMovimientos(String articulo, java.time.LocalDate desde, java.time.LocalDate hasta);
        List<Map<String, Object>> obtenerRecibos(java.time.LocalDate desde, java.time.LocalDate hasta);
        List<Map<String, Object>> obtenerConsumoAlimento(Map<String, String> filtros);
        List<Map<String, Object>> obtenerAplicacionesSanitarias(java.time.LocalDate desde, java.time.LocalDate hasta);
        List<Map<String, Object>> obtenerProduccion(Map<String, String> filtros);
        List<Map<String, Object>> obtenerMortalidad(java.time.LocalDate desde, java.time.LocalDate hasta);
    }

    private ReportesDataSource dataSource;

    public void setDataSource(ReportesDataSource dataSource) {
        this.dataSource = dataSource;
    }
}

