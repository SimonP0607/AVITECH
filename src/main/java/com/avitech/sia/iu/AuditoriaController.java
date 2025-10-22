package com.avitech.sia.iu;

import com.avitech.sia.App;
import com.avitech.sia.security.UserRole.Module;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditoriaController extends BaseController {

    @Override
    protected Module getRequiredModule() {
        return Module.AUDITORIA;
    }

    /* ===== Sidebar / topbar ===== */
    @FXML private Label lblUserInfo;
    @FXML private Label lblSystemStatus;
    @FXML private VBox sidebar;
    @FXML private ToggleGroup sideGroup;
    @FXML private Label lblHeader;

    /* ===== KPIs ===== */
    @FXML private Label lblTotal;
    @FXML private Label lblOK;
    @FXML private Label lblFail;
    @FXML private Label lblActivos;

    /* ===== Filtros ===== */
    @FXML private DatePicker dpInicio;
    @FXML private DatePicker dpFin;
    @FXML private ComboBox<String> cboUsuario;
    @FXML private ComboBox<String> cboModulo;
    @FXML private ComboBox<String> cboAccion;
    @FXML private TextField txtBuscar;

    /* ===== Tabla principal ===== */
    @FXML private TableView<AuditoriaDTO> tvAuditoria;
    @FXML private TableColumn<AuditoriaDTO, String> colUsuario;
    @FXML private TableColumn<AuditoriaDTO, String> colFecha;
    @FXML private TableColumn<AuditoriaDTO, String> colAccion;
    @FXML private TableColumn<AuditoriaDTO, String> colModulo;
    @FXML private TableColumn<AuditoriaDTO, String> colDetalle;
    @FXML private TableColumn<AuditoriaDTO, String> colVer;

    /* ===== Resúmenes ===== */
    @FXML private ListView<String> lvActividadModulo;
    @FXML private ListView<String> lvActividadUsuario;

    private final ObservableList<AuditoriaDTO> master = FXCollections.observableArrayList();

    @FXML
    @Override
    public void initialize() {
        super.initialize();

        // Cabecera
        // Configurar permisos del menú según el rol del usuario
        configureMenuPermissions();

        lblSystemStatus.setText("Sistema Offline – MySQL Local");
        lblHeader.setText("Administrador");

        // Filtros
        dpInicio.setValue(LocalDate.now().minusDays(7));
        dpFin.setValue(LocalDate.now());

        cboUsuario.setItems(FXCollections.observableArrayList(
                "Todos los usuarios", "María González", "Carlos Ruiz", "Ana Mendoza", "Sistema"
        ));
        cboUsuario.getSelectionModel().selectFirst();

        cboModulo.setItems(FXCollections.observableArrayList(
                "Todos los módulos", "Producción", "Suministros", "Sanitario", "Reportes", "Alertas", "Usuarios", "Parámetros", "Login", "Respaldos"
        ));
        cboModulo.getSelectionModel().selectFirst();

        cboAccion.setItems(FXCollections.observableArrayList(
                "Todas las acciones", "Ver", "Crear", "Modificar", "Eliminar", "Login", "Logout"
        ));
        cboAccion.getSelectionModel().selectFirst();

        // Tabla
        colUsuario.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().usuario()));
        colFecha.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().fechaHora()));
        colAccion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().accion()));
        colModulo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().modulo()));
        colDetalle.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().detalle()));

        // Botón "Ver Fuente"
        colVer.setCellFactory(makeVerFuenteCell());

        // Datos demo (placeholders)
        seedDemo();

        // KPIs
        refreshKpis();

        // Listas de resumen (demo)
        lvActividadModulo.setItems(FXCollections.observableArrayList(
                "Producción • 6 eventos",
                "Suministros • 4 eventos",
                "Sanitario • 3 eventos",
                "Reportes • 2 eventos",
                "Alertas • 1 evento",
                "Usuarios • 1 evento"
        ));
        lvActividadUsuario.setItems(FXCollections.observableArrayList(
                "María González • 3 acciones",
                "Carlos Ruiz • 2 acciones",
                "Ana Mendoza • 1 acción",
                "Sistema • 1 acción"
        ));
    }

    /* ====== Actions ====== */

    @FXML
    private void onFiltrar() {
        // Filtro básico en memoria (placeholder). Luego lo cambiamos a consulta SQL/DAO.
        String q = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim().toLowerCase();
        String u = cboUsuario.getValue();
        String m = cboModulo.getValue();
        String a = cboAccion.getValue();

        ObservableList<AuditoriaDTO> filtered = master.filtered(it -> {
            boolean byUser = "Todos los usuarios".equals(u) || it.usuario().equals(u);
            boolean byMod  = "Todos los módulos".equals(m)  || it.modulo().equals(m);
            boolean byAct  = "Todas las acciones".equals(a) || it.accion().equals(a);
            boolean byTxt  = q.isEmpty() || (it.detalle().toLowerCase().contains(q) || it.usuario().toLowerCase().contains(q));
            return byUser && byMod && byAct && byTxt;
        });

        tvAuditoria.setItems(filtered);
        refreshKpis();
    }

    @FXML
    private void onLimpiar() {
        dpInicio.setValue(LocalDate.now().minusDays(7));
        dpFin.setValue(LocalDate.now());
        cboUsuario.getSelectionModel().selectFirst();
        cboModulo.getSelectionModel().selectFirst();
        cboAccion.getSelectionModel().selectFirst();
        txtBuscar.clear();

        tvAuditoria.setItems(master);
        refreshKpis();
    }

    @FXML
    private void onExportCsv() {
        // Placeholder: exportación simple a consola. (Luego lo cambiamos a archivo real)
        System.out.println("CSV export — registros: " + tvAuditoria.getItems().size());
        tvAuditoria.getItems().forEach(r -> System.out.println(
                r.usuario()+";"+r.fechaHora()+";"+r.accion()+";"+r.modulo()+";"+r.detalle()
        ));
    }

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

    /* ====== Helpers ====== */

    private void seedDemo() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");

        master.setAll(
                new AuditoriaDTO("María González", LocalDateTime.now().minusMinutes(8).format(fmt), "Ver",       "Alertas",
                        "Consultó lista de alertas de stock bajo · ID: ALT-2024-000156", "ALT-2024-000156"),
                new AuditoriaDTO("Carlos Ruiz",     LocalDateTime.now().minusMinutes(42).format(fmt), "Modificar", "Sanitario",
                        "Actualizó estado de tratamiento SAN-2024-000451 a completado · ID: SAN-2024-000451", "SAN-2024-000451"),
                new AuditoriaDTO("Ana Mendoza",     LocalDateTime.now().minusHours(3).format(fmt), "Crear",    "Reportes",
                        "Generó reporte de producción por lote/semana · ID: REP-2024-000012", "REP-2024-000012"),
                new AuditoriaDTO("Sistema",         LocalDateTime.now().minusHours(5).format(fmt), "Login",    "Login",
                        "Inicio automático de rutina nocturna", "SYS-LOGIN")
        );

        tvAuditoria.setItems(master);
    }

    private void refreshKpis() {
        int total = tvAuditoria.getItems() == null ? 0 : tvAuditoria.getItems().size();
        int ok = (int) tvAuditoria.getItems().stream().filter(r -> r.accion().matches("Ver|Crear|Modificar|Login")).count();
        int fail = total - ok; // placeholder
        int activos = 2;       // placeholder

        lblTotal.setText(String.valueOf(total));
        lblOK.setText(String.valueOf(ok));
        lblFail.setText(String.valueOf(fail));
        lblActivos.setText(String.valueOf(activos));
    }

    private Callback<TableColumn<AuditoriaDTO, String>, TableCell<AuditoriaDTO, String>> makeVerFuenteCell() {
        return col -> new TableCell<>() {
            private final Button btn = new Button("Ver Fuente");
            {
                btn.getStyleClass().add("ghostBtn");
                btn.setOnAction(e -> {
                    AuditoriaDTO dto = getTableView().getItems().get(getIndex());
                    onVerFuente(dto);
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
                setText(null);
            }
        };
    }

    private void onVerFuente(AuditoriaDTO dto) {
        // Placeholder: en real, abrir modal con detalle o navegar a la entidad
        System.out.println("Ver fuente → " + dto.referencia());
    }

    /* ====== DTO ====== */
    public record AuditoriaDTO(
            String usuario,
            String fechaHora,
            String accion,
            String modulo,
            String detalle,
            String referencia
    ) { }
}
