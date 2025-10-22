package com.avitech.sia.iu.suministros;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class SalidaController implements UsesSuministrosDataSource {

    /* Formulario */
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cbTipo;     // Consumo, Merma, Transferencia
    @FXML private ComboBox<String> cbProducto;
    @FXML private TextField txtCantidad;
    @FXML private ComboBox<String> cbUnidad;
    @FXML private ComboBox<String> cbUbicacion; // origen
    @FXML private ComboBox<String> cbDestino;   // destino (si transferencia)
    @FXML private TextArea txtObs;

    /* Tabla detalle */
    @FXML private TableView<DetalleSalida> tblDetalle;
    @FXML private TableColumn<DetalleSalida, String> colProd, colCant, colTipo, colDestino;

    /* Footer */
    @FXML private Button btnRegistrar, btnCancelar;

    private final ObservableList<DetalleSalida> detalle = FXCollections.observableArrayList();
    private SuministrosDataSource dataSource = new EmptySuministrosDataSource();
    @Override public void setDataSource(SuministrosDataSource ds) { this.dataSource = (ds!=null?ds:new EmptySuministrosDataSource()); loadCombos(); }

    @FXML
    public void initialize() {
        dpFecha.setValue(LocalDate.now());

        colProd.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().producto()));
        colCant.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().cantidad().stripTrailingZeros().toPlainString() + " " + c.getValue().unidad()));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().tipo()));
        colDestino.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().destino()==null? "—" : c.getValue().destino()));

        tblDetalle.setItems(detalle);

        loadCombos();
    }

    private void loadCombos() {
        if (cbProducto == null) return;
        cbTipo.setItems(FXCollections.observableArrayList("Consumo","Merma","Transferencia"));
        cbProducto.setItems(FXCollections.observableArrayList(dataSource.getProductos().stream().map(SuministrosDataSource.Producto::nombre).toList()));
        cbUnidad.setItems(FXCollections.observableArrayList(dataSource.getUnidades()));
        var ubics = dataSource.getUbicaciones().stream().map(SuministrosDataSource.Ubicacion::nombre).toList();
        cbUbicacion.setItems(FXCollections.observableArrayList(ubics));
        cbDestino.setItems(FXCollections.observableArrayList(ubics));
    }

    @FXML
    private void addDetalle() {
        String tipo = sel(cbTipo);
        String prod = sel(cbProducto);
        String unidad = sel(cbUnidad);
        String ubic = sel(cbUbicacion);
        String destino = selAllowEmpty(cbDestino);

        if (isBlank(tipo))  { warn("Seleccione el tipo de salida."); return; }
        if (isBlank(prod))  { warn("Seleccione un producto."); return; }
        if (!isNumber(txtCantidad.getText())) { warn("Ingrese una cantidad válida."); return; }
        if (isBlank(unidad)) { warn("Seleccione la unidad."); return; }
        if (isBlank(ubic))   { warn("Seleccione la ubicación de origen."); return; }
        if ("Transferencia".equalsIgnoreCase(tipo) && isBlank(destino)) { warn("Seleccione el destino."); return; }

        detalle.add(new DetalleSalida(prod, bd(txtCantidad.getText()), unidad, tipo, nullIfBlank(destino)));
        clearRowFields();
    }

    @FXML
    private void undoLast() {
        if (!detalle.isEmpty()) detalle.remove(detalle.size() - 1);
    }

    @FXML
    private void guardarSalida() {
        if (detalle.isEmpty()) { warn("Agregue al menos un ítem al detalle."); return; }
        if (dpFecha.getValue() == null) { warn("Seleccione la fecha."); return; }
        if (isBlank(sel(cbUbicacion))) { warn("Seleccione la ubicación de origen."); return; }

        SuministrosDataSource.SalidaHeader header = new SuministrosDataSource.SalidaHeader(
                dpFecha.getValue(), sel(cbTipo), findUbicId(sel(cbUbicacion)), nullIfBlank(selAllowEmpty(cbDestino)), nullIfBlank(txtObs.getText())
        );
        List<SuministrosDataSource.SalidaLine> lines = detalle.stream().map(d -> new SuministrosDataSource.SalidaLine(
                findProductoId(d.producto()), d.cantidad(), d.unidad()
        )).toList();

        boolean ok = dataSource.saveSalida(header, lines);
        if (ok) { info("Salida registrada correctamente."); clearForm(); }
        else    { warn("No se pudo registrar la salida."); }
    }

    @FXML
    private void clearForm() {
        dpFecha.setValue(LocalDate.now());
        cbTipo.getSelectionModel().clearSelection();
        cbProducto.getSelectionModel().clearSelection();
        txtCantidad.clear();
        cbUnidad.getSelectionModel().clearSelection();
        cbUbicacion.getSelectionModel().clearSelection();
        cbDestino.getSelectionModel().clearSelection();
        txtObs.clear();
        detalle.clear();
    }

    /* helpers */
    private void clearRowFields() { cbProducto.getSelectionModel().clearSelection(); txtCantidad.clear(); cbUnidad.getSelectionModel().clearSelection(); cbDestino.getSelectionModel().clearSelection(); }
    private static boolean isBlank(String s){ return s == null || s.trim().isEmpty(); }
    private static boolean isNumber(String s){ try { new java.math.BigDecimal(s); return true; } catch(Exception e){ return false; } }
    private static BigDecimal bd(String s){ try { return new BigDecimal(s); } catch(Exception e){ return BigDecimal.ZERO; } }
    private static String nullIfBlank(String s){ return isBlank(s) ? null : s.trim(); }
    private String sel(ComboBox<String> cb){ var s = cb.getSelectionModel().getSelectedItem(); return s==null? "" : s; }
    private String selAllowEmpty(ComboBox<String> cb){ var s = cb.getSelectionModel().getSelectedItem(); return s==null? "" : s; }
    private void warn(String msg){ new Alert(Alert.AlertType.WARNING, msg).showAndWait(); }
    private void info(String msg){ new Alert(Alert.AlertType.INFORMATION, msg).showAndWait(); }

    private Long findProductoId(String nombre){
        return dataSource.getProductos().stream().filter(p -> p.nombre().equals(nombre)).map(SuministrosDataSource.Producto::id).findFirst().orElse(null);
    }
    private Long findUbicId(String nombre){
        return dataSource.getUbicaciones().stream().filter(u -> u.nombre().equals(nombre)).map(SuministrosDataSource.Ubicacion::id).findFirst().orElse(null);
    }

    /* modelo */
    public record DetalleSalida(String producto, BigDecimal cantidad, String unidad, String tipo, String destino) {}
}
