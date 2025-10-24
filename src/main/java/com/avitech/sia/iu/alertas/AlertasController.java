package com.avitech.sia.iu.alertas;

import com.avitech.sia.App;
import com.avitech.sia.iu.BaseController;
import com.avitech.sia.security.UserRole.Module;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Alertas — listo para BD con DataSource inyectable.
 * Mantiene datos DEMO si no hay DS.
 */
public class AlertasController extends BaseController implements UsesAlertasDataSource {

    @Override
    protected Module getRequiredModule() {
        return Module.ALERTAS;
    }

    /* ====== sidebar / topbar ====== */
    @FXML private Label lblSystemStatus;
    @FXML private Label lblUserInfo;
    @FXML private Label lblHeader;
    @FXML private VBox sidebar;
    @FXML private ToggleGroup sideGroup;

    /* ====== KPIs ====== */
    @FXML private Label kpiCritico;
    @FXML private Label kpiBajo;
    @FXML private Label kpiAdv;
    @FXML private Label kpiTotal;

    /* ====== Filtros ====== */
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbCategoria;
    @FXML private ComboBox<String> cbCriticidad;
    @FXML private ComboBox<String> cbUbicacion;

    /* ====== Tabla ====== */
    @FXML private TableView<ItemAlerta> tblAlertas;
    @FXML private TableColumn<ItemAlerta, String> colArticulo;
    @FXML private TableColumn<ItemAlerta, String> colCategoria;
    @FXML private TableColumn<ItemAlerta, String> colStockActual;
    @FXML private TableColumn<ItemAlerta, String> colStockMin;
    @FXML private TableColumn<ItemAlerta, String> colPorcentaje;
    @FXML private TableColumn<ItemAlerta, String> colDias;
    @FXML private TableColumn<ItemAlerta, String> colUbicacion;
    @FXML private TableColumn<ItemAlerta, String> colAccion;

    @FXML private Label lblCantidadTabla;
    @FXML private Label lblActualizado;
    @FXML private Label lblResCritico, lblResBajo, lblResAdvertencia;
    @FXML private Label lblBanner;

    // DataSource inyectable
    private AlertasDataSource ds;
    public void setAlertasDataSource(AlertasDataSource ds) { this.ds = ds; }
    @Override public AlertasDataSource alertasDs() { return ds; }

    private final ObservableList<ItemAlerta> master = FXCollections.observableArrayList();
    private final ObservableList<ItemAlerta> filtered = FXCollections.observableArrayList();

