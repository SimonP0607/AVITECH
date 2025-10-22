package com.avitech.sia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AuditoriaDAO {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");

    public static List<AuditoriaDTO> getAll() throws Exception {
        List<AuditoriaDTO> resultados = new ArrayList<>();
        // Asumo que existe una tabla 'Auditoria' con una FK a 'Usuarios'
        String sql = "SELECT u.usuario, a.fecha, a.accion, a.modulo, a.detalle, a.referencia " +
                     "FROM Auditoria a JOIN Usuarios u ON a.id_usuario = u.id_usuario " +
                     "ORDER BY a.fecha DESC";

        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultados.add(new AuditoriaDTO(
                        rs.getString("usuario"),
                        rs.getTimestamp("fecha").toLocalDateTime().format(FMT),
                        rs.getString("accion"),
                        rs.getString("modulo"),
                        rs.getString("detalle"),
                        rs.getString("referencia")));
            }
        }
        return resultados;
    }

    /* ====== DTO ====== */
    public record AuditoriaDTO(
            String usuario,
            String fechaHora,
            String accion,
            String modulo,
            String detalle,
            String referencia
    ) { }
}