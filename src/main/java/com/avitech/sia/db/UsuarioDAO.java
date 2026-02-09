package com.avitech.sia.db;

import com.avitech.sia.utils.PasswordUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAO {

    public static class Usuario {
        private int id;
        private String nombre;
        private String usuario;
        private String email;
        private String rol;
        private String estado;
        private Timestamp ultimoAcceso;

        public Usuario(int id, String nombre, String usuario, String email, String rol, String estado, Timestamp ultimoAcceso) {
            this.id = id;
            this.nombre = nombre;
            this.usuario = usuario;
            this.email = email;
            this.rol = rol;
            this.estado = estado;
            this.ultimoAcceso = ultimoAcceso;
        }

        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public String getUsuario() { return usuario; }
        public String getEmail() { return email; }
        public String getRol() { return rol; }
        public String getEstado() { return estado; }
        public Timestamp getUltimoAcceso() { return ultimoAcceso; }
    }

    public static List<Usuario> getAll() throws Exception {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id_usuario, nombre_completo, usuario, email, rol, estado, ultimo_acceso FROM Usuarios";
        try (Connection cn = DB.get(); PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                usuarios.add(new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre_completo"),
                        rs.getString("usuario"),
                        rs.getString("email"),
                        rs.getString("rol"),
                        rs.getString("estado"),
                        rs.getTimestamp("ultimo_acceso")
                ));
            }
        }
        return usuarios;
    }

    public static Optional<Usuario> findByUsuario(String u) throws Exception {
        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT id_usuario, nombre_completo, usuario, email, rol, estado, ultimo_acceso, password FROM Usuarios WHERE usuario=?")) {
            ps.setString(1, u);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Nota: el hash de la contraseña no se expone en el DTO principal
                    return Optional.of(new Usuario(
                            rs.getInt("id_usuario"),
                            rs.getString("nombre_completo"),
                            rs.getString("usuario"),
                            rs.getString("email"),
                            rs.getString("rol"),
                            rs.getString("estado"),
                            rs.getTimestamp("ultimo_acceso")
                    ));
                }
                return Optional.empty();
            }
        }
    }

    public static void crear(String nombre, String usuario, String email, String rol, String estado, String password) throws Exception {
        String sql = "INSERT INTO Usuarios (nombre_completo, usuario, email, rol, estado, password) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = DB.get(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, usuario);
            ps.setString(3, email);
            ps.setString(4, rol);
            ps.setString(5, estado);
            ps.setString(6, PasswordUtils.hashPassword(password));
            ps.executeUpdate();
        }
    }

    public static void actualizar(int id, String nombre, String usuario, String email, String rol, String estado) throws Exception {
        String sql = "UPDATE Usuarios SET nombre_completo=?, usuario=?, email=?, rol=?, estado=? WHERE id_usuario=?";
        try (Connection cn = DB.get(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, usuario);
            ps.setString(3, email);
            ps.setString(4, rol);
            ps.setString(5, estado);
            ps.setInt(6, id);
            ps.executeUpdate();
        }
    }

    public static void eliminar(int id) throws Exception {
        String sql = "DELETE FROM Usuarios WHERE id_usuario=?";
        try (Connection cn = DB.get(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public static void updatePassword(int id, String newHash) throws Exception {
        String sql = "UPDATE Usuarios SET password=? WHERE id_usuario=?";
        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }
}
