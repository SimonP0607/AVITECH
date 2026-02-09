package com.avitech.sia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RespaldosDAO {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm");

    public static List<RespaldoDTO> getAll() throws Exception {
        List<RespaldoDTO> resultados = new ArrayList<>();
        String sql = "SELECT r.id_respaldo, r.archivo, r.fecha_hora, r.tipo, r.tamaño, r.estado, u.usuario " +
                     "FROM Respaldos r JOIN Usuarios u ON r.usuario = u.id_usuario " +
                     "ORDER BY r.fecha_hora DESC";

        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultados.add(new RespaldoDTO(
                        rs.getInt("id_respaldo"),
                        rs.getString("archivo"),
                        rs.getTimestamp("fecha_hora").toLocalDateTime().format(FMT),
                        rs.getString("tipo"),
                        rs.getString("tamaño"),
                        rs.getString("estado"),
                        rs.getString("usuario")));
            }
        }
        return resultados;
    }

    public static void deleteById(int id) throws Exception {
        String sql = "DELETE FROM Respaldos WHERE id_respaldo = ?";
        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /* DTO class para mayor claridad */
    public static class RespaldoDTO {
        private int id;
        private String archivo;
        private String fecha;
        private String tipo;
        private String tamano;
        private String estado;
        private String usuario;

        public RespaldoDTO(int id, String archivo, String fecha, String tipo, String tamano, String estado, String usuario) {
            this.id = id;
            this.archivo = archivo;
            this.fecha = fecha;
            this.tipo = tipo;
            this.tamano = tamano;
            this.estado = estado;
            this.usuario = usuario;
        }

        public int getId() { return id; }
        public String getArchivo() { return archivo; }
        public String getFecha() { return fecha; }
        public String getTipo() { return tipo; }
        public String getTamano() { return tamano; }
        public String getEstado() { return estado; }
        public String getUsuario() { return usuario; }
    }
}