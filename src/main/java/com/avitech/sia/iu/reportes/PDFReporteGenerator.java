package com.avitech.sia.iu.reportes;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Generador de reportes en formato PDF usando iText7
 */
public class PDFReporteGenerator implements ReporteGenerator {

    @Override
    public File generarReporte(ReporteConfig config, List<? extends Object> datos) throws IOException {
        // Crear directorio de reportes si no existe
        File reportesDir = new File("reportes");
        if (!reportesDir.exists()) {
            reportesDir.mkdirs();
        }

        // Nombre del archivo
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = String.format("Reporte_%s_%s.pdf",
            config.getTipo().name(), timestamp);
        File pdfFile = new File(reportesDir, filename);

        // Crear PDF
        try (PdfWriter writer = new PdfWriter(pdfFile);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Título
            Paragraph titulo = new Paragraph("AVITECH - Sistema de Información Avícola")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titulo);

            // Subtítulo con tipo de reporte
            Paragraph subtitulo = new Paragraph(config.getTipo().getTitulo())
                    .setFontSize(14)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            document.add(subtitulo);

            // Fecha de generación
            String fechaGen = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            Paragraph fechaGeneracion = new Paragraph("Fecha de generación: " + fechaGen)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(5);
            document.add(fechaGeneracion);

            // Filtros aplicados
            if (!config.getFiltros().isEmpty() || config.getFechaDesde() != null) {
                document.add(new Paragraph("Filtros Aplicados:")
                        .setFontSize(12)
                        .setBold()
                        .setMarginTop(10));

                if (config.getFechaDesde() != null && config.getFechaHasta() != null) {
                    document.add(new Paragraph(String.format("Período: %s a %s",
                            config.getFechaDesde().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            config.getFechaHasta().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))))
                            .setFontSize(10));
                }

