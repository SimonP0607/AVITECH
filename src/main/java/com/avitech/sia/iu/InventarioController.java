package com.avitech.sia.iu;

import com.avitech.sia.App;
import com.avitech.sia.db.SuministrosDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class InventarioController {

    @FXML private TableView<SuministrosDAO.InventarioItem> tblInventario;
    @FXML private TableColumn<SuministrosDAO.InventarioItem, String> colProducto;
    @FXML private TableColumn<SuministrosDAO.InventarioItem, String> colStock;
    @FXML private TableColumn<SuministrosDAO.InventarioItem, String> colUnidad;

    private final ObservableList<SuministrosDAO.InventarioItem> inventario = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colProducto.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().producto));
        colStock.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().stock)));
        colUnidad.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().unidad));

        tblInventario.setItems(inventario);

        cargarInventario();
    }

    private void cargarInventario() {
        try {
            List<SuministrosDAO.InventarioItem> items = SuministrosDAO.getInventario();
            inventario.setAll(items);
        } catch (Exception e) {
            e.printStackTrace();
            // Manejar el error, por ejemplo, mostrando una alerta
        }
    }

    @FXML
    private void onVolver() {
        App.goTo("/fxml/suministros.fxml", "SIA Avitech — Suministros");
    }
}
