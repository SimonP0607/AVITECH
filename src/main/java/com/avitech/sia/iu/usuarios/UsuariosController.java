package com.avitech.sia.iu.usuarios;

import com.avitech.sia.App;
import com.avitech.sia.iu.ModalUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Gestión de Usuarios — UI lista para conectarse a BD.
 * Diseñada para trabajar con un UsersDataSource (inyectable).
 */
public class UsuariosController implements UsesUsersDataSource {

    /* ======= Topbar / sidebar ======= */
    @FXML private Label lblSystemStatus, lblHeader, lblUserInfo;

    /* ======= Filtros / CTA ======= */
    @FXML private TextField tfSearch;
    @FXML private ComboBox<String> cbRol;
    @FXML private ComboBox<String> cbEstado;
    @FXML private Button btnNuevo;

    /* ======= KPIs ======= */
    @FXML private Label lblTotal, lblActivos, lblAdmins, lblConHoy;

    /* ======= Tabla ======= */
    @FXML private TableView<UserRow> tblUsuarios;
    @FXML private TableColumn<UserRow, String> colNombre;
    @FXML private TableColumn<UserRow, String> colUsuario;
    @FXML private TableColumn<UserRow, String> colRol;
    @FXML private TableColumn<UserRow, String> colEstado;
    @FXML private TableColumn<UserRow, String> colUltimoAcc;
    @FXML private TableColumn<UserRow, HBox>   colAcciones;

    /* ======= DataSource (inyectable) ======= */
    private UsersDataSource usersDS;

    /* ======= Datos en memoria ======= */
    private final ObservableList<UserRow> baseData = FXCollections.observableArrayList();
    private FilteredList<UserRow> filtered;
    private final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Permite inyectar el DataSource real (ej. implementación MySQL). */
    public void setUsersDataSource(UsersDataSource ds) { this.usersDS = ds; }

    @Override
    public UsersDataSource usersDs() {
        return usersDS;
    }

