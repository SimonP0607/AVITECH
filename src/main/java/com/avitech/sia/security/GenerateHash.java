package com.avitech.sia.security;

import com.avitech.sia.db.PasswordUtil;

public class GenerateHash {
    public static void main(String[] args) {
        String pwd = System.getProperty("pwd");
        if ((pwd == null || pwd.isEmpty()) && args.length > 0) pwd = args[0];
        if (pwd == null || pwd.isEmpty()) {
            System.err.println("Uso: gradlew genHash -Ppwd=<contraseña>  (o pasar la contraseña como argumento)");
            System.exit(2);
        }
        String hash = PasswordUtil.hash(pwd);
        System.out.println(hash);
    }
}

