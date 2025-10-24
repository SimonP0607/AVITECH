package com.avitech.sia.iu.parametros;

import com.avitech.sia.iu.ModalUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

public class MedicamentosController {
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbTipo, cbEstado;
    @FXML private TableView<Medicamento> tbl;
    @FXML private TableColumn<Medicamento,String> cNombre,cPActivo,cTipo,cDosis,cRetiro,cEstado,cAccion;

    private final ObservableList<Medicamento> master = FXCollections.observableArrayList();
    private final ObservableList<Medicamento> filtered = FXCollections.observableArrayList();

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
            "Todos los tipos", "Antibiótico", "Vitamina", "Vacuna", "Antiparasitario", "Desinfectante"
        ));
        cbTipo.getSelectionModel().selectFirst();

        cbEstado.setItems(FXCollections.observableArrayList("Todos los estados","Activo","Inactivo"));
        cbEstado.getSelectionModel().selectFirst();
    }

    private void setupTable() {
        cNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().nombre));
        cPActivo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().pActivo));
        cTipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().tipo));
        cDosis.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().dosis));
        cRetiro.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().retiroDias));
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
                new Medicamento("Enrofloxacina 10%","Enrofloxacina","Antibiótico","10mg/kg peso vivo","5","Activo"),
                new Medicamento("Vitaminas AD3E","Vit. A, D3, E","Vitamina","1ml/L de agua","0","Activo"),
                new Medicamento("Vacuna Newcastle","Virus inactivado","Vacuna","0.5ml/ave","0","Activo"),
                new Medicamento("Ivermectina 1%","Ivermectina","Antiparasitario","0.2ml/kg","14","Activo"),
                new Medicamento("Complejo B","Vit. B1, B6, B12","Vitamina","2ml/L de agua","0","Activo"),
                new Medicamento("Sulfato de Cobre","CuSO4","Desinfectante","200ppm en agua","7","Activo")
        );
        applyFilters();
    }

    private void applyFilters() {
        String searchText = txtSearch.getText().toLowerCase();
        String tipoFilter = cbTipo.getValue();
        String estadoFilter = cbEstado.getValue();

        filtered.setAll(master.stream()
                .filter(m -> {
                    if (!searchText.isEmpty() &&
                        !m.nombre.toLowerCase().contains(searchText) &&
                        !m.pActivo.toLowerCase().contains(searchText)) {
                        return false;
                    }
                    if (tipoFilter != null && !tipoFilter.equals("Todos los tipos") &&
                        !m.tipo.equals(tipoFilter)) {
                        return false;
                    }
                    if (estadoFilter != null && !estadoFilter.equals("Todos los estados") &&
                        !m.estado.equals(estadoFilter)) {
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
        // Abrir modal para agregar nuevo medicamento
        NuevoMedicamentoController ctrl = ModalUtil.openModal(
                tbl,
                "/fxml/Parametros/modal_medicamento.fxml",
                "Nuevo Medicamento"
        );

        if (ctrl != null && ctrl.getResult() != null) {
            // Agregar a la lista
            master.add(ctrl.getResult());
            applyFilters();

            // TODO: Aquí se insertará en la base de datos
            System.out.println("✅ Nuevo medicamento agregado: " + ctrl.getResult().nombre);
        }
    }

    private void onEditar(Medicamento med) {
        // Abrir modal para editar medicamento
        NuevoMedicamentoController ctrl = ModalUtil.openModal(
                tbl,
                "/fxml/Parametros/modal_medicamento.fxml",
                "Editar Medicamento"
        );

        if (ctrl != null) {
            ctrl.setEditMode(med);

            if (ctrl.getResult() != null) {
                // Actualizar en la lista
                int index = master.indexOf(med);
                if (index >= 0) {
                    master.set(index, ctrl.getResult());
                    applyFilters();

                    // TODO: Aquí se actualizará en la base de datos
                    System.out.println("✅ Medicamento actualizado: " + ctrl.getResult().nombre);
                }
            }
        }
    }

    private void onEliminar(Medicamento med) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Eliminar el medicamento '" + med.nombre + "'?");
        alert.setContentText("Esta acción no se puede deshacer.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                master.remove(med);
                applyFilters();
                System.out.println("🗑 Eliminado: " + med.nombre);
            }
        });
    }

    public static class Medicamento {
        public final String nombre, pActivo, tipo, dosis, retiroDias, estado;

        public Medicamento(String nombre, String pActivo, String tipo, String dosis, String retiroDias, String estado) {
            this.nombre = nombre;
            this.pActivo = pActivo;
            this.tipo = tipo;
            this.dosis = dosis;
            this.retiroDias = retiroDias;
            this.estado = estado;
        }
    }
}