    @FXML
    @Override
    public void initialize() {
        super.initialize();
        lblSystemStatus.setText("Sistema Offline – MySQL Local");
        // Configurar permisos del menú según el rol del usuario
        configureMenuPermissions();

        lblHeader.setText("Administrador");

        // Combos base (si hay DS se sobrescriben)
        cbCategoria.setItems(FXCollections.observableArrayList("Todas", "Alimentos", "Medicamentos", "Limpieza", "Vacunas", "Suplementos"));
        cbUbicacion.setItems(FXCollections.observableArrayList("Todas", "Almacén Principal", "Farmacia", "Almacén Secundario", "Patio de Materiales"));
        cbCriticidad.setItems(FXCollections.observableArrayList("Todas", "Crítico", "Bajo", "Advertencia"));
        cbCategoria.getSelectionModel().selectFirst();
        cbUbicacion.getSelectionModel().selectFirst();
        cbCriticidad.getSelectionModel().selectFirst();

        // Tabla
        colArticulo.setCellValueFactory(new PropertyValueFactory<>("articulo"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colStockActual.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
        colStockMin.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        colPorcentaje.setCellValueFactory(new PropertyValueFactory<>("porcentaje"));
        colDias.setCellValueFactory(new PropertyValueFactory<>("diasRestantes"));
        colUbicacion.setCellValueFactory(new PropertyValueFactory<>("ubicacion"));
        colAccion.setCellValueFactory(param -> new SimpleStringProperty("Ir al ítem"));
        colAccion.setCellFactory(col -> new TableCell<>() {
            private final Hyperlink link = new Hyperlink("Ir al ítem");
            { link.setOnAction(e -> onGoItem(getTableView().getItems().get(getIndex()))); }
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                setGraphic(empty ? null : link);
                setText(null);
            }
        });

        // Si hay DS real: cargar catálogos y datos desde BD
        if (alertasDs() != null) {
            cbCategoria.setItems(FXCollections.observableArrayList("Todas"));
            cbCategoria.getItems().addAll(alertasDs().listCategorias());
            cbCategoria.getSelectionModel().selectFirst();

            cbUbicacion.setItems(FXCollections.observableArrayList("Todas"));
            cbUbicacion.getItems().addAll(alertasDs().listUbicaciones());
            cbUbicacion.getSelectionModel().selectFirst();

            cargarDesdeDS();
            refreshKpisFromDS();
            lblActualizado.setText("Actualizado: " + alertasDs().lastUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        } else {
            // DEMO local
            seedSample();
            applyFilterLocal();
            refreshKpisLocal();
            lblActualizado.setText("Actualizado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        }

        // Re-aplicar al cambiar filtros
        cbCategoria.valueProperty().addListener((o,a,b) -> onAplicarFiltros());
        cbUbicacion.valueProperty().addListener((o,a,b) -> onAplicarFiltros());
        cbCriticidad.valueProperty().addListener((o,a,b) -> onAplicarFiltros());
        txtSearch.textProperty().addListener((o,a,b) -> onAplicarFiltros());
    }

    /* ================== Navegación ================== */
    @FXML private void goDashboard()  { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
    @FXML private void goSupplies()   { App.goTo("/fxml/suministros/suministros.fxml", "SIA Avitech — Suministros"); }
    @FXML private void goHealth()     { App.goTo("/fxml/sanidad/sanidad.fxml", "SIA Avitech — Sanidad"); }
    @FXML private void goProduction() { App.goTo("/fxml/produccion/produccion.fxml", "SIA Avitech — Producción"); }
    @FXML private void goReports()    { App.goTo("/fxml/reportes.fxml", "SIA Avitech — Reportes"); }
    @FXML private void goAlerts()     { App.goTo("/fxml/alertas/alertas.fxml", "SIA Avitech — Alertas"); }
    @FXML private void goAudit()      { App.goTo("/fxml/auditoria/auditoria.fxml", "SIA Avitech — Auditoría"); }
    @FXML private void goParams()     { App.goTo("/fxml/parametros.fxml", "SIA Avitech — Parámetros"); }
    @FXML private void goUsers()      { App.goTo("/fxml/usuarios/usuarios.fxml", "SIA Avitech — Usuarios"); }
    @FXML private void goBackup()     { App.goTo("/fxml/respaldos/respaldos.fxml", "SIA Avitech — Respaldos"); }
    @FXML private void onExit()       { App.goTo("/fxml/login.fxml", "SIA Avitech — Inicio de sesión");}
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


    /* ================== Acciones UI ================== */

    @FXML
    private void onRefresh() {
        if (alertasDs() != null) {
            cargarDesdeDS();
            refreshKpisFromDS();
            lblActualizado.setText("Actualizado: " + alertasDs().lastUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        } else {
            applyFilterLocal();
            refreshKpisLocal();
            lblActualizado.setText("Actualizado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        }
    }

    @FXML
    private void onVerCriticos() {
        cbCriticidad.getSelectionModel().select("Crítico");
        onAplicarFiltros();
    }

    @FXML
    private void onAplicarFiltros() {
        if (alertasDs() != null) {
            cargarDesdeDS();
            refreshKpisFromDS();
        } else {
            applyFilterLocal();
            refreshKpisLocal();
        }
    }

    @FXML
    private void onLimpiarFiltros() {
        txtSearch.clear();
        cbCategoria.getSelectionModel().selectFirst();
        cbCriticidad.getSelectionModel().selectFirst();
        cbUbicacion.getSelectionModel().selectFirst();
        onAplicarFiltros();
    }

    private void onGoItem(ItemAlerta item) {
        // En real: navegación al detalle del artículo con ID
        goSupplies();
    }

    /* ================== Carga desde DS ================== */

    private void cargarDesdeDS() {
        var critSel = sel(cbCriticidad);
        Criticidad crit = switch (critSel) {
            case "Crítico" -> Criticidad.CRITICO;
            case "Bajo" -> Criticidad.BAJO;
            case "Advertencia" -> Criticidad.ADVERTENCIA;
            default -> null;
        };
        String cat = "Todas".equals(sel(cbCategoria)) ? null : sel(cbCategoria);
        String ubi = "Todas".equals(sel(cbUbicacion)) ? null : sel(cbUbicacion);

        var lista = alertasDs().searchAlertas(
                txtSearch.getText(), cat, crit, ubi
        );

        master.setAll(lista.stream().map(this::mapDtoToRow).toList());
        filtered.setAll(master);
        tblAlertas.setItems(filtered);
        lblCantidadTabla.setText("(" + filtered.size() + ")");
        // banner parcial con críticos
        long criticos = lista.stream().filter(a -> a.criticidad() == Criticidad.CRITICO).count();
        lblBanner.setText(criticos + " artículo(s) en stock crítico requieren atención inmediata");
    }

    private ItemAlerta mapDtoToRow(AlertaDTO d) {
        return new ItemAlerta(
                d.articulo(), d.categoria(), d.stockActual(), d.stockMinimo(),
                d.porcentaje(), d.diasRestantes(), d.ubicacion(),
                switch (d.criticidad()) {
                    case CRITICO -> "Crítico";
                    case BAJO -> "Bajo";
                    case ADVERTENCIA -> "Advertencia";
                }
        );
    }

    private void refreshKpisFromDS() {
        long crit = alertasDs().countPorCriticidad(Criticidad.CRITICO);
        long bajo = alertasDs().countPorCriticidad(Criticidad.BAJO);
        long adv  = alertasDs().countPorCriticidad(Criticidad.ADVERTENCIA);
        long tot  = alertasDs().countTotal();

        kpiCritico.setText(String.valueOf(crit));
        kpiBajo.setText(String.valueOf(bajo));
        kpiAdv.setText(String.valueOf(adv));
        kpiTotal.setText(String.valueOf(tot));

        lblResCritico.setText("Crítico — " + crit + " artículos");
        lblResBajo.setText("Bajo — " + bajo + " artículos");
        lblResAdvertencia.setText("Advertencia — " + adv + " artículos");
    }

    /* ================== Lógica local (DEMO) ================== */

    private void seedSample() {
        master.setAll(
                new ItemAlerta("Alimento Balanceado Postura", "Alimentos", "450 kg", "1000 kg", "45%", "2 días", "Almacén Principal", "Crítico"),
                new ItemAlerta("Enrofloxacina 10%", "Medicamentos", "20 frascos", "40 frascos", "50%", "15 días", "Farmacia", "Bajo"),
                new ItemAlerta("Vitaminas AD3E", "Suplementos", "12 frascos", "25 frascos", "48%", "8 días", "Farmacia", "Bajo"),
                new ItemAlerta("Alimento Pre-inicio", "Alimentos", "75 kg", "200 kg", "38%", "4 días", "Almacén Secundario", "Crítico"),
                new ItemAlerta("Desinfectante Clorado", "Limpieza", "30 litros", "50 litros", "60%", "12 días", "Patio de Materiales", "Advertencia"),
                new ItemAlerta("Vacuna Newcastle", "Vacunas", "100 dosis", "450 dosis", "22%", "6 días", "Farmacia", "Crítico"),
                new ItemAlerta("Probióticos", "Suplementos", "6 envases", "15 envases", "40%", "10 días", "Farmacia", "Bajo")
        );
    }

    private void applyFilterLocal() {
        String text = val(txtSearch).toLowerCase();
        String cat  = sel(cbCategoria);
        String cri  = sel(cbCriticidad);
        String ubi  = sel(cbUbicacion);

        filtered.setAll(master.filtered(it ->
                (text.isEmpty()
                        || it.articulo.toLowerCase().contains(text)
                        || it.categoria.toLowerCase().contains(text)
                        || it.ubicacion.toLowerCase().contains(text))
                        && (cat.equals("Todas") || it.categoria.equalsIgnoreCase(cat))
                        && (cri.equals("Todas") || it.nivel.equalsIgnoreCase(cri))
                        && (ubi.equals("Todas") || it.ubicacion.equalsIgnoreCase(ubi))
        ));
        tblAlertas.setItems(filtered);
        lblCantidadTabla.setText("(" + filtered.size() + ")");
        long criticos = filtered.stream().filter(i -> Objects.equals(i.nivel, "Crítico")).count();
        lblBanner.setText(criticos + " artículo(s) en stock crítico requieren atención inmediata");
    }

    private void refreshKpisLocal() {
        long crit = master.stream().filter(i -> i.nivel.equalsIgnoreCase("Crítico")).count();
        long bajo = master.stream().filter(i -> i.nivel.equalsIgnoreCase("Bajo")).count();
        long adv  = master.stream().filter(i -> i.nivel.equalsIgnoreCase("Advertencia")).count();

        kpiCritico.setText(String.valueOf(crit));
        kpiBajo.setText(String.valueOf(bajo));
        kpiAdv.setText(String.valueOf(adv));
        kpiTotal.setText(String.valueOf(master.size()));

        lblResCritico.setText("Crítico — " + crit + " artículos");
        lblResBajo.setText("Bajo — " + bajo + " artículos");
        lblResAdvertencia.setText("Advertencia — " + adv + " artículos");
    }

    /* ================== Utils ================== */
    private static String val(TextInputControl c) { return c.getText() == null ? "" : c.getText().trim(); }
    private static String sel(ComboBox<String> cb) {
        var s = cb.getSelectionModel().getSelectedItem();
        return (s == null) ? "Todas" : s;
    }

    /* ================== DTO para la tabla (UI) ================== */
    public static class ItemAlerta {
        public final String articulo, categoria, stockActual, stockMinimo, porcentaje, diasRestantes, ubicacion, nivel;
        public ItemAlerta(String articulo, String categoria, String stockActual, String stockMinimo,
                          String porcentaje, String diasRestantes, String ubicacion, String nivel) {
            this.articulo = articulo; this.categoria = categoria; this.stockActual = stockActual;
            this.stockMinimo = stockMinimo; this.porcentaje = porcentaje; this.diasRestantes = diasRestantes;
            this.ubicacion = ubicacion; this.nivel = nivel;
        }
        public String getArticulo() { return articulo; }
        public String getCategoria() { return categoria; }
        public String getStockActual() { return stockActual; }
        public String getStockMinimo() { return stockMinimo; }
        public String getPorcentaje() { return porcentaje; }
        public String getDiasRestantes() { return diasRestantes; }
        public String getUbicacion() { return ubicacion; }
        public String getNivel() { return nivel; }
    }
}
