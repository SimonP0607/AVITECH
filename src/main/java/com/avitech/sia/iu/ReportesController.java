package com.avitech.sia.iu;

import com.avitech.sia.App;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class ReportesController {

    // Sidebar / topbar
    @FXML private VBox sidebar;
    @FXML private Label lblUserInfo;
    @FXML private Label lblSystemStatus;
    @FXML private Label lblHeader;

    // Filtros
    @FXML private DatePicker dpDesde, dpHasta;
    @FXML private ComboBox<String> cbLote, cbArticulo, cbCategoria, cbResponsable;

    // Export/preview
    @FXML private RadioButton rbPdf, rbExcel;
    @FXML private CheckBox chkPreview;
    @FXML private Label lblInfoFiltros;
    @FXML private Button btnGenerar;

    // Recientes
    @FXML private TableView<?> tvRecientes;
    @FXML private TableColumn<?, ?> colRepNombre, colRepFecha, colRepTam, colRepAccion;

    // KPI/Stats
    @FXML private Label lblKpiMes, lblMasSolicitado, lblFormatos, lblTPromedio;
    @FXML private ProgressBar pbInv, pbProd, pbSan;

    /* ================== NAV ================== */
    @FXML private void goDashboard()  { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
    @FXML private void goSupplies()   { App.goTo("/fxml/supplies.fxml",       "SIA Avitech — Suministros"); }
    @FXML private void goHealth()     { App.goTo("/fxml/sanidad.fxml",        "SIA Avitech — Sanidad"); }
    @FXML private void goProduction() { App.goTo("/fxml/produccion.fxml",     "SIA Avitech — Producción"); }
    @FXML private void goReports()    { /* ya estamos aquí */ }
    @FXML private void goAlerts()     { /* pendiente */ }
    @FXML private void goAudit()      { /* pendiente */ }
    @FXML private void goParams()     { /* pendiente */ }
    @FXML private void goUsers()      { /* pendiente */ }
    @FXML private void goBackup()     { /* pendiente */ }

    @FXML
    private void onExit() {
        // Acción de cerrar sesión o volver al login
        App.goTo("/fxml/login.fxml", "Inicio de sesión — SIA Avitech");
    }

    /* ============ Acciones principales (stubs) ============ */
    @FXML private void onGenerar()      { /* TODO: construir payload con filtros y lanzar generación */ }
    @FXML private void onLimpiar() {
        dpDesde.setValue(null);
        dpHasta.setValue(null);
        cbLote.getSelectionModel().clearSelection();
        cbArticulo.getSelectionModel().clearSelection();
        cbCategoria.getSelectionModel().clearSelection();
        cbResponsable.getSelectionModel().clearSelection();
    }
    @FXML private void onExportarExcel() { /* TODO: exportar según selección */ }

    /* ============ Selección de tipo de reporte (tarjetas) ============ */
    @FXML private void selStockActual()           { /* setear tipo = STOCK */ }
    @FXML private void selRegistroArticulo()      { /* setear tipo = REG_ART */ }
    @FXML private void selRecibosInsumos()        { /* setear tipo = REC_INS */ }
    @FXML private void selConsumoAlimento()       { /* setear tipo = CON_ALI */ }
    @FXML private void selAplicacionesSanitarias(){ /* setear tipo = APP_SAN */ }
    @FXML private void selProduccionTam()         { /* setear tipo = PROD_TAM */ }
    @FXML private void selMortalidad()            { /* setear tipo = MORT */ }

    @FXML
    private void initialize() {
        // Tip utilitario: marca “Reportes” como activo en el sidebar si aplica una clase CSS
        lblHeader.setText("Administrador");
        lblSystemStatus.setText("Sistema Offline – MySQL Local");

        // Combos básicos (mock); luego se conectan a BD
        cbLote.getItems().addAll("Todos los lotes", "Lote 1", "Lote 2", "Lote 3");
        cbArticulo.getItems().addAll("Todos los artículos", "Maíz", "Trigo", "Alimento A");
        cbCategoria.getItems().addAll("Todas las categorías", "Insumos", "Alimento", "Vacunas");
        cbResponsable.getItems().addAll("Todos los responsables", "Juan", "Carla", "Admin");
        cbLote.getSelectionModel().selectFirst();
        cbArticulo.getSelectionModel().selectFirst();
        cbCategoria.getSelectionModel().selectFirst();
        cbResponsable.getSelectionModel().selectFirst();

        // KPI demo
        lblKpiMes.setText("47");
        lblMasSolicitado.setText("Producción");
        lblFormatos.setText("PDF / Excel");
        lblTPromedio.setText("2.1 min");
        pbInv.setProgress(0.45);
        pbProd.setProgress(0.30);
        pbSan.setProgress(0.25);
    }
}
