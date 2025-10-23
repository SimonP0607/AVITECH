package com.avitech.sia.iu.parametros;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

public class UnidadesController {

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbEstado;
    @FXML private TableView<Unidad> tbl;
    @FXML private TableColumn<Unidad, String> cNombre, cSimbolo, cTipo, cDesc, cEstado, cAccion;

    private final ObservableList<Unidad> master = FXCollections.observableArrayList();
    private final ObservableList<Unidad> filtered = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupComboBoxes();
        setupTable();
        loadData();

        // Listeners para búsqueda y filtrado
        txtSearch.textProperty().addListener((obs, old, val) -> applyFilters());
        cbEstado.valueProperty().addListener((obs, old, val) -> applyFilters());
    }

    private void setupComboBoxes() {
        cbEstado.setItems(FXCollections.observableArrayList("Todos los estados", "Activo", "Inactivo"));
        cbEstado.getSelectionModel().selectFirst();
    }

    private void setupTable() {
        cNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().nombre));
        cSimbolo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().simbolo));
        cTipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().tipo));
        cDesc.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().desc));
        cEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().estado));

        // Columna de acciones con botones
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
        // Por ahora, datos de ejemplo
        master.setAll(
                new Unidad("Kilogramo", "kg", "Peso", "Unidad de masa estándar", "Activo"),
                new Unidad("Gramo", "g", "Peso", "Unidad de masa menor", "Activo"),
                new Unidad("Litro", "L", "Volumen", "Unidad de volumen estándar", "Activo"),
                new Unidad("Mililitro", "mL", "Volumen", "Unidad de volumen menor", "Activo"),
                new Unidad("Dosis", "dosis", "Cantidad", "Dosis médica o veterinaria", "Activo"),
                new Unidad("Unidad", "und", "Cantidad", "Unidad individual o pieza", "Activo"),
                new Unidad("Libra", "lb", "Peso", "Unidad de peso imperial", "Inactivo")
        );
        applyFilters();
    }

    private void applyFilters() {
        String searchText = txtSearch.getText().toLowerCase();
        String estadoFilter = cbEstado.getValue();

        filtered.setAll(master.stream()
                .filter(u -> {
                    if (!searchText.isEmpty() &&
                        !u.nombre.toLowerCase().contains(searchText) &&
                        !u.simbolo.toLowerCase().contains(searchText) &&
                        !u.tipo.toLowerCase().contains(searchText)) {
                        return false;
                    }
                    if (estadoFilter != null && !estadoFilter.equals("Todos los estados") &&
                        !u.estado.equals(estadoFilter)) {
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
        cbEstado.getSelectionModel().selectFirst();
    }

    @FXML
    private void onAgregar() {
        // TODO: Abrir diálogo para agregar nueva unidad
        // Al guardar, insertar en BD y recargar
        System.out.println("📝 Agregar nueva unidad");
    }

    private void onEditar(Unidad unidad) {
        // TODO: Abrir diálogo para editar unidad
        // Al guardar, actualizar en BD y recargar
        System.out.println("✎ Editar unidad: " + unidad.nombre);
    }

    private void onEliminar(Unidad unidad) {
        // TODO: Confirmar y eliminar (lógico) de BD
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Eliminar la unidad '" + unidad.nombre + "'?");
        alert.setContentText("Esta acción no se puede deshacer.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // TODO: Eliminar de BD
                master.remove(unidad);
                applyFilters();
                System.out.println("🗑 Eliminada: " + unidad.nombre);
            }
        });
    }

    // Modelo de datos
    public static class Unidad {
        public final String nombre, simbolo, tipo, desc, estado;

        public Unidad(String nombre, String simbolo, String tipo, String desc, String estado) {
            this.nombre = nombre;
            this.simbolo = simbolo;
            this.tipo = tipo;
            this.desc = desc;
            this.estado = estado;
        }
    }
}
