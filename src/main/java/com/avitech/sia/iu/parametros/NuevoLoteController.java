package com.avitech.sia.iu.parametros;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import java.time.LocalDate;

public class NuevoLoteController {

    @FXML private StackPane modalRoot;
    @FXML private Label lblTitle;
    @FXML private TextField txtNombre;
    @FXML private TextField txtAves;
    @FXML private ComboBox<String> cbRaza;
    @FXML private ComboBox<String> cbGalpon;
    @FXML private DatePicker dpFechaIngreso;
    @FXML private TextField txtEdadInicial;
    @FXML private TextField txtProveedor;
    @FXML private TextField txtPesoInicial;
    @FXML private ComboBox<String> cbEstado;
    @FXML private TextArea txtObservaciones;
    @FXML private Button btnClose;
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;

    private LotesController.Lote result;
    private LotesController.Lote editingLote;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        // Configurar combos
        cbRaza.setItems(FXCollections.observableArrayList(
                "Hy-Line Brown", "Hy-Line W-36", "Lohmann LSL", "Lohmann Brown",
                "ISA Brown", "Dekalb White", "Bovans Brown", "Hisex Brown"
        ));

        cbGalpon.setItems(FXCollections.observableArrayList(
                "Galpón 1", "Galpón 2", "Galpón 3", "Galpón 4", "Galpón 5"
        ));

        cbEstado.setItems(FXCollections.observableArrayList(
                "Activo", "En preparación", "Terminado", "En cuarentena"
        ));
        cbEstado.getSelectionModel().select("Activo");

        // Configurar fecha por defecto
        dpFechaIngreso.setValue(LocalDate.now());

        // Validar campos numéricos
        txtAves.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                txtAves.setText(oldVal);
            }
        });

        txtEdadInicial.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                txtEdadInicial.setText(oldVal);
            }
        });

        txtPesoInicial.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                txtPesoInicial.setText(oldVal);
            }
        });

        // Configurar botones
        btnClose.setOnAction(e -> cerrarModal());
        btnCancelar.setOnAction(e -> cerrarModal());
        btnGuardar.setOnAction(e -> guardar());
    }

    /**
     * Configura el modal para editar un lote existente
     */
    public void setEditMode(LotesController.Lote lote) {
        if (lote == null) return;

        this.isEditMode = true;
        this.editingLote = lote;

        lblTitle.setText("Editar Lote de Aves");
        txtNombre.setText(lote.nombre);
        txtNombre.setDisable(true); // No permitir cambiar el código
        txtAves.setText(lote.aves);
        cbRaza.setValue(lote.raza);
        cbGalpon.setValue(lote.galpon);

        // Parsear fecha
        try {
            dpFechaIngreso.setValue(LocalDate.parse(lote.fechaIngreso));
        } catch (Exception e) {
            dpFechaIngreso.setValue(LocalDate.now());
        }

        txtEdadInicial.setText(lote.edad);
        cbEstado.setValue(lote.estado);

        btnGuardar.setText("Actualizar");
    }

    private void guardar() {
        // Validar campos
        if (!validarCampos()) {
            return;
        }

        // Calcular edad actual (si se necesita)
        String edad = txtEdadInicial.getText().trim();
        if (edad.isEmpty()) edad = "0";

        // Crear objeto con los datos
        result = new LotesController.Lote(
                txtNombre.getText().trim(),
                txtAves.getText().trim(),
                edad,
                cbRaza.getValue(),
                cbGalpon.getValue(),
                dpFechaIngreso.getValue().toString(),
                cbEstado.getValue()
        );

        // TODO: Aquí se conectará con la base de datos
        // Si es modo edición: UPDATE en BD
        // Si es modo creación: INSERT en BD
        // También se guardará: proveedor, peso inicial, observaciones

        cerrarModal();
    }

    private boolean validarCampos() {
        if (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) {
            mostrarError("El código de lote es obligatorio");
            txtNombre.requestFocus();
            return false;
        }

        if (txtAves.getText() == null || txtAves.getText().trim().isEmpty()) {
            mostrarError("La cantidad de aves es obligatoria");
            txtAves.requestFocus();
            return false;
        }

        try {
            int aves = Integer.parseInt(txtAves.getText().trim());
            if (aves < 0) {
                mostrarError("La cantidad de aves debe ser mayor o igual a cero");
                txtAves.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarError("La cantidad de aves debe ser un número válido");
            txtAves.requestFocus();
            return false;
        }

        if (cbRaza.getValue() == null) {
            mostrarError("Debe seleccionar una raza");
            cbRaza.requestFocus();
            return false;
        }

        if (cbGalpon.getValue() == null) {
            mostrarError("Debe seleccionar un galpón");
            cbGalpon.requestFocus();
            return false;
        }

        if (dpFechaIngreso.getValue() == null) {
            mostrarError("Debe seleccionar la fecha de ingreso");
            dpFechaIngreso.requestFocus();
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
    public LotesController.Lote getResult() {
        return result;
    }

    /**
     * Retorna si está en modo edición
     */
    public boolean isEditMode() {
        return isEditMode;
    }

    /**
     * Obtiene el lote que se está editando
     */
    public LotesController.Lote getEditingLote() {
        return editingLote;
    }
}

