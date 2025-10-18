package com.avitech.sia.iu.suministros;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EntradaController implements UsesSuministrosDataSource {

    /* Formulario */
    @FXML private ComboBox<String> cbProducto;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtCantidad;
    @FXML private ComboBox<String> cbUnidad;
    @FXML private ComboBox<String> cbUbicacion;
    @FXML private TextField txtProveedor;
    @FXML private TextField txtLote;
    @FXML private DatePicker dpVence;
    @FXML private TextArea txtObs;

    /* Tabla detalle */
    @FXML private TableView<DetalleEntrada> tblDetalle;
    @FXML private TableColumn<DetalleEntrada, String> colProd, colCant, colCosto, colLote, colVence;

    /* Footer */
    @FXML private Button btnRegistrar, btnCancelar;

    private final ObservableList<DetalleEntrada> detalle = FXCollections.observableArrayList();
    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private SuministrosDataSource dataSource = new EmptySuministrosDataSource();
    @Override public void setDataSource(SuministrosDataSource ds) { this.dataSource = (ds!=null?ds:new EmptySuministrosDataSource()); loadCombos(); }

    @FXML
    private void initialize() {
        dpFecha.setValue(LocalDate.now());

        colProd.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().producto()));
        colCant.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().cantidad() + " " + c.getValue().unidad()));
        colCosto.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().costo().toPlainString()));
        colLote.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().lote()==null? "—" : c.getValue().lote()));
        colVence.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().vence()==null? "—" : df.format(c.getValue().vence())));

        tblDetalle.setItems(detalle);

        loadCombos();
    }

    private void loadCombos() {
        if (cbProducto == null) return;
        List<String> productos = dataSource.getProductos().stream().map(SuministrosDataSource.Producto::nombre).toList();
        cbProducto.setItems(FXCollections.observableArrayList(productos));
        cbUnidad.setItems(FXCollections.observableArrayList(dataSource.getUnidades()));
        cbUbicacion.setItems(FXCollections.observableArrayList(
                dataSource.getUbicaciones().stream().map(SuministrosDataSource.Ubicacion::nombre).toList()
        ));
    }

    @FXML
    private void addDetalle() {
        String prod = sel(cbProducto);
        String unidad = sel(cbUnidad);
        String ubic = sel(cbUbicacion);

        if (isBlank(prod)) { warn("Seleccione un producto."); return; }
        if (!isNumber(txtCantidad.getText())) { warn("Ingrese una cantidad válida."); return; }
        if (isBlank(unidad)) { warn("Seleccione una unidad."); return; }
        if (isBlank(ubic)) { warn("Seleccione una ubicación."); return; }

        // costo unitario (si luego agregas campo, por ahora 0)
        BigDecimal costo = BigDecimal.ZERO;

        detalle.add(new DetalleEntrada(
                prod,
                bd(txtCantidad.getText()),
                unidad,
                costo,
                nullIfBlank(txtLote.getText()),
                dpVence.getValue()
        ));
        clearRowFields();
    }

    @FXML
    private void clearForm() {
        cbProducto.getSelectionModel().clearSelection();
        dpFecha.setValue(LocalDate.now());
        txtCantidad.clear();
        cbUnidad.getSelectionModel().clearSelection();
        cbUbicacion.getSelectionModel().clearSelection();
        txtProveedor.clear();
        txtLote.clear();
        dpVence.setValue(null);
        txtObs.clear();
        detalle.clear();
    }

    @FXML
    private void guardarEntrada() {
        if (detalle.isEmpty()) { warn("Agregue al menos un ítem al detalle."); return; }
        if (dpFecha.getValue() == null) { warn("Seleccione la fecha de la entrada."); return; }
        if (isBlank(sel(cbUbicacion))) { warn("Seleccione la ubicación."); return; }

        // Map cabecera + detalle a DTOs del DataSource
        Long ubicId = findUbicId(sel(cbUbicacion));
        SuministrosDataSource.EntradaHeader header = new SuministrosDataSource.EntradaHeader(
                dpFecha.getValue(), nullIfBlank(txtProveedor.getText()), ubicId, nullIfBlank(txtObs.getText())
        );
        List<SuministrosDataSource.EntradaLine> lines = detalle.stream().map(d -> new SuministrosDataSource.EntradaLine(
                findProductoId(d.producto()), d.cantidad(), d.unidad(), d.costo(), d.lote(), d.vence()
        )).toList();

        boolean ok = dataSource.saveEntrada(header, lines);
        if (ok) { info("Entrada registrada correctamente."); clearForm(); }
        else    { warn("No se pudo registrar la entrada."); }
    }

    /* helpers */
    private void clearRowFields() {
        cbProducto.getSelectionModel().clearSelection();
        txtCantidad.clear();
        cbUnidad.getSelectionModel().clearSelection();
        txtLote.clear();
        dpVence.setValue(null);
    }
    private String sel(ComboBox<String> cb){ var s = cb.getSelectionModel().getSelectedItem(); return s==null? "" : s; }
    private static boolean isBlank(String s){ return s == null || s.trim().isEmpty(); }
    private static boolean isNumber(String s){ try { new java.math.BigDecimal(s); return true; } catch(Exception e){ return false; } }
    private static BigDecimal bd(String s){ try { return new BigDecimal(s); } catch(Exception e){ return BigDecimal.ZERO; } }
    private static String nullIfBlank(String s){ return isBlank(s)? null : s.trim(); }
    private void warn(String msg){ new Alert(Alert.AlertType.WARNING, msg).showAndWait(); }
    private void info(String msg){ new Alert(Alert.AlertType.INFORMATION, msg).showAndWait(); }

    private Long findProductoId(String nombre){
        return dataSource.getProductos().stream().filter(p -> p.nombre().equals(nombre)).map(SuministrosDataSource.Producto::id).findFirst().orElse(null);
    }
    private Long findUbicId(String nombre){
        return dataSource.getUbicaciones().stream().filter(u -> u.nombre().equals(nombre)).map(SuministrosDataSource.Ubicacion::id).findFirst().orElse(null);
    }

    /* modelo UI */
    public record DetalleEntrada(String producto, BigDecimal cantidad, String unidad, BigDecimal costo, String lote, LocalDate vence) {}
}
