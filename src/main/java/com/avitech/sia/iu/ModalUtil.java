package com.avitech.sia.iu;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public final class ModalUtil {

    private ModalUtil() {}

    /**
     * Abre un modal y devuelve el controller del FXML ya inicializado.
     * - Si ownerNode está en una Scene, esa ventana será el owner.
     * - Si no, busca una ventana visible (fallback), así no depende de App.getPrimaryStage().
     */
    @SuppressWarnings("unchecked")
    public static <T> T openModal(Node ownerNode, String fxmlPath, String title) {
        try {
            FXMLLoader fx = new FXMLLoader(ModalUtil.class.getResource(fxmlPath));
            Parent root = fx.load();

            Stage dialog = new Stage(StageStyle.TRANSPARENT);
            dialog.setTitle(title);
            dialog.initModality(Modality.WINDOW_MODAL);

            // --- Owner seguro ---
            Window owner = null;
            if (ownerNode != null && ownerNode.getScene() != null) {
                owner = ownerNode.getScene().getWindow();
            }
            if (owner == null) {
                // fallback: toma cualquier ventana que esté mostrando
                owner = Window.getWindows().stream()
                        .filter(Window::isShowing)
                        .findFirst()
                        .orElse(null);
            }
            if (owner != null) {
                dialog.initOwner(owner);
            }

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            // CSS global (ajusta la ruta si tu theme está en otro lugar)
            scene.getStylesheets().add(
                    ModalUtil.class.getResource("/css/theme.css").toExternalForm()
            );
            dialog.setScene(scene);

            dialog.showAndWait();
            return (T) fx.getController();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
