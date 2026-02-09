package com.avitech.sia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

// Importar la clase ProduccionRow que definiremos en ProduccionController
import com.avitech.sia.iu.ProduccionController.ProduccionRow;
// Importar la clase ProduccionDTO que definiremos
import com.avitech.sia.iu.produccion.dto.ProduccionDTO;

public class ProduccionDAO {

    public static List<String> getGalponesNombres() throws Exception {
        List<String> galpones = new ArrayList<>();
        String sql = "SELECT nombre FROM Galpones ORDER BY nombre";
        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                galpones.add(rs.getString("nombre"));
            }
        }
        return galpones;
    }

    public static List<ProduccionRow> getProduccionDiaria(String galponFilter) throws Exception {
        List<ProduccionRow> resultados = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT ph.fecha, g.nombre as nombre_galpon, l.cantidadGallinas, ph.total_huevos, ph.huevos_L, ph.huevos_M, ph.huevos_S, ph.mortalidad, ph.responsable ");
        sql.append("FROM ProduccionHuevos ph ");
        sql.append("JOIN Galpones g ON ph.galpon = g.id_galpon ");
        sql.append("JOIN Lote l ON g.Id_lote = l.Id_lote ");
        if (galponFilter != null && !galponFilter.isEmpty() && !"Todos".equalsIgnoreCase(galponFilter)) {
            sql.append("WHERE g.nombre = ? ");
        }
        sql.append("ORDER BY ph.fecha DESC, g.nombre ASC");

        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            if (galponFilter != null && !galponFilter.isEmpty() && !"Todos".equalsIgnoreCase(galponFilter)) {
                ps.setString(1, galponFilter);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int totalHuevos = rs.getInt("total_huevos");
                    int cantidadGallinas = rs.getInt("cantidadGallinas");
                    double postura = (cantidadGallinas > 0) ? (double) totalHuevos / cantidadGallinas * 100 : 0.0;

                    resultados.add(new ProduccionRow(
                            rs.getDate("fecha").toLocalDate(),
                            rs.getString("nombre_galpon"),
                            cantidadGallinas,
                            totalHuevos,
                            String.format("%.2f%%", postura),
                            "N/A", // Peso Promedio (no está en ProduccionHuevos)
                            String.format("L:%d, M:%d, S:%d", rs.getInt("huevos_L"), rs.getInt("huevos_M"), rs.getInt("huevos_S")),
                            rs.getInt("mortalidad"),
                            rs.getString("responsable")
                    ));
                }
            }
        }
        return resultados;
    }

    public static void guardarProduccion(ProduccionDTO dto) throws Exception {
        String sql = "INSERT INTO ProduccionHuevos (fecha, galpon, total_huevos, huevos_L, huevos_M, huevos_S, mortalidad, responsable) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            int idGalpon = getIdFromTable(cn, "Galpones", "id_galpon", "nombre", dto.galpon());

            ps.setDate(1, Date.valueOf(dto.fecha()));
            ps.setInt(2, idGalpon);
            ps.setInt(3, dto.produccionTotal());
            // Mapeo de huevos del DTO a las columnas de la BD
            ps.setInt(4, dto.huevosAA() + dto.huevosAAA()); // huevos_L = Huevos AA + Huevos AAA
            ps.setInt(5, dto.huevosA()); // huevos_M = Huevos A
            ps.setInt(6, dto.huevosB()); // huevos_S = Huevos B
            ps.setInt(7, dto.mortalidad());
            ps.setString(8, dto.responsable());
            ps.executeUpdate();
        }
    }

    // Métodos para KPIs
    public static int getTotalProduccion() throws Exception {
        String sql = "SELECT SUM(total_huevos) FROM ProduccionHuevos";
        try (Connection cn = DB.get(); PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public static double getPromedioPostura() throws Exception {
        String sql = "SELECT SUM(ph.total_huevos) / SUM(l.cantidadGallinas) * 100 FROM ProduccionHuevos ph JOIN Galpones g ON ph.galpon = g.id_galpon JOIN Lote l ON g.Id_lote = l.Id_lote";
        try (Connection cn = DB.get(); PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        }
        return 0.0;
    }

    public static double getPromedioPeso() throws Exception {
        // La tabla ProduccionHuevos no tiene peso. Este KPI no se puede calcular con la BD actual.
        return 0.0;
    }

    public static int getTotalMortalidad() throws Exception {
        String sql = "SELECT SUM(mortalidad) FROM ProduccionHuevos";
        try (Connection cn = DB.get(); PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private static int getIdFromTable(Connection cn, String tableName, String idColumn, String nameColumn, String nameValue) throws Exception {
        String sql = "SELECT " + idColumn + " FROM " + tableName + " WHERE " + nameColumn + " = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nameValue);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new Exception("No se encontró el ID para '" + nameValue + "' en la tabla '" + tableName + "'.");
    }
}