package com.avitech.sia.db;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordUtil {

    // Genera un hash a partir de una contraseña en texto plano
    public static String hash(String plainTextPassword) {
        return BCrypt.withDefaults().hashToString(12, plainTextPassword.toCharArray());
    }

    // Verifica si una contraseña en texto plano coincide con un hash
    public static boolean check(String plainTextPassword, String hashedPassword) {
        return BCrypt.verifyer().verify(plainTextPassword.toCharArray(), hashedPassword).verified;
    }

}