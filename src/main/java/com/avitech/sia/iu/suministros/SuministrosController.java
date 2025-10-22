package com.avitech.sia.iu.suministros;

import com.avitech.sia.App;
import com.avitech.sia.iu.BaseController;
import com.avitech.sia.security.UserRole.Module;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


/** Controlador Suministros: navegación, filtros/tabla con DataSource y modales con inyección de DS. */
public class SuministrosController extends BaseController implements UsesSuministrosDataSource {

    @Override
    protected Module getRequiredModule() {
        return Module.SUMINISTROS;
    }

    /* topbar / sidebar */
    @FXML private Label lblSystemStatus;
    @FXML private Label lblHeader;
    @FXML private Label lblUserInfo;
    @FXML private VBox sidebar;
    @FXML private ToggleGroup sideGroup;

    /* host para modales */
    @FXML private StackPane overlayHost;
    @FXML private Pane      modalLayer;
    @FXML private BorderPane rootPane;

    /* KPIs */
    @FXML private Label kpiMovHoy, kpiActivos, kpiStockBajo, kpiValor;

    /* filtros */
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbTipo, cbResp;
    @FXML private DatePicker dpDesde, dpHasta;
    @FXML private Label lblMostrando;

    /* tabla */
    @FXML private TableView<Mov> tblMovs;
    @FXML private TableColumn<Mov, String> colFecha, colItem, colCant, colUnidad, colTipo, colResp, colDet, colStock, colAccion;

    private final ObservableList<Mov> master   = FXCollections.observableArrayList();
    private final ObservableList<Mov> filtered = FXCollections.observableArrayList();

    /* ===== DataSource ===== */
    private SuministrosDataSource dataSource = new EmptySuministrosDataSource();
    @Override public void setDataSource(SuministrosDataSource ds) {
        this.dataSource = (ds != null ? ds : new EmptySuministrosDataSource());
        // Si la UI ya está, refrescamos
        if (cbTipo != null) loadFiltersAndData();
    }

