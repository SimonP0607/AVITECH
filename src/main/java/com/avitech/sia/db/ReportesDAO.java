package com.avitech.sia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.avitech.sia.iu.ProduccionController.ProduccionRow; // Importar ProduccionRow

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

    public static List<SuministrosDAO.Mov> getRecibosInsumosReport(
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

                    return matchesDate && matchesArticulo && matchesResponsable && mov.tipo.equalsIgnoreCase("Entrada");
                })
                .collect(Collectors.toList());
    }

    public static List<AplicacionSanitariaReportItem> getAplicacionesSanitariasReport(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            String loteFilter,
            String articuloFilter,
            String responsableFilter
    ) throws Exception {
        List<AplicacionSanitariaReportItem> resultados = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT ps.fecha, l.nombre_lote, ps.nombre_enfermedad, ps.descripcion, ps.observaciones, m.nombre AS medicamento_nombre, ps.muertes ");
        sql.append("FROM Plan_Sanitario ps ");
        sql.append("JOIN Lote l ON ps.Id_lote = l.Id_lote ");
        sql.append("LEFT JOIN Medicamentos m ON ps.Id_medicamento = m.Id_Medicamento ");
        sql.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (fechaDesde != null) {
            sql.append("AND ps.fecha >= ? ");
            params.add(Date.valueOf(fechaDesde));
        }
        if (fechaHasta != null) {
            sql.append("AND ps.fecha <= ? ");
            params.add(Date.valueOf(fechaHasta));
        }
        if (loteFilter != null && !loteFilter.equals("Todos los lotes")) {
            sql.append("AND l.nombre_lote = ? ");
            params.add(loteFilter);
        }
        if (articuloFilter != null && !articuloFilter.equals("Todos los artículos")) {
            sql.append("AND (ps.nombre_enfermedad LIKE ? OR m.nombre LIKE ?) ");
            params.add("%" + articuloFilter + "%");
            params.add("%" + articuloFilter + "%");
        }
        if (responsableFilter != null && !responsableFilter.equals("Todos los responsables")) {
            // El responsable puede estar incrustado en las observaciones para eventos
            sql.append("AND ps.observaciones LIKE ? ");
            params.add("%" + responsableFilter + "%");
        }

        sql.append("ORDER BY ps.fecha DESC");

        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultados.add(new AplicacionSanitariaReportItem(
                            rs.getDate("fecha").toLocalDate(),
                            rs.getString("nombre_lote"),
                            rs.getString("nombre_enfermedad"),
                            rs.getString("descripcion"),
                            rs.getString("observaciones"),
                            rs.getString("medicamento_nombre"),
                            rs.getInt("muertes")
                    ));
                }
            }
        }
        return resultados;
    }

    public static List<ProduccionPorLoteReportItem> getProduccionPorLoteReport(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            String loteFilter,
            String responsableFilter
    ) throws Exception {
        List<ProduccionPorLoteReportItem> resultados = new ArrayList<>();
        List<ProduccionRow> produccionDiaria = ProduccionDAO.getProduccionDiaria(loteFilter.equals("Todos los lotes") ? null : loteFilter);

        for (ProduccionRow row : produccionDiaria) {
            LocalDate fecha = row.getFecha();
            String lote = row.getGalpon();
            String responsable = row.getResponsable();

            boolean matchesDate = true;
            if (fechaDesde != null) {
                matchesDate = !fecha.isBefore(fechaDesde);
            }
            if (fechaHasta != null) {
                matchesDate = matchesDate && !fecha.isAfter(fechaHasta);
            }

            boolean matchesResponsable = true;
            if (responsableFilter != null && !responsableFilter.equals("Todos los responsables")) {
                matchesResponsable = responsable.toLowerCase().contains(responsableFilter.toLowerCase());
            }

            if (matchesDate && matchesResponsable) {
                resultados.add(new ProduccionPorLoteReportItem(
                        fecha,
                        lote,
                        row.getAves(),
                        row.getProduccion(),
                        row.getPostura(),
                        row.getClasificacion(),
                        row.getMortalidad(),
                        responsable
                ));
            }
        }
        return resultados;
    }

    public static List<MortalidadReportItem> getMortalidadReport(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            String loteFilter,
            String responsableFilter
    ) throws Exception {
        List<MortalidadReportItem> resultados = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT ps.fecha, l.nombre_lote, ps.muertes, ps.descripcion, ps.observaciones ");
        sql.append("FROM Plan_Sanitario ps ");
        sql.append("JOIN Lote l ON ps.Id_lote = l.Id_lote ");
        sql.append("WHERE ps.nombre_enfermedad = 'Mortalidad' ");

        List<Object> params = new ArrayList<>();

        if (fechaDesde != null) {
            sql.append("AND ps.fecha >= ? ");
            params.add(Date.valueOf(fechaDesde));
        }
        if (fechaHasta != null) {
            sql.append("AND ps.fecha <= ? ");
            params.add(Date.valueOf(fechaHasta));
        }
        if (loteFilter != null && !loteFilter.equals("Todos los lotes")) {
            sql.append("AND l.nombre_lote = ? ");
            params.add(loteFilter);
        }
        if (responsableFilter != null && !responsableFilter.equals("Todos los responsables")) {
            sql.append("AND ps.observaciones LIKE ? "); // Asumiendo que el responsable está en observaciones
            params.add("%" + responsableFilter + "%");
        }

        sql.append("ORDER BY ps.fecha DESC");

        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultados.add(new MortalidadReportItem(
                            rs.getDate("fecha").toLocalDate(),
                            rs.getString("nombre_lote"),
                            rs.getInt("muertes"),
                            rs.getString("descripcion"),
                            rs.getString("observaciones")
                    ));
                }
            }
        }
        return resultados;
    }

    public static List<ConsumoAlimentoReportItem> getConsumoAlimentoReport(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            String loteFilter, // Este filtro no se puede aplicar directamente a la tabla Suministros sin una columna 'lote'
            String articuloFilter,
            String responsableFilter
    ) throws Exception {
        List<ConsumoAlimentoReportItem> resultados = new ArrayList<>();
        List<SuministrosDAO.Mov> allMovements = SuministrosDAO.getAll();

        for (SuministrosDAO.Mov mov : allMovements) {
            // Filtrar por tipo "Salida"
            if (!mov.tipo.equalsIgnoreCase("Salida")) {
                continue;
            }

            // Filtrar por fecha
            boolean matchesDate = true;
            if (fechaDesde != null && mov.localDate != null) {
                matchesDate = !mov.localDate.isBefore(fechaDesde);
            }
            if (fechaHasta != null && mov.localDate != null) {
                matchesDate = matchesDate && !mov.localDate.isAfter(fechaHasta);
            }
            if (!matchesDate) {
                continue;
            }

            // Filtrar por artículo (asumiendo que los ítems de 'Alimento' se especifican en articuloFilter o se manejan implícitamente)
            boolean matchesArticulo = true;
            if (articuloFilter != null && !articuloFilter.equals("Todos los artículos")) {
                matchesArticulo = mov.itemLc.contains(articuloFilter.toLowerCase());
            }
            if (!matchesArticulo) {
                continue;
            }

            // Filtrar por responsable
            boolean matchesResponsable = true;
            if (responsableFilter != null && !responsableFilter.equals("Todos los responsables")) {
                matchesResponsable = mov.respLc.contains(responsableFilter.toLowerCase());
            }
            if (!matchesResponsable) {
                continue;
            }

            // Nota: loteFilter no se puede aplicar aquí ya que la tabla Suministros no tiene una columna de lote.
            // Si el filtrado por lote es crítico para "Consumo de Alimento", el esquema de la BD o la fuente de datos necesita ajuste.

            resultados.add(new ConsumoAlimentoReportItem(
                    mov.localDate,
                    mov.item,
                    Integer.parseInt(mov.cantidad),
                    mov.unidad,
                    mov.responsable,
                    mov.detalles
            ));
        }
        return resultados;
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

    public static class AplicacionSanitariaReportItem {
        private final LocalDate fecha;
        private final String lote;
        private final String tipo; // e.g., "Aplicación de [Medicamento]" or "Enfermedad"
        private final String descripcion;
        private final String observaciones;
        private final String medicamento; // If it's an application
        private final int muertes; // If it's a mortality event

        public AplicacionSanitariaReportItem(LocalDate fecha, String lote, String tipo, String descripcion, String observaciones, String medicamento, int muertes) {
            this.fecha = fecha;
            this.lote = lote;
            this.tipo = tipo;
            this.descripcion = descripcion;
            this.observaciones = observaciones;
            this.medicamento = medicamento;
            this.muertes = muertes;
        }

        // Getters
        public LocalDate getFecha() { return fecha; }
        public String getLote() { return lote; }
        public String getTipo() { return tipo; }
        public String getDescripcion() { return descripcion; }
        public String getObservaciones() { return observaciones; }
        public String getMedicamento() { return medicamento; }
        public int getMuertes() { return muertes; }
    }

    public static class ProduccionPorLoteReportItem {
        private final LocalDate fecha;
        private final String lote;
        private final int aves;
        private final int produccionTotal;
        private final String postura;
        private final String clasificacionHuevos;
        private final int mortalidad;
        private final String responsable;

        public ProduccionPorLoteReportItem(LocalDate fecha, String lote, int aves, int produccionTotal, String postura, String clasificacionHuevos, int mortalidad, String responsable) {
            this.fecha = fecha;
            this.lote = lote;
            this.aves = aves;
            this.produccionTotal = produccionTotal;
            this.postura = postura;
            this.clasificacionHuevos = clasificacionHuevos;
            this.mortalidad = mortalidad;
            this.responsable = responsable;
        }

        // Getters
        public LocalDate getFecha() { return fecha; }
        public String getLote() { return lote; }
        public int getAves() { return aves; }
        public int getProduccionTotal() { return produccionTotal; }
        public String getPostura() { return postura; }
        public String getClasificacionHuevos() { return clasificacionHuevos; }
        public int getMortalidad() { return mortalidad; }
        public String getResponsable() { return responsable; }
    }

    public static class MortalidadReportItem {
        private final LocalDate fecha;
        private final String lote;
        private final int muertes;
        private final String descripcion;
        private final String observaciones;

        public MortalidadReportItem(LocalDate fecha, String lote, int muertes, String descripcion, String observaciones) {
            this.fecha = fecha;
            this.lote = lote;
            this.muertes = muertes;
            this.descripcion = descripcion;
            this.observaciones = observaciones;
        }

        // Getters
        public LocalDate getFecha() { return fecha; }
        public String getLote() { return lote; }
        public int getMuertes() { return muertes; }
        public String getDescripcion() { return descripcion; }
        public String getObservaciones() { return observaciones; }
    }

    public static class ConsumoAlimentoReportItem {
        private final LocalDate fecha;
        private final String item;
        private final int cantidad;
        private final String unidad;
        private final String responsable;
        private final String motivo;

        public ConsumoAlimentoReportItem(LocalDate fecha, String item, int cantidad, String unidad, String responsable, String motivo) {
            this.fecha = fecha;
            this.item = item;
            this.cantidad = cantidad;
            this.unidad = unidad;
            this.responsable = responsable;
            this.motivo = motivo;
        }

        // Getters
        public LocalDate getFecha() { return fecha; }
        public String getItem() { return item; }
        public int getCantidad() { return cantidad; }
        public String getUnidad() { return unidad; }
        public String getResponsable() { return responsable; }
        public String getMotivo() { return motivo; }
    }
}
