package com.avitech.sia.iu;

import com.avitech.sia.App;
import com.avitech.sia.db.SanidadDAO;
import com.avitech.sia.iu.ModalUtil;
import com.avitech.sia.iu.sanidad.RegAplicacionController;
import com.avitech.sia.iu.sanidad.RegEventoController;
import com.avitech.sia.iu.sanidad.dto.AplicacionDTO;
import com.avitech.sia.iu.sanidad.dto.EventoDTO;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SanidadController {

    @FXML private Label lblSystemStatus, lblHeader, lblUserInfo;
    @FXML private Label kpiAplicaciones, kpiAplicacionesDelta, kpiMortalidad, kpiCasosActivos, kpiMedStock, kpiMedBajos;
    @FXML private TableView<PlanRow> tblPlanes;
    @FXML private TableColumn<PlanRow, String> colPlan, colDesc, colEdad, colEstadoPlan;
    @FXML private TableView<MedRow> tblMedicamentos;
    @FXML private TableColumn<MedRow, String> colMed, colStock, colInv;
    @FXML private TableColumn<MedRow, Double> colBar;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbGalpon, cbMedicamento;
    @FXML private DatePicker dpDesde, dpHasta;
    @FXML private Button btnRegistrarAplicacion, btnRegistrarEvento;

    private final ObservableList<PlanRow> planesData = FXCollections.observableArrayList();
    private final ObservableList<MedRow> medicamentosData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        lblHeader.setText("Administrador");
        lblUserInfo.setText("Administrador");
        lblSystemStatus.setText("Sistema Online – MySQL Local");
        setupTableColumns();
        loadFilters();
        refreshData();
    }

    private void setupTableColumns() {
        colPlan.setCellValueFactory(d -> d.getValue().nombreEnfermedadProperty());
        colDesc.setCellValueFactory(d -> d.getValue().descripcionProperty());
        colEdad.setCellValueFactory(d -> d.getValue().medicamentoProperty()); // Re-propósito de la columna
        colEstadoPlan.setCellValueFactory(d -> d.getValue().estadoProperty());
        tblPlanes.setItems(planesData);

        colMed.setCellValueFactory(d -> d.getValue().nombreProperty());
        colStock.setCellValueFactory(d -> d.getValue().stockTextoProperty());
        colInv.setCellValueFactory(d -> d.getValue().inventarioProperty());
        colBar.setCellValueFactory(d -> d.getValue().nivelProperty().asObject());
        colBar.setCellFactory(ProgressBarTableCell.forTableColumn());
        tblMedicamentos.setItems(medicamentosData);
    }

    private void loadFilters() {
        try {
            List<String> lotes = new ArrayList<>();
            lotes.add("Todos");
            lotes.addAll(SanidadDAO.getLotes());
            cbGalpon.setItems(FXCollections.observableArrayList(lotes));
            cbGalpon.getSelectionModel().selectFirst();

            List<String> medicamentos = new ArrayList<>();
            medicamentos.add("Todos");
            medicamentos.addAll(SanidadDAO.getMedicamentosNombres());
            cbMedicamento.setItems(FXCollections.observableArrayList(medicamentos));
            cbMedicamento.getSelectionModel().selectFirst();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudieron cargar los filtros: " + e.getMessage()).showAndWait();
        }
        dpDesde.setValue(LocalDate.now().minusDays(30));
        dpHasta.setValue(LocalDate.now());
    }

    private void refreshData() {
        try {
            planesData.setAll(SanidadDAO.getPlanesSanitarios());
            medicamentosData.setAll(SanidadDAO.getMedicamentosStock());
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudieron cargar los datos de sanidad: " + e.getMessage()).showAndWait();
        }
        refreshKpis();
    }

    private void refreshKpis() {
        kpiAplicaciones.setText(String.valueOf(planesData.size()));
        kpiMortalidad.setText("0"); // TODO: Necesita una consulta específica
        kpiCasosActivos.setText("0"); // TODO: Necesita una consulta específica
        kpiMedStock.setText(String.valueOf(medicamentosData.size()));
        long bajos = medicamentosData.stream().filter(m -> m.getNivel() < 0.25).count();
        kpiMedBajos.setText(bajos + " bajo(s)");
    }

    @FXML
    private void onRegistrarAplicacion() {
        RegAplicacionController ctrl = ModalUtil.openModal(btnRegistrarAplicacion, "/fxml/sanidad/modal_reg_aplicacion.fxml", "Registrar Aplicación de Medicamento");
        if (ctrl != null && ctrl.getResult() != null) {
            try {
                SanidadDAO.guardarAplicacion(ctrl.getResult());
                new Alert(Alert.AlertType.INFORMATION, "Aplicación registrada correctamente.").showAndWait();
                refreshData();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "No se pudo guardar la aplicación: " + e.getMessage()).showAndWait();
            }
        }
    }

    @FXML
    private void onRegistrarEvento() {
        RegEventoController ctrl = ModalUtil.openModal(btnRegistrarEvento, "/fxml/sanidad/modal_reg_evento.fxml", "Registrar Evento Sanitario");
        if (ctrl != null && ctrl.getResult() != null) {
            EventoDTO dto = ctrl.getResult();
            try {
                SanidadDAO.guardarEvento(dto);
                new Alert(Alert.AlertType.INFORMATION, "Evento registrado correctamente.").showAndWait();
                refreshData();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "No se pudo guardar el evento: " + e.getMessage()).showAndWait();
            }
        }
    }

    @FXML private void onBuscar() { /* TODO */ }
    @FXML private void onLimpiar() { /* TODO */ }
    @FXML private void goDashboard() { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
    @FXML private void goSupplies() { App.goTo("/fxml/suministros.fxml", "SIA Avitech — Suministros"); }
    @FXML private void goHealth() { /* ya aquí */ }
    @FXML private void goProduction() { App.goTo("/fxml/produccion.fxml", "SIA Avitech — Producción"); }
    @FXML private void goReports() { App.goTo("/fxml/reportes.fxml", "SIA Avitech — Reportes"); }
    @FXML private void goAlerts() { App.goTo("/fxml/alertas.fxml", "SIA Avitech — Alertas"); }
    @FXML private void goAudit() { App.goTo("/fxml/auditoria.fxml", "SIA Avitech — Auditoría"); }
    @FXML private void goParams() { App.goTo("/fxml/parametros.fxml", "SIA Avitech — Parámetros"); }
    @FXML private void goUsers() { App.goTo("/fxml/usuarios.fxml", "SIA Avitech — Usuarios"); }
    @FXML private void goBackup() { App.goTo("/fxml/respaldos.fxml", "SIA Avitech — Respaldos"); }
    @FXML private void onExit() { App.goTo("/fxml/login.fxml", "SIA Avitech — LOGIN"); }


    public static class PlanRow {
        private final StringProperty nombreEnfermedad = new SimpleStringProperty();
        private final StringProperty descripcion = new SimpleStringProperty();
        private final StringProperty medicamento = new SimpleStringProperty();
        private final StringProperty estado = new SimpleStringProperty();
        public PlanRow(String p, String d, String e, String s) { nombreEnfermedad.set(p); descripcion.set(d); medicamento.set(e); estado.set(s); }
        public StringProperty nombreEnfermedadProperty() { return nombreEnfermedad; }
        public StringProperty descripcionProperty() { return descripcion; }
        public StringProperty medicamentoProperty() { return medicamento; }
        public StringProperty estadoProperty() { return estado; }
    }

    public static class MedRow {
        private final StringProperty nombre = new SimpleStringProperty();
        private final IntegerProperty stock = new SimpleIntegerProperty();
        private final DoubleProperty nivel = new SimpleDoubleProperty();
        private final StringProperty inventario = new SimpleStringProperty();
        private final StringProperty presentacion = new SimpleStringProperty();
        public MedRow(String n, int s, double pct, String inv) { nombre.set(n); stock.set(s); nivel.set(pct); inventario.set(inv); this.presentacion.set("unidades"); }
        public StringProperty nombreProperty() { return nombre; }
        public StringProperty stockTextoProperty() { return new SimpleStringProperty(stock.get() + " " + presentacion.get()); }
        public DoubleProperty nivelProperty() { return nivel; }
        public double getNivel() { return nivel.get(); }
        public StringProperty inventarioProperty() { return inventario; }
    }
}
