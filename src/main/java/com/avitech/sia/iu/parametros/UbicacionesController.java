package com.avitech.sia.iu.parametros;

import com.avitech.sia.iu.ModalUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

public class UbicacionesController {
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbTipo, cbEstado;
    @FXML private TableView<Ubicacion> tbl;
    @FXML private TableColumn<Ubicacion,String> cNombre,cTipo,cCapacidad,cResp,cDesc,cEstado,cAccion;

    private final ObservableList<Ubicacion> master = FXCollections.observableArrayList();
    private final ObservableList<Ubicacion> filtered = FXCollections.observableArrayList();

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
        cbTipo.setItems(FXCollections.observableArrayList(
            "Todos los tipos", "Galpón", "Almacén", "Bodega", "Área de Proceso", "Oficina"
        ));
        cbTipo.getSelectionModel().selectFirst();

        cbEstado.setItems(FXCollections.observableArrayList("Todos los estados","Activo","Inactivo","En mantenimiento"));
        cbEstado.getSelectionModel().selectFirst();
    }

    private void setupTable() {
        cNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().nombre));
        cTipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().tipo));
        cCapacidad.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().capacidad));
        cResp.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().responsable));
        cDesc.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().descripcion));
        cEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().estado));

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
                new Ubicacion("Almacén Principal","Almacén","500 m²","Carlos Pérez","Almacén general de insumos","Activo"),
                new Ubicacion("Galpón 1","Galpón","5000 aves","Ana Rodríguez","Galpón de producción A","Activo"),
                new Ubicacion("Galpón 2","Galpón","5000 aves","Ana Rodríguez","Galpón de producción B","Activo"),
                new Ubicacion("Galpón 3","Galpón","5000 aves","Luis Morales","Galpón de producción C","Activo"),
                new Ubicacion("Galpón 4","Galpón","5000 aves","Luis Morales","Galpón de producción D","En mantenimiento"),
                new Ubicacion("Área de Incubación","Área de Proceso","10000 huevos","Elena Vargas","Incubadora automática","Activo"),
                new Ubicacion("Almacén de Medicamentos","Almacén","50 m²","Elena Vargas","Bodega farmacéutica","Activo"),
                new Ubicacion("Bodega de Alimentos","Bodega","200 m²","Carlos Pérez","Almacén de alimento balanceado","Activo")
        );
        applyFilters();
    }

    private void applyFilters() {
        String searchText = txtSearch.getText().toLowerCase();
        String tipoFilter = cbTipo.getValue();
        String estadoFilter = cbEstado.getValue();

        filtered.setAll(master.stream()
                .filter(u -> {
                    if (!searchText.isEmpty() &&
                        !u.nombre.toLowerCase().contains(searchText) &&
                        !u.responsable.toLowerCase().contains(searchText)) {
                        return false;
                    }
                    if (tipoFilter != null && !tipoFilter.equals("Todos los tipos") &&
                        !u.tipo.equals(tipoFilter)) {
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
        cbTipo.getSelectionModel().selectFirst();
        cbEstado.getSelectionModel().selectFirst();
    }

    @FXML
    private void onAgregar() {
        // Abrir modal para agregar nueva ubicación
        NuevaUbicacionController ctrl = ModalUtil.openModal(
                tbl,
                "/fxml/Parametros/modal_ubicacion.fxml",
                "Nueva Ubicación"
        );

        if (ctrl != null && ctrl.getResult() != null) {
            // Agregar a la lista
            master.add(ctrl.getResult());
            applyFilters();

            // TODO: Aquí se insertará en la base de datos
            System.out.println("✅ Nueva ubicación agregada: " + ctrl.getResult().nombre);
        }
    }

    private void onEditar(Ubicacion ubicacion) {
        // Abrir modal para editar ubicación
        NuevaUbicacionController ctrl = ModalUtil.openModal(
                tbl,
                "/fxml/Parametros/modal_ubicacion.fxml",
                "Editar Ubicación"
        );

        if (ctrl != null) {
            ctrl.setEditMode(ubicacion);

            if (ctrl.getResult() != null) {
                // Actualizar en la lista
                int index = master.indexOf(ubicacion);
                if (index >= 0) {
                    master.set(index, ctrl.getResult());
                    applyFilters();

                    // TODO: Aquí se actualizará en la base de datos
                    System.out.println("✅ Ubicación actualizada: " + ctrl.getResult().nombre);
                }
            }
        }
    }

    private void onEliminar(Ubicacion ubicacion) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Eliminar la ubicación '" + ubicacion.nombre + "'?");
        alert.setContentText("Esta acción no se puede deshacer.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                master.remove(ubicacion);
                applyFilters();
                System.out.println("🗑 Eliminada: " + ubicacion.nombre);
            }
        });
    }

    public static class Ubicacion {
        public final String nombre, tipo, capacidad, responsable, descripcion, estado;

        public Ubicacion(String nombre, String tipo, String capacidad, String responsable, String descripcion, String estado) {
            this.nombre = nombre;
            this.tipo = tipo;
            this.capacidad = capacidad;
            this.responsable = responsable;
            this.descripcion = descripcion;
            this.estado = estado;
        }
    }
}
