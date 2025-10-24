package com.avitech.sia.iu;

import com.avitech.sia.App;
import com.avitech.sia.db.ProduccionDAO; // Importar para cbLote
import com.avitech.sia.db.SuministrosDAO; // Importar para cbArticulo
import com.avitech.sia.db.MedicamentosDAO; // Importar para cbArticulo
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

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

    // Enum para tipos de reporte
    private enum ReportType {
        STOCK_ACTUAL,
        REGISTRO_ARTICULO,
        RECIBOS_INSUMOS,
        CONSUMO_ALIMENTO,
        APLICACIONES_SANITARIAS,
        PRODUCCION_TAM,
        MORTALIDAD
    }

    private ReportType selectedReportType;

    @FXML
    private void initialize() {
        lblHeader.setText("Administrador");
        lblSystemStatus.setText("Sistema Offline – MySQL Local");

        // Deshabilitar botón de generar reporte inicialmente
        btnGenerar.setDisable(true);

        // Cargar datos en los ComboBoxes de filtro
        populateFilterComboBoxes();

        // KPI demo (mantener por ahora, se actualizará después)
        lblKpiMes.setText("47");
        lblMasSolicitado.setText("Producción");
        lblFormatos.setText("PDF / Excel");
        lblTPromedio.setText("2.1 min");
        pbInv.setProgress(0.45);
        pbProd.setProgress(0.30);
        pbSan.setProgress(0.25);
    }

    private void populateFilterComboBoxes() {
        try {
            // cbLote (asumiendo que Lote corresponde a Galpones para este contexto)
            List<String> galpones = new ArrayList<>();
            galpones.add("Todos los lotes");
            galpones.addAll(ProduccionDAO.getGalponesNombres());
            cbLote.setItems(FXCollections.observableArrayList(galpones));
            cbLote.getSelectionModel().selectFirst();

            // cbArticulo (combinando suministros y medicamentos)
            List<String> articulos = new ArrayList<>();
            articulos.add("Todos los artículos");
            articulos.addAll(SuministrosDAO.getProductos());
            MedicamentosDAO.getInventario().forEach(item -> articulos.add(item.nombre));
            cbArticulo.setItems(FXCollections.observableArrayList(articulos));
            cbArticulo.getSelectionModel().selectFirst();

            // cbCategoria (mock por ahora, podría ser dinámico)
            cbCategoria.getItems().addAll("Todas las categorías", "Insumos", "Alimento", "Vacunas", "Medicamentos");
            cbCategoria.getSelectionModel().selectFirst();

            // cbResponsable (mock por ahora, se necesitaría un UsuarioDAO.getResponsables() real)
            cbResponsable.getItems().addAll("Todos los responsables", "Juan", "Carla", "Admin");
            cbResponsable.getSelectionModel().selectFirst();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al cargar filtros: " + e.getMessage()).showAndWait();
        }
    }

    /* ================== Navegación ================== */
    @FXML private void goDashboard()  { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
    @FXML private void goSupplies()   { App.goTo("/fxml/suministros.fxml", "SIA Avitech — Suministros"); }
    @FXML private void goHealth()     { App.goTo("/fxml/sanidad.fxml", "SIA Avitech — Sanidad"); }
    @FXML private void goProduction() { App.goTo("/fxml/produccion.fxml", "SIA Avitech — Producción"); }
    @FXML private void goReports()    { App.goTo("/fxml/reportes.fxml", "SIA Avitech — Reportes"); }
    @FXML private void goAlerts()     { App.goTo("/fxml/alertas.fxml", "SIA Avitech — Alertas"); }
    @FXML private void goAudit()      { App.goTo("/fxml/auditoria.fxml", "SIA Avitech — Auditoría"); }
    @FXML private void goParams()     { App.goTo("/fxml/parametros.fxml", "SIA Avitech — Parámetros"); }
    @FXML private void goUsers()      { App.goTo("/fxml/usuarios.fxml", "SIA Avitech — Usuarios"); }
    @FXML private void goBackup()     { App.goTo("/fxml/respaldos.fxml", "SIA Avitech — Respaldos"); }
    @FXML private void onExit() {
        App.goTo("/fxml/login.fxml", "SIA Avitech — Inicio de sesión");
    }


    /* ============ Acciones principales ============ */
    @FXML
    private void onGenerar() {
        if (selectedReportType == null) {
            new Alert(Alert.AlertType.WARNING, "Por favor, selecciona un tipo de reporte antes de generar.").showAndWait();
            return;
        }
        // TODO: Aquí se construiría el payload con los filtros y se llamaría a un servicio/DAO para generar el reporte
        System.out.println("Generando reporte: " + selectedReportType + " con filtros...");
        // Ejemplo de cómo obtener los filtros:
        // LocalDate desde = dpDesde.getValue();
        // LocalDate hasta = dpHasta.getValue();
        // String lote = cbLote.getSelectionModel().getSelectedItem();
        // ... y así sucesivamente

        new Alert(Alert.AlertType.INFORMATION, "Reporte de " + selectedReportType + " generado (funcionalidad en desarrollo).").showAndWait();
    }

    @FXML
    private void onLimpiar() {
        dpDesde.setValue(null);
        dpHasta.setValue(null);
        cbLote.getSelectionModel().selectFirst();
        cbArticulo.getSelectionModel().selectFirst();
        cbCategoria.getSelectionModel().selectFirst();
        cbResponsable.getSelectionModel().selectFirst();
        selectedReportType = null; // Limpiar el tipo de reporte seleccionado
        btnGenerar.setDisable(true); // Deshabilitar el botón de generar
    }

    @FXML
    private void onExportarExcel() { /* TODO: exportar según selección */
        new Alert(Alert.AlertType.INFORMATION, "Exportar a Excel (funcionalidad en desarrollo).").showAndWait();
    }

    /* ============ Selección de tipo de reporte (tarjetas) ============ */
    private void selectReport(ReportType type) {
        this.selectedReportType = type;
        btnGenerar.setDisable(false);
        // Opcional: Resaltar la tarjeta seleccionada o actualizar un Label con el tipo de reporte
        System.out.println("Tipo de reporte seleccionado: " + type);
    }

    @FXML private void selStockActual()           { selectReport(ReportType.STOCK_ACTUAL); }
    @FXML private void selRegistroArticulo()      { selectReport(ReportType.REGISTRO_ARTICULO); }
    @FXML private void selRecibosInsumos()        { selectReport(ReportType.RECIBOS_INSUMOS); }
    @FXML private void selConsumoAlimento()       { selectReport(ReportType.CONSUMO_ALIMENTO); }
    @FXML private void selAplicacionesSanitarias(){ selectReport(ReportType.APLICACIONES_SANITARIAS); }
    @FXML private void selProduccionTam()         { selectReport(ReportType.PRODUCCION_TAM); }
    @FXML private void selMortalidad()            { selectReport(ReportType.MORTALIDAD); }
}
