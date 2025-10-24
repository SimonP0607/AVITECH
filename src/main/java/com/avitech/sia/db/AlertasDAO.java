package com.avitech.sia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class AlertasDAO {

    public static List<ItemAlerta> getAll() throws Exception {
        List<ItemAlerta> resultados = new ArrayList<>();

        // --- Query para Alertas de Medicamentos (existente) ---
        String sqlMedicamentos = "SELECT a.tipo, a.descripcion, a.categoria, a.fecha, a.estado, " +
                                 "m.stock, m.stock_minimo, m.nombre AS item_nombre " +
                                 "FROM Alertas a " +
                                 "LEFT JOIN Medicamentos m ON a.id_item = m.Id_Medicamento " +
                                 "WHERE a.estado = 'Activa' " +
                                 "ORDER BY a.fecha DESC";

        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement(sqlMedicamentos);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String articulo = rs.getString("item_nombre") != null ? rs.getString("item_nombre") : rs.getString("descripcion");
                String stockActual = rs.getObject("stock") != null ? String.valueOf(rs.getInt("stock")) : "N/A";
                String stockMinimo = rs.getObject("stock_minimo") != null ? String.valueOf(rs.getInt("stock_minimo")) : "N/A";
                String porcentaje = "N/A";
                if (rs.getObject("stock") != null && rs.getObject("stock_minimo") != null) {
                    int stock = rs.getInt("stock");
                    int min = rs.getInt("stock_minimo");
                    if (min > 0) {
                        double pct = (double) stock / min * 100;
                        porcentaje = String.format("%.1f%%", pct);
                    }
                }

                String diasRestantes = "N/A";
                if (rs.getDate("fecha") != null) {
                    LocalDate fechaAlerta = rs.getDate("fecha").toLocalDate();
                    long daysBetween = ChronoUnit.DAYS.between(fechaAlerta, LocalDate.now());
                    diasRestantes = String.valueOf(daysBetween);
                }

                resultados.add(new ItemAlerta(
                        articulo,
                        rs.getString("descripcion"),
                        rs.getString("categoria"),
                        stockActual,
                        stockMinimo,
                        porcentaje,
                        diasRestantes,
                        rs.getString("tipo")
                ));
            }
        }

        // --- Query para Alertas de Suministros ---
        String sqlSuministros = "SELECT s.item, s.unidad, " +
                                "SUM(CASE WHEN s.tipo = 'Entrada' THEN s.cantidad ELSE -s.cantidad END) as current_stock, " +
                                "MAX(s.stock_minimo) as min_stock_threshold " + // Usamos MAX para el umbral mínimo
                                "FROM Suministros s " +
                                "WHERE s.item NOT IN (SELECT nombre FROM Medicamentos) " + // Excluir ítems que son medicamentos
                                "GROUP BY s.item, s.unidad " +
                                "HAVING current_stock < MAX(s.stock_minimo)"; // Filtrar por stock bajo

        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement(sqlSuministros);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String itemNombre = rs.getString("item");
                String unidad = rs.getString("unidad");
                int currentStock = rs.getInt("current_stock");
                int minStockThreshold = rs.getInt("min_stock_threshold");

                String stockActualStr = String.valueOf(currentStock);
                String stockMinimoStr = String.valueOf(minStockThreshold);
                String porcentajeStr = "N/A";
                if (minStockThreshold > 0) {
                    double pct = (double) currentStock / minStockThreshold * 100;
                    porcentajeStr = String.format("%.1f%%", pct);
                }

                // Para suministros, los días restantes se establecen en 0 (alerta actual)
                String diasRestantesStr = "0";

                // Categoría y tipo por defecto para alertas de suministro
                String categoria = "Suministro";
                String tipo = "Bajo";

                resultados.add(new ItemAlerta(
                        itemNombre + " (" + unidad + ")", // Nombre del artículo con unidad
                        "Stock bajo para " + itemNombre + " (" + unidad + ")", // Descripción
                        categoria,
                        stockActualStr,
                        stockMinimoStr,
                        porcentajeStr,
                        diasRestantesStr,
                        tipo
                ));
            }
        }

        return resultados;
    }

    public static class ItemAlerta {
        private String articulo;
        private String descripcion;
        private String categoria;
        private String stockActual;
        private String stockMinimo;
        private String porcentaje;
        private String diasRestantes;
        private String tipo;

        public ItemAlerta(String articulo, String descripcion, String categoria, String stockActual, String stockMinimo,
                          String porcentaje, String diasRestantes, String tipo) {
            this.articulo = articulo;
            this.descripcion = descripcion;
            this.categoria = categoria;
            this.stockActual = stockActual;
            this.stockMinimo = stockMinimo;
            this.porcentaje = porcentaje;
            this.diasRestantes = diasRestantes;
            this.tipo = tipo;
        }

        // Getters para compatibilidad con PropertyValueFactory de JavaFX
        public String getArticulo() { return articulo; }
        public String getDescripcion() { return descripcion; }
        public String getCategoria() { return categoria; }
        public String getStockActual() { return stockActual; }
        public String getStockMinimo() { return stockMinimo; }
        public String getPorcentaje() { return porcentaje; }
        public String getDiasRestantes() { return diasRestantes; }
        public String getTipo() { return tipo; }
    }
}
