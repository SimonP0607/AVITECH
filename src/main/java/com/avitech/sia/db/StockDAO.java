package com.avitech.sia.db;

import com.avitech.sia.iu.ReportesController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StockDAO {

    /**
     * Obtiene la lista actual de ítems en stock desde la base de datos.
     * @return Una lista de objetos StockItem.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    public static List<ReportesController.StockItem> getCurrentStock() throws SQLException {
        List<ReportesController.StockItem> stockItems = new ArrayList<>();
        String sql = "SELECT nombre, categoria, cantidad_actual, unidad_medida FROM productos ORDER BY nombre";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String nombre = rs.getString("nombre");
                String categoria = rs.getString("categoria");
                double cantidad = rs.getDouble("cantidad_actual");
                String unidadMedida = rs.getString("unidad_medida");
                stockItems.add(new ReportesController.StockItem(nombre, categoria, cantidad, unidadMedida));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener el stock actual: " + e.getMessage());
            throw e;
        }
        return stockItems;
    }
}
