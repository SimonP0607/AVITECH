package com.avitech.sia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SuministrosDAO {

    public static List<Mov> getAll() throws Exception {
        List<Mov> resultados = new ArrayList<>();
        String sql = "SELECT fecha, tipo, item, cantidad, unidad, responsable, motivo FROM Suministros ORDER BY fecha DESC";

        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultados.add(new Mov(
                        rs.getDate("fecha").toLocalDate().toString(), // Simplificado a fecha
                        rs.getString("item"),
                        String.valueOf(rs.getInt("cantidad")),
                        rs.getString("unidad"),
                        rs.getString("tipo"),
                        rs.getString("responsable"),
                        rs.getString("motivo"),
                        "—" // Columna de stock no está en la tabla
                ));
            }
        }
        return resultados;
    }

    public static List<InventarioItem> getInventario() throws Exception {
        List<InventarioItem> resultados = new ArrayList<>();
        String sql = "SELECT item, unidad, SUM(CASE WHEN tipo = 'Entrada' THEN cantidad ELSE -cantidad END) as stock_actual " +
                     "FROM Suministros GROUP BY item, unidad ORDER BY item";

        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultados.add(new InventarioItem(
                        rs.getString("item"),
                        rs.getInt("stock_actual"),
                        rs.getString("unidad")
                ));
            }
        }
        return resultados;
    }

    public static List<String> getProductos() throws Exception {
        List<String> resultados = new ArrayList<>();
        String sql = "SELECT DISTINCT item FROM Suministros ORDER BY item";
        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultados.add(rs.getString("item"));
            }
        }
        return resultados;
    }

    public static void addMovimiento(String item, int cantidad, String tipo, String motivo, String responsable) throws Exception {
        String unidad = getUnidadForItem(item);
        String sql = "INSERT INTO Suministros (fecha, item, cantidad, unidad, tipo, motivo, responsable) VALUES (NOW(), ?, ?, ?, ?, ?, ?)";

        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, item);
            ps.setInt(2, cantidad);
            ps.setString(3, unidad);
            ps.setString(4, tipo);
            ps.setString(5, motivo);
            ps.setString(6, responsable);
            ps.executeUpdate();
        }
    }

    private static String getUnidadForItem(String item) throws Exception {
        String sql = "SELECT unidad FROM Suministros WHERE item = ? ORDER BY fecha DESC LIMIT 1";
        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, item);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("unidad");
                }
            }
        }
        return "unidad"; // Valor por defecto si no se encuentra
    }

    public static class Mov {
        public final String fecha, item, cantidad, unidad, tipo, responsable, detalles, stock;
        public final String itemLc, detallesLc, respLc;
        public final LocalDate localDate;

        public Mov(String fecha, String item, String cantidad, String unidad, String tipo,
                   String responsable, String detalles, String stock) {
            this.fecha = fecha;
            this.item = item;
            this.cantidad = cantidad;
            this.unidad = unidad;
            this.tipo = tipo;
            this.responsable = responsable;
            this.detalles = detalles;
            this.stock = stock;

            this.itemLc = item.toLowerCase();
            this.detallesLc = detalles.toLowerCase();
            this.respLc = responsable.toLowerCase();

            // intenta parsear yyyy-MM-dd desde el prefijo de fecha
            LocalDate ld = null;
            try {
                ld = LocalDate.parse(fecha.substring(0, 10));
            } catch (Exception ignored) {}
            this.localDate = ld;
        }
    }

    public static class InventarioItem {
        public final String producto;
        public final int stock;
        public final String unidad;

        public InventarioItem(String producto, int stock, String unidad) {
            this.producto = producto;
            this.stock = stock;
            this.unidad = unidad;
        }
    }
}
