package com.avitech.sia.iu.produccion;

import com.avitech.sia.App;
import com.avitech.sia.iu.BaseController;
import com.avitech.sia.security.UserRole.Module;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;

/** Producción — UI lista para conectar a BD vía ProduccionDataSource. */
public class ProduccionController extends BaseController implements UsesProduccionDataSource {

    @Override
    protected Module getRequiredModule() {
        return Module.PRODUCCION;
    }

    /* ===== Topbar / sidebar (opcional mostrarlos si los tienes en FXML) ===== */
    @FXML private Label lblSystemStatus, lblHeader, lblUserInfo;
    @FXML private VBox sidebar;
    @FXML private ToggleGroup sideGroup;

    /* ===== KPIs ===== */
    @FXML private Label lblKpiProduccionTotal, lblKpiPosturaPromedio, lblKpiPesoPromedio, lblKpiMortalidadTotal;

    /* ===== Filtro ===== */
    @FXML private ComboBox<String> cbFiltroGalpon;

    /* ===== Tabla diaria ===== */
    @FXML private TableView<DailyRow> tblDiario;
    @FXML private TableColumn<DailyRow, String>  colGalpon, colClasif;
    @FXML private TableColumn<DailyRow, Number>  colAves, colProduccion, colPostura, colPeso, colMortalidad;

    /* ===== Form registro ===== */
    @FXML private ComboBox<String> cbGalpon;
    @FXML private TextField tfProduccionTotal, tfPesoPromedio, tfMortalidad, tfHuevosB, tfHuevosA, tfHuevosAA, tfHuevosAAA;
    @FXML private Button btnGuardarRegistro, btnCancelarRegistro;

    private final ObservableList<DailyRow> diarioItems = FXCollections.observableArrayList();

    @FXML
    @Override
    public void initialize() {
        super.initialize();
        // Header demo (puedes traerlo de config)
        if (lblHeader != null)      lblHeader.setText("Administrador");
        if (lblUserInfo != null)    lblUserInfo.setText("Administrador");
        // Configurar permisos del menú según el rol del usuario
        configureMenuPermissions();

        if (lblSystemStatus != null)lblSystemStatus.setText("Sistema Offline – MySQL Local");

        // === Tabla: binding de columnas ===
        colGalpon.setCellValueFactory(d -> d.getValue().galponProperty());
        colAves.setCellValueFactory(d -> d.getValue().avesProperty());
        colProduccion.setCellValueFactory(d -> d.getValue().produccionProperty());
        colPostura.setCellValueFactory(d -> d.getValue().posturaProperty());
        colPeso.setCellValueFactory(d -> d.getValue().pesoPromProperty());
        colClasif.setCellValueFactory(d -> d.getValue().clasificacionProperty());
        colMortalidad.setCellValueFactory(d -> d.getValue().mortalidadProperty());
        tblDiario.setPlaceholder(new Label("Sin registros"));
        tblDiario.setItems(diarioItems);

        // === Combos ===
        List<String> galpones = produccionDs().findGalpones();
        cbFiltroGalpon.setItems(FXCollections.observableArrayList(galpones.isEmpty() ? List.of("Todos") : galpones));
        if (!cbFiltroGalpon.getItems().isEmpty()) cbFiltroGalpon.getSelectionModel().selectFirst();

        cbGalpon.setItems(FXCollections.observableArrayList(galpones));
        if (!cbGalpon.getItems().isEmpty()) cbGalpon.getSelectionModel().selectFirst();

        // === KPIs ===
        refreshKpis();

        // === Tabla (inicial) ===
        refreshTabla();
    }

    private void refreshKpis() {
        ProduccionKpis k = produccionDs().getKpis(); // ya decidirás si quieres filtros de fecha aquí
        lblKpiProduccionTotal.setText(k.produccionTotal());
        lblKpiPosturaPromedio.setText(k.posturaPromedio());
        lblKpiPesoPromedio.setText(k.pesoPromedio());
        lblKpiMortalidadTotal.setText(k.mortalidadTotal());
    }

    private void refreshTabla() {
        String galpon = cbFiltroGalpon.getValue();
        List<ProduccionDTO> data = produccionDs().listarDiario(galpon == null ? "Todos" : galpon);
        diarioItems.setAll(data.stream().map(DailyRow::fromDTO).toList());
    }

    /* ================== Acciones formulario ================== */

