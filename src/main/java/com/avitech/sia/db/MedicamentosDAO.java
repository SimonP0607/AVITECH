package com.avitech.sia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MedicamentosDAO {

    public static List<MedicamentoItem> getInventario() throws Exception {
        List<MedicamentoItem> resultados = new ArrayList<>();
        String sql = "SELECT Id_Medicamento, nombre, stock, stock_minimo FROM Medicamentos ORDER BY nombre";

        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultados.add(new MedicamentoItem(
                        rs.getInt("Id_Medicamento"),
                        rs.getString("nombre"),
                        rs.getInt("stock"),
                        rs.getInt("stock_minimo")
                ));
            }
        }
        return resultados;
    }

    public static class MedicamentoItem {
        public final int id;
        public final String nombre;
        public final int stock;
        public final int stockMinimo;

        public MedicamentoItem(int id, String nombre, int stock, int stockMinimo) {
            this.id = id;
            this.nombre = nombre;
            this.stock = stock;
            this.stockMinimo = stockMinimo;
        }
    }
}
