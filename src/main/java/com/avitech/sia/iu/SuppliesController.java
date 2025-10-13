package com.avitech.sia.iu;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SuppliesController {
    // Topbar / user
    @FXML private Label lblHeader;
    @FXML private Label lblSystemStatus;
    @FXML private Label lblUserInfo;

    // Cards
    @FXML private VBox cardMovimientosHoy;
    @FXML private VBox cardProductos;
    @FXML private VBox cardStockBajo;
    @FXML private VBox cardValorTotal;
    @FXML private Label lblMovimientosHoy;
    @FXML private Label lblProductos;
    @FXML private Label lblStockBajo;
    @FXML private Label lblValorTotal;

    // Filters
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbCategoria;
    @FXML private DatePicker dpFecha;
    @FXML private Button btnAplicarFiltros;
    @FXML private Button btnLimpiarFiltros;

    // Table
    @FXML private TableView<Movimiento> tblMovimientos;
    @FXML private TableColumn<Movimiento, String> colFecha;
    @FXML private TableColumn<Movimiento, String> colTipo;
    @FXML private TableColumn<Movimiento, String> colProducto;
    @FXML private TableColumn<Movimiento, String> colCantidad;
    @FXML private TableColumn<Movimiento, String> colResponsable;
    @FXML private TableColumn<Movimiento, String> colNotas;

    // Sidebar
    @FXML private VBox sidebar;

    private final ObservableList<Movimiento> movimientos = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        if (cbCategoria != null) {
            cbCategoria.setItems(FXCollections.observableArrayList(
                    "Alimentos",
                    "Medicamentos",
                    "Limpieza",
                    "Equipamiento",
                    "Otros"
            ));
        }

        setupTable();
        loadMock();
        markActive("Suministros");
    }

    private void setupTable() {
        if (colFecha != null) colFecha.setCellValueFactory(data -> data.getValue().fechaProperty());
        if (colTipo != null) colTipo.setCellValueFactory(data -> data.getValue().tipoProperty());
        if (colProducto != null) colProducto.setCellValueFactory(data -> data.getValue().productoProperty());
        if (colCantidad != null) colCantidad.setCellValueFactory(data -> data.getValue().cantidadProperty());
        if (colResponsable != null) colResponsable.setCellValueFactory(data -> data.getValue().responsableProperty());
        if (colNotas != null) colNotas.setCellValueFactory(data -> data.getValue().notasProperty());

        if (tblMovimientos != null) {
            tblMovimientos.setItems(movimientos);
        }
    }

    private void loadMock() {
        if (lblSystemStatus != null) {
            lblSystemStatus.setText("Sistema Online – MySQL Local");
        }

        if (lblMovimientosHoy != null) lblMovimientosHoy.setText("18 movimientos");
        if (lblProductos != null) lblProductos.setText("85 productos");
        if (lblStockBajo != null) lblStockBajo.setText("7 críticos");
        if (lblValorTotal != null) lblValorTotal.setText("S/ 42,350");

        movimientos.setAll(
                new Movimiento("08:30", "Entrada", "Alimento balanceado", "+1,200 kg", "Juan Pérez", "Reposición semanal"),
                new Movimiento("09:15", "Salida", "Desinfectante avícola", "-25 l", "María Ramos", "Limpieza galpón 4"),
                new Movimiento("10:05", "Transferencia", "Maíz amarillo", "-450 kg", "Carlos Díaz", "A bodega secundaria"),
                new Movimiento("11:40", "Entrada", "Vacuna Newcastle", "+350 dosis", "Proveedor VetFarm", "Lote 2024-09"),
                new Movimiento("13:20", "Salida", "Vitaminas mixtas", "-80 frascos", "Lucía Méndez", "Programa nutricional"),
                new Movimiento("14:05", "Transferencia", "Alimento iniciador", "-300 kg", "Juan Pérez", "Galpón recría"),
                new Movimiento("15:30", "Entrada", "Camas de viruta", "+120 fardos", "Proveedor AgroSur", "Ingreso por compras"),
                new Movimiento("16:10", "Salida", "Guantes sanitarios", "-200 pares", "María Ramos", "Reposición personal"),
                new Movimiento("17:45", "Transferencia", "Alimento balanceado", "-600 kg", "Carlos Díaz", "Galpón postura"),
                new Movimiento("18:30", "Entrada", "Suplemento calcio", "+95 sacos", "Proveedor NutriAvic", "Stock crítico cubierto")
        );
    }

    public void setHeader(String header) {
        if (lblHeader != null) lblHeader.setText(header);
        if (lblUserInfo != null) lblUserInfo.setText(header);
    }

    @FXML
    private void onExit() {
        if (lblHeader != null) {
            Stage st = (Stage) lblHeader.getScene().getWindow();
            st.close();
        }
    }

    /* ======== NAV (stubs) ======== */
    @FXML private void goDashboard()  { markActive("Tablero"); }
    @FXML private void goSupplies()   { markActive("Suministros"); }
    @FXML private void goHealth()     { markActive("Sanidad"); }
    @FXML private void goProduction() { markActive("Producción"); }
    @FXML private void goReports()    { markActive("Reportes"); }
    @FXML private void goAlerts()     { markActive("Alertas"); }
    @FXML private void goAudit()      { markActive("Auditoría"); }
    @FXML private void goParams()     { markActive("Parámetros"); }
    @FXML private void goUsers()      { markActive("Usuarios"); }
    @FXML private void goBackup()     { markActive("Respaldos"); }

    private void markActive(String text) {
        if (sidebar == null) return;
        sidebar.lookupAll(".side-btn").forEach(n -> n.getStyleClass().remove("active"));
        sidebar.lookupAll(".side-btn").stream()
                .filter(n -> n instanceof ToggleButton tb && tb.getText().equals(text))
                .findFirst()
                .ifPresent(n -> n.getStyleClass().add("active"));
    }

    /* ======== ACCIONES ======== */
    @FXML private void onApplyFilters() {
        // Placeholder para la lógica de filtrado
    }

    @FXML private void onClearFilters() {
        if (txtBuscar != null) txtBuscar.clear();
        if (cbCategoria != null) cbCategoria.getSelectionModel().clearSelection();
        if (dpFecha != null) dpFecha.setValue(null);
    }

    @FXML private void onEntrada() {
        // Placeholder para registrar una entrada de suministros
    }

    @FXML private void onSalida() {
        // Placeholder para registrar una salida de suministros
    }

    @FXML private void onMoveStock() {
        // Placeholder para mover stock entre almacenes
    }

    public static class Movimiento {
        private final SimpleStringProperty fecha = new SimpleStringProperty();
        private final SimpleStringProperty tipo = new SimpleStringProperty();
        private final SimpleStringProperty producto = new SimpleStringProperty();
        private final SimpleStringProperty cantidad = new SimpleStringProperty();
        private final SimpleStringProperty responsable = new SimpleStringProperty();
        private final SimpleStringProperty notas = new SimpleStringProperty();

        public Movimiento(String fecha, String tipo, String producto, String cantidad, String responsable, String notas) {
            this.fecha.set(fecha);
            this.tipo.set(tipo);
            this.producto.set(producto);
            this.cantidad.set(cantidad);
            this.responsable.set(responsable);
            this.notas.set(notas);
        }

        public String getFecha() { return fecha.get(); }
        public SimpleStringProperty fechaProperty() { return fecha; }

        public String getTipo() { return tipo.get(); }
        public SimpleStringProperty tipoProperty() { return tipo; }

        public String getProducto() { return producto.get(); }
        public SimpleStringProperty productoProperty() { return producto; }

        public String getCantidad() { return cantidad.get(); }
        public SimpleStringProperty cantidadProperty() { return cantidad; }

        public String getResponsable() { return responsable.get(); }
        public SimpleStringProperty responsableProperty() { return responsable; }

        public String getNotas() { return notas.get(); }
        public SimpleStringProperty notasProperty() { return notas; }
    }
}
