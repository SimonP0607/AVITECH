package com.avitech.sia.iu.suministros;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

public class StockController implements UsesSuministrosDataSource {

    /* Filtros top */
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbCategoria;
    @FXML private ComboBox<String> cbUbicacion;

    /* Tabla */
    @FXML private TableView<ItemStock> tblStock;
    @FXML private TableColumn<ItemStock, String> colCodigo, colNombre, colCat, colLote, colVence, colUbic;
    @FXML private TableColumn<ItemStock, Number> colDisp, colMin;

    /* Totales */
    @FXML private Label lblTotalItems, lblTotalCant;

    /* Lista de alertas (abajo) */
    @FXML private ListView<String> listStockBajo;

    private final ObservableList<ItemStock> master = FXCollections.observableArrayList();
    private final ObservableList<ItemStock> filtered = FXCollections.observableArrayList();

    private SuministrosDataSource dataSource = new EmptySuministrosDataSource();
    @Override public void setDataSource(SuministrosDataSource ds) { this.dataSource = (ds!=null?ds:new EmptySuministrosDataSource()); loadCatalogsAndData(); }

    @FXML
    public void initialize() {
        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().codigo()));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().nombre()));
        colCat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().categoria()));
        colLote.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().lote()==null?"—":c.getValue().lote()));
        colVence.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().vence()==null?"—":c.getValue().vence().toString()));
        colUbic.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().ubicacion()));
        colDisp.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().disponible()));
        colMin.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().minimo()));
        tblStock.setItems(filtered);

        loadCatalogsAndData();
    }

    private void loadCatalogsAndData() {
        if (cbCategoria == null) return;
        List<String> cats = dataSource.getCategorias();
        cbCategoria.setItems(FXCollections.observableArrayList("Todas"));
        cbCategoria.getItems().addAll(cats);

        List<String> ubics = dataSource.getUbicaciones().stream().map(SuministrosDataSource.Ubicacion::nombre).toList();
        cbUbicacion.setItems(FXCollections.observableArrayList("Todas"));
        cbUbicacion.getItems().addAll(ubics);

        cbCategoria.getSelectionModel().selectFirst();
        cbUbicacion.getSelectionModel().selectFirst();

        reloadStock();
    }

    private void reloadStock() {
        var filter = new SuministrosDataSource.StockFilter(
                txtBuscar==null? "" : txtBuscar.getText(),
                sel(cbCategoria),
                sel(cbUbicacion)
        );
        var items = dataSource.getStock(filter);
        master.setAll(items.stream().map(StockController::mapItem).toList());
        applyFilter();
    }

    private static ItemStock mapItem(SuministrosDataSource.StockItem it) {
        return new ItemStock(
                nz(it.codigo()), nz(it.nombre()), nz(it.categoria()),
                it.lote(), it.vence(),
                nz(it.ubicacion()),
                it.disponible()==null? 0 : it.disponible().intValue(),
                it.minimo()==null? 0 : it.minimo().intValue()
        );
    }

    @FXML
    private void exportCsv() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("stock_export.csv", false))) {
            pw.println("Codigo,Nombre,Categoria,Lote,Vence,Ubicacion,Disponible,Minimo");
            for (ItemStock it : filtered) {
                pw.printf("%s,%s,%s,%s,%s,%s,%d,%d%n",
                        it.codigo(), it.nombre(), it.categoria(),
                        it.lote()==null?"":it.lote(),
                        it.vence()==null?"":it.vence(),
                        it.ubicacion(),
                        it.disponible(), it.minimo());
            }
            info("Exportado correctamente a stock_export.csv");
        } catch (Exception e) {
            error("No se pudo exportar el CSV: " + e.getMessage());
        }
    }

    @FXML
    private void verAlertasBajo() {
        if (listStockBajo.getItems().isEmpty()) {
            info("No hay productos con stock bajo.");
        } else {
            StringBuilder sb = new StringBuilder("Productos con stock bajo:\n");
            listStockBajo.getItems().forEach(s -> sb.append("• ").append(s).append("\n"));
            info(sb.toString());
        }
    }

    @FXML
    private void applyFilter() {
        String q = (txtBuscar.getText()==null? "" : txtBuscar.getText().toLowerCase().trim());
        String cat = sel(cbCategoria);
        String ubic = sel(cbUbicacion);

        filtered.setAll(master.filtered(it ->
                (q.isEmpty() || it.nombre().toLowerCase().contains(q) || it.codigo().toLowerCase().contains(q)) &&
                        ("Todas".equalsIgnoreCase(cat) || it.categoria().equalsIgnoreCase(cat)) &&
                        ("Todas".equalsIgnoreCase(ubic) || it.ubicacion().equalsIgnoreCase(ubic))
        ));
        tblStock.refresh();
        refreshTotals();
        fillLowStockList();
    }

    private void refreshTotals() {
        lblTotalItems.setText("Total ítems: " + filtered.size());
        int sum = filtered.stream().mapToInt(ItemStock::disponible).sum();
        lblTotalCant.setText("Cantidad total: " + sum);
    }

    private void fillLowStockList() {
        listStockBajo.getItems().setAll(
                filtered.stream()
                        .filter(it -> it.disponible() <= it.minimo())
                        .map(it -> it.nombre() + " — Disp: " + it.disponible() + " / Min: " + it.minimo())
                        .toList()
        );
    }

    private static String sel(ComboBox<String> cb) { var s = cb.getSelectionModel().getSelectedItem(); return s==null? "Todas" : s; }
    private static void info(String msg){ new Alert(Alert.AlertType.INFORMATION, msg).showAndWait(); }
    private static void error(String msg){ new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
    private static String nz(String s){ return s==null? "—": s; }

    /* modelo UI */
    public record ItemStock(String codigo, String nombre, String categoria, String lote, LocalDate vence,
                            String ubicacion, int disponible, int minimo) {}
}
