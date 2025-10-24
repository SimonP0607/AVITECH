package com.avitech.sia.db;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReportesDAO {

    public static List<StockActualReportItem> getStockActualReport(String articuloFilter, String categoriaFilter) throws Exception {
        List<StockActualReportItem> reportItems = new ArrayList<>();

        // Obtener inventario de suministros
        List<SuministrosDAO.InventarioItem> suministros = SuministrosDAO.getInventario();
        for (SuministrosDAO.InventarioItem item : suministros) {
            reportItems.add(new StockActualReportItem(
                    item.producto,
                    item.unidad,
                    item.stock,
                    0, // Stock mínimo no disponible directamente aquí para suministros, se necesitaría otra consulta o un campo en InventarioItem
                    "Suministro"
            ));
        }

        // Obtener inventario de medicamentos
        List<MedicamentosDAO.MedicamentoItem> medicamentos = MedicamentosDAO.getInventario();
        for (MedicamentosDAO.MedicamentoItem item : medicamentos) {
            reportItems.add(new StockActualReportItem(
                    item.nombre,
                    "unidad", // Asumiendo una unidad por defecto para medicamentos si no está en la DB
                    item.stock,
                    item.stockMinimo,
                    "Medicamento"
            ));
        }

        // Aplicar filtros
        return reportItems.stream()
                .filter(item -> articuloFilter == null || articuloFilter.equals("Todos los artículos") || item.getArticulo().equalsIgnoreCase(articuloFilter))
                .filter(item -> categoriaFilter == null || categoriaFilter.equals("Todas las categorías") || item.getCategoria().equalsIgnoreCase(categoriaFilter))
                .collect(Collectors.toList());
    }

    public static List<SuministrosDAO.Mov> getRegistroArticuloReport(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            String articuloFilter,
            String responsableFilter
    ) throws Exception {
        List<SuministrosDAO.Mov> allMovements = SuministrosDAO.getAll();

        return allMovements.stream()
                .filter(mov -> {
                    boolean matchesDate = true;
                    if (fechaDesde != null && mov.localDate != null) {
                        matchesDate = !mov.localDate.isBefore(fechaDesde);
                    }
                    if (fechaHasta != null && mov.localDate != null) {
                        matchesDate = matchesDate && !mov.localDate.isAfter(fechaHasta);
                    }

                    boolean matchesArticulo = true;
                    if (articuloFilter != null && !articuloFilter.equals("Todos los artículos")) {
                        matchesArticulo = mov.itemLc.contains(articuloFilter.toLowerCase());
                    }

                    boolean matchesResponsable = true;
                    if (responsableFilter != null && !responsableFilter.equals("Todos los responsables")) {
                        matchesResponsable = mov.respLc.contains(responsableFilter.toLowerCase());
                    }

                    return matchesDate && matchesArticulo && matchesResponsable;
                })
                .collect(Collectors.toList());
    }

    public static class StockActualReportItem {
        private final String articulo;
        private final String unidad;
        private final int stockActual;
        private final int stockMinimo;
        private final String categoria;

        public StockActualReportItem(String articulo, String unidad, int stockActual, int stockMinimo, String categoria) {
            this.articulo = articulo;
            this.unidad = unidad;
            this.stockActual = stockActual;
            this.stockMinimo = stockMinimo;
            this.categoria = categoria;
        }

        public String getArticulo() { return articulo; }
        public String getUnidad() { return unidad; }
        public int getStockActual() { return stockActual; }
        public int getStockMinimo() { return stockMinimo; }
        public String getCategoria() { return categoria; }
    }
}
