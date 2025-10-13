package com.avitech.sia.db;

import java.sql.*;
import java.util.Optional;

public class UsuarioDAO {
    // nombre completo, usuario (login), rol, estado (activo), hash
    public record Usuario(int id, String nombre, String usuario, String rol, boolean activo, String passHash) {}

    public static Optional<Usuario> findByUsuario(String u) throws Exception {
        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT id, nombre, usuario, rol, estado, pass_hash FROM usuarios WHERE usuario=?")) {
            ps.setString(1, u);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Usuario(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("usuario"),
                            rs.getString("rol"),
                            rs.getBoolean("estado"),
                            rs.getString("pass_hash")
                    ));
                }
                return Optional.empty();
            }
        }
    }
}
