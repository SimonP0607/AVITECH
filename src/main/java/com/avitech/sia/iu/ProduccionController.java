package com.avitech.sia.iu;

import com.avitech.sia.App;
import com.avitech.sia.db.ProduccionDAO;
import com.avitech.sia.db.UsuarioDAO;
import com.avitech.sia.iu.produccion.dto.ProduccionDTO;

// IMPORTS FALTANTES AÑADIDOS
import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProduccionController {

    // Top / sidebar
    @FXML private Label lblSystemStatus, lblHeader, lblUserInfo;

    // KPIs
    @FXML private Label lblKpiProduccionTotal, lblKpiPosturaPromedio, lblKpiPesoPromedio, lblKpiMortalidadTotal;
    @FXML private Label lblKpiProdDelta, lblKpiPosturaDelta, lblKpiPesoDelta, lblKpiMortalidadDelta;

    // Filtros
    @FXML private ComboBox<String> cbFiltroGalpon;

    // Tabla de Producción Diaria
    @FXML private TableView<ProduccionRow> tblDiario;
    @FXML private TableColumn<ProduccionRow, LocalDate> colFecha;
    @FXML private TableColumn<ProduccionRow, String> colGalpon, colPostura, colPeso, colClasif, colResponsable;
    @FXML private TableColumn<ProduccionRow, Integer> colAves, colProduccion, colMortalidad;

    // Formulario de Registro
    @FXML private ComboBox<String> cbGalpon;
    @FXML private TextField tfProduccionTotal, tfPesoPromedio, tfMortalidad, tfHuevosB, tfHuevosA, tfHuevosAA, tfHuevosAAA;
    @FXML private Button btnGuardarRegistro, btnCancelarRegistro;

    private final ObservableList<ProduccionRow> produccionData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        lblHeader.setText("Administrador");
        lblUserInfo.setText("Administrador");
        lblSystemStatus.setText("Sistema Online – MySQL Local");

        setupTableColumns();
        loadGalpones();
        refreshData();
    }

    private void setupTableColumns() {
        colFecha.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getFecha()));
        colGalpon.setCellValueFactory(cellData -> cellData.getValue().galponProperty());
        colAves.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAves()).asObject());
        colProduccion.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getProduccion()).asObject());
        colPostura.setCellValueFactory(cellData -> cellData.getValue().posturaProperty());
        colPeso.setCellValueFactory(cellData -> cellData.getValue().pesoPromedioProperty());
        colClasif.setCellValueFactory(cellData -> cellData.getValue().clasificacionProperty());
        colMortalidad.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getMortalidad()).asObject());
        colResponsable.setCellValueFactory(cellData -> cellData.getValue().responsableProperty());
        tblDiario.setItems(produccionData);
    }

    private void loadGalpones() {
        try {
            List<String> galpones = new ArrayList<>();
            galpones.add("Todos");
            galpones.addAll(ProduccionDAO.getGalponesNombres());
            cbFiltroGalpon.setItems(FXCollections.observableArrayList(galpones));
            cbFiltroGalpon.getSelectionModel().selectFirst();

            cbGalpon.setItems(FXCollections.observableArrayList(ProduccionDAO.getGalponesNombres()));
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al cargar galpones: " + e.getMessage()).showAndWait();
        }
    }

    private void refreshData() {
        try {
            String selectedGalpon = cbFiltroGalpon.getSelectionModel().getSelectedItem();
            produccionData.setAll(ProduccionDAO.getProduccionDiaria(selectedGalpon));
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudieron cargar los datos de producción: " + e.getMessage()).showAndWait();
        }
        refreshKpis();
    }

    private void refreshKpis() {
        try {
            lblKpiProduccionTotal.setText(String.valueOf(ProduccionDAO.getTotalProduccion()));
            lblKpiPosturaPromedio.setText(String.format("%.2f%%", ProduccionDAO.getPromedioPostura()));
            lblKpiPesoPromedio.setText(String.format("%.2fg", ProduccionDAO.getPromedioPeso()));
            lblKpiMortalidadTotal.setText(String.valueOf(ProduccionDAO.getTotalMortalidad()));

            // TODO: Implementar cálculo de deltas para KPIs
            lblKpiProdDelta.setText("+0%");
            lblKpiPosturaDelta.setText("+0%");
            lblKpiPesoDelta.setText("+0g");
            lblKpiMortalidadDelta.setText("0 aves");

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al cargar KPIs de producción: " + e.getMessage()).showAndWait();
        }
    }

    /* ================== Acciones ================== */

    @FXML
    private void onGuardarRegistro() {
        if (!validarFormulario()) return;

        try {
            ProduccionDTO dto = new ProduccionDTO(
                    LocalDate.now(), // Fecha actual
                    cbGalpon.getValue(),
                    Integer.parseInt(tfProduccionTotal.getText()),
                    Double.parseDouble(tfPesoPromedio.getText()),
                    Integer.parseInt(tfMortalidad.getText()),
                    Integer.parseInt(tfHuevosB.getText()),
                    Integer.parseInt(tfHuevosA.getText()),
                    Integer.parseInt(tfHuevosAA.getText()),
                    Integer.parseInt(tfHuevosAAA.getText())
            );
            ProduccionDAO.guardarProduccion(dto);
            new Alert(Alert.AlertType.INFORMATION, "Registro de producción guardado exitosamente.").showAndWait();
            limpiarFormulario();
            refreshData();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Por favor, ingresa números válidos en los campos de cantidad y peso.").showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al guardar el registro de producción: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onCancelarRegistro() {
        limpiarFormulario();
    }

    @FXML
    private void onFiltrar() {
        refreshData();
    }

    @FXML
    private void onLimpiarFiltro() {
        cbFiltroGalpon.getSelectionModel().selectFirst();
        refreshData();
    }

    private boolean validarFormulario() {
        StringBuilder sb = new StringBuilder();
        if (cbGalpon.getValue() == null || cbGalpon.getValue().isBlank()) sb.append("• Galpón.\n");
        if (tfProduccionTotal.getText() == null || tfProduccionTotal.getText().isBlank()) sb.append("• Producción Total.\n");
        if (tfPesoPromedio.getText() == null || tfPesoPromedio.getText().isBlank()) sb.append("• Peso Promedio.\n");
        if (tfMortalidad.getText() == null || tfMortalidad.getText().isBlank()) sb.append("• Mortalidad.\n");
        if (tfHuevosB.getText() == null || tfHuevosB.getText().isBlank()) sb.append("• Huevos B.\n");
        if (tfHuevosA.getText() == null || tfHuevosA.getText().isBlank()) sb.append("• Huevos A.\n");
        if (tfHuevosAA.getText() == null || tfHuevosAA.getText().isBlank()) sb.append("• Huevos AA.\n");
        if (tfHuevosAAA.getText() == null || tfHuevosAAA.getText().isBlank()) sb.append("• Huevos AAA.\n");

        try { Integer.parseInt(tfProduccionTotal.getText()); } catch (NumberFormatException e) { sb.append("• Producción Total debe ser un número válido.\n"); }
        try { Double.parseDouble(tfPesoPromedio.getText()); } catch (NumberFormatException e) { sb.append("• Peso Promedio debe ser un número válido.\n"); }
        try { Integer.parseInt(tfMortalidad.getText()); } catch (NumberFormatException e) { sb.append("• Mortalidad debe ser un número válido.\n"); }
        try { Integer.parseInt(tfHuevosB.getText()); } catch (NumberFormatException e) { sb.append("• Huevos B debe ser un número válido.\n"); }
        try { Integer.parseInt(tfHuevosA.getText()); } catch (NumberFormatException e) { sb.append("• Huevos A debe ser un número válido.\n"); }
        try { Integer.parseInt(tfHuevosAA.getText()); } catch (NumberFormatException e) { sb.append("• Huevos AA debe ser un número válido.\n"); }
        try { Integer.parseInt(tfHuevosAAA.getText()); } catch (NumberFormatException e) { sb.append("• Huevos AAA debe ser un número válido.\n"); }

        if (sb.length() > 0) {
            new Alert(Alert.AlertType.WARNING, "Completa y corrige los campos:\n\n" + sb).showAndWait();
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        cbGalpon.getSelectionModel().clearSelection();
        tfProduccionTotal.clear();
        tfPesoPromedio.clear();
        tfMortalidad.clear();
        tfHuevosB.clear();
        tfHuevosA.clear();
        tfHuevosAA.clear();
        tfHuevosAAA.clear();
    }

    /* ================== Navegación ================== */
    @FXML private void goDashboard()  { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
    @FXML private void goSupplies()   { App.goTo("/fxml/suministros.fxml", "SIA Avitech — Suministros"); }
    @FXML private void goHealth()     { App.goTo("/fxml/sanidad.fxml", "SIA Avitech — Sanidad"); }
    @FXML private void goProduction() { /* ya aquí */ }
    @FXML private void goReports()    { App.goTo("/fxml/reportes.fxml", "SIA Avitech — Reportes"); }
    @FXML private void goAlerts()     { App.goTo("/fxml/alertas.fxml", "SIA Avitech — Alertas"); }
    @FXML private void goAudit()      { App.goTo("/fxml/auditoria.fxml", "SIA Avitech — Auditoría"); }
    @FXML private void goParams()     { App.goTo("/fxml/parametros.fxml", "SIA Avitech — Parámetros"); }
    @FXML private void goUsers()      { App.goTo("/fxml/usuarios.fxml", "SIA Avitech — Usuarios"); }
    @FXML private void goBackup()     { App.goTo("/fxml/respaldos.fxml", "SIA Avitech — Respaldos"); }
    @FXML private void onExit() {
        App.goTo("/fxml/login.fxml", "SIA Avitech — Inicio de sesión");
    }

    /* ================== Row Model ================== */
    public static class ProduccionRow {
        private final LocalDate fecha;
        private final StringProperty galpon = new SimpleStringProperty();
        private final IntegerProperty aves = new SimpleIntegerProperty();
        private final IntegerProperty produccion = new SimpleIntegerProperty();
        private final StringProperty postura = new SimpleStringProperty();
        private final StringProperty pesoPromedio = new SimpleStringProperty();
        private final StringProperty clasificacion = new SimpleStringProperty();
        private final IntegerProperty mortalidad = new SimpleIntegerProperty(); // Corregido: eliminado 'new' duplicado
        private final StringProperty responsable = new SimpleStringProperty();

        public ProduccionRow(LocalDate fecha, String galpon, int aves, int produccion, String postura, String pesoPromedio, String clasificacion, int mortalidad, String responsable) {
            this.fecha = fecha;
            this.galpon.set(galpon);
            this.aves.set(aves);
            this.produccion.set(produccion);
            this.postura.set(postura);
            this.pesoPromedio.set(pesoPromedio);
            this.clasificacion.set(clasificacion);
            this.mortalidad.set(mortalidad);
            this.responsable.set(responsable);
        }

        public LocalDate getFecha() { return fecha; }
        public StringProperty galponProperty() { return galpon; }
        public int getAves() { return aves.get(); }
        public int getProduccion() { return produccion.get(); }
        public StringProperty posturaProperty() { return postura; }
        public StringProperty pesoPromedioProperty() { return pesoPromedio; }
        public StringProperty clasificacionProperty() { return clasificacion; }
        public int getMortalidad() { return mortalidad.get(); }
        public StringProperty responsableProperty() { return responsable; }
    }
}
