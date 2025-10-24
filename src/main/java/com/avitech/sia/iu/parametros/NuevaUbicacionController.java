package com.avitech.sia.iu.parametros;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

public class NuevaUbicacionController {

    @FXML private StackPane modalRoot;
    @FXML private Label lblTitle;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cbTipo;
    @FXML private TextField txtCapacidad;
    @FXML private ComboBox<String> cbResponsable;
    @FXML private TextField txtCodigo;
    @FXML private ComboBox<String> cbEstado;
    @FXML private TextArea txtDescripcion;
    @FXML private TextArea txtDireccion;
    @FXML private Button btnClose;
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;

    private UbicacionesController.Ubicacion result;
    private UbicacionesController.Ubicacion editingUbicacion;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        // Configurar combos
        cbTipo.setItems(FXCollections.observableArrayList(
                "Galpón", "Almacén", "Bodega", "Área de Proceso",
                "Oficina", "Laboratorio", "Área Externa", "Cuarentena"
        ));

        cbResponsable.setItems(FXCollections.observableArrayList(
                "Carlos Pérez", "Ana Rodríguez", "Luis Morales", "Elena Vargas", "María González"
        ));

        cbEstado.setItems(FXCollections.observableArrayList(
                "Activo", "Inactivo", "En mantenimiento", "En construcción"
        ));
        cbEstado.getSelectionModel().selectFirst();

        // Configurar botones
        btnClose.setOnAction(e -> cerrarModal());
        btnCancelar.setOnAction(e -> cerrarModal());
        btnGuardar.setOnAction(e -> guardar());
    }

    /**
     * Configura el modal para editar una ubicación existente
     */
    public void setEditMode(UbicacionesController.Ubicacion ubicacion) {
        if (ubicacion == null) return;

        this.isEditMode = true;
        this.editingUbicacion = ubicacion;

        lblTitle.setText("Editar Ubicación");
        txtNombre.setText(ubicacion.nombre);
        cbTipo.setValue(ubicacion.tipo);
        txtCapacidad.setText(ubicacion.capacidad);
        cbResponsable.setValue(ubicacion.responsable);
        txtDescripcion.setText(ubicacion.descripcion);
        cbEstado.setValue(ubicacion.estado);

        btnGuardar.setText("Actualizar");
    }

    private void guardar() {
        // Validar campos
        if (!validarCampos()) {
            return;
        }

        // Obtener el responsable (puede ser del combo o texto ingresado)
        String responsable = cbResponsable.getValue();
        if (responsable == null || responsable.trim().isEmpty()) {
            responsable = cbResponsable.getEditor().getText().trim();
        }

        // Crear objeto con los datos
        result = new UbicacionesController.Ubicacion(
                txtNombre.getText().trim(),
                cbTipo.getValue(),
                txtCapacidad.getText().trim(),
                responsable,
                txtDescripcion.getText().trim(),
                cbEstado.getValue()
        );

        // TODO: Aquí se conectará con la base de datos
        // Si es modo edición: UPDATE en BD
        // Si es modo creación: INSERT en BD
        // También se guardará: código, dirección/ubicación física

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

        if (txtCapacidad.getText() == null || txtCapacidad.getText().trim().isEmpty()) {
            mostrarError("La capacidad es obligatoria");
            txtCapacidad.requestFocus();
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
    public UbicacionesController.Ubicacion getResult() {
        return result;
    }

    /**
     * Retorna si está en modo edición
     */
    public boolean isEditMode() {
        return isEditMode;
    }

    /**
     * Obtiene la ubicación que se está editando
     */
    public UbicacionesController.Ubicacion getEditingUbicacion() {
        return editingUbicacion;
    }
}

