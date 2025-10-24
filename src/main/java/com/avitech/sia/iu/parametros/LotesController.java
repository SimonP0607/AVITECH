package com.avitech.sia.iu.parametros;

import com.avitech.sia.iu.ModalUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

public class LotesController {
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbGalpon, cbEstado;
    @FXML private TableView<Lote> tbl;
    @FXML private TableColumn<Lote,String> cNombre,cAves,cEdad,cRaza,cGalpon,cFecha,cEstado,cAccion;

    private final ObservableList<Lote> master = FXCollections.observableArrayList();
    private final ObservableList<Lote> filtered = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupComboBoxes();
        setupTable();
        loadData();

        txtSearch.textProperty().addListener((obs, old, val) -> applyFilters());
        cbGalpon.valueProperty().addListener((obs, old, val) -> applyFilters());
        cbEstado.valueProperty().addListener((obs, old, val) -> applyFilters());
    }

    private void setupComboBoxes() {
        cbGalpon.setItems(FXCollections.observableArrayList(
            "Todos los galpones", "Galpón 1", "Galpón 2", "Galpón 3", "Galpón 4"
        ));
        cbGalpon.getSelectionModel().selectFirst();

        cbEstado.setItems(FXCollections.observableArrayList("Todos los estados","Activo","Terminado","En preparación"));
        cbEstado.getSelectionModel().selectFirst();
    }

    private void setupTable() {
        cNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().nombre));
        cAves.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().aves));
        cEdad.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().edad));
        cRaza.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().raza));
        cGalpon.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().galpon));
        cFecha.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().fechaIngreso));
        cEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().estado));

        cAccion.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("✎");
            private final Button btnView = new Button("👁");
            private final HBox box = new HBox(6, btnView, btnEdit);

            {
                btnEdit.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: #176A52; -fx-font-size: 14px;");
                btnView.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: #3B82F6; -fx-font-size: 14px;");
                box.setAlignment(Pos.CENTER);

                btnView.setOnAction(e -> onVer(getTableView().getItems().get(getIndex())));
                btnEdit.setOnAction(e -> onEditar(getTableView().getItems().get(getIndex())));
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
                new Lote("Lote A-2024-001","5000","22","Hy-Line Brown","Galpón 1","2024-06-15","Activo"),
                new Lote("Lote A-2024-002","4800","20","Hy-Line Brown","Galpón 2","2024-07-01","Activo"),
                new Lote("Lote A-2024-003","5200","18","Lohmann LSL","Galpón 3","2024-07-20","Activo"),
                new Lote("Lote A-2023-015","4500","78","Hy-Line Brown","Galpón 4","2023-01-10","Terminado"),
                new Lote("Lote A-2024-004","0","0","Hy-Line Brown","Galpón 4","2024-11-01","En preparación")
        );
        applyFilters();
    }

    private void applyFilters() {
        String searchText = txtSearch.getText().toLowerCase();
        String galponFilter = cbGalpon.getValue();
        String estadoFilter = cbEstado.getValue();

        filtered.setAll(master.stream()
                .filter(l -> {
                    if (!searchText.isEmpty() &&
                        !l.nombre.toLowerCase().contains(searchText) &&
                        !l.raza.toLowerCase().contains(searchText)) {
                        return false;
                    }
                    if (galponFilter != null && !galponFilter.equals("Todos los galpones") &&
                        !l.galpon.equals(galponFilter)) {
                        return false;
                    }
                    if (estadoFilter != null && !estadoFilter.equals("Todos los estados") &&
                        !l.estado.equals(estadoFilter)) {
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
        cbGalpon.getSelectionModel().selectFirst();
        cbEstado.getSelectionModel().selectFirst();
    }

    @FXML
    private void onAgregar() {
        // Abrir modal para agregar nuevo lote
        NuevoLoteController ctrl = ModalUtil.openModal(
                tbl,
                "/fxml/Parametros/modal_lote.fxml",
                "Nuevo Lote de Aves"
        );

        if (ctrl != null && ctrl.getResult() != null) {
            // Agregar a la lista
            master.add(ctrl.getResult());
            applyFilters();

            // TODO: Aquí se insertará en la base de datos
            System.out.println("✅ Nuevo lote agregado: " + ctrl.getResult().nombre);
        }
    }

    private void onVer(Lote lote) {
        // Abrir modal en modo solo lectura/vista detallada
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles del Lote");
        alert.setHeaderText(lote.nombre);
        alert.setContentText(String.format(
                "Aves: %s\nEdad: %s semanas\nRaza: %s\nGalpón: %s\nFecha Ingreso: %s\nEstado: %s",
                lote.aves, lote.edad, lote.raza, lote.galpon, lote.fechaIngreso, lote.estado
        ));
        alert.showAndWait();

        // TODO: Crear una vista detallada más completa con historial de producción, sanidad, etc.
    }

    private void onEditar(Lote lote) {
        // Abrir modal para editar lote
        NuevoLoteController ctrl = ModalUtil.openModal(
                tbl,
                "/fxml/Parametros/modal_lote.fxml",
                "Editar Lote de Aves"
        );

        if (ctrl != null) {
            ctrl.setEditMode(lote);

            if (ctrl.getResult() != null) {
                // Actualizar en la lista
                int index = master.indexOf(lote);
                if (index >= 0) {
                    master.set(index, ctrl.getResult());
                    applyFilters();

                    // TODO: Aquí se actualizará en la base de datos
                    System.out.println("✅ Lote actualizado: " + ctrl.getResult().nombre);
                }
            }
        }
    }

    public static class Lote {
        public final String nombre, aves, edad, raza, galpon, fechaIngreso, estado;

        public Lote(String nombre, String aves, String edad, String raza, String galpon, String fechaIngreso, String estado) {
            this.nombre = nombre;
            this.aves = aves;
            this.edad = edad;
            this.raza = raza;
            this.galpon = galpon;
            this.fechaIngreso = fechaIngreso;
            this.estado = estado;
        }
    }
}
