package com.avitech.sia.iu;

import com.avitech.sia.App;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

/**
 * Controlador base que proporciona funcionalidad común a todos los controladores.
 * Incluye acceso al ScreenManager y métodos útiles para ajustar componentes.
 */
public abstract class BaseController {

    protected ScreenManager screenManager;

    /**
     * Inicialización automática del ScreenManager
     */
    @FXML
    public void initialize() {
        screenManager = App.getScreenManager();
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

