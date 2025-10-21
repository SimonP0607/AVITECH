package com.avitech.sia.iu;

import com.avitech.sia.App;
import com.avitech.sia.db.AuditoriaDAO;
import com.avitech.sia.db.UsuarioDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.ArrayList;

public class AuditoriaController {

    /* ===== Sidebar / topbar ===== */
    @FXML private Label lblUserInfo;
    @FXML private Label lblSystemStatus;
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
    @FXML private TableView<AuditoriaDAO.AuditoriaDTO> tvAuditoria;
    @FXML private TableColumn<AuditoriaDAO.AuditoriaDTO, String> colUsuario;
    @FXML private TableColumn<AuditoriaDAO.AuditoriaDTO, String> colFecha;
    @FXML private TableColumn<AuditoriaDAO.AuditoriaDTO, String> colAccion;
    @FXML private TableColumn<AuditoriaDAO.AuditoriaDTO, String> colModulo;
    @FXML private TableColumn<AuditoriaDAO.AuditoriaDTO, String> colDetalle;
    @FXML private TableColumn<AuditoriaDAO.AuditoriaDTO, String> colVer;

    /* ===== Resúmenes ===== */
    @FXML private ListView<String> lvActividadModulo;
    @FXML private ListView<String> lvActividadUsuario;
    private final ObservableList<AuditoriaDAO.AuditoriaDTO> master = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Cabecera
        lblSystemStatus.setText("Sistema Offline – MySQL Local");
        lblHeader.setText("Administrador");

        // Filtros
        dpInicio.setValue(LocalDate.now().minusDays(7));
        dpFin.setValue(LocalDate.now());

        try {
            var usuarios = new ArrayList<String>();
            usuarios.add("Todos los usuarios");
            usuarios.addAll(UsuarioDAO.getAllUsernames());
            cboUsuario.setItems(FXCollections.observableArrayList(usuarios));
            cboUsuario.getSelectionModel().selectFirst();
        } catch (Exception e) {
            e.printStackTrace();
            cboUsuario.setItems(FXCollections.observableArrayList("Todos los usuarios"));
        }

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

        // Cargar datos desde la BD
        try {
            master.setAll(AuditoriaDAO.getAll());
            tvAuditoria.setItems(master);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudieron cargar los datos de auditoría: " + e.getMessage()).showAndWait();
        }

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

        ObservableList<AuditoriaDAO.AuditoriaDTO> filtered = master.filtered(it -> {
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
    @FXML private void goHealth()     { App.goTo("/fxml/sanidad.fxml", "SIA Avitech — Sanidad"); }
    @FXML private void goProduction() { App.goTo("/fxml/produccion.fxml", "SIA Avitech — Producción"); }
    @FXML private void goReports()    { App.goTo("/fxml/reportes.fxml", "SIA Avitech — Reportes"); }
    @FXML private void goAlerts()     { App.goTo("/fxml/alertas.fxml", "SIA Avitech — Alertas"); }
    @FXML private void goAudit()      { App.goTo("/fxml/auditoria.fxml", "SIA Avitech — Auditoría"); }
    @FXML private void goParams()     { App.goTo("/fxml/parametros.fxml", "SIA Avitech — Parámetros"); }
    @FXML private void goUsers()      { App.goTo("/fxml/usuarios.fxml", "SIA Avitech — Usuarios"); }
    @FXML private void goBackup()     { App.goTo("/fxml/respaldos.fxml", "SIA Avitech — Respaldos"); }
    @FXML private void onExit() {
        App.goTo("/fxml/login.fxml", "SIA Avitech — Inicio de sesión");
    }

    /* ====== Helpers ====== */

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

    private Callback<TableColumn<AuditoriaDAO.AuditoriaDTO, String>, TableCell<AuditoriaDAO.AuditoriaDTO, String>> makeVerFuenteCell() {
        return col -> new TableCell<>() {
            private final Button btn = new Button("Ver Fuente");
            {
                btn.getStyleClass().add("ghostBtn");
                btn.setOnAction(e -> {
                    AuditoriaDAO.AuditoriaDTO dto = getTableView().getItems().get(getIndex());
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

    private void onVerFuente(AuditoriaDAO.AuditoriaDTO dto) {
        // Placeholder: en real, abrir modal con detalle o navegar a la entidad
        System.out.println("Ver fuente → " + dto.referencia());
    }
}
