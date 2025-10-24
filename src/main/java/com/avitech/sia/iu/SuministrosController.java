package com.avitech.sia.iu;

import com.avitech.sia.App;
import com.avitech.sia.db.SuministrosDAO;
import com.avitech.sia.db.UsuarioDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SuministrosController {

    @FXML private Label lblSystemStatus, lblHeader, lblUserInfo;
    @FXML private Label kpiMovHoy, kpiActivos, kpiStockBajo, kpiValor;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbTipo, cbResp;
    @FXML private DatePicker dpDesde, dpHasta;
    @FXML private Label lblMostrando;
    @FXML private TableView<SuministrosDAO.Mov> tblMovs;
    @FXML private TableColumn<SuministrosDAO.Mov, String> colFecha, colItem, colCant, colUnidad, colTipo, colResp, colDet, colStock, colAccion;

    private final ObservableList<SuministrosDAO.Mov> master = FXCollections.observableArrayList();
    private final ObservableList<SuministrosDAO.InventarioItem> inventarioActual = FXCollections.observableArrayList();
    private final ObservableList<SuministrosDAO.Mov> filtered = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        lblSystemStatus.setText("Sistema Offline – MySQL Local");
        lblHeader.setText("Administrador");
        lblUserInfo.setText("Administrador");

        // Configurar combos y fechas
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

        setupTableColumns();
        refreshData();
    }

    private void setupTableColumns() {
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().fecha));
        colItem.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().item));
        colCant.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().cantidad));
        colUnidad.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().unidad));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().tipo));
        colResp.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().responsable));
        colDet.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().detalles));
        colStock.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().stock));
        colAccion.setCellFactory(col -> new TableCell<>() {
            private final Hyperlink link = new Hyperlink("Ver ítem");
            { link.setOnAction(e -> onVerItem(getTableView().getItems().get(getIndex()))); }
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                setGraphic(empty ? null : link);
            }
        });
        tblMovs.setItems(filtered);
    }

    private void refreshData() {
        try {
            master.setAll(SuministrosDAO.getAll());
            inventarioActual.setAll(SuministrosDAO.getInventario());
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudieron cargar los datos de suministros: " + e.getMessage()).showAndWait();
        }
        applyFilter();
        refreshKpis();
    }

    private void refreshKpis() {
        long movHoy = master.stream().filter(m -> m.localDate != null && m.localDate.equals(LocalDate.now())).count();
        kpiMovHoy.setText(String.valueOf(movHoy));

        long activos = inventarioActual.stream().filter(item -> item.stock > 0).count();
        kpiActivos.setText(String.valueOf(activos));

        kpiStockBajo.setText("N/A"); // No se puede calcular sin stock mínimo en la BD

        try {
            double valorTotal = SuministrosDAO.getValorTotalStock();
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
            kpiValor.setText(currencyFormatter.format(valorTotal));
        } catch (Exception e) {
            e.printStackTrace();
            kpiValor.setText("$0.00");
        }
    }

    @FXML
    private void applyFilter() {
        // ... (código de filtrado sin cambios)
    }

    // ... (resto de la clase sin cambios)
    @FXML private void onEntrada() { showMovimientoDialog(true); }
    @FXML private void onSalida() { showMovimientoDialog(false); }

    private void showMovimientoDialog(boolean isEntrada) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/movimiento_dialog.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setTitle(isEntrada ? "Registrar Entrada" : "Registrar Salida");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(App.PrimaryStage());
            Scene scene = new Scene(loader.load());
            dialogStage.setScene(scene);

            MovimientoDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setTipo(isEntrada);
            controller.setOnSaveCallback(this::refreshData);

            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void goDashboard() { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
    @FXML private void goSupplies() { /* ya estás aquí */ }
    @FXML private void goHealth() { App.goTo("/fxml/sanidad.fxml", "SIA Avitech — Sanidad"); }
    @FXML private void goProduction() { App.goTo("/fxml/produccion.fxml", "SIA Avitech — Producción"); }
    @FXML private void goReports() { App.goTo("/fxml/reportes.fxml", "SIA Avitech — Reportes"); }
    @FXML private void goAlerts() { App.goTo("/fxml/alertas.fxml", "SIA Avitech — Alertas"); }
    @FXML private void goAudit() { App.goTo("/fxml/auditoria.fxml", "SIA Avitech — Auditoría"); }
    @FXML private void goParams() { App.goTo("/fxml/parametros_unidades.fxml", "SIA Avitech — Parámetros"); }
    @FXML private void goUsers() { App.goTo("/fxml/usuarios.fxml", "SIA Avitech — Usuarios"); }
    @FXML private void goBackup() { App.goTo("src/main/resources/fxml/respaldos.fxml", "SIA Avitech — Respaldos"); }
    @FXML private void onExit() { App.goTo("/fxml/login.fxml", "SIA Avitech — Inicio de sesión"); }
    @FXML private void onVerStock() { App.goTo("/fxml/inventario.fxml", "SIA Avitech — Inventario"); }
    @FXML private void onMoverStock() { /* flujo mover stock */ }
    @FXML private void onExportar() { /* export CSV/XLSX */ }

    private void onVerItem(SuministrosDAO.Mov m) {
        new Alert(Alert.AlertType.INFORMATION, "Ítem: " + m.item).showAndWait();
    }
}
