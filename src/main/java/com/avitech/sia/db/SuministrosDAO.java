package com.avitech.sia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuministrosDAO {

    public static List<Mov> getAll() throws Exception {
        List<Mov> allMovementsAsc = new ArrayList<>();
        String sqlFetchAll = "SELECT id_movimiento, fecha, tipo, item, cantidad, unidad, responsable, motivo, valor_total FROM Suministros ORDER BY fecha ASC, id_movimiento ASC";

        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement(sqlFetchAll);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                allMovementsAsc.add(new Mov(
                        rs.getDate("fecha").toLocalDate().toString(),
                        rs.getString("item"),
                        String.valueOf(rs.getInt("cantidad")),
                        rs.getString("unidad"), // <-- Argumento 4 (unidad)
                        rs.getString("tipo"),
                        rs.getString("responsable"),
                        rs.getString("motivo"),
                        null // El stock se calculará más tarde
                ));
            }
        }
        System.out.println("SuministrosDAO.getAll(): Recuperados " + allMovementsAsc.size() + " movimientos de la BD.");

        Map<String, Integer> itemCurrentStockMap = new HashMap<>();
        for (InventarioItem item : getInventario()) {
            itemCurrentStockMap.put(item.getCompositeKey(), item.stock);
        }

        List<Mov> resultsDesc = new ArrayList<>();
        for (int i = allMovementsAsc.size() - 1; i >= 0; i--) {
            Mov currentMov = allMovementsAsc.get(i);
            String compositeKey = currentMov.getCompositeKey(); // Usar la clave compuesta
            int quantity = Integer.parseInt(currentMov.cantidad);
            String type = currentMov.tipo;

            int stockAfterThisMovement = itemCurrentStockMap.getOrDefault(compositeKey, 0);

            resultsDesc.add(new Mov(
                    currentMov.fecha,
                    currentMov.item,
                    currentMov.cantidad,
                    currentMov.unidad,
                    currentMov.tipo,
                    currentMov.responsable,
                    currentMov.detalles,
                    String.valueOf(stockAfterThisMovement) // Este es el stock *después* de este movimiento
            ));

            if ("Entrada".equalsIgnoreCase(type)) {
                itemCurrentStockMap.put(compositeKey, stockAfterThisMovement - quantity);
            } else if ("Salida".equalsIgnoreCase(type)) {
                itemCurrentStockMap.put(compositeKey, stockAfterThisMovement + quantity);
            }
        }

        return resultsDesc; // Esta lista ya está en orden descendente de fecha
    }

    public static double getValorTotalStock() throws Exception {
        String sql = """
            SELECT SUM(CASE WHEN tipo = 'Entrada' THEN valor_total ELSE -valor_total END) as valor_total_inventario
            FROM Suministros
            WHERE item NOT IN (SELECT nombre FROM Medicamentos);
        """;

        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("valor_total_inventario");
            }
        }
        return 0.0;
    }

    public static List<InventarioItem> getInventario() throws Exception {
        List<InventarioItem> resultados = new ArrayList<>();
        String sql = "SELECT item, unidad, SUM(CASE WHEN tipo = 'Entrada' THEN cantidad ELSE -cantidad END) as stock_actual " +
                     "FROM Suministros " +
                     "WHERE item NOT IN (SELECT nombre FROM Medicamentos) " +
                     "GROUP BY item, unidad ORDER BY item";

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
        String sql = "SELECT DISTINCT item FROM Suministros WHERE item NOT IN (SELECT nombre FROM Medicamentos) ORDER BY item";
        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultados.add(rs.getString("item"));
            }
        }
        return resultados;
    }

    public static void addMovimiento(String item, int cantidad, String unidad, String proveedor, double valorTotal, String tipo, String motivo, String responsable) throws Exception {
        String sql = "INSERT INTO Suministros (fecha, item, cantidad, unidad, tipo, motivo, responsable, proveedor, valor_total) VALUES (NOW(), ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, item);
            ps.setInt(2, cantidad);
            ps.setString(3, unidad);
            ps.setString(4, tipo);
            ps.setString(5, motivo);
            ps.setString(6, responsable);
            ps.setString(7, proveedor);
            ps.setDouble(8, valorTotal);
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
        public final String itemLc, detallesLc, respLc, unidadLc;
        public final LocalDate localDate;

        public Mov(String fecha, String item, String cantidad, String unidad, String tipo,
                   String responsable, String detalles, String stock) {
            this.fecha = fecha;
            this.item = item;
            this.cantidad = cantidad;
            this.unidad = unidad; // Usar movUnidad para la unidad del movimiento
            this.tipo = tipo;
            this.responsable = responsable;
            this.detalles = detalles;
            this.stock = stock;

            this.itemLc = item != null ? item.toLowerCase() : "";
            this.detallesLc = detalles != null ? detalles.toLowerCase() : "";
            this.respLc = responsable != null ? responsable.toLowerCase() : "";
            this.unidadLc = unidad != null ? unidad.toLowerCase() : "";

            LocalDate ld = null;
            try {
                ld = LocalDate.parse(fecha.substring(0, 10));
            } catch (Exception ignored) {}
            this.localDate = ld;
        }

        public String getCompositeKey() {
            return (item != null ? item : "") + "::" + (unidad != null ? unidad : "");
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

        public String getCompositeKey() {
            return (producto != null ? producto : "") + "::" + (unidad != null ? unidad : "");
        }
    }
}
