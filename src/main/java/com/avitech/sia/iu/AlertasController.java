package com.avitech.sia.iu;

import com.avitech.sia.App; // usa el mismo helper de navegación que ya tienes
import com.avitech.sia.db.AlertasDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller base para la vista de Alertas.
 * Deja lista la navegación, filtros mínimos y la tabla con una columna de acción.
 * La carga real desde BD se conecta más adelante.
 */
public class AlertasController {

    /* ====== sidebar / topbar ====== */
    @FXML private Label lblSystemStatus;
    @FXML private Label lblUserInfo;
    @FXML private Label lblHeader;

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
    @FXML private TableView<AlertasDAO.ItemAlerta> tblAlertas;
    @FXML private TableColumn<AlertasDAO.ItemAlerta, String> colArticulo;
    @FXML private TableColumn<AlertasDAO.ItemAlerta, String> colCategoria;
    @FXML private TableColumn<AlertasDAO.ItemAlerta, String> colStockActual;
    @FXML private TableColumn<AlertasDAO.ItemAlerta, String> colStockMin;
    @FXML private TableColumn<AlertasDAO.ItemAlerta, String> colPorcentaje;
    @FXML private TableColumn<AlertasDAO.ItemAlerta, String> colDias;
    @FXML private TableColumn<AlertasDAO.ItemAlerta, String> colUbicacion;
    @FXML private TableColumn<AlertasDAO.ItemAlerta, String> colAccion;

    @FXML private Label lblCantidadTabla;
    @FXML private Label lblActualizado;
    @FXML private Label lblResCritico, lblResBajo, lblResAdvertencia;
    @FXML private Label lblBanner;

    private final ObservableList<AlertasDAO.ItemAlerta> master = FXCollections.observableArrayList();
    private final ObservableList<AlertasDAO.ItemAlerta> filtered = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        lblSystemStatus.setText("Sistema Offline – MySQL Local");
        lblHeader.setText("Administrador");

        // Combos dummy
        cbCategoria.setItems(FXCollections.observableArrayList("Todas", "Alimentos", "Medicamentos", "Limpieza", "Vacunas", "Suplem."));
        cbCriticidad.setItems(FXCollections.observableArrayList("Todas", "Crítico", "Bajo", "Advertencia"));
        cbUbicacion.setItems(FXCollections.observableArrayList("Todas", "Almacén Principal", "Farmacia", "Almacén Secundario", "Patio de Materiales"));

        cbCategoria.getSelectionModel().selectFirst();
        cbCriticidad.getSelectionModel().selectFirst();
        cbUbicacion.getSelectionModel().selectFirst();

