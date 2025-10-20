package com.avitech.sia.iu;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

import java.io.IOException;

/**
 * Abre modales como overlays dentro de la misma escena (no crea Stage).
 * Requiere un StackPane con fx:id="overlayHost" en el FXML de la vista.
 * Los FXML de los modales deben tener el root con styleClass "modal-root".
 */
public final class ModalUtil {

    private ModalUtil() {}

    /**
     * Carga el FXML y lo inserta como overlay dentro de #overlayHost.
     * Devuelve el controlador del modal (o null si algo falla).
     */
    public static <T> T openModal(Node trigger, String fxmlPath, String titleIgnored) {
        if (trigger == null || trigger.getScene() == null) return null;

        try {
            // Cargar el modal
            FXMLLoader loader = new FXMLLoader(ModalUtil.class.getResource(fxmlPath));
            Node modalRoot = loader.load();

            // Buscar el host del overlay en la escena actual
            Scene scene = trigger.getScene();
            StackPane overlayHost = (StackPane) scene.lookup("#overlayHost");
            if (overlayHost == null) {
                System.err.println("[ModalUtil] No se encontró #overlayHost en la escena.");
                return null;
            }

            // Insertar el modal arriba del contenido
            overlayHost.getChildren().add(modalRoot);

            // Cerrar al hacer click en el fondo si el overlay lo permite
            modalRoot.setOnMouseClicked(ev -> {
                Node target = (Node) ev.getTarget();
                if (target.getStyleClass().contains("modal-overlay")) {
                    closeModal(modalRoot);
                }
            });

            return loader.getController();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Cierra el modal removiéndolo del overlayHost.
     * Puedes llamarlo desde el controlador del modal.
     */
    public static void closeModal(Node anyNodeInsideModal) {
        if (anyNodeInsideModal == null) return;
        Scene scene = anyNodeInsideModal.getScene();
        if (scene == null) return;

        // El modal raíz tiene styleClass "modal-root". Subimos hasta él.
        Node modalRoot = anyNodeInsideModal;
        while (modalRoot != null && (modalRoot.getStyleClass() == null ||
                !modalRoot.getStyleClass().contains("modal-root"))) {
            modalRoot = modalRoot.getParent();
        }
        if (modalRoot == null) return;

        StackPane overlayHost = (StackPane) scene.lookup("#overlayHost");
        if (overlayHost != null) {
            overlayHost.getChildren().remove(modalRoot);
        }
    }
}
