package com.avitech.sia.iu;

import com.avitech.sia.App;
import com.avitech.sia.security.SessionManager;
import com.avitech.sia.security.UserRole;
import com.avitech.sia.security.UserRole.Module;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;

/**
 * Controlador base que proporciona funcionalidad común a todos los controladores.
 * Incluye acceso al ScreenManager, SessionManager y validación de permisos.
 */
public abstract class BaseController {

    protected ScreenManager screenManager;
    protected SessionManager sessionManager;

    /**
     * Inicialización automática del ScreenManager y SessionManager
     */
    @FXML
    public void initialize() {
        screenManager = App.getScreenManager();
        sessionManager = SessionManager.getInstance();

        // Verificar autenticación
        if (!sessionManager.isAuthenticated()) {
            redirectToLogin();
            return;
        }

        // Verificar permisos del módulo actual
        Module requiredModule = getRequiredModule();
        if (requiredModule != null && !sessionManager.hasAccessTo(requiredModule)) {
            showAccessDeniedAndRedirect(requiredModule);
            return;
        }

        onScreenReady();
    }

    /**
     * Método que se llama después de que el ScreenManager está disponible.
     * Sobrescribir en las clases hijas para ajustar componentes según la pantalla.
     */
    protected void onScreenReady() {
        // Implementar en clases hijas si es necesario
    }

    /**
     * Define qué módulo requiere este controlador para funcionar.
     * Sobrescribir en clases hijas para activar validación automática de permisos.
     */
    protected Module getRequiredModule() {
        return null; // Por defecto no requiere módulo específico
    }

    /**
     * Verifica si el usuario actual tiene acceso a un módulo específico.
     */
    protected boolean hasAccessTo(Module module) {
        return sessionManager.hasAccessTo(module);
    }

    /**
     * Redirige al login y muestra mensaje de sesión expirada.
     */
    protected void redirectToLogin() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Sesión Expirada");
        alert.setHeaderText("Debe iniciar sesión");
        alert.setContentText("Su sesión ha expirado o no ha iniciado sesión.");
        alert.showAndWait();

        App.goTo("/fxml/login.fxml", "SIA Avitech — Inicio de sesión");
    }

    /**
     * Muestra mensaje de acceso denegado y redirige al dashboard.
     */
    protected void showAccessDeniedAndRedirect(Module module) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Acceso Denegado");
        alert.setHeaderText("No tiene permisos para acceder a este módulo");
        alert.setContentText("Su rol (" + sessionManager.getUserRole().name() +
                           ") no tiene acceso al módulo: " + getModuleName(module));
        alert.showAndWait();

        // Redirigir al dashboard correspondiente
        App.goTo(sessionManager.getDashboardPath(), sessionManager.getDashboardTitle());
    }

    /**
     * Cierra sesión y vuelve al login.
     */
    protected void logout() {
        sessionManager.logout();
        App.goTo("/fxml/login.fxml", "SIA Avitech — Inicio de sesión");
    }

    /**
     * Obtiene el nombre legible de un módulo.
     */
    private String getModuleName(Module module) {
        return switch (module) {
            case DASHBOARD -> "Dashboard";
            case SUMINISTROS -> "Suministros";
            case SANIDAD -> "Sanidad";
            case PRODUCCION -> "Producción";
            case REPORTES -> "Reportes";
            case ALERTAS -> "Alertas";
            case AUDITORIA -> "Auditoría";
            case PARAMETROS -> "Parámetros";
            case USUARIOS -> "Usuarios";
            case RESPALDOS -> "Respaldos";
        };
    }

    /**
     * Ajusta el ancho de una columna de tabla según la resolución de pantalla
     */
    protected void adjustColumnWidth(TableColumn<?, ?> column, double baseWidth) {
        if (screenManager != null) {
            column.setPrefWidth(screenManager.getColumnWidth(baseWidth));
        }
    }

    /**
     * Obtiene información sobre el tamaño de pantalla actual
     */
    protected String getScreenInfo() {
        if (screenManager != null) {
            return String.format("%s (%dx%d)",
                screenManager.getScreenSize().getName(),
                (int)screenManager.getScreenWidth(),
                (int)screenManager.getScreenHeight());
        }
        return "Desconocido";
    }

    /**
     * Verifica si se debe mostrar UI simplificada (pantalla pequeña)
     */
    protected boolean shouldUseSimplifiedUI() {
        return screenManager != null && screenManager.isSmallScreen();
    }

    /**
     * Obtiene el factor de escala para ajustar tamaños de fuentes/iconos
     */
    protected double getUIScaleFactor() {
        return screenManager != null ? screenManager.getScaleFactor() : 1.0;
    }
}

