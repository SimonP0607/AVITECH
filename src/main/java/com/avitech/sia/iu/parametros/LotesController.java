package com.avitech.sia.iu.parametros;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LotesController {

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbEstado;
    @FXML private TableView<Row> tbl;
    @FXML private TableColumn<Row,String> cNombre,cAves,cEdad,cRaza,cGalpon,cEstado,cAccion;

    private final ObservableList<Row> master = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        cbEstado.setItems(FXCollections.observableArrayList("Todos los estados","Activo","Terminado"));
        cbEstado.getSelectionModel().selectFirst();

        cNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().nombre));
        cAves.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().aves));
        cEdad.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().edad));
        cRaza.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().raza));
        cGalpon.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().galpon));
        cEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().estado));
        cAccion.setCellFactory(col -> new TableCell<>() {
            final Hyperlink link = new Hyperlink("✎ Editar");
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                setGraphic(empty? null : link);
            }
        });

        master.setAll(
                new Row("Lote A-2024-001","5000","22","Hy-Line Brown","Galpón 1","Activo"),
                new Row("Lote A-2024-002","4800","20","Hy-Line Brown","Galpón 2","Activo"),
                new Row("Lote A-2023-015","4500","78","Hy-Line Brown","Galpón 3","Terminado")
        );
        tbl.setItems(master);
    }

    @FXML private void onAgregar(){ System.out.println("Agregar lote"); }

    public static class Row {
        public final String nombre, aves, edad, raza, galpon, estado;
        public Row(String n,String a,String e,String r,String g,String s){nombre=n;aves=a;edad=e;raza=r;galpon=g;estado=s;}
    }
}