    @FXML
    private void initialize() {
        lblSystemStatus.setText("Sistema Offline – MySQL Local");
        lblHeader.setText("Administrador");
        lblUserInfo.setText("Administrador");

        colNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().nombre()));
        colUsuario.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().usuario()));
        colRol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().rol()));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().estado()));
        colUltimoAcc.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().ultimoAcceso() == null ? "—" : DF.format(d.getValue().ultimoAcceso())
        ));
        colAcciones.setCellValueFactory(d -> new SimpleObjectProperty<>(buildActions(d.getValue())));
        tblUsuarios.setPlaceholder(new Label("Sin usuarios para mostrar"));

        loadCatalogs();
        refreshTable();

        filtered = new FilteredList<>(baseData, r -> true);
        tfSearch.textProperty().addListener((obs, a, b) -> applyFilter());
        cbRol.valueProperty().addListener((obs, a, b) -> applyFilter());
        cbEstado.valueProperty().addListener((obs, a, b) -> applyFilter());
        tblUsuarios.setItems(filtered);

        lblTotal.textProperty().bind(Bindings.size(filtered).asString());
        lblActivos.textProperty().bind(Bindings.createStringBinding(
                () -> String.valueOf(filtered.stream().filter(u -> "Activo".equalsIgnoreCase(u.estado())).count()),
                filtered));
        lblAdmins.textProperty().bind(Bindings.createStringBinding(
                () -> String.valueOf(filtered.stream().filter(u -> "Administrador".equalsIgnoreCase(u.rol())).count()),
                filtered));
        lblConHoy.textProperty().bind(Bindings.createStringBinding(
                () -> String.valueOf(filtered.stream()
                        .filter(u -> u.ultimoAcceso() != null && u.ultimoAcceso().toLocalDate().equals(LocalDate.now()))
                        .count()),
                filtered));

        btnNuevo.setOnAction(e -> onNuevoUsuario());
    }

    private void loadCatalogs() {
        List<String> roles = (usersDs() != null)
                ? usersDs().listRoles()
                : List.of("Todos los roles", "Administrador", "Supervisor", "Operador");

        List<String> estados = (usersDs() != null)
                ? usersDs().listEstados()
                : List.of("Todos los estados", "Activo", "Inactivo");

        cbRol.setItems(FXCollections.observableArrayList(roles));
        cbEstado.setItems(FXCollections.observableArrayList(estados));
        if (!cbRol.getItems().isEmpty()) cbRol.getSelectionModel().selectFirst();
        if (!cbEstado.getItems().isEmpty()) cbEstado.getSelectionModel().selectFirst();
    }

    private void refreshTable() {
        baseData.clear();
        if (usersDs() != null) {
            List<UserRow> rows = usersDs().searchUsers(tfSearch.getText(), cbRol.getValue(), cbEstado.getValue());
            baseData.setAll(rows);
        }
    }

    private void applyFilter() {
        final String q = tfSearch.getText() == null ? "" : tfSearch.getText().trim().toLowerCase();
        final String rol = cbRol.getValue();
        final String est = cbEstado.getValue();

        filtered.setPredicate(u -> {
            boolean qOk = q.isEmpty()
                    || u.nombre().toLowerCase().contains(q)
                    || u.usuario().toLowerCase().contains(q);
            boolean rolOk = rol == null || rol.toLowerCase().startsWith("todos") || Objects.equals(rol, u.rol());
            boolean estOk = est == null || est.toLowerCase().startsWith("todos") || Objects.equals(est, u.estado());
            return qOk && rolOk && estOk;
        });
    }

    private HBox buildActions(UserRow row) {
        Button btnEdit = new Button("✏");
        Button btnPwd = new Button("🔑");
        Button btnDel = new Button("🗑");

        btnEdit.getStyleClass().addAll("ghostBtn", "small");
        btnPwd.getStyleClass().addAll("ghostBtn", "small");
        btnDel.getStyleClass().addAll("ghostBtn", "small");

        btnEdit.setOnAction(e -> onEditarUsuario(row));
        btnPwd.setOnAction(e -> onResetPassword(row));
        btnDel.setOnAction(e -> onEliminarUsuario(row));

        return new HBox(6, btnEdit, btnPwd, btnDel);
    }

    // ...
    @FXML
    private void onNuevoUsuario() {
        NuevoUsuarioController ctrl = ModalUtil.openModal(
                btnNuevo,
                "/fxml/usuarios/usuario_nuevo.fxml",
                "Crear Nuevo Usuario"
        );
        if (ctrl == null) return;

        if (usersDs() != null) {
            ctrl.setRoles(usersDs().listRoles());
            ctrl.setRoleDescriptions(usersDs().roleDescriptions());
            ctrl.setEstados(usersDs().listEstados());
        }

        if (ctrl.getResult() != null && usersDs() != null) {
            try {
                long id = usersDs().createUser(ctrl.getResult());
                System.out.println("Usuario creado con id=" + id);
                refreshTable();
                applyFilter();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Error creando usuario: " + ex.getMessage()).showAndWait();
            }
        }
    }

    private void onEditarUsuario(UserRow row) {
        System.out.println("Editar usuario: " + row.nombre());
    }

    private void onResetPassword(UserRow row) {
        if (usersDs() == null) {
            new Alert(Alert.AlertType.INFORMATION, "Demo: reset pwd para " + row.usuario()).showAndWait();
            return;
        }
        try {
            usersDs().resetPassword(row.id());
            new Alert(Alert.AlertType.INFORMATION, "Contraseña reiniciada para " + row.usuario()).showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Error reiniciando contraseña: " + ex.getMessage()).showAndWait();
        }
    }

    private void onEliminarUsuario(UserRow row) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar a " + row.nombre() + "?", ButtonType.OK, ButtonType.CANCEL);
        a.showAndWait();
        if (a.getResult() != ButtonType.OK) return;

        if (usersDs() == null) {
            baseData.remove(row);
            return;
        }
        try {
            usersDs().deleteUser(row.id());
            refreshTable();
            applyFilter();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Error eliminando usuario: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML private void goDashboard()  { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
    @FXML private void goSupplies()   { App.goTo("/fxml/suministros.fxml", "SIA Avitech — Suministros"); }
    @FXML private void goHealth()     { App.goTo("/fxml/sanidad/sanidad.fxml", "SIA Avitech — Sanidad"); }
    @FXML private void goProduction() { App.goTo("/fxml/produccion/produccion.fxml", "SIA Avitech — Producción"); }
    @FXML private void goReports()    { App.goTo("/fxml/reportes.fxml", "SIA Avitech — Reportes"); }
    @FXML private void goAlerts()     { App.goTo("/fxml/alertas.fxml", "SIA Avitech — Alertas"); }
    @FXML private void goAudit()      { App.goTo("/fxml/auditoria.fxml", "SIA Avitech — Auditoría"); }
    @FXML private void goParams()     { App.goTo("/fxml/parametros.fxml", "SIA Avitech — Parámetros"); }
    @FXML private void goUsers()      { App.goTo("/fxml/usuarios/usuarios.fxml", "SIA Avitech — Usuarios"); }
    @FXML private void goBackup()     { App.goTo("/fxml/respaldos.fxml", "SIA Avitech — Respaldos"); }
    @FXML private void onExit()       { App.goTo("/fxml/login.fxml", "SIA Avitech — Inicio de sesión"); }

    public record UserRow(long id,
                          String nombre,
                          String usuario,
                          String rol,
                          String estado,
                          LocalDateTime ultimoAcceso) {}

    public record UsuarioDTO(String nombre,
                             String usuario,
                             String estado,
                             String rol,
                             String rolDescripcion,
                             String password) {}
}
