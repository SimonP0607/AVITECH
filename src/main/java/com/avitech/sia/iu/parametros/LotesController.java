package com.avitech.sia.iu.parametros;

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
        // TODO: Abrir diálogo para agregar nuevo lote
        System.out.println("📝 Agregar nuevo lote");
    }

    private void onVer(Lote lote) {
        // TODO: Mostrar detalles completos del lote
        System.out.println("👁 Ver detalles de: " + lote.nombre);
    }

    private void onEditar(Lote lote) {
        // TODO: Abrir diálogo para editar
        System.out.println("✎ Editar lote: " + lote.nombre);
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