    @FXML
    private void onGuardarRegistro() {
        ProduccionDTO dto = readForm();
        if (dto == null) return;

        try {
            long id = produccionDs().saveRegistro(dto);
            System.out.println("Registro guardado id=" + id);
            clearForm();
            refreshTabla();
            refreshKpis();
            new Alert(Alert.AlertType.INFORMATION, "Registro guardado correctamente.").showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Error guardando registro: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onCancelarRegistro() {
        clearForm();
    }

    private ProduccionDTO readForm() {
        String galpon = cbGalpon.getValue();
        if (galpon == null || galpon.isBlank()) {
            alertWarn("Selecciona un galpón."); return null;
        }
        Integer prod  = parseInt(tfProduccionTotal, "Producción total");
        if (prod == null) return null;
        Double  peso  = parseDouble(tfPesoPromedio, "Peso promedio (g)");
        if (peso == null) return null;
        Integer mort  = parseInt(tfMortalidad, "Mortalidad (aves)");
        if (mort == null) return null;

        Integer b   = parseIntOptional(tfHuevosB);
        Integer a   = parseIntOptional(tfHuevosA);
        Integer aa  = parseIntOptional(tfHuevosAA);
        Integer aaa = parseIntOptional(tfHuevosAAA);

        return new ProduccionDTO(
                LocalDate.now(),      // si luego usas selector de fecha, cámbialo
                galpon,
                prod,
                peso,
                mort,
                b == null ? 0 : b,
                a == null ? 0 : a,
                aa == null ? 0 : aa,
                aaa == null ? 0 : aaa
        );
    }

    private void clearForm() {
        tfProduccionTotal.clear();
        tfPesoPromedio.clear();
        tfMortalidad.clear();
        tfHuevosB.clear();
        tfHuevosA.clear();
        tfHuevosAA.clear();
        tfHuevosAAA.clear();
        if (!cbGalpon.getItems().isEmpty()) cbGalpon.getSelectionModel().selectFirst();
    }

    private Integer parseInt(TextField tf, String label) {
        try {
            String s = tf.getText();
            if (s == null || s.isBlank()) { alertWarn("Completa: " + label); return null; }
            int v = Integer.parseInt(s.trim());
            if (v < 0) throw new NumberFormatException();
            return v;
        } catch (NumberFormatException ex) {
            alertWarn(label + " debe ser un entero válido (>= 0)."); return null;
        }
    }

    private Double parseDouble(TextField tf, String label) {
        try {
            String s = tf.getText();
            if (s == null || s.isBlank()) { alertWarn("Completa: " + label); return null; }
            double v = Double.parseDouble(s.trim());
            if (v < 0) throw new NumberFormatException();
            return v;
        } catch (NumberFormatException ex) {
            alertWarn(label + " debe ser un número válido (>= 0)."); return null;
        }
    }

    private Integer parseIntOptional(TextField tf) {
        String s = tf.getText();
        if (s == null || s.isBlank()) return null;
        try {
            int v = Integer.parseInt(s.trim());
            return Math.max(v, 0);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void alertWarn(String msg) { new Alert(Alert.AlertType.WARNING, msg).showAndWait(); }

    /* ================== Filtro ================== */

    @FXML
    private void onFiltrar() {
        refreshTabla();
    }

    @FXML
    private void onLimpiarFiltro() {
        if (!cbFiltroGalpon.getItems().isEmpty()) cbFiltroGalpon.getSelectionModel().selectFirst();
        refreshTabla();
    }

    /* ================== Navegación ================== */

    @FXML private void goDashboard()  { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
    @FXML private void goSupplies()   { App.goTo("/fxml/suministros/suministros.fxml", "SIA Avitech — Suministros"); }
    @FXML private void goHealth()     { App.goTo("/fxml/sanidad/sanidad.fxml", "SIA Avitech — Sanidad"); }
    @FXML private void goProduction() { App.goTo("/fxml/produccion/produccion.fxml", "SIA Avitech — Producción"); }
    @FXML private void goReports()    { App.goTo("/fxml/reportes.fxml",        "SIA Avitech — Reportes"); }
    @FXML private void goAlerts()     { App.goTo("/fxml/alertas.fxml",         "SIA Avitech — Alertas"); }
    @FXML private void goAudit()      { App.goTo("/fxml/auditoria.fxml",       "SIA Avitech — Auditoría"); }
    @FXML private void goParams()     { App.goTo("/fxml/parametros.fxml",      "SIA Avitech — Parámetros"); }
    @FXML private void goUsers()      { App.goTo("/fxml/usuarios/usuarios.fxml",        "SIA Avitech — Usuarios"); }
    @FXML private void goBackup()     { App.goTo("/fxml/respaldos/respaldos.fxml",       "SIA Avitech — Respaldos"); }
    @FXML private void onExit()       { App.goTo("/fxml/login.fxml",           "SIA Avitech — Inicio de sesión"); }

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

    /* ================== Row model para la tabla ================== */
    public static class DailyRow {
        private final StringProperty  galpon = new SimpleStringProperty();
        private final IntegerProperty aves = new SimpleIntegerProperty();
        private final IntegerProperty produccion = new SimpleIntegerProperty();
        private final DoubleProperty  postura = new SimpleDoubleProperty();     // %
        private final DoubleProperty  pesoProm = new SimpleDoubleProperty();    // g
        private final StringProperty  clasificacion = new SimpleStringProperty();
        private final IntegerProperty mortalidad = new SimpleIntegerProperty();

        public static DailyRow fromDTO(ProduccionDTO d) {
            DailyRow r = new DailyRow();
            r.galpon.set(d.galpon());
            r.aves.set(d.aves() == null ? 0 : d.aves()); // si tu datasource trae aves del galpón
            r.produccion.set(d.produccionTotal());
            r.postura.set(d.posturaPorcentual() == null ? 0.0 : d.posturaPorcentual());
            r.pesoProm.set(d.pesoPromedio());
            r.clasificacion.set(String.format("B:%d  A:%d  AA:%d  AAA:%d", d.huevosB(), d.huevosA(), d.huevosAA(), d.huevosAAA()));
            r.mortalidad.set(d.mortalidad());
            return r;
        }

        public StringProperty galponProperty() { return galpon; }
        public IntegerProperty avesProperty() { return aves; }
        public IntegerProperty produccionProperty() { return produccion; }
        public DoubleProperty  posturaProperty() { return postura; }
        public DoubleProperty  pesoPromProperty() { return pesoProm; }
        public StringProperty  clasificacionProperty() { return clasificacion; }
        public IntegerProperty mortalidadProperty() { return mortalidad; }
    }
}
