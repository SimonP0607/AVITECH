package com.avitech.sia.security;

import com.avitech.sia.db.DB;
import at.favre.lib.crypto.bcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Herramienta de desarrollo para detectar contraseñas almacenadas en texto plano
 * y mostrar/actualizar su hash BCrypt.
 *
 * Uso:
 *  - Dry run (por defecto, no modifica la DB): gradlew rehashPasswords
 *  - Aplicar cambios: gradlew rehashPasswords -Papply=true
 */
public class RehashPasswords {
    public static void main(String[] args) throws Exception {
        boolean apply = "true".equalsIgnoreCase(System.getProperty("apply", "false"));
        System.out.println("RehashPasswords: modo " + (apply ? "APLICAR" : "DRY-RUN (no aplica cambios)"));

        List<String> updates = new ArrayList<>();

        try (Connection cn = DB.get();
             PreparedStatement ps = cn.prepareStatement("SELECT id_usuario, usuario, password FROM Usuarios");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id_usuario");
                String usuario = rs.getString("usuario");
                String pass = rs.getString("password");

                if (pass == null || pass.trim().isEmpty()) continue;
                String t = pass.trim();
                // Si ya parece BCrypt (comienza con $2a$ $2b$ $2y$) ignorar
                if (t.startsWith("$2a$") || t.startsWith("$2b$") || t.startsWith("$2y$")) continue;

                // Generar hash
                String hash = BCrypt.withDefaults().hashToString(12, t.toCharArray());
                updates.add(String.format("UPDATE Usuarios SET password='%s' WHERE id_usuario=%d; -- usuario=%s", hash, id, usuario));

                if (apply) {
                    try (PreparedStatement ups = cn.prepareStatement("UPDATE Usuarios SET password=? WHERE id_usuario=?")) {
                        ups.setString(1, hash);
                        ups.setInt(2, id);
                        ups.executeUpdate();
                    }
                }
            }
        }

        if (updates.isEmpty()) {
            System.out.println("No se encontraron contraseñas en texto plano. No hay acciones a tomar.");
            return;
        }

        System.out.println("Encontradas " + updates.size() + " contraseñas para rehash:");
        for (String u : updates) {
            System.out.println(u);
        }

        if (apply) {
            System.out.println("Actualización aplicada.");
        } else {
            System.out.println("Modo dry-run: para aplicar las actualizaciones ejecutar: gradlew rehashPasswords -Papply=true");
        }
    }
}

