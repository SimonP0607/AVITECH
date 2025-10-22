package com.avitech.sia.security;

import com.avitech.sia.security.UserRole.Module;

/**
 * Gestor de sesión centralizado que mantiene información del usuario autenticado.
 * Singleton para acceso global desde cualquier controlador.
 */
public class SessionManager {

    private static SessionManager instance;

    private String username;
    private String fullName;
    private UserRole userRole;
    private boolean authenticated;

    private SessionManager() {
        this.authenticated = false;
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Inicia sesión con un usuario y rol específico.
     */
    public void login(String username, String fullName, UserRole role) {
        this.username = username;
        this.fullName = fullName;
        this.userRole = role;
        this.authenticated = true;
    }

    /**
     * Cierra la sesión actual.
     */
    public void logout() {
        this.username = null;
        this.fullName = null;
        this.userRole = null;
        this.authenticated = false;
    }

    /**
     * Verifica si hay una sesión activa.
     */
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * Obtiene el rol del usuario actual.
     */
    public UserRole getUserRole() {
        return userRole;
    }

    /**
     * Obtiene el nombre de usuario.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Obtiene el nombre completo del usuario.
     */
    public String getFullName() {
        return fullName != null ? fullName : username;
    }

    /**
     * Verifica si el usuario actual tiene acceso a un módulo específico.
     */
    public boolean hasAccessTo(Module module) {
        if (!authenticated || userRole == null) {
            return false;
        }
        return userRole.hasAccessTo(module);
    }

    /**
     * Obtiene la ruta del dashboard según el rol del usuario actual.
     */
    public String getDashboardPath() {
        if (!authenticated || userRole == null) {
            return "/fxml/login.fxml";
        }

        return switch (userRole) {
            case ADMIN -> "/fxml/dashboard_admin.fxml";
            case OPERADOR -> "/fxml/dashboard_oper.fxml";
            case SUPERVISOR -> "/fxml/dashboard_super.fxml";
        };
    }

    /**
     * Obtiene el título del dashboard según el rol.
     */
    public String getDashboardTitle() {
        if (!authenticated || userRole == null) {
            return "SIA Avitech — Inicio de sesión";
        }

        return switch (userRole) {
            case ADMIN -> "SIA Avitech — Administrador";
            case OPERADOR -> "SIA Avitech — Operador";
            case SUPERVISOR -> "SIA Avitech — Supervisor";
        };
    }
}

