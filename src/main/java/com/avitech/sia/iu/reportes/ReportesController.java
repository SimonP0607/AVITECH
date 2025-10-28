package com.avitech.sia.iu.reportes;

import com.avitech.sia.App;
import com.avitech.sia.iu.BaseController;
import com.avitech.sia.security.UserRole.Module;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    @FXML private TableView<ReporteReciente> tvRecientes;
    @FXML private TableColumn<ReporteReciente, String> colRepNombre, colRepFecha, colRepTam, colRepAccion;

    // KPI/Stats
    @FXML private Label lblKpiMes, lblMasSolicitado, lblFormatos, lblTPromedio;
    @FXML private ProgressBar pbInv, pbProd, pbSan;

    // Servicio de reportes
    private final ReporteService reporteService = new ReporteService();

    // Tipo de reporte seleccionado
    private TipoReporte tipoReporteSeleccionado = TipoReporte.STOCK_ACTUAL;

    // Lista de reportes generados
    private final ObservableList<ReporteReciente> reportesRecientes = FXCollections.observableArrayList();

    /* ================== Navegación ================== */
    @FXML private void goDashboard()  { App.goTo(sessionManager.getDashboardPath(), sessionManager.getDashboardTitle()); }
    @FXML private void goSupplies()   { App.goTo("/fxml/suministros/suministros.fxml", "SIA Avitech — Suministros"); }
    @FXML private void goHealth()     { App.goTo("/fxml/sanidad/sanidad.fxml", "SIA Avitech — Sanidad"); }
    @FXML private void goProduction() { App.goTo("/fxml/produccion/produccion.fxml", "SIA Avitech — Producción"); }
    @FXML private void goReports()    { App.goTo("/fxml/reportes/reportes.fxml", "SIA Avitech — Reportes"); }
    @FXML private void goAlerts()     { App.goTo("/fxml/alertas/alertas.fxml", "SIA Avitech — Alertas"); }
    @FXML private void goAudit()      { App.goTo("/fxml/auditoria/auditoria.fxml", "SIA Avitech — Auditoría"); }
    @FXML private void goParams()     { App.goTo("/fxml/parametros.fxml", "SIA Avitech — Parámetros"); }
    @FXML private void goUsers()      { App.goTo("/fxml/usuarios/usuarios.fxml", "SIA Avitech — Usuarios"); }
    @FXML private void goBackup()     { App.goTo("/fxml/respaldos/respaldos.fxml", "SIA Avitech — Respaldos"); }
    @FXML private void onExit()       { logout(); }

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
    @FXML
    @Override
    public void initialize() {
        super.initialize();

        lblSystemStatus.setText("Sistema Offline – MySQL Local");
        lblHeader.setText(sessionManager != null ? sessionManager.getCurrentUser().username() : "Administrador");

        // Configurar permisos
        configureMenuPermissions();

        // Inicializar filtros
        inicializarFiltros();

        // Configurar tabla de recientes
        configurarTablaRecientes();

        // Configurar KPIs
        actualizarKPIs();

        // Seleccionar PDF por defecto
        rbPdf.setSelected(true);
    }

    private void inicializarFiltros() {
        // Combos con datos de ejemplo (luego se llenarán desde BD)
        cbLote.setItems(FXCollections.observableArrayList(
            "Todos los lotes", "Lote A-2024", "Lote B-2024", "Lote C-2024"
        ));
        cbLote.getSelectionModel().selectFirst();

        cbArticulo.setItems(FXCollections.observableArrayList(
            "Todos los artículos", "Alimento Balanceado", "Vacunas", "Medicamentos"
        ));
        cbArticulo.getSelectionModel().selectFirst();

        cbCategoria.setItems(FXCollections.observableArrayList(
            "Todas las categorías", "Alimentos", "Medicamentos", "Limpieza", "Vacunas"
        ));
        cbCategoria.getSelectionModel().selectFirst();

        cbResponsable.setItems(FXCollections.observableArrayList(
            "Todos los responsables", "Juan Pérez", "María López", "Carlos Ruiz"
        ));
        cbResponsable.getSelectionModel().selectFirst();

        // Fechas por defecto: último mes
        dpHasta.setValue(LocalDate.now());
        dpDesde.setValue(LocalDate.now().minusMonths(1));
    }

    private void configurarTablaRecientes() {
        colRepNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colRepFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colRepTam.setCellValueFactory(new PropertyValueFactory<>("tamano"));
        colRepAccion.setCellValueFactory(param -> new SimpleStringProperty("Abrir"));

        // Botón de acción
        colRepAccion.setCellFactory(col -> new TableCell<>() {
            private final Hyperlink link = new Hyperlink("Abrir");
            {
                link.setOnAction(e -> {
                    ReporteReciente reporte = getTableView().getItems().get(getIndex());
                    abrirReporte(reporte.archivo);
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : link);
                setText(null);
            }
        });

        tvRecientes.setItems(reportesRecientes);
    }

    private void actualizarKPIs() {
        lblKpiMes.setText(String.valueOf(reportesRecientes.size()));
        lblMasSolicitado.setText(tipoReporteSeleccionado.getTitulo());
        lblFormatos.setText("PDF / Excel");
        lblTPromedio.setText("< 5 seg");

        // Progress bars (placeholder)
        pbInv.setProgress(0.45);
        pbProd.setProgress(0.30);
        pbSan.setProgress(0.25);
    }

    /* ============ Acciones principales ============ */

    @FXML
    private void onGenerar() {
        generarReporte();
    }

    @FXML
    private void onLimpiar() {
        dpDesde.setValue(LocalDate.now().minusMonths(1));
        dpHasta.setValue(LocalDate.now());
        cbLote.getSelectionModel().selectFirst();
        cbArticulo.getSelectionModel().selectFirst();
        cbCategoria.getSelectionModel().selectFirst();
        cbResponsable.getSelectionModel().selectFirst();
    }

    @FXML
    private void onExportarExcel() {
        rbExcel.setSelected(true);
        generarReporte();
    }

    /* ============ Selección de tipo de reporte ============ */

    @FXML private void selStockActual() {
        tipoReporteSeleccionado = TipoReporte.STOCK_ACTUAL;
        mostrarMensaje("Reporte seleccionado: " + tipoReporteSeleccionado.getTitulo());
    }

    @FXML private void selRegistroArticulo() {
        tipoReporteSeleccionado = TipoReporte.REGISTRO_ARTICULO;
        mostrarMensaje("Reporte seleccionado: " + tipoReporteSeleccionado.getTitulo());
            case "Sanidad" -> Module.SANIDAD;

    @FXML private void selRecibosInsumos() {
        tipoReporteSeleccionado = TipoReporte.RECIBOS_INSUMOS;
        mostrarMensaje("Reporte seleccionado: " + tipoReporteSeleccionado.getTitulo());
    }

    @FXML private void selConsumoAlimento() {
        tipoReporteSeleccionado = TipoReporte.CONSUMO_ALIMENTO;
        mostrarMensaje("Reporte seleccionado: " + tipoReporteSeleccionado.getTitulo());
    }

    @FXML private void selAplicacionesSanitarias() {
        tipoReporteSeleccionado = TipoReporte.APLICACIONES_SANITARIAS;
        mostrarMensaje("Reporte seleccionado: " + tipoReporteSeleccionado.getTitulo());
    }

    @FXML private void selProduccionTam() {
        tipoReporteSeleccionado = TipoReporte.PRODUCCION_LOTE;
        mostrarMensaje("Reporte seleccionado: " + tipoReporteSeleccionado.getTitulo());
    }

    @FXML private void selMortalidad() {
        tipoReporteSeleccionado = TipoReporte.MORTALIDAD;
        mostrarMensaje("Reporte seleccionado: " + tipoReporteSeleccionado.getTitulo());
    }

    /* ============ Generación de reportes ============ */

    private void generarReporte() {
        // Crear configuración del reporte
        FormatoReporte formato = rbPdf.isSelected() ? FormatoReporte.PDF : FormatoReporte.EXCEL;
        ReporteConfig config = new ReporteConfig(tipoReporteSeleccionado, formato);

        // Aplicar filtros
        config.setFechaDesde(dpDesde.getValue());
        config.setFechaHasta(dpHasta.getValue());
        config.setVistaPrevia(chkPreview.isSelected());

        // Agregar filtros específicos
        if (cbLote.getValue() != null && !cbLote.getValue().equals("Todos los lotes")) {
            config.addFiltro("Lote", cbLote.getValue());
        }
        if (cbArticulo.getValue() != null && !cbArticulo.getValue().equals("Todos los artículos")) {
            config.addFiltro("Artículo", cbArticulo.getValue());
        }
        if (cbCategoria.getValue() != null && !cbCategoria.getValue().equals("Todas las categorías")) {
            config.addFiltro("Categoría", cbCategoria.getValue());
        }
        if (cbResponsable.getValue() != null && !cbResponsable.getValue().equals("Todos los responsables")) {
            config.addFiltro("Responsable", cbResponsable.getValue());
        }

        // Generar reporte de forma asíncrona
        Task<File> task = reporteService.generarReporteAsync(config);

        // Diálogo de progreso
        Alert progressAlert = new Alert(Alert.AlertType.INFORMATION);
        progressAlert.setTitle("Generando Reporte");
        progressAlert.setHeaderText("Por favor espere...");
        progressAlert.setContentText("Generando reporte " + formato.getNombre());

        // Mostrar progreso
        task.messageProperty().addListener((obs, oldMsg, newMsg) ->
            Platform.runLater(() -> progressAlert.setContentText(newMsg))
        );

        // Al completar
        task.setOnSucceeded(e -> {
            progressAlert.close();
            File archivo = task.getValue();

            // Agregar a la tabla de recientes
            String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            String tamano = String.format("%.1f KB", archivo.length() / 1024.0);
            reportesRecientes.add(0, new ReporteReciente(
                tipoReporteSeleccionado.getTitulo() + " - " + formato.getNombre(),
                fecha,
                tamano,
                archivo
            ));

            actualizarKPIs();

            // Mostrar éxito
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Reporte Generado");
            success.setHeaderText("¡Éxito!");
            success.setContentText("Reporte generado correctamente:\n" + archivo.getAbsolutePath());

            ButtonType btnAbrir = new ButtonType("Abrir Reporte");
            ButtonType btnCerrar = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
            success.getButtonTypes().setAll(btnAbrir, btnCerrar);

            success.showAndWait().ifPresent(response -> {
                if (response == btnAbrir) {
                    abrirReporte(archivo);
                }
            });
        });

        // Al fallar
        task.setOnFailed(e -> {
            progressAlert.close();
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("Error al generar reporte");
            error.setContentText(task.getException().getMessage());
            error.showAndWait();
        });

        // Ejecutar tarea
        new Thread(task).start();
        progressAlert.show();
    }

    private void abrirReporte(File archivo) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(archivo);
            } else {
                mostrarMensaje("No se puede abrir el archivo automáticamente.\nUbicación: " + archivo.getAbsolutePath());
            }
        } catch (IOException ex) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("No se pudo abrir el archivo");
            error.setContentText(ex.getMessage());
            error.showAndWait();
        }
    }

    private void mostrarMensaje(String mensaje) {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Información");
        info.setHeaderText(null);
        info.setContentText(mensaje);
        info.show();
    }

    /* ============ Clase auxiliar para tabla de recientes ============ */

    public static class ReporteReciente {
        private final String nombre;
        private final String fecha;
        private final String tamano;
        private final File archivo;

        public ReporteReciente(String nombre, String fecha, String tamano, File archivo) {
            this.nombre = nombre;
            this.fecha = fecha;
            this.tamano = tamano;
            this.archivo = archivo;
        }

        public String getNombre() { return nombre; }
        public String getFecha() { return fecha; }
        public String getTamano() { return tamano; }
        public File getArchivo() { return archivo; }
    }
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
