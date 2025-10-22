    package com.avitech.sia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AlertasDAO {

    public static List<ItemAlerta> getAll() throws Exception {
        List<ItemAlerta> resultados = new ArrayList<>();
        String sql = "SELECT tipo, descripcion, categoria, fecha, estado FROM Alertas WHERE estado = 'Activa' ORDER BY fecha DESC";

        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultados.add(new ItemAlerta(
                        rs.getString("descripcion"),
                        rs.getString("categoria"),
                        "N/A", // stockActual no está en la tabla Alertas
                        "N/A", // stockMinimo no está en la tabla Alertas
                        "N/A", // porcentaje no está en la tabla Alertas
                        "N/A", // diasRestantes no está en la tabla Alertas
                        "N/A", // ubicacion no está en la tabla Alertas
                        rs.getString("tipo")
                ));
            }
        }
        return resultados;
    }

    public record ItemAlerta(String descripcion, String categoria, String stockActual, String stockMinimo,
                             String porcentaje, String diasRestantes, String ubicacion, String tipo) {

        // Getters para compatibilidad con PropertyValueFactory de JavaFX
        public String getArticulo() { return descripcion; } // Mapeo a la columna 'Artículo'
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