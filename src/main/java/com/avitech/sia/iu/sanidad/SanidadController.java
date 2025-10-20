package com.avitech.sia.iu.sanidad;

import com.avitech.sia.App;
import com.avitech.sia.iu.ModalUtil;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;
import java.time.LocalDate;


/** Control de Sanidad — preparado para BD (sin datos mock). */
public class SanidadController implements UsesSanidadDataSource {

    /* ======= Top / sidebar ======= */
    @FXML private Label lblSystemStatus, lblHeader, lblUserInfo;

    /* ======= KPIs ======= */
    @FXML private Label kpiAplicaciones, kpiAplicacionesDelta, kpiMortalidad,
            kpiCasosActivos, kpiMedStock, kpiMedBajos;

    /* ======= Planes sanitarios ======= */
    @FXML private TableView<PlanRow> tblPlanes;
    @FXML private TableColumn<PlanRow, String> colPlan, colDesc, colEdad, colEstadoPlan;

    /* ======= Medicamentos ======= */
    @FXML private TableView<MedRow> tblMedicamentos;
    @FXML private TableColumn<MedRow, String>  colMed, colStock, colInv;
    @FXML private TableColumn<MedRow, Double> colBar;

    /* ======= Filtros ======= */
    @FXML private TextField  txtBuscar;
    @FXML private ComboBox<String> cbGalpon, cbMedicamento;
    @FXML private DatePicker dpDesde, dpHasta;
    @FXML private Button btnRegistrarAplicacion, btnRegistrarEvento;

    @FXML
    private void initialize() {
        /* Cabecera */
        lblHeader.setText("Administrador");
        lblUserInfo.setText("Administrador");
        lblSystemStatus.setText("Sistema Offline – MySQL Local");

        /* KPIs vacíos (se llenarán desde BD) */
        kpiAplicaciones.setText("—");
        kpiAplicacionesDelta.setText("—");
        kpiMortalidad.setText("—");
        kpiCasosActivos.setText("—");
        kpiMedStock.setText("—");
        kpiMedBajos.setText("—");

        /* Config tablas (sin setear items mock) */
        // Planes
        colPlan.setCellValueFactory(d -> d.getValue().planProperty());
        colDesc.setCellValueFactory(d -> d.getValue().descProperty());
        colEdad.setCellValueFactory(d -> d.getValue().edadProperty());
        colEstadoPlan.setCellValueFactory(d -> d.getValue().estadoProperty());
        tblPlanes.setPlaceholder(new Label("Sin planes para mostrar"));
        tblPlanes.setItems(FXCollections.observableArrayList()); // vacío

        // Medicamentos
        colMed.setCellValueFactory(d -> d.getValue().nombreProperty());
        colStock.setCellValueFactory(d -> d.getValue().stockTextoProperty());
        colInv.setCellValueFactory(d -> d.getValue().inventarioProperty());
        colBar.setCellValueFactory(d -> d.getValue().nivelProperty().asObject());
        colBar.setCellFactory(ProgressBarTableCell.forTableColumn());
        tblMedicamentos.setPlaceholder(new Label("Sin medicamentos para mostrar"));
        tblMedicamentos.setItems(FXCollections.observableArrayList()); // vacío

        /* Filtros: opciones y fechas por defecto */
        cbGalpon.setItems(FXCollections.observableArrayList("Todos")); cbGalpon.getSelectionModel().selectFirst();
        cbMedicamento.setItems(FXCollections.observableArrayList("Todos")); cbMedicamento.getSelectionModel().selectFirst();
        dpDesde.setValue(LocalDate.now().minusDays(30));
        dpHasta.setValue(LocalDate.now());

        /* TODO (cuando haya BD):
           - Traer KPIs: sanidadDs().getKpis()
           - Cargar combos: sanidadDs().listarGalpones(), listarMedicamentos()
           - Cargar tablas iniciales si aplica
        */
    }

    /* ================= Acciones (modales) ================= */