        // Tabla
        colArticulo.setCellValueFactory(new PropertyValueFactory<>("articulo"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colStockActual.setCellValueFactory(new PropertyValueFactory<>("stockActual")); // Muestra "N/A"
        colStockMin.setCellValueFactory(new PropertyValueFactory<>("stockMinimo")); // Muestra "N/A"
        colPorcentaje.setCellValueFactory(new PropertyValueFactory<>("porcentaje")); // Muestra "N/A"
        colDias.setCellValueFactory(new PropertyValueFactory<>("diasRestantes")); // Muestra "N/A"
        colUbicacion.setCellValueFactory(new PropertyValueFactory<>("ubicacion")); // Muestra "N/A"

        // Columna de acción con botón "Ir al ítem"
        colAccion.setCellValueFactory(param -> new SimpleStringProperty("Ir al ítem"));
        colAccion.setCellFactory(col -> new TableCell<>() {
            private final Hyperlink link = new Hyperlink("Resolver");
            { link.setOnAction(e -> onGoItem(getTableView().getItems().get(getIndex()))); }
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                setGraphic(empty ? null : link);
                setText(null);
            }
        });

        // Cargar datos desde la BD
        try {
            master.setAll(AlertasDAO.getAll());
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudieron cargar las alertas: " + e.getMessage()).showAndWait();
        }

        // Mostrar filtrado inicial
        applyFilter();

        // KPIs
        refreshKpis();

        // Fecha de actualización
        lblActualizado.setText("Actualizado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
    }

    /* ================== Navegación ================== */
    @FXML private void goDashboard()  { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
    @FXML private void goSupplies()   { App.goTo("/fxml/suministros.fxml", "SIA Avitech — Suministros"); }
    @FXML private void goHealth()     { App.goTo("/fxml/sanidad.fxml", "SIA Avitech — Sanidad"); }
    @FXML private void goProduction() { App.goTo("/fxml/produccion.fxml", "SIA Avitech — Producción"); }
    @FXML private void goReports()    { App.goTo("/fxml/reportes.fxml", "SIA Avitech — Reportes"); }
    @FXML private void goAlerts()     { App.goTo("/fxml/alertas.fxml", "SIA Avitech — Alertas"); }
    @FXML private void goAudit()      { App.goTo("/fxml/auditoria.fxml", "SIA Avitech — Auditoría"); }
    @FXML private void goParams()     { App.goTo("/fxml/parametros_unidades.fxml", "SIA Avitech — Parámetros"); }
    @FXML private void goUsers()      { App.goTo("/fxml/usuarios.fxml", "SIA Avitech — Usuarios"); }
    @FXML private void goBackup()     { App.goTo("/fxml/respaldos.fxml", "SIA Avitech — Respaldos"); }
    @FXML private void onExit() {      App.goTo("/fxml/login.fxml", "SIA Avitech — Inicio de sesión");}

    /* ================== Acciones UI ================== */

    @FXML
    private void onRefresh() {
        // En real: recargar desde servicio/DAO y recalcular KPIs.
        applyFilter();
        refreshKpis();
        lblActualizado.setText("Actualizado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
    }

    @FXML
    private void onVerCriticos() {
        cbCriticidad.getSelectionModel().select("Crítico");
        applyFilter();
    }

    @FXML
    private void onAplicarFiltros() { applyFilter(); }

    @FXML
    private void onLimpiarFiltros() {
        txtSearch.clear();
        cbCategoria.getSelectionModel().selectFirst();
        cbCriticidad.getSelectionModel().selectFirst();
        cbUbicacion.getSelectionModel().selectFirst();
        applyFilter();
    }

    private void onGoItem(AlertasDAO.ItemAlerta item) {
        // En real: navegación al detalle del artículo en Suministros con el ID.
        new Alert(Alert.AlertType.INFORMATION, "Resolviendo alerta (placeholder): " + item.getDescripcion()).showAndWait();
    }

    /* ================== Lógica local (dummy) ================== */

    private void applyFilter() {
        String text = txtSearch.getText() == null ? "" : txtSearch.getText().toLowerCase().trim();
        String cat  = sel(cbCategoria);
        String cri  = sel(cbCriticidad);
        String ubi  = sel(cbUbicacion);

        filtered.setAll(master.filtered(it ->
                (text.isEmpty() || it.getDescripcion().toLowerCase().contains(text) || it.getCategoria().toLowerCase().contains(text)) &&
                        (cat.equals("Todas") || it.getCategoria().equalsIgnoreCase(cat)) &&
                        (cri.equals("Todas") || it.getTipo().equalsIgnoreCase(cri)) &&
                        (ubi.equals("Todas") /*|| it.getUbicacion().equalsIgnoreCase(ubi)*/) // Ubicación no está en la tabla Alertas
        ));

        tblAlertas.setItems(filtered);
        lblCantidadTabla.setText("(" + filtered.size() + ")");
    }

    private String sel(ComboBox<String> cb) {
        var s = cb.getSelectionModel().getSelectedItem();
        return (s == null) ? "Todas" : s;
    }

    private void refreshKpis() {
        long crit = master.stream().filter(i -> i.getTipo().equalsIgnoreCase("Crítico")).count();
        long bajo = master.stream().filter(i -> i.getTipo().equalsIgnoreCase("Bajo")).count();
        long adv  = master.stream().filter(i -> i.getTipo().equalsIgnoreCase("Advertencia")).count();

        kpiCritico.setText(String.valueOf(crit));
        kpiBajo.setText(String.valueOf(bajo));
        kpiAdv.setText(String.valueOf(adv));
        kpiTotal.setText(String.valueOf(master.size()));

        lblResCritico.setText("Crítico — " + crit + " artículos");
        lblResBajo.setText("Bajo — " + bajo + " artículos");
        lblResAdvertencia.setText("Advertencia — " + adv + " artículos");

        lblBanner.setText(crit + " artículo(s) en stock crítico requieren atención inmediata");
    }
}
