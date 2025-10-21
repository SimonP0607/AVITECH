package com.avitech.sia.iu;

import com.avitech.sia.App;
import com.avitech.sia.db.RespaldosDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controlador base para la vista de Respaldos.
 * - Navegación lista
 * - KPIs y banner con valores dummy
 * - Tabla con acciones (descargar/restaurar/borrar)
 * Conectar a la BD más adelante.
 */
public class RespaldosController {

    // topbar
    @FXML private Label lblSystemStatus;
    @FXML private Label lblHeader;
    @FXML private Label lblUserInfo;

    // banner auto
    @FXML private Label lblAutoUltimo, lblAutoProximo, lblAutoTasa, lblAutoEstado;

    // KPIs
    @FXML private Label kpiTotal, kpiOk, kpiErr, kpiSpace;

    // tabla
    @FXML private TableView<RespaldosDAO.RespaldoDTO> tblBackups;
    @FXML private TableColumn<RespaldosDAO.RespaldoDTO, String> colArchivo, colFecha, colTipo, colTam, colEstado, colAcciones;

    private final ObservableList<RespaldosDAO.RespaldoDTO> master = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
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
            private RespaldosDAO.RespaldoDTO getItemAtRow() { return getIndex() >= 0 && getIndex() < tblBackups.getItems().size()
                    ? tblBackups.getItems().get(getIndex()) : null; }
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                setGraphic(empty ? null : box);
                setText(null);
            }
        });

        try {
            master.setAll(RespaldosDAO.getAll());
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudieron cargar los respaldos: " + e.getMessage()).showAndWait();
        }

        tblBackups.setItems(master);
        refreshKpis();
        refreshBanner();
    }

    /* ==================== NAV ==================== */
    @FXML private void onExit()        { App.goTo("/fxml/login.fxml",            "SIA Avitech — Inicio de sesión"); }
    @FXML private void goDashboard()   { App.goTo("/fxml/dashboard_admin.fxml",  "SIA Avitech — ADMIN"); }
    @FXML private void goSupplies()    { App.goTo("/fxml/suministros.fxml",      "SIA Avitech — Suministros"); }
    @FXML private void goHealth()      { App.goTo("/fxml/sanidad.fxml",          "SIA Avitech — Sanidad"); }
    @FXML private void goProduction()  { App.goTo("/fxml/produccion.fxml",       "SIA Avitech — Producción"); }
    @FXML private void goReports()     { App.goTo("/fxml/reportes.fxml",         "SIA Avitech — Reportes"); }
    @FXML private void goAlerts()      { App.goTo("/fxml/alertas.fxml",          "SIA Avitech — Alertas"); }
    @FXML private void goAudit()       { App.goTo("/fxml/auditoria.fxml",        "SIA Avitech — Auditoría"); }
    @FXML private void goParams()      { App.goTo("/fxml/parametros_unidades.fxml", "SIA Avitech — Parámetros"); }
    @FXML private void goUsers()       { App.goTo("/fxml/usuarios.fxml",         "SIA Avitech — Usuarios"); }

    /* ==================== Acciones Generales ==================== */
    @FXML private void onNuevoRespaldo() {
        // dummy: agrega un registro “en progreso/completado”
        // En una app real, esto iniciaría un proceso en segundo plano y luego actualizaría la UI.
        new Alert(Alert.AlertType.INFORMATION, "Iniciando nuevo respaldo manual (placeholder)...").showAndWait();
        // onActualizar(); // Se llamaría al finalizar el proceso

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
    private void onDescargar(RespaldosDAO.RespaldoDTO it) { if (it != null) info("Descargando: " + it.archivo()); }
    private void onRestaurar(RespaldosDAO.RespaldoDTO it) { if (it != null) info("Restaurando: " + it.archivo()); }
    private void onBorrar(RespaldosDAO.RespaldoDTO it) {
        if (it == null) return;
        try {
            RespaldosDAO.deleteById(it.id());
            master.remove(it);
            refreshKpis();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo borrar el respaldo: " + e.getMessage()).showAndWait();
        }
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
}