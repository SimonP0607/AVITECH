package com.avitech.sia.iu.parametros;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

public class NuevaUnidadController {

    @FXML private StackPane modalRoot;
    @FXML private Label lblTitle;
    @FXML private TextField txtNombre;
    @FXML private TextField txtSimbolo;
    @FXML private ComboBox<String> cbTipo;
    @FXML private TextArea txtDescripcion;
    @FXML private ComboBox<String> cbEstado;
    @FXML private Button btnClose;
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;

    private UnidadesController.Unidad result;
    private UnidadesController.Unidad editingUnidad;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        // Configurar combos
        cbTipo.setItems(FXCollections.observableArrayList(
                "Peso", "Volumen", "Cantidad", "Longitud", "Temperatura"
        ));

        cbEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        cbEstado.getSelectionModel().selectFirst();

        // Configurar botones
        btnClose.setOnAction(e -> cerrarModal());
        btnCancelar.setOnAction(e -> cerrarModal());
        btnGuardar.setOnAction(e -> guardar());
    }

    /**
     * Configura el modal para editar una unidad existente
     */
    public void setEditMode(UnidadesController.Unidad unidad) {
        if (unidad == null) return;

        this.isEditMode = true;
        this.editingUnidad = unidad;

        lblTitle.setText("Editar Unidad de Medida");
        txtNombre.setText(unidad.nombre);
        txtSimbolo.setText(unidad.simbolo);
        cbTipo.setValue(unidad.tipo);
        txtDescripcion.setText(unidad.desc);
        cbEstado.setValue(unidad.estado);

        btnGuardar.setText("Actualizar");
    }

    private void guardar() {
        // Validar campos
        if (!validarCampos()) {
            return;
        }

        // Crear objeto con los datos
        result = new UnidadesController.Unidad(
                txtNombre.getText().trim(),
                txtSimbolo.getText().trim(),
                cbTipo.getValue(),
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

        if (txtSimbolo.getText() == null || txtSimbolo.getText().trim().isEmpty()) {
            mostrarError("El símbolo es obligatorio");
            txtSimbolo.requestFocus();
            return false;
        }

        if (cbTipo.getValue() == null) {
            mostrarError("Debe seleccionar un tipo");
            cbTipo.requestFocus();
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
    public UnidadesController.Unidad getResult() {
        return result;
    }

    /**
     * Retorna si está en modo edición
     */
    public boolean isEditMode() {
        return isEditMode;
    }

    /**
     * Obtiene la unidad que se está editando
     */
    public UnidadesController.Unidad getEditingUnidad() {
        return editingUnidad;
    }
}

