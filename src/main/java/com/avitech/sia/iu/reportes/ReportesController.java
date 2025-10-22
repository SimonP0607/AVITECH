package com.avitech.sia.iu.reportes;

import com.avitech.sia.App;
import com.avitech.sia.iu.BaseController;
import com.avitech.sia.security.UserRole.Module;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class ReportesController extends BaseController {

    @Override
    protected Module getRequiredModule() {
        return Module.REPORTES;
    }

    // Sidebar / topbar
    @FXML private VBox sidebar;
    @FXML private Label lblUserInfo;
    @FXML private Label lblSystemStatus;
    @FXML private Label lblHeader;
    @FXML private ToggleGroup sideGroup;

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

    /* ================== Navegación ================== */
    @FXML private void goDashboard()  { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
    @FXML private void goSupplies()   { App.goTo("/fxml/suministros.fxml", "SIA Avitech — Suministros"); }
    @FXML private void goHealth()     { App.goTo("/fxml/sanidad/sanidad.fxml", "SIA Avitech — Sanidad"); }
    @FXML private void goProduction() { App.goTo("/fxml/produccion/produccion.fxml", "SIA Avitech — Producción"); }
    @FXML private void goReports()    { App.goTo("/fxml/reportes.fxml", "SIA Avitech — Reportes"); }
    @FXML private void goAlerts()     { App.goTo("/fxml/alertas.fxml", "SIA Avitech — Alertas"); }
    @FXML private void goAudit()      { App.goTo("/fxml/auditoria.fxml", "SIA Avitech — Auditoría"); }
    @FXML private void goParams()     { App.goTo("/fxml/parametros.fxml", "SIA Avitech — Parámetros"); }
    @FXML private void goUsers()      { App.goTo("/fxml/usuarios/usuarios.fxml", "SIA Avitech — Usuarios"); }
    @FXML private void goBackup()     { App.goTo("/fxml/respaldos/respaldos.fxml", "SIA Avitech — Respaldos"); }
    @FXML private void onExit() {
        App.goTo("/fxml/login.fxml", "SIA Avitech — Inicio de sesión");
    }

    /**
     * Configura la visibilidad de los botones del menú según los permisos del usuario.
     */
    private void configureMenuPermissions() {
        if (sidebar == null || sessionManager == null) return;

        sidebar.getChildren().stream()
            .filter(node -> node instanceof ToggleButton)
            .map(node -> (ToggleButton) node)
            .forEach(button -> {
                Module module = getModuleFromButtonText(button.getText());
                if (module != null) {
                    boolean hasAccess = sessionManager.hasAccessTo(module);
                    button.setVisible(hasAccess);
                    button.setManaged(hasAccess);
                }
            });
    }

    private Module getModuleFromButtonText(String text) {
        return switch (text) {
            case "Tablero" -> Module.DASHBOARD;
            case "Suministros" -> Module.SUMINISTROS;
            case "Sanidad" -> Module.SANIDAD;
            case "Producción" -> Module.PRODUCCION;
            case "Reportes" -> Module.REPORTES;
            case "Alertas" -> Module.ALERTAS;
            case "Auditoría" -> Module.AUDITORIA;
            case "Parámetros" -> Module.PARAMETROS;
            case "Usuarios" -> Module.USUARIOS;
            case "Respaldos" -> Module.RESPALDOS;
            default -> null;
        };
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
    public void initialize() {
        // Llamar al initialize del padre (BaseController)
        super.initialize();

        // Configurar permisos del menú según el rol del usuario
        configureMenuPermissions();

        // Tip utilitario: marca "Reportes" como activo en el sidebar si aplica una clase CSS
        lblHeader.setText("Administrador");
        lblSystemStatus.setText("Sistema Offline – MySQL Local");

        // Combos básicos (mock); luego se conectan a BD
        cbLote.getItems().addAll("Todos los lotes", "Lote 1", "Lote 2", "Lote 3");
        cbArticulo.getItems().addAll("Todos los artículos", "Maíz", "Trigo", "Alimento A");
        cbCategoria.getItems().addAll("Todas las categorías", "Insumos", "Alimento", "Vacunas");
        cbResponsable.getItems().addAll("Todos los responsables", "Juan", "Carla", "Admin");

        // Seleccionar primeros items por defecto
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

    @Override
    protected void onScreenReady() {
        // Este método se llama cuando el ScreenManager está disponible

        // Ajustar anchos de columnas según la resolución de pantalla
        adjustColumnWidth(colRepNombre, 360);   // Base: 360px
        adjustColumnWidth(colRepFecha, 140);    // Base: 140px
        adjustColumnWidth(colRepTam, 120);      // Base: 120px
        adjustColumnWidth(colRepAccion, 160);   // Base: 160px

        // Mostrar información de la pantalla en la consola (útil para debugging)
        System.out.println("📱 Módulo Reportes ejecutándose en: " + getScreenInfo());

        // Si la pantalla es pequeña, podríamos simplificar la UI
        if (shouldUseSimplifiedUI()) {
            System.out.println("⚠️ Pantalla pequeña detectada - UI simplificada activada");
            // Aquí podrías ocultar columnas secundarias o reducir padding
        }
    }
}
