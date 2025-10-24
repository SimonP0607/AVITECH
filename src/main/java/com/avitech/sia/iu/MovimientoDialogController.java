package com.avitech.sia.iu;

import com.avitech.sia.db.SuministrosDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class MovimientoDialogController {

    @FXML private Label lblTitulo;
    @FXML private ComboBox<String> cbProducto;
    @FXML private TextField txtCantidad;
    @FXML private TextArea txtDetalle;
    @FXML private Button btnGuardar;

    private Stage dialogStage;
    private boolean isEntrada;
    private Runnable onSaveCallback;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setTipo(boolean isEntrada) {
        this.isEntrada = isEntrada;
        lblTitulo.setText(isEntrada ? "Registrar Entrada" : "Registrar Salida");
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @FXML
    private void initialize() {
        // Cargar productos en el ComboBox
        try {
            List<String> productos = SuministrosDAO.getProductos();
            cbProducto.getItems().setAll(productos);
        } catch (Exception e) {
            e.printStackTrace();
            // Manejar error
        }
    }

    @FXML
    private void onSave() {
        // Validar y guardar
        String producto = cbProducto.getValue();
        String cantidadStr = txtCantidad.getText();
        String detalle = txtDetalle.getText();

        if (producto == null || cantidadStr.isEmpty()) {
            // Mostrar alerta
            return;
        }

        try {
            int cantidad = Integer.parseInt(cantidadStr);
            String tipo = isEntrada ? "Entrada" : "Salida";

            SuministrosDAO.addMovimiento(producto, cantidad, tipo, detalle, "admin"); // Usuario harcodeado por ahora

            if (onSaveCallback != null) {
                onSaveCallback.run();
            }

            dialogStage.close();

        } catch (NumberFormatException e) {
            // Mostrar alerta de cantidad inválida
        } catch (Exception e) {
            e.printStackTrace();
            // Mostrar alerta de error al guardar
        }
    }

    @FXML
    private void onCancel() {
        dialogStage.close();
    }
}
