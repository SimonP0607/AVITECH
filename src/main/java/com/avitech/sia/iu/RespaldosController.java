package com.avitech.sia.iu;

import com.avitech.sia.App;
import com.avitech.sia.security.UserRole.Module;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class RespaldosController extends BaseController {

    @Override
    protected Module getRequiredModule() {
        return Module.RESPALDOS;
    }

    // topbar
    @FXML private Label lblSystemStatus;
    @FXML private Label lblHeader;
    @FXML private Label lblUserInfo;
    @FXML private VBox sidebar;
    @FXML private ToggleGroup sideGroup;

    // banner auto
    @FXML private Label lblAutoUltimo, lblAutoProximo, lblAutoTasa, lblAutoEstado;

    // KPIs
    @FXML private Label kpiTotal, kpiOk, kpiErr, kpiSpace;

    // tabla
    @FXML private TableView<BackupItem> tblBackups;
    @FXML private TableColumn<BackupItem, String> colArchivo, colFecha, colTipo, colTam, colEstado, colAcciones;

    private final ObservableList<BackupItem> master = FXCollections.observableArrayList();

    @FXML
    @Override
    public void initialize() {
        super.initialize();
        lblSystemStatus.setText("Sistema Offline – MySQL Local");
        lblHeader.setText("Administrador");
        lblUserInfo.setText("Administrador");

        // columnas
        colArchivo.setCellValueFactory(new PropertyValueFactory<>("archivo"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colTam.setCellValueFactory(new PropertyValueFactory<>("tamano"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colAcciones.setCellValueFactory(param -> new SimpleStringProperty("acciones"));
        colAcciones.setCellFactory(c -> new TableCell<>() {
            private final Hyperlink btnDesc = new Hyperlink("Descargar");
            private final Hyperlink btnRest = new Hyperlink("Restaurar");
            private final Hyperlink btnDel  = new Hyperlink("Borrar");
            private final HBox box = new HBox(12, btnDesc, btnRest, btnDel);
            {
                btnDesc.setOnAction(e -> onDescargar(getItemAtRow()));
                btnRest.setOnAction(e -> onRestaurar(getItemAtRow()));
                btnDel.setOnAction(e -> onBorrar(getItemAtRow()));
            }
            private BackupItem getItemAtRow() { return getIndex() >= 0 && getIndex() < tblBackups.getItems().size()
                    ? tblBackups.getItems().get(getIndex()) : null; }
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                setGraphic(empty ? null : box);
                setText(null);
            }
        });

        // datos de ejemplo
        seed();

        tblBackups.setItems(master);
        refreshKpis();
        refreshBanner();
    }

    /* ==================== NAV ==================== */
    @FXML private void onExit()        { App.goTo("/fxml/login.fxml",            "SIA Avitech — Inicio de sesión"); }
    @FXML private void goDashboard()   { App.goTo("/fxml/dashboard_admin.fxml",  "SIA Avitech — ADMIN"); }
    @FXML private void goSupplies()    { App.goTo("/fxml/suministros/suministros.fxml",      "SIA Avitech — Suministros"); }
    @FXML private void goHealth()      { App.goTo("/fxml/sanidad/sanidad.fxml",          "SIA Avitech — Sanidad"); }
    @FXML private void goProduction()  { App.goTo("/fxml/produccion/produccion.fxml",       "SIA Avitech — Producción"); }
    @FXML private void goReports()     { App.goTo("/fxml/reportes.fxml",         "SIA Avitech — Reportes"); }
    @FXML private void goAlerts()      { App.goTo("/fxml/alertas/alertas.fxml",          "SIA Avitech — Alertas"); }
    @FXML private void goAudit()       { App.goTo("/fxml/auditoria/auditoria.fxml",        "SIA Avitech — Auditoría"); }
    @FXML private void goParams()      { App.goTo("/fxml/parametros.fxml", "SIA Avitech — Parámetros"); }
    @FXML private void goUsers()       { App.goTo("/fxml/usuarios/usuarios.fxml",         "SIA Avitech — Usuarios"); }

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

    /* ===================== Respaldo manual ===================== */
    @FXML private void onNuevoRespaldo() {
        // dummy: agrega un registro “en progreso/completado”
        var now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm"));
        master.add(0, new BackupItem("avitech_manual_" + System.currentTimeMillis() + ".bak",
                now, "Manual", "2.0 GB", "Completado"));
        tblBackups.refresh();
        refreshKpis();
        refreshBanner();
    }

    @FXML private void onActualizar() {
        // futuro: volver a consultar servicio
        tblBackups.refresh();
        refreshKpis();
    }

    @FXML private void onRestaurarGeneral() {
        // placeholder para abrir diálogo de restauración general
        info("Restauración general (placeholder)");
    }

    /* ==================== Acciones por fila ==================== */
    private void onDescargar(BackupItem it) { if (it != null) info("Descargando: " + it.archivo()); }
    private void onRestaurar(BackupItem it) { if (it != null) info("Restaurando: " + it.archivo()); }
    private void onBorrar(BackupItem it) {
        if (it == null) return;
        master.remove(it);
        refreshKpis();
    }

    /* ==================== Helpers ==================== */
    private void seed() {
        master.setAll(
                new BackupItem("avitech_auto_20241007_020000.bak", "07/10/2024, 02:00", "Automático", "2.4 GB", "Completado"),
                new BackupItem("avitech_auto_20241006_020000.bak", "06/10/2024, 02:00", "Automático", "2.3 GB", "Completado"),
                new BackupItem("avitech_manual_actualizacion_20241005.bak", "05/10/2024, 14:30", "Manual", "2.2 GB", "Completado"),
                new BackupItem("avitech_auto_20241003_020000.bak", "03/10/2024, 02:00", "Automático", "2.0 GB", "Error"),
                new BackupItem("avitech_auto_20241002_020000.bak", "02/10/2024, 02:00", "Automático", "2.1 GB", "Completado")
        );
    }

    private void refreshKpis() {
        long total = master.size();
        long ok    = master.stream().filter(b -> b.estado().equalsIgnoreCase("Completado")).count();
        long err   = master.stream().filter(b -> b.estado().equalsIgnoreCase("Error")).count();
        double spaceGb = master.stream().mapToDouble(b -> parseGb(b.tamano())).sum();

        kpiTotal.setText(String.valueOf(total));
        kpiOk.setText(String.valueOf(ok));
        kpiErr.setText(String.valueOf(err));
        kpiSpace.setText(String.format("%.1f GB", spaceGb));
    }

    private void refreshBanner() {
        var fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm");
        lblAutoUltimo.setText("Último respaldo: " + LocalDateTime.now().minusDays(1).withHour(2).withMinute(0).format(fmt));
        lblAutoProximo.setText("Próximo respaldo: " + LocalDateTime.now().plusDays(1).withHour(2).withMinute(0).format(fmt));
        lblAutoTasa.setText("Tasa de éxito: 95.6% (4/5 respaldos)");
        lblAutoEstado.setText("Estado: Activo");
    }

    private double parseGb(String s) {
        try {
            var clean = s.trim().toLowerCase().replace("gb","").trim().replace(",","." );
            return Double.parseDouble(clean);
        } catch (Exception e) { return 0d; }
    }

    private void info(String msg) {
        // en real: usar diálogo propio
        System.out.println(msg);
    }

    /* DTO record para mayor claridad */
    public record BackupItem(String archivo, String fecha, String tipo, String tamano, String estado) {}
}