    @FXML
    @Override
    public void initialize() {
        super.initialize();
        lblSystemStatus.setText("Sistema Offline – MySQL Local");
        lblHeader.setText("Administrador");
        // Configurar permisos del menú según el rol del usuario
        configureMenuPermissions();

        lblUserInfo.setText("Administrador");

        /* columnas */
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().fecha));
        colItem.setCellValueFactory(c  -> new SimpleStringProperty(c.getValue().item));
        colCant.setCellValueFactory(c  -> new SimpleStringProperty(c.getValue().cantidad));
        colUnidad.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().unidad));
        colTipo.setCellValueFactory(c  -> new SimpleStringProperty(c.getValue().tipo));
        colResp.setCellValueFactory(c  -> new SimpleStringProperty(c.getValue().responsable));
        colDet.setCellValueFactory(c   -> new SimpleStringProperty(c.getValue().detalles));
        colStock.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().stock));
        colAccion.setCellValueFactory(c -> new SimpleStringProperty("ver"));
        colAccion.setCellFactory(col -> new TableCell<>() {
            private final Hyperlink link = new Hyperlink("Ver ítem");
            { link.setOnAction(e -> onVerItem(getTableView().getItems().get(getIndex()))); }
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                setGraphic(empty ? null : link); setText(null);
            }
        });

        tblMovs.setItems(filtered);

        // Cargar combos, datos e iniciales desde el DS
        loadFiltersAndData();
    }

    private void loadFiltersAndData() {
        // Combos / Fechas
        cbTipo.setItems(FXCollections.observableArrayList("Todos","Entrada","Salida","Transferencia"));
        List<String> responsables = dataSource.getResponsables().stream().map(SuministrosDataSource.Responsable::nombre).toList();
        cbResp.setItems(FXCollections.observableArrayList("Todos"));
        cbResp.getItems().addAll(responsables);
        cbTipo.getSelectionModel().selectFirst();
        cbResp.getSelectionModel().selectFirst();
        dpDesde.setValue(LocalDate.now().minusDays(7));
        dpHasta.setValue(LocalDate.now());

        // KPIs
        var k = dataSource.getKpis();
        kpiMovHoy.setText(String.valueOf(nz(k.movimientosHoy())));
        kpiActivos.setText(String.valueOf(nz(k.productosActivos())));
        kpiStockBajo.setText(String.valueOf(nz(k.stockBajo())));
        kpiValor.setText(formMoney(k.valorTotalStock()));

        // Movimientos
        reloadMovs();
    }

    private void reloadMovs() {
        var filter = new SuministrosDataSource.MovFilter(
                safeLower(txtSearch.getText()),
                sel(cbTipo),
                null, // si a futuro quieres mapear id de responsable
                dpDesde.getValue(),
                dpHasta.getValue()
        );
        var list = dataSource.getMovimientos(filter);
        master.setAll(list.stream().map(SuministrosController::mapMov).toList());
        applyFilter();
    }

    private static Mov mapMov(SuministrosDataSource.Movimiento m) {
        String fecha = (m.fechaHora()==null) ? "—" : m.fechaHora().toString();
        String cant  = (m.cantidad()==null) ? "—" : stripZeros(m.cantidad());
        String stock = m.stockPosterior()==null ? "—" : m.stockPosterior();
        return new Mov(fecha, nv(m.item()), cant, nv(m.unidad()), nv(m.tipo()), nv(m.responsable()), nv(m.detalles()), stock);
    }

    /* ========= Navegación ========= */
    @FXML private void goDashboard()  { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
    @FXML private void goSupplies()   { /* ya estás aquí */ }
    @FXML private void goHealth()     { App.goTo("/fxml/sanidad/sanidad.fxml", "SIA Avitech — Sanidad"); }
    @FXML private void goProduction() { App.goTo("/fxml/produccion/produccion.fxml", "SIA Avitech — Producción"); }
    @FXML private void goReports()    { App.goTo("/fxml/reportes.fxml", "SIA Avitech — Reportes"); }
    @FXML private void goAlerts()     { App.goTo("/fxml/alertas.fxml", "SIA Avitech — Alertas"); }
    @FXML private void goAudit()      { App.goTo("/fxml/auditoria.fxml", "SIA Avitech — Auditoría"); }
    @FXML private void goParams()     { /* pendiente */ }
    @FXML private void goUsers()      { App.goTo("/fxml/usuarios/usuarios.fxml", "SIA Avitech — Usuarios"); }
    @FXML private void goBackup()     { /* pendiente */ }
    @FXML private void onExit()       { App.goTo("/fxml/login.fxml", "SIA Avitech — Inicio de sesión"); }

    /**
     * Configura la visibilidad de los botones del menú según los permisos del usuario.
     */
    private void configureMenuPermissions() {
        if (sidebar == null || sessionManager == null) return;

        // Recorrer todos los ToggleButton del sidebar
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

    /**
     * Obtiene el módulo asociado al texto de un botón del menú.
     */
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

    /* ========= Acciones (modales) ========= */
    @FXML private void onEntrada()   { showModal("/fxml/suministros/entrada.fxml"); }
    @FXML private void onSalida()    { showModal("/fxml/suministros/salida.fxml"); }
    @FXML private void onVerStock()  { showModal("/fxml/suministros/stock.fxml"); }
    @FXML private void onMoverStock(){ showModal("/fxml/suministros/moverstock.fxml"); }



    private void onVerItem(Mov m) {
        new Alert(Alert.AlertType.INFORMATION, "Ítem: " + m.item).showAndWait();
    }

    /* ========= Overlay & Modales (inyectando DS) ========= */
    private void showModal(String fxmlPath) {
        try {
            FXMLLoader fx = new FXMLLoader(getClass().getResource(fxmlPath));
            StackPane modal = fx.load();

            // pasar datasource si aplica
            Object ctrl = fx.getController();
            if (ctrl instanceof UsesSuministrosDataSource u) u.setDataSource(dataSource);

            Pane center = (Pane) rootPane.getCenter();
            center.getChildren().add(modal);

            // ----- LIMITE DE TAMAÑO Y CENTRADO DEL MODAL -----
            // Busca el contenedor de la tarjeta por su styleClass="modal-card"
            Region card = (Region) modal.lookup(".modal-card");
            if (card != null) {
                // Máximo 55% del ancho y 85% del alto de la vista actual
                card.maxWidthProperty().bind(rootPane.widthProperty().multiply(0.55));
                card.maxHeightProperty().bind(rootPane.heightProperty().multiply(0.85));
                StackPane.setAlignment(card, Pos.CENTER);
                StackPane.setMargin(card, new Insets(12)); // separarlo de los bordes
            }

            // Cerrar con click en el overlay
            modal.lookupAll(".modal-overlay").forEach(n ->
                    n.setOnMouseClicked(e -> center.getChildren().remove(modal)));

            // Cerrar con botones
            Node close = modal.lookup("#btnClose");
            if (close instanceof Button b1) b1.setOnAction(e -> center.getChildren().remove(modal));
            Node cancel = modal.lookup("#btnCancelar");
            if (cancel instanceof Button b2) b2.setOnAction(e -> center.getChildren().remove(modal));

            // Cerrar con ESC
            modal.setOnKeyPressed(e -> {
                switch (e.getCode()) {
                    case ESCAPE -> center.getChildren().remove(modal);
                }
            });
            modal.requestFocus();
            // -----------------------------------------------

        } catch (IOException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir el modal: " + fxmlPath).showAndWait();
        }
    }



    private void closeModal(Node modal) {
        modalLayer.getChildren().remove(modal);
    }

    /* ========= Filtros ========= */
    @FXML
    private void applyFilter() {
        final String t = safeLower(txtSearch.getText());
        final String tipo = sel(cbTipo);
        final String resp = sel(cbResp);
        final LocalDate d1 = dpDesde.getValue();
        final LocalDate d2 = dpHasta.getValue();

        filtered.setAll(master.filtered(m ->
                (t.isEmpty() || m.itemLc.contains(t) || m.detallesLc.contains(t) || m.respLc.contains(t)) &&
                        ("Todos".equalsIgnoreCase(tipo) || m.tipo.equalsIgnoreCase(tipo)) &&
                        ("Todos".equalsIgnoreCase(resp)  || m.responsable.equalsIgnoreCase(resp)) &&
                        between(m.localDate, d1, d2)
        ));
        tblMovs.setItems(filtered);
        lblMostrando.setText(filtered.size() + " ítems");
    }

    /* ========= Utilidades ========= */
    private static boolean between(LocalDate f, LocalDate d1, LocalDate d2) {
        if (f == null) return true;
        boolean ok1 = (d1 == null) || !f.isBefore(d1);
        boolean ok2 = (d2 == null) || !f.isAfter(d2);
        return ok1 && ok2;
    }
    private static String safeLower(String s){ return s==null? "" : s.toLowerCase().trim(); }
    private static String sel(ComboBox<String> cb){ var s = cb.getSelectionModel().getSelectedItem(); return s==null? "Todos": s; }
    private static int nz(Integer i){ return i==null? 0: i; }
    private static String formMoney(BigDecimal v){ return (v==null? "—" : "$" + v); }
    private static String nv(String s){ return s==null? "—" : s; }
    private static String stripZeros(BigDecimal v){ return v.stripTrailingZeros().toPlainString(); }

    /* ========= DTO para la tabla ========= */
    public static class Mov {
        public final String fecha, item, cantidad, unidad, tipo, responsable, detalles, stock;
        public final String itemLc, detallesLc, respLc;
        public final LocalDate localDate;

        public Mov(String fecha, String item, String cantidad, String unidad, String tipo,
                   String responsable, String detalles, String stock) {
            this.fecha = fecha; this.item = item; this.cantidad = cantidad; this.unidad = unidad;
            this.tipo = tipo; this.responsable = responsable; this.detalles = detalles; this.stock = stock;

            this.itemLc = item.toLowerCase();
            this.detallesLc = detalles.toLowerCase();
            this.respLc = responsable.toLowerCase();

            LocalDate ld = null;
            try { ld = LocalDate.parse(fecha.substring(0, 10)); } catch (Exception ignored) {}
            this.localDate = ld;
        }
    }
}
