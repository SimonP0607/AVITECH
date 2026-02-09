package com.avitech.sia.security;

import com.avitech.sia.db.UsuarioDAO;

/**
 * Clase simple para mantener el usuario autenticado en memoria durante la ejecución.
 * No es persistente; sirve para navegación entre pantallas.
 */
public final class Session {
    private static UsuarioDAO.Usuario currentUser;

    private Session() {}

    public static void setCurrentUser(UsuarioDAO.Usuario u) {
        currentUser = u;
    }

    public static UsuarioDAO.Usuario getCurrentUser() {
        return currentUser;
    }

    public static boolean isAuthenticated() {
        return currentUser != null;
    }

    public static void clear() {
        currentUser = null;
    }
}

