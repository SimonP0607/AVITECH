package com.avitech.sia.iu;

import com.avitech.sia.App;
import com.avitech.sia.db.UsuarioDAO;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class UsuariosController {

    // Topbar
    @FXML private Label lblSystemStatus;
    @FXML private Label lblHeader;
    @FXML private Label lblUserInfo;

    // Filtros y CTA
    @FXML private TextField tfSearch;
    @FXML private ComboBox<String> cbRol;
    @FXML private ComboBox<String> cbEstado;
    @FXML private Button btnNuevo;

    // KPIs
    @FXML private Label lblTotal;
    @FXML private Label lblActivos;
    @FXML private Label lblAdmins;
    @FXML private Label lblConHoy;

    // Tabla
    @FXML private TableView<UsuarioDAO.Usuario> tblUsuarios;
    @FXML private TableColumn<UsuarioDAO.Usuario, String> colNombre;
    @FXML private TableColumn<UsuarioDAO.Usuario, String> colUsuario;
    @FXML private TableColumn<UsuarioDAO.Usuario, String> colRol;
    @FXML private TableColumn<UsuarioDAO.Usuario, String> colEstado;
    @FXML private TableColumn<UsuarioDAO.Usuario, String> colUltimoAcc;
    @FXML private TableColumn<UsuarioDAO.Usuario, HBox>   colAcciones;

    // Datos
    private final ObservableList<UsuarioDAO.Usuario> baseData = FXCollections.observableArrayList();
    private FilteredList<UsuarioDAO.Usuario> filtered;

    private final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private void initialize() {
        lblSystemStatus.setText("Sistema Offline – MySQL Local");
        lblHeader.setText("Administrador");
        lblUserInfo.setText("Administrador");

        // Opciones filtros
        cbRol.setItems(FXCollections.observableArrayList("Todos los roles", "Administrador", "Supervisor", "Operador"));
        cbEstado.setItems(FXCollections.observableArrayList("Todos los estados", "Activo", "Inactivo"));
        cbRol.getSelectionModel().selectFirst();
        cbEstado.getSelectionModel().selectFirst();

        // Columnas
        colNombre.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getNombre()));
        colUsuario.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getUsuario()));
        colRol.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getRol()));
        colEstado.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getEstado()));
        colUltimoAcc.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getUltimoAcceso() != null ? DF.format(d.getValue().getUltimoAcceso().toLocalDateTime()) : "N/A"));

        colAcciones.setCellValueFactory(d -> new SimpleObjectProperty<>(buildActions(d.getValue())));

        // Cargar datos
        loadUserData();

        // Filtrado
        filtered = new FilteredList<>(baseData, r -> true);
        tfSearch.textProperty().addListener((obs, a, b) -> applyFilter());
        cbRol.valueProperty().addListener((obs, a, b) -> applyFilter());
        cbEstado.valueProperty().addListener((obs, a, b) -> applyFilter());

        tblUsuarios.setItems(filtered);

        // KPIs atados
        lblTotal.textProperty().bind(Bindings.size(filtered).asString());
        lblActivos.textProperty().bind(Bindings.createStringBinding(
                () -> String.valueOf(filtered.stream().filter(u -> "Activo".equals(u.getEstado())).count()),
                filtered));
        lblAdmins.textProperty().bind(Bindings.createStringBinding(
                () -> String.valueOf(filtered.stream().filter(u -> "Administrador".equals(u.getRol())).count()),
                filtered));
        // Conectados hoy (demo: usuarios con último acceso del día actual)
        lblConHoy.textProperty().bind(Bindings.createStringBinding(
                () -> String.valueOf(filtered.stream().filter(u -> u.getUltimoAcceso() != null && u.getUltimoAcceso().toLocalDateTime().toLocalDate().equals(LocalDateTime.now().toLocalDate())).count()),
                filtered));

        // CTA nuevo
        btnNuevo.setOnAction(e -> onNuevoUsuario());
    }

    private void loadUserData() {
        try {
            baseData.setAll(UsuarioDAO.getAll());
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "No se pudieron cargar los usuarios: " + e.getMessage()).showAndWait();
        }
    }

    private void applyFilter() {
        String q = tfSearch.getText() == null ? "" : tfSearch.getText().trim().toLowerCase();
        String rol = cbRol.getValue();
        String est = cbEstado.getValue();

        filtered.setPredicate(u -> {
            boolean qOk = q.isEmpty()
                    || u.getNombre().toLowerCase().contains(q)
                    || u.getUsuario().toLowerCase().contains(q);
            boolean rolOk = rol == null || rol.equals("Todos los roles") || Objects.equals(rol, u.getRol());
            boolean estOk = est == null || est.equals("Todos los estados") || Objects.equals(est, u.getEstado());
            return qOk && rolOk && estOk;
        });
    }

    private HBox buildActions(UsuarioDAO.Usuario row) {
        Button btnEdit = new Button("✏");
        Button btnPwd  = new Button("🔑");
        Button btnDel  = new Button("🗑");

        btnEdit.getStyleClass().add("ghostBtn");
        btnPwd.getStyleClass().add("ghostBtn");
        btnDel.getStyleClass().add("ghostBtn");

        btnEdit.setOnAction(e -> onEditUsuario(row));
        btnPwd.setOnAction(e -> System.out.println("Credenciales: " + row.getNombre()));
        btnDel.setOnAction(e -> onDeleteUsuario(row));

        HBox box = new HBox(6, btnEdit, btnPwd, btnDel);
        return box;
    }

    private void onNuevoUsuario() {
        // TODO: Abrir modal de creación de usuario
        System.out.println("Nuevo Usuario… (abrir modal)");
    }

    private void onEditUsuario(UsuarioDAO.Usuario usuario) {
        // TODO: Abrir modal de edición de usuario
        System.out.println("Editar: " + usuario.getNombre());
    }

    private void onDeleteUsuario(UsuarioDAO.Usuario usuario) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Estás seguro de que quieres eliminar a " + usuario.getNombre() + "?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    UsuarioDAO.eliminar(usuario.getId());
                    loadUserData();
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR, "No se pudo eliminar el usuario: " + e.getMessage()).showAndWait();
                }
            }
        });
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
}
