package com.avitech.sia.iu.parametros;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

public class NuevoMedicamentoController {

    @FXML private StackPane modalRoot;
    @FXML private Label lblTitle;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrincipioActivo;
    @FXML private ComboBox<String> cbTipo;
    @FXML private TextField txtPresentacion;
    @FXML private TextField txtDosis;
    @FXML private TextField txtRetiroDias;
    @FXML private TextField txtFabricante;
    @FXML private TextField txtRegistro;
    @FXML private ComboBox<String> cbEstado;
    @FXML private TextArea txtIndicaciones;
    @FXML private Button btnClose;
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;

    private MedicamentosController.Medicamento result;
    private MedicamentosController.Medicamento editingMedicamento;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        // Configurar combos
        cbTipo.setItems(FXCollections.observableArrayList(
                "Antibiótico", "Vitamina", "Vacuna", "Antiparasitario",
                "Desinfectante", "Suplemento", "Antipirético", "Analgésico"
        ));

        cbEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        cbEstado.getSelectionModel().selectFirst();

        // Validar que retiroDias sea numérico
        txtRetiroDias.setText("0");
        txtRetiroDias.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                txtRetiroDias.setText(oldVal);
            }
        });

        // Configurar botones
        btnClose.setOnAction(e -> cerrarModal());
        btnCancelar.setOnAction(e -> cerrarModal());
        btnGuardar.setOnAction(e -> guardar());
    }

    /**
     * Configura el modal para editar un medicamento existente
     */
    public void setEditMode(MedicamentosController.Medicamento medicamento) {
        if (medicamento == null) return;

        this.isEditMode = true;
        this.editingMedicamento = medicamento;

        lblTitle.setText("Editar Medicamento");
        txtNombre.setText(medicamento.nombre);
        txtPrincipioActivo.setText(medicamento.pActivo);
        cbTipo.setValue(medicamento.tipo);
        txtDosis.setText(medicamento.dosis);
        txtRetiroDias.setText(medicamento.retiroDias);
        cbEstado.setValue(medicamento.estado);

        btnGuardar.setText("Actualizar");
    }

    private void guardar() {
        // Validar campos
        if (!validarCampos()) {
            return;
        }

        // Crear objeto con los datos
        result = new MedicamentosController.Medicamento(
                txtNombre.getText().trim(),
                txtPrincipioActivo.getText().trim(),
                cbTipo.getValue(),
                txtDosis.getText().trim(),
                txtRetiroDias.getText().trim(),
                cbEstado.getValue()
        );

        // TODO: Aquí se conectará con la base de datos
        // Si es modo edición: UPDATE en BD
        // Si es modo creación: INSERT en BD
        // También se guardará: presentación, fabricante, registro, indicaciones

        cerrarModal();
    }

    private boolean validarCampos() {
        if (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) {
            mostrarError("El nombre comercial es obligatorio");
            txtNombre.requestFocus();
            return false;
        }

        if (txtPrincipioActivo.getText() == null || txtPrincipioActivo.getText().trim().isEmpty()) {
            mostrarError("El principio activo es obligatorio");
            txtPrincipioActivo.requestFocus();
            return false;
        }

        if (cbTipo.getValue() == null) {
            mostrarError("Debe seleccionar un tipo");
            cbTipo.requestFocus();
            return false;
        }

        if (txtDosis.getText() == null || txtDosis.getText().trim().isEmpty()) {
            mostrarError("La dosis recomendada es obligatoria");
            txtDosis.requestFocus();
            return false;
        }

        if (txtRetiroDias.getText() == null || txtRetiroDias.getText().trim().isEmpty()) {
            mostrarError("El tiempo de retiro es obligatorio (use 0 si no aplica)");
            txtRetiroDias.requestFocus();
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
    public MedicamentosController.Medicamento getResult() {
        return result;
    }

    /**
     * Retorna si está en modo edición
     */
    public boolean isEditMode() {
        return isEditMode;
    }

    /**
     * Obtiene el medicamento que se está editando
     */
    public MedicamentosController.Medicamento getEditingMedicamento() {
        return editingMedicamento;
    }
}

