package com.avitech.sia.iu.parametros;

import com.avitech.sia.iu.ModalUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

public class CategoriasController {
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbTipo, cbEstado;
    @FXML private TableView<Categoria> tbl;
    @FXML private TableColumn<Categoria, String> cNombre, cTipo, cColor, cDesc, cEstado, cAccion;

    private final ObservableList<Categoria> master = FXCollections.observableArrayList();
    private final ObservableList<Categoria> filtered = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupComboBoxes();
        setupTable();
        loadData();

        txtSearch.textProperty().addListener((obs, old, val) -> applyFilters());
        cbTipo.valueProperty().addListener((obs, old, val) -> applyFilters());
        cbEstado.valueProperty().addListener((obs, old, val) -> applyFilters());
    }

    private void setupComboBoxes() {
        cbTipo.setItems(FXCollections.observableArrayList("Todos los tipos", "Insumo", "Producto", "Alimento", "Medicamento"));
        cbTipo.getSelectionModel().selectFirst();

        cbEstado.setItems(FXCollections.observableArrayList("Todos los estados", "Activo", "Inactivo"));
        cbEstado.getSelectionModel().selectFirst();
    }

    private void setupTable() {
        cNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().nombre));
        cTipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().tipo));
        cDesc.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().desc));
        cEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().estado));

        // Columna de color con indicador visual
        cColor.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String color, boolean empty) {
                super.updateItem(color, empty);
                if (empty || color == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Circle circle = new Circle(8);
                    circle.setStyle("-fx-fill: " + color + ";");
                    HBox box = new HBox(circle);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });
        cColor.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().color));

        // Columna de acciones
        cAccion.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("✎");
            private final Button btnDelete = new Button("🗑");
            private final HBox box = new HBox(6, btnEdit, btnDelete);

            {
                btnEdit.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: #176A52; -fx-font-size: 14px;");
                btnDelete.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: #B91C1C; -fx-font-size: 14px;");
                box.setAlignment(Pos.CENTER);

                btnEdit.setOnAction(e -> onEditar(getTableView().getItems().get(getIndex())));
                btnDelete.setOnAction(e -> onEliminar(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        tbl.setItems(filtered);
    }

    private void loadData() {
        // TODO: Cargar desde la base de datos
        master.setAll(
                new Categoria("Alimento Balanceado", "Insumo", "#10B981", "Alimentos preparados para aves", "Activo"),
                new Categoria("Medicamentos", "Insumo", "#3B82F6", "Productos farmacéuticos y veterinarios", "Activo"),
                new Categoria("Vitaminas y Suplementos", "Insumo", "#F59E0B", "Suplementos vitamínicos y minerales", "Activo"),
                new Categoria("Vacunas", "Medicamento", "#8B5CF6", "Vacunas preventivas", "Activo"),
                new Categoria("Huevos Comerciales", "Producto", "#EF4444", "Huevos para venta", "Activo"),
                new Categoria("Huevos Fértiles", "Producto", "#EC4899", "Huevos para incubación", "Activo"),
                new Categoria("Material de Limpieza", "Insumo", "#6B7280", "Desinfectantes y limpieza", "Activo")
        );
        applyFilters();
    }

    private void applyFilters() {
        String searchText = txtSearch.getText().toLowerCase();
        String tipoFilter = cbTipo.getValue();
        String estadoFilter = cbEstado.getValue();

        filtered.setAll(master.stream()
                .filter(c -> {
                    if (!searchText.isEmpty() &&
                        !c.nombre.toLowerCase().contains(searchText) &&
                        !c.tipo.toLowerCase().contains(searchText)) {
                        return false;
                    }
                    if (tipoFilter != null && !tipoFilter.equals("Todos los tipos") &&
                        !c.tipo.equals(tipoFilter)) {
                        return false;
                    }
                    if (estadoFilter != null && !estadoFilter.equals("Todos los estados") &&
                        !c.estado.equals(estadoFilter)) {
                        return false;
                    }
                    return true;
                })
                .toList());
    }

    @FXML private void onSearch() { applyFilters(); }

    @FXML private void onRefresh() {
        loadData();
        txtSearch.clear();
        cbTipo.getSelectionModel().selectFirst();
        cbEstado.getSelectionModel().selectFirst();
    }

    @FXML
    private void onAgregar() {
        // Abrir modal para agregar nueva categoría
        NuevaCategoriaController ctrl = ModalUtil.openModal(
                tbl,
                "/fxml/Parametros/modal_categoria.fxml",
                "Nueva Categoría"
        );

        if (ctrl != null && ctrl.getResult() != null) {
            // Agregar a la lista
            master.add(ctrl.getResult());
            applyFilters();

            // TODO: Aquí se insertará en la base de datos
            System.out.println("✅ Nueva categoría agregada: " + ctrl.getResult().nombre);
        }
    }

    private void onEditar(Categoria categoria) {
        // Abrir modal para editar categoría
        NuevaCategoriaController ctrl = ModalUtil.openModal(
                tbl,
                "/fxml/Parametros/modal_categoria.fxml",
                "Editar Categoría"
        );

        if (ctrl != null) {
            ctrl.setEditMode(categoria);

            if (ctrl.getResult() != null) {
                // Actualizar en la lista
                int index = master.indexOf(categoria);
                if (index >= 0) {
                    master.set(index, ctrl.getResult());
                    applyFilters();

                    // TODO: Aquí se actualizará en la base de datos
                    System.out.println("✅ Categoría actualizada: " + ctrl.getResult().nombre);
                }
            }
        }
    }

    private void onEliminar(Categoria categoria) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Eliminar la categoría '" + categoria.nombre + "'?");
        alert.setContentText("Esta acción no se puede deshacer.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                master.remove(categoria);
                applyFilters();
                System.out.println("🗑 Eliminada: " + categoria.nombre);
            }
        });
    }

    public static class Categoria {
        public final String nombre, tipo, color, desc, estado;

        public Categoria(String nombre, String tipo, String color, String desc, String estado) {
            this.nombre = nombre;
            this.tipo = tipo;
            this.color = color;
            this.desc = desc;
            this.estado = estado;
        }
    }
}
