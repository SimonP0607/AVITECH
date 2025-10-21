package com.avitech.sia.iu;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Gestor de pantalla que detecta la resolución y ajusta las ventanas automáticamente.
 * Proporciona información sobre la pantalla actual y métodos para configurar ventanas.
 */
public class ScreenManager {

    private static ScreenManager instance;
    private Rectangle2D screenBounds;
    private double screenWidth;
    private double screenHeight;
    private ScreenSize screenSize;

    /**
     * Enum para categorizar tamaños de pantalla
     */
    public enum ScreenSize {
        SMALL("Pequeña", 1366, 768),      // HD Ready
        MEDIUM("Mediana", 1600, 900),     // HD+
        LARGE("Grande", 1920, 1080),      // Full HD
        XLARGE("Extra Grande", 2560, 1440), // 2K/QHD
        ULTRA("Ultra", 3840, 2160);       // 4K

        private final String name;
        private final int width;
        private final int height;

        ScreenSize(String name, int width, int height) {
            this.name = name;
            this.width = width;
            this.height = height;
        }

        public String getName() { return name; }
        public int getWidth() { return width; }
        public int getHeight() { return height; }
    }

    private ScreenManager() {
        detectScreen();
    }

    /**
     * Obtiene la instancia única del ScreenManager
     */
    public static ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }

    /**
     * Detecta la resolución y características de la pantalla principal
     */
    private void detectScreen() {
        Screen primaryScreen = Screen.getPrimary();
        screenBounds = primaryScreen.getVisualBounds();
        screenWidth = screenBounds.getWidth();
        screenHeight = screenBounds.getHeight();
        screenSize = categorizeScreenSize();

        System.out.println("═══════════════════════════════════════════════");
        System.out.println("  AVITECH - Detección de Pantalla");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("  Resolución: " + (int)screenWidth + " x " + (int)screenHeight);
        System.out.println("  Categoría: " + screenSize.getName());
        System.out.println("  DPI: " + primaryScreen.getDpi());
        System.out.println("═══════════════════════════════════════════════");
    }

    /**
     * Categoriza el tamaño de pantalla según la resolución
     */
    private ScreenSize categorizeScreenSize() {
        if (screenWidth >= 3840) return ScreenSize.ULTRA;
        if (screenWidth >= 2560) return ScreenSize.XLARGE;
        if (screenWidth >= 1920) return ScreenSize.LARGE;
        if (screenWidth >= 1600) return ScreenSize.MEDIUM;
        return ScreenSize.SMALL;
    }

    /**
     * Configura una ventana para que se ajuste a la pantalla
     * @param stage La ventana a configurar
     * @param maximized Si debe abrirse maximizada
     */
    public void configureStage(Stage stage, boolean maximized) {
        if (maximized) {
            // Maximizar la ventana ocupando todo el espacio visual disponible
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenWidth);
            stage.setHeight(screenHeight);
            stage.setMaximized(true);
        } else {
            // Usar un porcentaje de la pantalla (90%)
            double windowWidth = screenWidth * 0.9;
            double windowHeight = screenHeight * 0.9;

            // Centrar la ventana
            stage.setWidth(windowWidth);
            stage.setHeight(windowHeight);
            stage.setX((screenWidth - windowWidth) / 2 + screenBounds.getMinX());
            stage.setY((screenHeight - windowHeight) / 2 + screenBounds.getMinY());
        }
    }

    /**
     * Obtiene el ancho de la pantalla
     */
    public double getScreenWidth() {
        return screenWidth;
    }

    /**
     * Obtiene el alto de la pantalla
     */
    public double getScreenHeight() {
        return screenHeight;
    }

    /**
     * Obtiene los límites visuales de la pantalla
     */
    public Rectangle2D getScreenBounds() {
        return screenBounds;
    }

    /**
     * Obtiene la categoría de tamaño de pantalla
     */
    public ScreenSize getScreenSize() {
        return screenSize;
    }

    /**
     * Verifica si la pantalla es pequeña (menor a Full HD)
     */
    public boolean isSmallScreen() {
        return screenSize == ScreenSize.SMALL;
    }

    /**
     * Verifica si la pantalla es grande (Full HD o superior)
     */
    public boolean isLargeScreen() {
        return screenSize == ScreenSize.LARGE ||
               screenSize == ScreenSize.XLARGE ||
               screenSize == ScreenSize.ULTRA;
    }

    /**
     * Obtiene un factor de escala sugerido para UI según el tamaño de pantalla
     */
    public double getScaleFactor() {
        switch (screenSize) {
            case SMALL: return 0.75;
            case MEDIUM: return 0.85;
            case LARGE: return 1.0;
            case XLARGE: return 1.15;
            case ULTRA: return 1.5;
            default: return 1.0;
        }
    }

    /**
     * Calcula un ancho preferido para columnas de tabla según la pantalla
     */
    public double getColumnWidth(double baseWidth) {
        return baseWidth * (screenWidth / 1920.0);
    }

    /**
     * Calcula un alto preferido para componentes según la pantalla
     */
    public double getComponentHeight(double baseHeight) {
        return baseHeight * (screenHeight / 1080.0);
    }

    /**
     * Imprime información de depuración sobre la pantalla
     */
    public void printDebugInfo() {
        System.out.println("\n--- Información de Pantalla ---");
        System.out.println("Resolución: " + (int)screenWidth + "x" + (int)screenHeight);
        System.out.println("Categoría: " + screenSize.getName());
        System.out.println("Factor de escala: " + getScaleFactor());
        System.out.println("Bounds: " + screenBounds);
        System.out.println("-------------------------------\n");
    }
}

