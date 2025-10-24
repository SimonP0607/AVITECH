package com.avitech.sia.iu;

import com.avitech.sia.App;
import com.avitech.sia.db.SuministrosDAO;
import com.avitech.sia.db.UsuarioDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.ArrayList;

/** Controlador base de Suministros: navegación lista, filtros dummy y tabla. */
public class SuministrosController {

    /* topbar / sidebar */
    @FXML private Label lblSystemStatus;
    @FXML private Label lblHeader;
    @FXML private Label lblUserInfo;

    /* KPIs */
    @FXML private Label kpiMovHoy, kpiActivos, kpiStockBajo, kpiValor;

    /* filtros */
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbTipo, cbResp;
    @FXML private DatePicker dpDesde, dpHasta;
    @FXML private Label lblMostrando;

    /* tabla */
    @FXML private TableView<SuministrosDAO.Mov> tblMovs;
    @FXML private TableColumn<SuministrosDAO.Mov, String> colFecha, colItem, colCant, colUnidad, colTipo, colResp, colDet, colStock, colAccion;

    private final ObservableList<SuministrosDAO.Mov> master = FXCollections.observableArrayList();
    private final ObservableList<SuministrosDAO.Mov> filtered = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        lblSystemStatus.setText("Sistema Offline – MySQL Local");
        lblHeader.setText("Administrador");
        lblUserInfo.setText("Administrador");

        /* combos / fechas */
        cbTipo.setItems(FXCollections.observableArrayList("Todos", "Entrada", "Salida"));
        cbTipo.getSelectionModel().selectFirst();
        try {
            var usuarios = new ArrayList<String>();
            usuarios.add("Todos");
            usuarios.addAll(UsuarioDAO.getAllUsernames());
            cbResp.setItems(FXCollections.observableArrayList(usuarios));
        } catch (Exception e) {
            e.printStackTrace();
            cbResp.setItems(FXCollections.observableArrayList("Todos"));
        }
        cbResp.getSelectionModel().selectFirst();
        dpDesde.setValue(LocalDate.now().minusDays(7));
        dpHasta.setValue(LocalDate.now());

        /* columnas */
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().fecha));
        colItem.setCellValueFactory(c  -> new SimpleStringProperty(c.getValue().item));
        colCant.setCellValueFactory(c  -> new SimpleStringProperty(c.getValue().cantidad));
        colUnidad.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().unidad));
        colTipo.setCellValueFactory(c  -> new SimpleStringProperty(c.getValue().tipo));
        colResp.setCellValueFactory(c  -> new SimpleStringProperty(c.getValue().responsable));
        colDet.setCellValueFactory(c   -> new SimpleStringProperty(c.getValue().detalles));
        colStock.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().stock));

        /* botón en columna Acciones */
        colAccion.setCellValueFactory(c -> new SimpleStringProperty("ver"));
        colAccion.setCellFactory(col -> new TableCell<>() {
            private final Hyperlink link = new Hyperlink("Ver ítem");
            { link.setOnAction(e -> onVerItem(getTableView().getItems().get(getIndex()))); }
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                setGraphic(empty ? null : link);
                setText(null);
            }
        });

        try {
            master.setAll(SuministrosDAO.getAll());
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudieron cargar los movimientos: " + e.getMessage()).showAndWait();
        }

        applyFilter();// primer filtrado
        refreshKpis();// KPIs
    }

    /* ======= Navegación ======= */
    @FXML private void goDashboard()  { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
    @FXML private void goSupplies()   { /* ya estás aquí */ }
    @FXML private void goHealth()     { App.goTo("/fxml/sanidad.fxml", "SIA Avitech — Sanidad"); }
    @FXML private void goProduction() { App.goTo("/fxml/produccion.fxml", "SIA Avitech — Producción"); }
    @FXML private void goReports()    { App.goTo("/fxml/reportes.fxml", "SIA Avitech — Reportes"); }
    @FXML private void goAlerts()     { App.goTo("/fxml/alertas.fxml", "SIA Avitech — Alertas"); }
    @FXML private void goAudit()      { App.goTo("/fxml/auditoria.fxml", "SIA Avitech — Auditoría"); }
    @FXML private void goParams()     { App.goTo("/fxml/parametros_unidades.fxml", "SIA Avitech — Parámetros"); }
    @FXML private void goUsers()      { App.goTo("/fxml/usuarios.fxml", "SIA Avitech — Usuarios"); }
    @FXML private void goBackup()     { App.goTo("/fxml/respaldos.fxml", "SIA Avitech — Respaldos"); }
    @FXML private void onExit()       { App.goTo("/fxml/login.fxml", "SIA Avitech — Inicio de sesión"); }

    /* ======= Acciones ======= */
    @FXML private void onEntrada()   { /* abrir modal entrada */ }
    @FXML private void onSalida()    { /* abrir modal salida  */ }
    @FXML private void onVerStock()  { App.goTo("/fxml/inventario.fxml", "SIA Avitech — Inventario"); }
    @FXML private void onMoverStock(){ /* flujo mover stock   */ }
    @FXML private void onExportar()  { /* export CSV/XLSX     */ }

    private void onVerItem(SuministrosDAO.Mov m) {
        // En real: abrir detalle del ítem (o navegar a inventario con el ID)
        new Alert(Alert.AlertType.INFORMATION, "Ítem: " + m.item).showAndWait();
    }

    /* ======= Filtros ======= */
    @FXML
    private void applyFilter() {
        final String t = lower(txtSearch.getText());
        final String tipo = sel(cbTipo);
        final String resp = sel(cbResp);
        final LocalDate d1 = dpDesde.getValue();
        final LocalDate d2 = dpHasta.getValue();

        filtered.setAll(master.filtered(m ->
                (t.isEmpty() || m.itemLc.contains(t) || m.detallesLc.contains(t) || m.respLc.contains(t)) &&
                        ("Todos".equals(tipo) || m.tipo.equalsIgnoreCase(tipo)) &&
                        ("Todos".equals(resp) || m.responsable.equalsIgnoreCase(resp)) &&
                        (between(m.localDate, d1, d2))
        ));

        tblMovs.setItems(filtered);
        lblMostrando.setText(filtered.size() + " ítems");
    }

    private static boolean between(LocalDate f, LocalDate d1, LocalDate d2) {
        if (f == null) return true;
        boolean ok1 = (d1 == null) || !f.isBefore(d1);
        boolean ok2 = (d2 == null) || !f.isAfter(d2);
        return ok1 && ok2;
    }
    private static String lower(String s) { return s == null ? "" : s.toLowerCase().trim(); }
    private static String sel(ComboBox<String> cb) {
        String s = cb.getSelectionModel().getSelectedItem();
        return s == null ? "Todos" : s;
    }

    /* ======= KPIs ======= */
    private void refreshKpis() {
        long hoy = master.stream().filter(m -> m.fecha.startsWith(LocalDate.now().toString())).count();
        long activos = 5; // valor dummy
        long bajos = master.stream().filter(m -> m.tipo.equalsIgnoreCase("Salida")).count(); // ilustrativo
        String valor = "$3,339.05"; // dummy

        kpiMovHoy.setText(String.valueOf(hoy));
        kpiActivos.setText(String.valueOf(activos));
        kpiStockBajo.setText(String.valueOf(bajos));
        kpiValor.setText(valor);
    }

}
