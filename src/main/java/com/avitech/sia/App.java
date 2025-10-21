package com.avitech.sia;

import com.avitech.sia.iu.ScreenManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {
    private static Stage primaryStage;
    private static ScreenManager screenManager;

    public static Stage PrimaryStage() {
        return primaryStage;
    }

    public static ScreenManager getScreenManager() {
        return screenManager;
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Inicializar el gestor de pantalla
        screenManager = ScreenManager.getInstance();

        // Cargar fuentes (asegúrate de tenerlas en resources/fonts/)
        Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Regular.ttf"), 12);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-SemiBold.ttf"), 12);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Bold.ttf"), 12);

        primaryStage = stage;

        // Cargar la pantalla de inicio (login)
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(App.class.getResource("/css/theme.css").toExternalForm());

        stage.setTitle("SIA Avitech — Inicio de sesión");

        // Configurar el tamaño de la ventana según la pantalla detectada
        screenManager.configureStage(stage, true); // true = maximizada

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Método público para cambiar de escena (pantalla)
     * desde cualquier controlador.
     * @param fxmlPath ruta del archivo FXML
     * @param title título de la ventana
     */
    public static void goTo(String fxmlPath, String title) {
        try {
            boolean wasMaximized = primaryStage.isMaximized();
            double width = primaryStage.getWidth();
            double height = primaryStage.getHeight();

            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(App.class.getResource("/css/theme.css").toExternalForm());

            primaryStage.setTitle(title);
            primaryStage.setScene(scene);

            // Mantener el estado de maximización o tamaño previo
            if (wasMaximized) {
                screenManager.configureStage(primaryStage, true);
            } else {
                if (!Double.isNaN(width) && width > 0 && !Double.isNaN(height) && height > 0) {
                    primaryStage.setWidth(width);
                    primaryStage.setHeight(height);
                } else {
                    // Si no hay dimensiones válidas, usar el ScreenManager
                    screenManager.configureStage(primaryStage, false);
                }
            }
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al cambiar de escena: " + fxmlPath, e);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