    @FXML
    private void onRegistrarAplicacion() {
        AplicacionController ctrl = ModalUtil.openModal(
                btnRegistrarAplicacion,
                "/fxml/sanidad/sanidad_aplicacion.fxml",
                "Registrar Aplicación de Medicamento"
        );
        if (ctrl == null) return;

        // Catálogos
        ctrl.setLotes(sanidadDs().findLotes());
        ctrl.setMedicamentos(sanidadDs().findMedicamentos());
        ctrl.setVias(sanidadDs().findViasAplicacion());
        ctrl.setResponsables(sanidadDs().findResponsables());

        if (ctrl.getResult() != null) {
            try {
                long id = sanidadDs().saveAplicacion(ctrl.getResult());
                System.out.println("Aplicación guardada con id=" + id);
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR,
                        "Error guardando aplicación: " + ex.getMessage()).showAndWait();
            }
        }
    }




    @FXML
    private void onRegistrarEvento() {
        EventoController ctrl = ModalUtil.openModal(
                btnRegistrarEvento,
                "/fxml/sanidad/sanidad_evento.fxml",
                "Registrar Evento Sanitario"
        );
        if (ctrl == null) return;

        // Catálogos
        ctrl.setLotes(sanidadDs().findLotes());
        ctrl.setTiposEvento(sanidadDs().findTiposEvento());
        ctrl.setResponsables(sanidadDs().findResponsables());

        if (ctrl.getResult() != null) {
            try {
                long id = sanidadDs().saveEvento(ctrl.getResult());
                System.out.println("Evento guardado con id=" + id);
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR,
                        "Error guardando evento: " + ex.getMessage()).showAndWait();
            }
        }
    }


    /* ================= Búsqueda ================= */

    @FXML private void onBuscar() {
        // TODO: consultar sanidadDs().buscar(…) con los filtros y llenar tablas
        System.out.printf("Buscar: q=%s, galpón=%s, med=%s, desde=%s, hasta=%s%n",
                txtBuscar.getText(), cbGalpon.getValue(), cbMedicamento.getValue(),
                dpDesde.getValue(), dpHasta.getValue());
    }

    @FXML private void onLimpiar() {
        txtBuscar.clear();
        cbGalpon.getSelectionModel().selectFirst();
        cbMedicamento.getSelectionModel().selectFirst();
        dpDesde.setValue(LocalDate.now().minusDays(30));
        dpHasta.setValue(LocalDate.now());
        // TODO: recargar resultados por defecto
    }

    /* ================= Navegación ================= */

    @FXML private void goDashboard()  { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
    @FXML private void goSupplies()   { App.goTo("/fxml/suministros/suministros.fxml", "SIA Avitech — Suministros"); }
    @FXML private void goHealth()     { /* ya aquí */ }
    @FXML private void goProduction() { App.goTo("/fxml/produccion/produccion.fxml", "SIA Avitech — Producción"); }
    @FXML private void goReports()    { App.goTo("/fxml/reportes.fxml",        "SIA Avitech — Reportes"); }
    @FXML private void goAlerts()     { App.goTo("/fxml/alertas.fxml",         "SIA Avitech — Alertas"); }
    @FXML private void goAudit()      { App.goTo("/fxml/auditoria.fxml",       "SIA Avitech — Auditoría"); }
    @FXML private void goParams()     { App.goTo("/fxml/parametros.fxml",      "SIA Avitech — Parámetros"); }
    @FXML private void goUsers()      { App.goTo("/fxml/usuarios/usuarios.fxml",        "SIA Avitech — Usuarios"); }
    @FXML private void goBackup()     { App.goTo("/fxml/respaldos.fxml",       "SIA Avitech — Respaldos"); }
    @FXML private void onExit()       { App.goTo("/fxml/login.fxml",           "SIA Avitech — LOGIN"); }

    /* ================= Modelos fila (TableView) ================= */

    public static class PlanRow {
        private final StringProperty plan = new SimpleStringProperty();
        private final StringProperty desc = new SimpleStringProperty();
        private final StringProperty edad = new SimpleStringProperty();
        private final StringProperty estado = new SimpleStringProperty();
        public PlanRow() { }
        public PlanRow(String p, String d, String e, String s) { plan.set(p); desc.set(d); edad.set(e); estado.set(s); }
        public StringProperty planProperty()   { return plan; }
        public StringProperty descProperty()   { return desc; }
        public StringProperty edadProperty()   { return edad; }
        public StringProperty estadoProperty() { return estado; }
    }

    public static class MedRow {
        private final StringProperty nombre = new SimpleStringProperty();
        private final IntegerProperty stock = new SimpleIntegerProperty();
        private final DoubleProperty  nivel = new SimpleDoubleProperty(); // 0..1
        private final StringProperty inventario = new SimpleStringProperty();
        public MedRow() { }
        public MedRow(String n, int s, double pct, String inv) { nombre.set(n); stock.set(s); nivel.set(pct); inventario.set(inv); }
        public StringProperty nombreProperty()       { return nombre; }
        public StringProperty stockTextoProperty()   { return new SimpleStringProperty(stock.get() + " frascos"); }
        public DoubleProperty  nivelProperty()       { return nivel; }
        public StringProperty inventarioProperty()   { return inventario; }
    }
}
