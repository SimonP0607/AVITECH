package com.avitech.sia.iu;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
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
    @FXML private TableView<UserRow> tblUsuarios;
    @FXML private TableColumn<UserRow, String> colNombre;
    @FXML private TableColumn<UserRow, String> colUsuario;
    @FXML private TableColumn<UserRow, String> colRol;
    @FXML private TableColumn<UserRow, String> colEstado;
    @FXML private TableColumn<UserRow, String> colUltimoAcc;
    @FXML private TableColumn<UserRow, HBox>   colAcciones;

    // Datos
    private final ObservableList<UserRow> baseData = FXCollections.observableArrayList();
    private FilteredList<UserRow> filtered;

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
        colNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().nombre()));
        colUsuario.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().usuario()));
        colRol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().rol()));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().estado()));
        colUltimoAcc.setCellValueFactory(d -> new SimpleStringProperty(DF.format(d.getValue().ultimoAcceso())));

        colAcciones.setCellValueFactory(d -> new SimpleObjectProperty<>(buildActions(d.getValue())));

        // Datos demo (reemplazar cuando conectes BD)
        seedDemo();

        // Filtrado
        filtered = new FilteredList<>(baseData, r -> true);
        tfSearch.textProperty().addListener((obs, a, b) -> applyFilter());
        cbRol.valueProperty().addListener((obs, a, b) -> applyFilter());
        cbEstado.valueProperty().addListener((obs, a, b) -> applyFilter());

        tblUsuarios.setItems(filtered);

        // KPIs atados
        lblTotal.textProperty().bind(Bindings.size(filtered).asString());
        lblActivos.textProperty().bind(Bindings.createStringBinding(
                () -> String.valueOf(filtered.stream().filter(u -> "Activo".equals(u.estado())).count()),
                filtered));
        lblAdmins.textProperty().bind(Bindings.createStringBinding(
                () -> String.valueOf(filtered.stream().filter(u -> "Administrador".equals(u.rol())).count()),
                filtered));
        // Conectados hoy (demo: usuarios con último acceso del día actual)
        lblConHoy.textProperty().bind(Bindings.createStringBinding(
                () -> String.valueOf(filtered.stream().filter(u -> u.ultimoAcceso().toLocalDate().equals(LocalDateTime.now().toLocalDate())).count()),
                filtered));

        // CTA nuevo
        btnNuevo.setOnAction(e -> onNuevoUsuario());
    }

    private void seedDemo() {
        baseData.setAll(
                new UserRow("María González",     "@mgonzalez", "Administrador", "Activo",   LocalDateTime.now().withHour(20).withMinute(42)),
                new UserRow("Carlos Pérez",        "@cperez",     "Supervisor",    "Activo",   LocalDateTime.now().withHour(15).withMinute(15)),
                new UserRow("Ana Rodríguez",       "@arodriguez", "Operador",      "Activo",   LocalDateTime.now().withHour(8).withMinute(15)),
                new UserRow("Dr. Luis Morales",    "@lmorales",   "Supervisor",    "Activo",   LocalDateTime.now().minusDays(1).withHour(10).withMinute(25)),
                new UserRow("Elena Vargas",        "@evargas",    "Operador",      "Inactivo", LocalDateTime.now().minusDays(5).withHour(11).withMinute(30))
        );
    }

    private void applyFilter() {
        String q = tfSearch.getText() == null ? "" : tfSearch.getText().trim().toLowerCase();
        String rol = cbRol.getValue();
        String est = cbEstado.getValue();

        filtered.setPredicate(u -> {
            boolean qOk = q.isEmpty()
                    || u.nombre().toLowerCase().contains(q)
                    || u.usuario().toLowerCase().contains(q);
            boolean rolOk = rol == null || rol.equals("Todos los roles") || Objects.equals(rol, u.rol());
            boolean estOk = est == null || est.equals("Todos los estados") || Objects.equals(est, u.estado());
            return qOk && rolOk && estOk;
        });
    }

    private HBox buildActions(UserRow row) {
        Button btnEdit = new Button("✏");
        Button btnPwd  = new Button("🔑");
        Button btnDel  = new Button("🗑");

        btnEdit.getStyleClass().add("ghostBtn");
        btnPwd.getStyleClass().add("ghostBtn");
        btnDel.getStyleClass().add("ghostBtn");

        btnEdit.setOnAction(e -> System.out.println("Editar: " + row.nombre()));
        btnPwd.setOnAction(e -> System.out.println("Credenciales: " + row.nombre()));
        btnDel.setOnAction(e -> System.out.println("Eliminar: " + row.nombre()));

        HBox box = new HBox(6, btnEdit, btnPwd, btnDel);
        return box;
    }

    private void onNuevoUsuario() {
        // Base: por ahora log. Luego podrás abrir tu modal de creación.
        System.out.println("Nuevo Usuario… (abrir modal)");
    }

    /* ===== Navegación sidebar (stubs, ya los conectas con App.goTo o tu router) ===== */
    @FXML private void goDashboard()  { System.out.println("Ir: Tablero"); }
    @FXML private void goSupplies()   { System.out.println("Ir: Suministros"); }
    @FXML private void goHealth()     { System.out.println("Ir: Sanidad"); }
    @FXML private void goProduction() { System.out.println("Ir: Producción"); }
    @FXML private void goReports()    { System.out.println("Ir: Reportes"); }
    @FXML private void goAlerts()     { System.out.println("Ir: Alertas"); }
    @FXML private void goAudit()      { System.out.println("Ir: Auditoría"); }
    @FXML private void goParams()     { System.out.println("Ir: Parámetros"); }
    @FXML private void goBackup()     { System.out.println("Ir: Respaldos"); }
    @FXML private void onExit()       { System.out.println("Salir…"); }

    /* ===== Modelo de fila ===== */
    public record UserRow(String nombre,
                          String usuario,
                          String rol,
                          String estado,
                          LocalDateTime ultimoAcceso) {}
}
