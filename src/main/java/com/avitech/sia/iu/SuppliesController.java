package com.avitech.sia.iu;

import com.avitech.sia.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SuppliesController {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    @FXML private VBox sidebar;
    @FXML private Label lblResumenFecha;
    @FXML private Label lblEstadoStock;
    @FXML private Label lblStockDisponible;
    @FXML private Label lblStockMeta;
    @FXML private Label lblConsumoHoy;
    @FXML private Label lblEntradasProgramadas;
    @FXML private Label lblAlertasActivas;
    @FXML private Label lblStockTotal;
    @FXML private Label lblUltimoMovimiento;
    @FXML private Label lblRecomendacion;
    @FXML private TableView<StockMovement> tblMovimientos;
    @FXML private TableColumn<StockMovement, String> colFecha;
    @FXML private TableColumn<StockMovement, String> colProducto;
    @FXML private TableColumn<StockMovement, String> colTipo;
    @FXML private TableColumn<StockMovement, String> colCantidad;
    @FXML private TableColumn<StockMovement, String> colUbicacion;
    @FXML private TableColumn<StockMovement, String> colReferencia;
    @FXML private ListView<String> lstAlertas;
    @FXML private ComboBox<String> cbFiltroProducto;
    @FXML private ComboBox<String> cbFiltroUbicacion;
    @FXML private ComboBox<String> cbFiltroTipo;
    @FXML private DatePicker dpFiltroDesde;

    private final ObservableList<StockMovement> movimientos = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        markActive("Suministros");
        configurarTablas();
        configurarFiltros();
        cargarDatosDemo();
    }

    @FXML private void goDashboard()  { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
    @FXML private void goSupplies()   { App.goTo("/fxml/supplies.fxml", "SIA Avitech — Suministros"); }
    @FXML private void goHealth()     { markActive("Sanidad"); }
    @FXML private void goProduction() { markActive("Producción"); }
    @FXML private void goReports()    { markActive("Reportes"); }
    @FXML private void goAlerts()     { markActive("Alertas"); }
    @FXML private void goAudit()      { markActive("Auditoría"); }
    @FXML private void goParams()     { markActive("Parámetros"); }
    @FXML private void goUsers()      { markActive("Usuarios"); }
    @FXML private void goBackup()     { markActive("Respaldos"); }

    @FXML
    private void onRegisterEntry() {
        Dialog<ButtonType> dialog = construirDialogoMovimiento(
                "Registrar Entrada de Stock",
                "Registra una nueva entrada de suministros.",
                "Registrar entrada",
                List.of(
                        new FieldSpec("Producto", new ComboBox<>(FXCollections.observableArrayList(
                                "Maíz Amarillo", "Concentrado Ponedor", "Vitaminas A-D")), true),
                        new FieldSpec("Fecha", new DatePicker(LocalDate.now()), true),
                        new FieldSpec("Cantidad", new TextField(), true),
                        new FieldSpec("Unidad", new ComboBox<>(FXCollections.observableArrayList("Kilogramos", "Sacos", "Litros")), true),
                        new FieldSpec("Ubicación", new ComboBox<>(FXCollections.observableArrayList("Bodega Central", "Galpón 3", "Silo Norte")), true),
                        new FieldSpec("Proveedor", new TextField(), false),
                        new FieldSpec("Número de lote", new TextField(), false),
                        new FieldSpec("Notas", new TextArea(), false)
                ),
                "Nota: Al confirmar, se actualizará automáticamente el inventario del producto.");

        dialog.showAndWait().ifPresent(result -> {
            if (result.getButtonData().isDefaultButton()) {
                mostrarConfirmacion("Entrada registrada correctamente.");
            }
        });
    }

    @FXML
    private void onRegisterExit() {
        Dialog<ButtonType> dialog = construirDialogoMovimiento(
                "Registrar Salida de Stock",
                "Registra una salida por consumo interno o movimiento.",
                "Registrar salida",
                List.of(
                        new FieldSpec("Producto", new ComboBox<>(FXCollections.observableArrayList(
                                "Concentrado Ponedor", "Vitaminas A-D", "Desinfectante")), true),
                        new FieldSpec("Fecha", new DatePicker(LocalDate.now()), true),
                        new FieldSpec("Cantidad", new TextField(), true),
                        new FieldSpec("Unidad", new ComboBox<>(FXCollections.observableArrayList("Kilogramos", "Litros", "Unidades")), true),
                        new FieldSpec("Ubicación", new ComboBox<>(FXCollections.observableArrayList("Galpón 1", "Galpón 2", "Enfermería")), true),
                        new FieldSpec("Fuente / Motivo", new TextField(), false),
                        new FieldSpec("Destino / Referencia", new TextField(), false),
                        new FieldSpec("Notas", new TextArea(), false)
                ),
                "Importante: Verifica el stock disponible antes de confirmar el retiro.");

        dialog.showAndWait().ifPresent(result -> {
            if (result.getButtonData().isDefaultButton()) {
                mostrarConfirmacion("Salida registrada correctamente.");
            }
        });
    }

    @FXML
    private void onViewStock() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Resumen de Stock Actual");
        dialog.setHeaderText("Resumen general del inventario por producto");
        establecerOwner(dialog);
        dialog.getDialogPane().getStylesheets().add(App.class.getResource("/css/theme.css").toExternalForm());

        DialogPane pane = dialog.getDialogPane();
        pane.getButtonTypes().add(ButtonType.CLOSE);
        pane.setPrefWidth(520);

        VBox content = new VBox(12);
        content.setPadding(new Insets(10, 0, 0, 0));

        VBox listado = new VBox(10);
        listado.getChildren().addAll(
                crearResumenProducto("Concentrado Ponedor", "Bodega Central", "980 kg", false),
                crearResumenProducto("Maíz Amarillo", "Silo Norte", "1.250 kg", false),
                crearResumenProducto("Antibiótico BroadCare", "Enfermería", "12 frascos", true)
        );

        Text alerta = new Text("Productos con stock bajo\n• Antibiótico BroadCare — Quedan 12 frascos\n• Desinfectante bioseguridad C16 — Quedan 8 litros");
        alerta.getStyleClass().add("muted");

        VBox alertaBox = new VBox(alerta);
        alertaBox.getStyleClass().add("card");
        alertaBox.setStyle("-fx-background-color: rgba(248,113,113,0.12);");
        alertaBox.setPadding(new Insets(12));

        content.getChildren().addAll(listado, alertaBox);
        pane.setContent(content);

        dialog.showAndWait();
    }

    @FXML
    private void onMoveStock() {
        Dialog<ButtonType> dialog = construirDialogoMovimiento(
                "Mover Stock entre Ubicaciones",
                "Transfiere productos entre diferentes almacenes de la granja.",
                "Mover stock",
                List.of(
                        new FieldSpec("Producto", new ComboBox<>(FXCollections.observableArrayList(
                                "Concentrado Ponedor", "Maíz Amarillo", "Desinfectante")), true),
                        new FieldSpec("Cantidad a mover", new TextField(), true),
                        new FieldSpec("Ubicación origen", new ComboBox<>(FXCollections.observableArrayList("Bodega Central", "Silo Norte", "Galpón 3")), true),
                        new FieldSpec("Ubicación destino", new ComboBox<>(FXCollections.observableArrayList("Galpón 1", "Galpón 2", "Enfermería")), true),
                        new FieldSpec("Responsable", new TextField(), false),
                        new FieldSpec("Motivo del movimiento", new TextArea(), false)
                ),
                "Recuerda documentar el motivo del movimiento para mantener la trazabilidad.");

        dialog.showAndWait().ifPresent(result -> {
            if (result.getButtonData().isDefaultButton()) {
                mostrarConfirmacion("Movimiento registrado correctamente.");
            }
        });
    }

    private void configurarTablas() {
        if (tblMovimientos == null) {
            return;
        }

        colFecha.setCellValueFactory(data -> data.getValue().dateProperty());
        colProducto.setCellValueFactory(data -> data.getValue().productProperty());
        colTipo.setCellValueFactory(data -> data.getValue().typeProperty());
        colCantidad.setCellValueFactory(data -> data.getValue().quantityProperty());
        colUbicacion.setCellValueFactory(data -> data.getValue().locationProperty());
        colReferencia.setCellValueFactory(data -> data.getValue().referenceProperty());

        colTipo.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(item.toUpperCase());
                    badge.getStyleClass().add("badge");
                    if ("Entrada".equalsIgnoreCase(item)) {
                        badge.getStyleClass().add("success");
                    } else if ("Salida".equalsIgnoreCase(item)) {
                        badge.getStyleClass().add("warning");
                    } else {
                        badge.getStyleClass().add("info");
                    }
                    setGraphic(badge);
                    setText(null);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        tblMovimientos.setItems(movimientos);
    }

    private void configurarFiltros() {
        if (cbFiltroProducto != null) {
            cbFiltroProducto.getItems().setAll("Todos", "Concentrado Ponedor", "Maíz Amarillo", "Vitaminas", "Desinfectante");
            cbFiltroProducto.getSelectionModel().selectFirst();
        }

        if (cbFiltroUbicacion != null) {
            cbFiltroUbicacion.getItems().setAll("Todas", "Bodega Central", "Silo Norte", "Galpón 1", "Galpón 2");
            cbFiltroUbicacion.getSelectionModel().selectFirst();
        }

        if (cbFiltroTipo != null) {
            cbFiltroTipo.getItems().setAll("Todos", "Entradas", "Salidas", "Ajustes");
            cbFiltroTipo.getSelectionModel().selectFirst();
        }

        if (dpFiltroDesde != null) {
            dpFiltroDesde.setValue(LocalDate.now().minusDays(7));
        }
    }

    private void cargarDatosDemo() {
        lblResumenFecha.setText("Actualizado " + LocalDate.now().format(DATE_FORMAT));
        lblEstadoStock.setText("Stock saludable");
        lblStockDisponible.setText("1.245 kg");
        lblStockMeta.setText("Meta diaria: 1.200 kg");
        lblConsumoHoy.setText("312 kg");
        lblEntradasProgramadas.setText("3");
        lblAlertasActivas.setText("2");
        lblStockTotal.setText("1.245 kg");
        lblUltimoMovimiento.setText("Salida - 18 kg (Galpón 2)");
        lblRecomendacion.setText("Siguiente revisión: 4:00 PM");

        movimientos.setAll(
                new StockMovement("21 May 2024", "Concentrado Ponedor", "Salida", "18 kg", "Galpón 2", "Consumo diario"),
                new StockMovement("21 May 2024", "Maíz Amarillo", "Entrada", "500 kg", "Bodega Central", "Factura 2215"),
                new StockMovement("20 May 2024", "Vitaminas A-D", "Salida", "6 frascos", "Enfermería", "Lote tratamiento"),
                new StockMovement("20 May 2024", "Desinfectante C16", "Ajuste", "-2 L", "Galpón 1", "Inventario mensual")
        );

        if (lstAlertas != null) {
            lstAlertas.setItems(FXCollections.observableArrayList(
                    "Stock bajo: Antibiótico BroadCare (12 frascos)",
                    "Entrada programada: Concentrado Ponedor — 22/05",
                    "Revisión pendiente: Silo Norte (limpieza programada)"
            ));
        }
    }

    private void mostrarConfirmacion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Operación realizada");
        alert.setHeaderText(null);
        alert.setContentText(mensaje + "\nEstas acciones pueden enlazarse con la base de datos en el futuro.");
        alert.getDialogPane().getStylesheets().add(App.class.getResource("/css/theme.css").toExternalForm());
        javafx.stage.Window owner = obtenerVentana();
        if (owner != null) {
            alert.initOwner(owner);
        }
        alert.showAndWait();
    }

    private Dialog<ButtonType> construirDialogoMovimiento(String titulo,
                                                          String descripcion,
                                                          String textoConfirmar,
                                                          List<FieldSpec> campos,
                                                          String notaFinal) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.setHeaderText(descripcion);
        establecerOwner(dialog);
        dialog.getDialogPane().getStylesheets().add(App.class.getResource("/css/theme.css").toExternalForm());
        dialog.getDialogPane().setPrefWidth(540);

        ButtonType confirmar = new ButtonType(textoConfirmar, ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.CANCEL, confirmar);

        Node confirmButton = dialog.getDialogPane().lookupButton(confirmar);
        if (confirmButton != null) {
            confirmButton.getStyleClass().add("primary");
        }
        Node cancelButton = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        if (cancelButton != null) {
            cancelButton.getStyleClass().add("ghostBtn");
        }

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(10, 0, 0, 0));

        int row = 0;
        for (FieldSpec campo : campos) {
            Label label = new Label(campo.label() + (campo.requerido() ? " *" : ""));
            label.getStyleClass().add("field-label");

            Node control = campo.control();
            if (control instanceof TextArea area) {
                area.setPrefRowCount(3);
                area.setWrapText(true);
            }
            if (control instanceof ComboBox<?> combo) {
                combo.setMaxWidth(Double.MAX_VALUE);
            }
            if (control instanceof TextField field) {
                field.setMaxWidth(Double.MAX_VALUE);
            }

            GridPane.setConstraints(label, 0, row);
            GridPane.setConstraints(control, 1, row);
            GridPane.setHgrow(control, javafx.scene.layout.Priority.ALWAYS);
            grid.getChildren().addAll(label, control);
            row++;
        }

        VBox contenedor = new VBox(12, grid);
        if (notaFinal != null && !notaFinal.isBlank()) {
            Label nota = new Label(notaFinal);
            nota.getStyleClass().add("muted");
            nota.setWrapText(true);
            contenedor.getChildren().add(nota);
        }

        dialog.getDialogPane().setContent(contenedor);
        return dialog;
    }

    private HBox crearResumenProducto(String producto, String ubicacion, String cantidad, boolean critico) {
        Label lblProducto = new Label(producto);
        lblProducto.getStyleClass().add("card-title");

        Label lblUbicacion = new Label(ubicacion);
        lblUbicacion.getStyleClass().add("muted");

        Label lblCantidad = new Label(cantidad);
        lblCantidad.getStyleClass().add("stock-value");
        if (critico) {
            lblCantidad.getStyleClass().add("critical");
        }

        Pane spacer = new Pane();
        HBox box = new HBox(12, lblProducto, new Separator(), lblUbicacion, spacer, lblCantidad);
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private javafx.stage.Window obtenerVentana() {
        return sidebar != null && sidebar.getScene() != null ? sidebar.getScene().getWindow() : null;
    }

    private void establecerOwner(Dialog<?> dialog) {
        javafx.stage.Window owner = obtenerVentana();
        if (owner != null) {
            dialog.initOwner(owner);
        }
    }

    private void markActive(String text) {
        if (sidebar == null) {
            return;
        }

        sidebar.lookupAll(".side-btn").forEach(n -> n.getStyleClass().remove("active"));
        sidebar.lookupAll(".side-btn").stream()
                .filter(n -> n instanceof ToggleButton tb && tb.getText().equals(text))
                .findFirst()
                .ifPresent(n -> n.getStyleClass().add("active"));
    }

    private record FieldSpec(String label, Control control, boolean requerido) {}

    public static class StockMovement {
        private final javafx.beans.property.SimpleStringProperty date;
        private final javafx.beans.property.SimpleStringProperty product;
        private final javafx.beans.property.SimpleStringProperty type;
        private final javafx.beans.property.SimpleStringProperty quantity;
        private final javafx.beans.property.SimpleStringProperty location;
        private final javafx.beans.property.SimpleStringProperty reference;

        public StockMovement(String date, String product, String type, String quantity, String location, String reference) {
            this.date = new javafx.beans.property.SimpleStringProperty(date);
            this.product = new javafx.beans.property.SimpleStringProperty(product);
            this.type = new javafx.beans.property.SimpleStringProperty(type);
            this.quantity = new javafx.beans.property.SimpleStringProperty(quantity);
            this.location = new javafx.beans.property.SimpleStringProperty(location);
            this.reference = new javafx.beans.property.SimpleStringProperty(reference);
        }

        public javafx.beans.property.StringProperty dateProperty() { return date; }
        public javafx.beans.property.StringProperty productProperty() { return product; }
        public javafx.beans.property.StringProperty typeProperty() { return type; }
        public javafx.beans.property.StringProperty quantityProperty() { return quantity; }
        public javafx.beans.property.StringProperty locationProperty() { return location; }
        public javafx.beans.property.StringProperty referenceProperty() { return reference; }
    }
}
