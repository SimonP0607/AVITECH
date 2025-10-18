package com.avitech.sia.iu.suministros;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;

public class MoverStockController implements UsesSuministrosDataSource {

    @FXML private ComboBox<String> cbProducto;
    @FXML private TextField txtCantidad;
    @FXML private ComboBox<String> cbDesde;
    @FXML private ComboBox<String> cbHacia;
    @FXML private TextArea txtMotivo;

    @FXML private Button btnMover;
    @FXML private Button btnCancelar;

    private SuministrosDataSource dataSource = new EmptySuministrosDataSource();
    @Override public void setDataSource(SuministrosDataSource ds) { this.dataSource = (ds!=null?ds:new EmptySuministrosDataSource()); loadCombos(); }

    @FXML
    private void initialize() { loadCombos(); }

    private void loadCombos() {
        if (cbProducto == null) return;
        cbProducto.setItems(FXCollections.observableArrayList(
                dataSource.getProductos().stream().map(SuministrosDataSource.Producto::nombre).toList()
        ));
        var ubics = dataSource.getUbicaciones().stream().map(SuministrosDataSource.Ubicacion::nombre).toList();
        cbDesde.setItems(FXCollections.observableArrayList(ubics));
        cbHacia.setItems(FXCollections.observableArrayList(ubics));
    }

    @FXML
    private void onMover() {
        String prod = sel(cbProducto);
        String desde = sel(cbDesde);
        String hacia = sel(cbHacia);

        if (isBlank(prod))  { warn("Seleccione un producto."); return; }
        if (!isNumber(txtCantidad.getText())) { warn("Ingrese una cantidad válida."); return; }
        if (isBlank(desde)) { warn("Seleccione la ubicación origen."); return; }
        if (isBlank(hacia)) { warn("Seleccione la ubicación destino."); return; }
        if (desde.equals(hacia)) { warn("La ubicación origen y destino no pueden ser la misma."); return; }

        var req = new SuministrosDataSource.TransferReq(
                findProductoId(prod),
                new BigDecimal(txtCantidad.getText()),
                findUbicId(desde),
                findUbicId(hacia),
                nullIfBlank(txtMotivo.getText())
        );
        boolean ok = dataSource.transferir(req);
        if (ok) { info("Movimiento realizado correctamente."); clear(); }
        else    { warn("No se pudo realizar la transferencia."); }
    }

    @FXML
    private void onCancelar() { clear(); }

    private void clear() {
        cbProducto.getSelectionModel().clearSelection();
        txtCantidad.clear();
        cbDesde.getSelectionModel().clearSelection();
        cbHacia.getSelectionModel().clearSelection();
        txtMotivo.clear();
    }

    /* helpers */
    private static boolean isBlank(String s){ return s == null || s.trim().isEmpty(); }
    private static boolean isNumber(String s){ try { new java.math.BigDecimal(s); return true; } catch(Exception e){ return false; } }
    private String sel(ComboBox<String> cb){ var s = cb.getSelectionModel().getSelectedItem(); return s==null? "" : s; }
    private void warn(String msg){ new Alert(Alert.AlertType.WARNING, msg).showAndWait(); }
    private void info(String msg){ new Alert(Alert.AlertType.INFORMATION, msg).showAndWait(); }
    private static String nullIfBlank(String s){ return isBlank(s) ? null : s.trim(); }
    private Long findProductoId(String nombre){
        return dataSource.getProductos().stream().filter(p -> p.nombre().equals(nombre)).map(SuministrosDataSource.Producto::id).findFirst().orElse(null);
    }
    private Long findUbicId(String nombre){
        return dataSource.getUbicaciones().stream().filter(u -> u.nombre().equals(nombre)).map(SuministrosDataSource.Ubicacion::id).findFirst().orElse(null);
    }
}
