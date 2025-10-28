package com.avitech.sia.iu.reportes;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Generador de reportes en formato Excel usando Apache POI
 */
public class ExcelReporteGenerator implements ReporteGenerator {

    @Override
    public File generarReporte(ReporteConfig config, List<? extends Object> datos) throws IOException {
        // Crear directorio de reportes si no existe
        File reportesDir = new File("reportes");
        if (!reportesDir.exists()) {
            reportesDir.mkdirs();
        }

        // Nombre del archivo
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = String.format("Reporte_%s_%s.xlsx",
            config.getTipo().name(), timestamp);
        File excelFile = new File(reportesDir, filename);

        // Crear libro de Excel
        try (Workbook workbook = new XSSFWorkbook()) {
            // Crear hoja
            Sheet sheet = workbook.createSheet(config.getTipo().getTitulo());

            // Estilos
            CellStyle headerStyle = crearEstiloEncabezado(workbook);
            CellStyle titleStyle = crearEstiloTitulo(workbook);
            CellStyle dataStyle = crearEstiloDatos(workbook);

            int rowNum = 0;

            // Título principal
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("AVITECH - Sistema de Información Avícola");
            titleCell.setCellStyle(titleStyle);

            // Subtítulo
            Row subtitleRow = sheet.createRow(rowNum++);
            Cell subtitleCell = subtitleRow.createCell(0);
            subtitleCell.setCellValue(config.getTipo().getTitulo());
            subtitleCell.setCellStyle(titleStyle);

            // Fecha de generación
            Row dateRow = sheet.createRow(rowNum++);
            Cell dateCell = dateRow.createCell(0);
            String fechaGen = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            dateCell.setCellValue("Fecha de generación: " + fechaGen);

            // Espacio
            rowNum++;

            // Filtros aplicados
            if (!config.getFiltros().isEmpty() || config.getFechaDesde() != null) {
                Row filtrosHeaderRow = sheet.createRow(rowNum++);
                Cell filtrosHeaderCell = filtrosHeaderRow.createCell(0);
                filtrosHeaderCell.setCellValue("Filtros Aplicados:");
                filtrosHeaderCell.setCellStyle(headerStyle);

                if (config.getFechaDesde() != null && config.getFechaHasta() != null) {
                    Row periodoRow = sheet.createRow(rowNum++);
                    periodoRow.createCell(0).setCellValue(String.format("Período: %s a %s",
                            config.getFechaDesde().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            config.getFechaHasta().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
                }

                for (Map.Entry<String, String> filtro : config.getFiltros().entrySet()) {
                    Row filtroRow = sheet.createRow(rowNum++);
                    filtroRow.createCell(0).setCellValue(filtro.getKey() + ": " + filtro.getValue());
                }

                rowNum++; // Espacio
            }

            // Contenido según tipo de reporte
            rowNum = generarContenido(sheet, rowNum, config.getTipo(), datos, headerStyle, dataStyle);

            // Ajustar ancho de columnas
            for (int i = 0; i < 10; i++) {
                sheet.autoSizeColumn(i);
            }

            // Guardar archivo
            try (FileOutputStream outputStream = new FileOutputStream(excelFile)) {
                workbook.write(outputStream);
            }
        }

        return excelFile;
    }

    private CellStyle crearEstiloEncabezado(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle crearEstiloTitulo(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle crearEstiloDatos(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private int generarContenido(Sheet sheet, int startRow, TipoReporte tipo,
                                  List<? extends Object> datos, CellStyle headerStyle, CellStyle dataStyle) {
        return switch (tipo) {
            case STOCK_ACTUAL -> generarReporteStock(sheet, startRow, datos, headerStyle, dataStyle);
            case REGISTRO_ARTICULO -> generarReporteArticulo(sheet, startRow, datos, headerStyle, dataStyle);
            case RECIBOS_INSUMOS -> generarReporteRecibos(sheet, startRow, datos, headerStyle, dataStyle);
            case CONSUMO_ALIMENTO -> generarReporteConsumo(sheet, startRow, datos, headerStyle, dataStyle);
            case APLICACIONES_SANITARIAS -> generarReporteAplicaciones(sheet, startRow, datos, headerStyle, dataStyle);
            case PRODUCCION_LOTE -> generarReporteProduccion(sheet, startRow, datos, headerStyle, dataStyle);
            case MORTALIDAD -> generarReporteMortalidad(sheet, startRow, datos, headerStyle, dataStyle);
        };
    }

    private int generarReporteStock(Sheet sheet, int startRow, List<? extends Object> datos,
                                    CellStyle headerStyle, CellStyle dataStyle) {
        int rowNum = startRow;

        // Encabezados
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Artículo", "Categoría", "Stock Actual", "Stock Mínimo", "Ubicación", "Estado"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Datos
        if (datos.isEmpty()) {
            String[][] datosEjemplo = {
                {"Alimento Balanceado Premium", "Alimentos", "500 kg", "200 kg", "Almacén Principal", "Normal"},
                {"Vacuna Newcastle", "Medicamentos", "150 dosis", "100 dosis", "Farmacia", "Normal"},
                {"Desinfectante Cloro", "Limpieza", "45 L", "50 L", "Almacén Secundario", "Bajo"}
            };
            rowNum = agregarDatosEjemplo(sheet, rowNum, datosEjemplo, dataStyle);
        } else {
            for (Object dato : datos) {
                if (dato instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> row = (Map<String, Object>) dato;
                    Row dataRow = sheet.createRow(rowNum++);
                    int col = 0;
                    crearCeldaConEstilo(dataRow, col++, String.valueOf(row.getOrDefault("articulo", "")), dataStyle);
                    crearCeldaConEstilo(dataRow, col++, String.valueOf(row.getOrDefault("categoria", "")), dataStyle);
                    crearCeldaConEstilo(dataRow, col++, String.valueOf(row.getOrDefault("stock_actual", "")), dataStyle);
                    crearCeldaConEstilo(dataRow, col++, String.valueOf(row.getOrDefault("stock_minimo", "")), dataStyle);
                    crearCeldaConEstilo(dataRow, col++, String.valueOf(row.getOrDefault("ubicacion", "")), dataStyle);
                    crearCeldaConEstilo(dataRow, col++, String.valueOf(row.getOrDefault("estado", "")), dataStyle);
                }
            }
        }

        return rowNum;
    }

    private int generarReporteArticulo(Sheet sheet, int startRow, List<? extends Object> datos,
                                       CellStyle headerStyle, CellStyle dataStyle) {
        int rowNum = startRow;

        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Fecha", "Tipo", "Cantidad", "Responsable", "Observaciones"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        if (datos.isEmpty()) {
            String[][] datosEjemplo = {
                {"15/10/2024", "Entrada", "+500 kg", "Juan Pérez", "Compra mensual"},
                {"20/10/2024", "Salida", "-120 kg", "María López", "Alimentación lote A"},
                {"25/10/2024", "Salida", "-80 kg", "Carlos Ruiz", "Alimentación lote B"}
            };
            rowNum = agregarDatosEjemplo(sheet, rowNum, datosEjemplo, dataStyle);
        }

        return rowNum;
    }

    private int generarReporteRecibos(Sheet sheet, int startRow, List<? extends Object> datos,
                                      CellStyle headerStyle, CellStyle dataStyle) {
        int rowNum = startRow;

        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Fecha", "Proveedor", "Artículo", "Cantidad", "Costo Total"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        if (datos.isEmpty()) {
            String[][] datosEjemplo = {
                {"01/10/2024", "Proveedora Avícola S.A.", "Alimento Balanceado", "1000 kg", "$850.00"},
                {"10/10/2024", "Farmacia Veterinaria", "Vacunas", "500 dosis", "$1,200.00"}
            };
            rowNum = agregarDatosEjemplo(sheet, rowNum, datosEjemplo, dataStyle);
        }

        return rowNum;
    }

    private int generarReporteConsumo(Sheet sheet, int startRow, List<? extends Object> datos,
                                      CellStyle headerStyle, CellStyle dataStyle) {
        int rowNum = startRow;

        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Lote", "Período", "Consumo Total", "Promedio Diario", "Aves"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        if (datos.isEmpty()) {
            String[][] datosEjemplo = {
                {"Lote A-2024", "01-15/10/24", "750 kg", "50 kg/día", "500"},
                {"Lote B-2024", "01-15/10/24", "600 kg", "40 kg/día", "400"}
            };
            rowNum = agregarDatosEjemplo(sheet, rowNum, datosEjemplo, dataStyle);
        }

        return rowNum;
    }

    private int generarReporteAplicaciones(Sheet sheet, int startRow, List<? extends Object> datos,
                                           CellStyle headerStyle, CellStyle dataStyle) {
        int rowNum = startRow;

        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Fecha", "Tratamiento", "Lote", "Dosis", "Responsable"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        if (datos.isEmpty()) {
            String[][] datosEjemplo = {
                {"05/10/2024", "Vacuna Newcastle", "Lote A-2024", "500 dosis", "Dr. Martínez"},
                {"12/10/2024", "Vitaminas", "Lote B-2024", "400 ml", "Dr. Martínez"}
            };
            rowNum = agregarDatosEjemplo(sheet, rowNum, datosEjemplo, dataStyle);
        }

        return rowNum;
    }

    private int generarReporteProduccion(Sheet sheet, int startRow, List<? extends Object> datos,
                                        CellStyle headerStyle, CellStyle dataStyle) {
        int rowNum = startRow;

        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Lote", "Fecha", "Tamaño", "Cantidad", "Calidad"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        if (datos.isEmpty()) {
            String[][] datosEjemplo = {
                {"Lote A-2024", "20/10/2024", "Grande", "1200 huevos", "A"},
                {"Lote A-2024", "20/10/2024", "Mediano", "800 huevos", "A"},
                {"Lote B-2024", "20/10/2024", "Grande", "950 huevos", "B"}
            };
            rowNum = agregarDatosEjemplo(sheet, rowNum, datosEjemplo, dataStyle);
        }

        return rowNum;
    }

    private int generarReporteMortalidad(Sheet sheet, int startRow, List<? extends Object> datos,
                                        CellStyle headerStyle, CellStyle dataStyle) {
        int rowNum = startRow;

        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Fecha", "Lote", "Cantidad", "Causa", "% Mortalidad"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        if (datos.isEmpty()) {
            String[][] datosEjemplo = {
                {"15/10/2024", "Lote A-2024", "3", "Enfermedad respiratoria", "0.6%"},
                {"18/10/2024", "Lote B-2024", "2", "Causa natural", "0.5%"}
            };
            rowNum = agregarDatosEjemplo(sheet, rowNum, datosEjemplo, dataStyle);
        }

        return rowNum;
    }

    private int agregarDatosEjemplo(Sheet sheet, int startRow, String[][] datos, CellStyle dataStyle) {
        int rowNum = startRow;
        for (String[] fila : datos) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < fila.length; i++) {
                crearCeldaConEstilo(row, i, fila[i], dataStyle);
            }
        }
        return rowNum;
    }

    private void crearCeldaConEstilo(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    @Override
    public boolean soportaFormato(FormatoReporte formato) {
        return formato == FormatoReporte.EXCEL;
    }
}

