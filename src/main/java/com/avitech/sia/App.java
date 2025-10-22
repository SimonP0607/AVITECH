package com.avitech.sia;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

public class App extends Application {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static Stage primaryStage;

    public static Stage PrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage stage) throws IOException {
        logger.info("Iniciando aplicación SIA Avitech");

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
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();

        logger.info("Aplicación iniciada correctamente");
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
            if (wasMaximized) {
                primaryStage.setMaximized(true);
            } else {
                if (!Double.isNaN(width) && width > 0) {
                    primaryStage.setWidth(width);
                }
                if (!Double.isNaN(height) && height > 0) {
                    primaryStage.setHeight(height);
                }
            }
            primaryStage.show();
            logger.info("Navegando a: {}", fxmlPath);
        } catch (IOException e) {
            logger.error("Error al cambiar de escena: {}", fxmlPath, e);
            throw new RuntimeException("Error al cambiar de escena: " + fxmlPath, e);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
