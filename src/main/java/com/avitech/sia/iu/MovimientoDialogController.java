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
    @FXML private TextField txtUnidad;
    @FXML private TextField txtProveedor;
    @FXML private TextField txtValorTotal;
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
        try {
            List<String> productos = SuministrosDAO.getProductos();
            if (productos.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "No se encontraron productos no medicinales en la base de datos. Asegúrate de tener entradas en la tabla Suministros que no sean medicamentos.").showAndWait();
            } else {
                cbProducto.getItems().setAll(productos);
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al cargar productos: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onSave() {
        if (!valid()) return;

        String producto = cbProducto.getValue();
        String cantidadStr = txtCantidad.getText();
        String unidad = txtUnidad.getText();
        String proveedor = txtProveedor.getText();
        String valorTotalStr = txtValorTotal.getText();
        String detalle = txtDetalle.getText();

        try {
            int cantidad = Integer.parseInt(cantidadStr);
            double valorTotal = Double.parseDouble(valorTotalStr);
            String tipo = isEntrada ? "Entrada" : "Salida";

            SuministrosDAO.addMovimiento(producto, cantidad, unidad, proveedor, valorTotal, tipo, detalle, "admin"); // Responsable hardcodeado por ahora

            if (onSaveCallback != null) {
                onSaveCallback.run();
            }

            dialogStage.close();

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "La cantidad y el valor total deben ser números válidos.").showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al guardar el movimiento: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onCancel() {
        dialogStage.close();
    }

    private boolean valid() {
        StringBuilder sb = new StringBuilder();
        if (cbProducto.getValue() == null || cbProducto.getValue().isBlank()) sb.append("• Producto.\n");
        if (txtCantidad.getText() == null || txtCantidad.getText().isBlank()) sb.append("• Cantidad.\n");
        if (txtUnidad.getText() == null || txtUnidad.getText().isBlank()) sb.append("• Unidad.\n");
        if (txtProveedor.getText() == null || txtProveedor.getText().isBlank()) sb.append("• Proveedor.\n");
        if (txtValorTotal.getText() == null || txtValorTotal.getText().isBlank()) sb.append("• Valor Total.\n");

        // Validar que Cantidad y Valor Total sean números
        try { Integer.parseInt(txtCantidad.getText()); } catch (NumberFormatException e) { sb.append("• Cantidad debe ser un número válido.\n"); }
        try { Double.parseDouble(txtValorTotal.getText()); } catch (NumberFormatException e) { sb.append("• Valor Total debe ser un número válido.\n"); }

        if (sb.length() > 0) {
            new Alert(Alert.AlertType.WARNING, "Completa y corrige los campos:\n\n" + sb).showAndWait();
            return false;
        }
        return true;
    }

    private boolean isEmpty(ComboBox<String> cb) {
        return cb.getValue() == null || cb.getValue().isBlank();
    }
}
