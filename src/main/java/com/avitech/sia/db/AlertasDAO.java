    package com.avitech.sia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AlertasDAO {

    public static List<ItemAlerta> getAll() throws Exception {
        List<ItemAlerta> resultados = new ArrayList<>();
        String sql = "SELECT a.tipo, a.descripcion, a.categoria, a.fecha, a.estado, " +
                     "m.stock, m.stock_minimo, m.nombre AS item_nombre " +
                     "FROM Alertas a " +
                     "LEFT JOIN Medicamentos m ON a.id_item = m.Id_Medicamento " +
                     "WHERE a.estado = 'Activa' " +
                     "ORDER BY a.fecha DESC";

        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement(sql);
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
                String diasRestantes = "N/A"; // Placeholder, podría calcular días desde fecha si es relevante
                String ubicacion = "N/A"; // Placeholder, no hay ubicación en las tablas actuales

                resultados.add(new ItemAlerta(
                        articulo,
                        rs.getString("descripcion"),
                        rs.getString("categoria"),
                        stockActual,
                        stockMinimo,
                        porcentaje,
                        diasRestantes,
                        ubicacion,
                        rs.getString("tipo")
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
        private String ubicacion;
        private String tipo;

        public ItemAlerta(String articulo, String descripcion, String categoria, String stockActual, String stockMinimo,
                          String porcentaje, String diasRestantes, String ubicacion, String tipo) {
            this.articulo = articulo;
            this.descripcion = descripcion;
            this.categoria = categoria;
            this.stockActual = stockActual;
            this.stockMinimo = stockMinimo;
            this.porcentaje = porcentaje;
            this.diasRestantes = diasRestantes;
            this.ubicacion = ubicacion;
            this.tipo = tipo;
        }

        // Getters para compatibilidad con PropertyValueFactory de JavaFX
        public String getArticulo() { return articulo; } // Mapeo a la columna 'Artículo'
        public String getDescripcion() { return descripcion; }
        public String getCategoria() { return categoria; }
        public String getStockActual() { return stockActual; }
        public String getStockMinimo() { return stockMinimo; }
        public String getPorcentaje() { return porcentaje; }
        public String getDiasRestantes() { return diasRestantes; }
        public String getUbicacion() { return ubicacion; }
        public String getTipo() { return tipo; }
    }
}