                for (Map.Entry<String, String> filtro : config.getFiltros().entrySet()) {
                    document.add(new Paragraph(filtro.getKey() + ": " + filtro.getValue())
                            .setFontSize(10));
                }
            }

            // Espacio
            document.add(new Paragraph("\n"));

            // Contenido según tipo de reporte
            generarContenido(document, config.getTipo(), datos);

            // Pie de página
            document.add(new Paragraph("\n\n"));
            document.add(new Paragraph("_".repeat(80))
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Este reporte fue generado automáticamente por el Sistema AVITECH")
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setItalic());
        }

        return pdfFile;
    }

    private void generarContenido(Document document, TipoReporte tipo, List<? extends Object> datos) {
        switch (tipo) {
            case STOCK_ACTUAL -> generarReporteStock(document, datos);
            case REGISTRO_ARTICULO -> generarReporteArticulo(document, datos);
            case RECIBOS_INSUMOS -> generarReporteRecibos(document, datos);
            case CONSUMO_ALIMENTO -> generarReporteConsumo(document, datos);
            case APLICACIONES_SANITARIAS -> generarReporteAplicaciones(document, datos);
            case PRODUCCION_LOTE -> generarReporteProduccion(document, datos);
            case MORTALIDAD -> generarReporteMortalidad(document, datos);
        }
    }

    private void generarReporteStock(Document document, List<? extends Object> datos) {
        document.add(new Paragraph("Inventario Actual")
                .setFontSize(12)
                .setBold()
                .setMarginBottom(10));

        // Crear tabla
        float[] columnWidths = {3, 2, 2, 2, 2, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        // Encabezados
        String[] headers = {"Artículo", "Categoría", "Stock Actual", "Stock Mínimo", "Ubicación", "Estado"};
        for (String header : headers) {
            Cell cell = new Cell().add(new Paragraph(header).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER);
            table.addCell(cell);
        }

        // Datos (placeholder - se llenará con datos reales de BD)
        if (datos.isEmpty()) {
            agregarDatosEjemplo(table,
                new String[]{"Alimento Balanceado Premium", "Alimentos", "500 kg", "200 kg", "Almacén Principal", "Normal"},
                new String[]{"Vacuna Newcastle", "Medicamentos", "150 dosis", "100 dosis", "Farmacia", "Normal"},
                new String[]{"Desinfectante Cloro", "Limpieza", "45 L", "50 L", "Almacén Secundario", "Bajo"});
        } else {
            // Aquí se procesarán los datos reales de la BD
            for (Object dato : datos) {
                if (dato instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> row = (Map<String, Object>) dato;
                    table.addCell(String.valueOf(row.getOrDefault("articulo", "")));
                    table.addCell(String.valueOf(row.getOrDefault("categoria", "")));
                    table.addCell(String.valueOf(row.getOrDefault("stock_actual", "")));
                    table.addCell(String.valueOf(row.getOrDefault("stock_minimo", "")));
                    table.addCell(String.valueOf(row.getOrDefault("ubicacion", "")));
                    table.addCell(String.valueOf(row.getOrDefault("estado", "")));
                }
            }
        }

        document.add(table);
    }

    private void generarReporteArticulo(Document document, List<? extends Object> datos) {
        document.add(new Paragraph("Registro de Movimientos por Artículo")
                .setFontSize(12)
                .setBold()
                .setMarginBottom(10));

        float[] columnWidths = {2, 2, 2, 2, 3};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        String[] headers = {"Fecha", "Tipo", "Cantidad", "Responsable", "Observaciones"};
        for (String header : headers) {
            table.addCell(new Cell().add(new Paragraph(header).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
        }

        if (datos.isEmpty()) {
            agregarDatosEjemplo(table,
                new String[]{"15/10/2024", "Entrada", "+500 kg", "Juan Pérez", "Compra mensual"},
                new String[]{"20/10/2024", "Salida", "-120 kg", "María López", "Alimentación lote A"},
                new String[]{"25/10/2024", "Salida", "-80 kg", "Carlos Ruiz", "Alimentación lote B"});
        }

        document.add(table);
    }

    private void generarReporteRecibos(Document document, List<? extends Object> datos) {
        document.add(new Paragraph("Registro de Recibos de Insumos")
                .setFontSize(12)
                .setBold()
                .setMarginBottom(10));

        float[] columnWidths = {2, 3, 2, 2, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        String[] headers = {"Fecha", "Proveedor", "Artículo", "Cantidad", "Costo Total"};
        for (String header : headers) {
            table.addCell(new Cell().add(new Paragraph(header).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
        }

        if (datos.isEmpty()) {
            agregarDatosEjemplo(table,
                new String[]{"01/10/2024", "Proveedora Avícola S.A.", "Alimento Balanceado", "1000 kg", "$850.00"},
                new String[]{"10/10/2024", "Farmacia Veterinaria", "Vacunas", "500 dosis", "$1,200.00"});
        }

        document.add(table);
    }

    private void generarReporteConsumo(Document document, List<? extends Object> datos) {
        document.add(new Paragraph("Análisis de Consumo de Alimento")
                .setFontSize(12)
                .setBold()
                .setMarginBottom(10));

        float[] columnWidths = {2, 2, 2, 2, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        String[] headers = {"Lote", "Período", "Consumo Total", "Promedio Diario", "Aves"};
        for (String header : headers) {
            table.addCell(new Cell().add(new Paragraph(header).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
        }

        if (datos.isEmpty()) {
            agregarDatosEjemplo(table,
                new String[]{"Lote A-2024", "01-15/10/24", "750 kg", "50 kg/día", "500"},
                new String[]{"Lote B-2024", "01-15/10/24", "600 kg", "40 kg/día", "400"});
        }

        document.add(table);
    }

    private void generarReporteAplicaciones(Document document, List<? extends Object> datos) {
        document.add(new Paragraph("Registro de Aplicaciones Sanitarias")
                .setFontSize(12)
                .setBold()
                .setMarginBottom(10));

        float[] columnWidths = {2, 3, 2, 2, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        String[] headers = {"Fecha", "Tratamiento", "Lote", "Dosis", "Responsable"};
        for (String header : headers) {
            table.addCell(new Cell().add(new Paragraph(header).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
        }

        if (datos.isEmpty()) {
            agregarDatosEjemplo(table,
                new String[]{"05/10/2024", "Vacuna Newcastle", "Lote A-2024", "500 dosis", "Dr. Martínez"},
                new String[]{"12/10/2024", "Vitaminas", "Lote B-2024", "400 ml", "Dr. Martínez"});
        }

        document.add(table);
    }

    private void generarReporteProduccion(Document document, List<? extends Object> datos) {
        document.add(new Paragraph("Análisis de Producción por Lote")
                .setFontSize(12)
                .setBold()
                .setMarginBottom(10));

        float[] columnWidths = {2, 2, 2, 2, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        String[] headers = {"Lote", "Fecha", "Tamaño", "Cantidad", "Calidad"};
        for (String header : headers) {
            table.addCell(new Cell().add(new Paragraph(header).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
        }

        if (datos.isEmpty()) {
            agregarDatosEjemplo(table,
                new String[]{"Lote A-2024", "20/10/2024", "Grande", "1200 huevos", "A"},
                new String[]{"Lote A-2024", "20/10/2024", "Mediano", "800 huevos", "A"},
                new String[]{"Lote B-2024", "20/10/2024", "Grande", "950 huevos", "B"});
        }

        document.add(table);
    }

    private void generarReporteMortalidad(Document document, List<? extends Object> datos) {
        document.add(new Paragraph("Registro de Mortalidad")
                .setFontSize(12)
                .setBold()
                .setMarginBottom(10));

        float[] columnWidths = {2, 2, 2, 3, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        String[] headers = {"Fecha", "Lote", "Cantidad", "Causa", "% Mortalidad"};
        for (String header : headers) {
            table.addCell(new Cell().add(new Paragraph(header).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
        }

        if (datos.isEmpty()) {
            agregarDatosEjemplo(table,
                new String[]{"15/10/2024", "Lote A-2024", "3", "Enfermedad respiratoria", "0.6%"},
                new String[]{"18/10/2024", "Lote B-2024", "2", "Causa natural", "0.5%"});
        }

        document.add(table);
    }

    private void agregarDatosEjemplo(Table table, String[]... filas) {
        for (String[] fila : filas) {
            for (String celda : fila) {
                table.addCell(new Cell().add(new Paragraph(celda)));
            }
        }
    }

    @Override
    public boolean soportaFormato(FormatoReporte formato) {
        return formato == FormatoReporte.PDF;
    }
}
