package com.avitech.sia.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAO {
    // nombre completo, usuario (login), rol, estado (activo), hash
    public record Usuario(int id, String usuario, String rol, String passHash) {}

    public static Optional<Usuario> findByUsuario(String u) throws Exception {
        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT id_usuario, usuario, rol, password FROM Usuarios WHERE usuario=?")) {
            ps.setString(1, u);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Usuario(
                            rs.getInt("id_usuario"),
                            rs.getString("usuario"),
                            rs.getString("rol"),
                            rs.getString("password")
                    ));
                }
                return Optional.empty();
            }
        }
    }

    public static List<String> getAllUsernames() throws Exception {
        List<String> usernames = new ArrayList<>();
        String sql = "SELECT usuario FROM Usuarios ORDER BY usuario ASC";
        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usernames.add(rs.getString("usuario"));
            }
        }
        return usernames;
    }
}
