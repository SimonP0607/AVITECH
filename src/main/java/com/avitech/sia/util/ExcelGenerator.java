package com.avitech.sia.util;

import com.avitech.sia.iu.ReportesController;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelGenerator {

    /**
     * Genera un reporte de stock actual en formato Excel.
     * @param stockItems La lista de ítems de stock a incluir en el reporte.
     * @param filePath La ruta completa donde se guardará el archivo Excel.
     * @throws IOException Si ocurre un error al escribir el archivo.
     */
    public static void generateStockReport(List<ReportesController.StockItem> stockItems, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Stock Actual");

        // Crear estilo para el encabezado
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
        headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Crear encabezados
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Nombre", "Categoría", "Cantidad", "Unidad de Medida"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerCellStyle);
        }

        // Llenar datos
        int rowNum = 1;
        for (ReportesController.StockItem item : stockItems) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.nombre);
            row.createCell(1).setCellValue(item.categoria);
            row.createCell(2).setCellValue(item.cantidad);
            row.createCell(3).setCellValue(item.unidadMedida);
        }

        // Autoajustar el tamaño de las columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Escribir el archivo
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }
}
