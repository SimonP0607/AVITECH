package com.avitech.sia.db;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordUtil {

    // Retorna la contraseña hasheada usando BCrypt
    public static String hash(String plainTextPassword) {
        if (plainTextPassword == null) return null;
        // Cost de 12 por defecto (balance entre seguridad y rendimiento)
        return BCrypt.withDefaults().hashToString(12, plainTextPassword.toCharArray());
    }

    // Verifica si la contraseña coincide con la almacenada (hash)
    public static boolean check(String plainTextPassword, String storedPassword) {
        if (plainTextPassword == null || storedPassword == null) return false;
        return BCrypt.verifyer().verify(plainTextPassword.toCharArray(), storedPassword).verified;
    }

}
