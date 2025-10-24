package com.avitech.sia.iu.parametros;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class NuevaCategoriaController {

    @FXML private StackPane modalRoot;
    @FXML private Label lblTitle;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cbTipo;
    @FXML private ColorPicker cpColor;
    @FXML private TextArea txtDescripcion;
    @FXML private ComboBox<String> cbEstado;
    @FXML private Button btnClose;
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;

    private CategoriasController.Categoria result;
    private CategoriasController.Categoria editingCategoria;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        // Configurar combos
        cbTipo.setItems(FXCollections.observableArrayList(
                "Insumo", "Producto", "Alimento", "Medicamento", "Equipo", "Herramienta"
        ));

        cbEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        cbEstado.getSelectionModel().selectFirst();

        // Configurar ColorPicker con color por defecto
        cpColor.setValue(Color.web("#10B981")); // Verde por defecto

        // Configurar botones
        btnClose.setOnAction(e -> cerrarModal());
        btnCancelar.setOnAction(e -> cerrarModal());
        btnGuardar.setOnAction(e -> guardar());
    }

    /**
     * Configura el modal para editar una categoría existente
     */
    public void setEditMode(CategoriasController.Categoria categoria) {
        if (categoria == null) return;

        this.isEditMode = true;
        this.editingCategoria = categoria;

        lblTitle.setText("Editar Categoría");
        txtNombre.setText(categoria.nombre);
        cbTipo.setValue(categoria.tipo);

        // Convertir color hex a Color
        try {
            cpColor.setValue(Color.web(categoria.color));
        } catch (Exception e) {
            cpColor.setValue(Color.web("#10B981"));
        }

        txtDescripcion.setText(categoria.desc);
        cbEstado.setValue(categoria.estado);

        btnGuardar.setText("Actualizar");
    }

    private void guardar() {
        // Validar campos
        if (!validarCampos()) {
            return;
        }

        // Convertir Color a hex string
        String colorHex = String.format("#%02X%02X%02X",
                (int) (cpColor.getValue().getRed() * 255),
                (int) (cpColor.getValue().getGreen() * 255),
                (int) (cpColor.getValue().getBlue() * 255));

        // Crear objeto con los datos
        result = new CategoriasController.Categoria(
                txtNombre.getText().trim(),
                cbTipo.getValue(),
                colorHex,
                txtDescripcion.getText().trim(),
                cbEstado.getValue()
        );

        // TODO: Aquí se conectará con la base de datos
        // Si es modo edición: UPDATE en BD
        // Si es modo creación: INSERT en BD

        cerrarModal();
    }

    private boolean validarCampos() {
        if (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) {
            mostrarError("El nombre es obligatorio");
            txtNombre.requestFocus();
            return false;
        }

        if (cbTipo.getValue() == null) {
            mostrarError("Debe seleccionar un tipo");
            cbTipo.requestFocus();
            return false;
        }

        if (cpColor.getValue() == null) {
            mostrarError("Debe seleccionar un color");
            return false;
        }

        if (cbEstado.getValue() == null) {
            mostrarError("Debe seleccionar un estado");
            cbEstado.requestFocus();
            return false;
        }

        return true;
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validación");
        alert.setHeaderText("Datos incompletos");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cerrarModal() {
        if (modalRoot != null && modalRoot.getParent() != null) {
            ((javafx.scene.layout.Pane) modalRoot.getParent()).getChildren().remove(modalRoot);
        }
    }

    /**
     * Obtiene el resultado después de cerrar el modal
     */
    public CategoriasController.Categoria getResult() {
        return result;
    }

    /**
     * Retorna si está en modo edición
     */
    public boolean isEditMode() {
        return isEditMode;
    }

    /**
     * Obtiene la categoría que se está editando
     */
    public CategoriasController.Categoria getEditingCategoria() {
        return editingCategoria;
    }
}

