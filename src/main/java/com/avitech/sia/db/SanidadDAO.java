package com.avitech.sia.db;

import com.avitech.sia.iu.SanidadController.PlanRow;
import com.avitech.sia.iu.SanidadController.MedRow;
import com.avitech.sia.iu.sanidad.dto.AplicacionDTO;
import com.avitech.sia.iu.sanidad.dto.EventoDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SanidadDAO {

    public static List<PlanRow> getPlanesSanitarios() throws Exception {
        List<PlanRow> resultados = new ArrayList<>();
        String sql = "SELECT ps.nombre_enfermedad, ps.descripcion, m.nombre as medicamento_nombre " +
                     "FROM Plan_Sanitario ps " +
                     "JOIN Medicamentos m ON ps.Id_medicamento = m.Id_Medicamento " +
                     "ORDER BY ps.fecha DESC";
        try (Connection cn = DB.get(); PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultados.add(new PlanRow(rs.getString("nombre_enfermedad"), rs.getString("descripcion"), rs.getString("medicamento_nombre"), "Registrado"));
            }
        }
        return resultados;
    }

    public static List<MedRow> getMedicamentosStock() throws Exception {
        List<MedRow> resultados = new ArrayList<>();
        String sql = "SELECT nombre, stock, stock_minimo, presentacion FROM Medicamentos ORDER BY nombre";
        try (Connection cn = DB.get(); PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int stock = rs.getInt("stock");
                int stockMinimo = rs.getInt("stock_minimo");
                double nivel = (stockMinimo > 0) ? Math.min((double)stock / (stockMinimo * 2), 1.0) : 0.0;
                resultados.add(new MedRow(rs.getString("nombre"), stock, nivel, rs.getString("presentacion")));
            }
        }
        return resultados;
    }

    public static List<String> getLotes() throws Exception {
        List<String> resultados = new ArrayList<>();
        String sql = "SELECT nombre_lote FROM Lote ORDER BY nombre_lote";
        try (Connection cn = DB.get(); PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultados.add(rs.getString("nombre_lote"));
            }
        }
        return resultados;
    }

    public static List<String> getMedicamentosNombres() throws Exception {
        List<String> resultados = new ArrayList<>();
        String sql = "SELECT nombre FROM Medicamentos ORDER BY nombre";
        try (Connection cn = DB.get(); PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultados.add(rs.getString("nombre"));
            }
        }
        return resultados;
    }

    public static List<String> getTiposEvento() {
        return Arrays.asList("Enfermedad", "Incidencia", "Mortalidad", "Otro");
    }

    public static void guardarAplicacion(AplicacionDTO dto) throws Exception {
        Connection cn = null;
        try {
            cn = DB.get();
            cn.setAutoCommit(false);

            int idLote = getIdFromTable(cn, "Lote", "Id_lote", "nombre_lote", dto.loteGalpon());
            int idMedicamento = getIdFromTable(cn, "Medicamentos", "Id_Medicamento", "nombre", dto.medicamento());

            String sqlPlan = "INSERT INTO Plan_Sanitario (Id_lote, Id_medicamento, fecha, nombre_enfermedad, descripcion, observaciones) " +
                           "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = cn.prepareStatement(sqlPlan)) {
                ps.setInt(1, idLote);
                ps.setInt(2, idMedicamento);
                ps.setDate(3, Date.valueOf(dto.fecha()));
                ps.setString(4, "Aplicación de: " + dto.medicamento());
                ps.setString(5, "Dosis: " + dto.dosis() + ", Vía: " + dto.viaAplicacion());
                ps.setString(6, dto.observaciones());
                ps.executeUpdate();
            }

            int cantidad;
            try {
                cantidad = Integer.parseInt(dto.dosis().replaceAll("[^0-9]", ""));
            } catch (NumberFormatException e) {
                throw new Exception("El campo 'Dosis' debe contener un valor numérico para descontar del inventario.");
            }

            String sqlUpdateStock = "UPDATE Medicamentos SET stock = stock - ? WHERE Id_Medicamento = ?";
            try (PreparedStatement ps = cn.prepareStatement(sqlUpdateStock)) {
                ps.setInt(1, cantidad);
                ps.setInt(2, idMedicamento);
                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new Exception("No se pudo actualizar el stock del medicamento.");
                }
            }

            cn.commit();

        } catch (Exception e) {
            if (cn != null) { try { cn.rollback(); } catch (Exception ex) { ex.printStackTrace(); } }
            throw e;
        } finally {
            if (cn != null) { try { cn.close(); } catch (Exception e) { e.printStackTrace(); } }
        }
    }

    public static void guardarEvento(EventoDTO dto) throws Exception {
        Connection cn = null;
        try {
            cn = DB.get();
            cn.setAutoCommit(false);

            int idLote = getIdFromTable(cn, "Lote", "Id_lote", "nombre_lote", dto.loteAfectado());

            // TODO: Manejar Id_medicamento para eventos generales. Por ahora, usamos el ID del primer medicamento.
            int idMedicamento = getFirstMedicamentoId(cn);
            if (idMedicamento == -1) {
                throw new Exception("No se encontró ningún medicamento en la base de datos para asociar al evento.");
            }

            int muertes = 0;
            try {
                muertes = Integer.parseInt(dto.avesAfectadas());
            } catch (NumberFormatException ignored) { /* Si no es un número, se queda en 0 */ }

            String sql = "INSERT INTO Plan_Sanitario (Id_lote, Id_medicamento, fecha, nombre_enfermedad, muertes, descripcion, observaciones) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setInt(1, idLote);
                ps.setInt(2, idMedicamento);
                ps.setDate(3, Date.valueOf(dto.fecha()));
                ps.setString(4, dto.tipoEvento());
                ps.setInt(5, muertes);
                ps.setString(6, dto.descripcion());
                ps.setString(7, dto.accionesTomadas() + " (Responsable: " + dto.responsable() + ")");
                ps.executeUpdate();
            }

            cn.commit();

        } catch (Exception e) {
            if (cn != null) { try { cn.rollback(); } catch (Exception ex) { ex.printStackTrace(); } }
            throw e;
        } finally {
            if (cn != null) { try { cn.close(); } catch (Exception e) { e.printStackTrace(); } }
        }
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

    private static int getFirstMedicamentoId(Connection cn) throws Exception {
        String sql = "SELECT Id_Medicamento FROM Medicamentos LIMIT 1";
        try (PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("Id_Medicamento");
            }
        }
        return -1; // No se encontró ningún medicamento
    }
}
