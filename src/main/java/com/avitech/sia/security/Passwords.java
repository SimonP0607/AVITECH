package com.avitech.sia.security;

import at.favre.lib.crypto.bcrypt.BCrypt;

public final class Passwords {
    private Passwords() {}

    public static String hash(String plain) {
        return BCrypt.withDefaults().hashToString(12, plain.toCharArray());
    }

    public static boolean verify(String plain, String hash) {
        return BCrypt.verifyer().verify(plain.toCharArray(), hash).verified;
    }
}
