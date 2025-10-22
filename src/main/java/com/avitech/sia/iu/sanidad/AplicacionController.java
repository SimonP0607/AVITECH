package com.avitech.sia.iu.sanidad;

import com.avitech.sia.iu.sanidad.dto.AplicacionDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Window;
import com.avitech.sia.iu.ModalUtil;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

public class AplicacionController {

    // ===== FXML =====
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cbLoteGalpon;
    @FXML private ComboBox<String> cbMedicamento;
    @FXML private TextField txtDosis;
    @FXML private ComboBox<String> cbVia;
    @FXML private ComboBox<String> cbResponsable;
    @FXML private TextArea  txtObs;

    @FXML private Button btnClose;
    @FXML private Button btnCancelar;
    @FXML private Button btnRegistrar;

    // ===== Resultado =====
    private AplicacionDTO result;
    public AplicacionDTO getResult() { return result; }

    // ===== Catálogos (inyectados por el caller) =====
    private final ObservableList<String> lotes       = FXCollections.observableArrayList();
    private final ObservableList<String> medicamentos= FXCollections.observableArrayList();
    private final ObservableList<String> vias        = FXCollections.observableArrayList();
    private final ObservableList<String> responsables= FXCollections.observableArrayList();

    /** El caller (SanidadController) puede llamar a cualquiera de estos setters antes de mostrar el modal */
    public void setLotes(Collection<String> items)        { lotes.setAll(nullSafe(items)); }
    public void setMedicamentos(Collection<String> items) { medicamentos.setAll(nullSafe(items)); }
    public void setVias(Collection<String> items)         { vias.setAll(nullSafe(items)); }
    public void setResponsables(Collection<String> items) { responsables.setAll(nullSafe(items)); }

    @FXML
    public void initialize() {
        // Enlazar combos a las listas (que el caller llenará)
        cbLoteGalpon.setItems(lotes);
        cbMedicamento.setItems(medicamentos);
        cbVia.setItems(vias);
        cbResponsable.setItems(responsables);

        // Defaults mínimos (el caller puede sobreescribir con sus catálogos reales)
        dpFecha.setValue(LocalDate.now());
        if (lotes.isEmpty())        lotes.setAll("Galpón 1", "Galpón 2", "Galpón 3");
        if (medicamentos.isEmpty()) medicamentos.setAll("Vacuna Newcastle", "Antibiótico Respiratorio", "Vitamina E+Selenio");
        if (vias.isEmpty())         vias.setAll("Oral (agua)", "Inyección", "Aerosol");
        if (responsables.isEmpty()) responsables.setAll("Dr. María González", "Dr. Carlos Ruiz", "Téc. Ana López");

        // Cierre / acciones
        btnClose.setOnAction(e -> close());
        btnCancelar.setOnAction(e -> close());
        btnRegistrar.setOnAction(e -> onConfirm());

        // Atajos: ESC = cerrar, ENTER = confirmar
        btnRegistrar.sceneProperty().addListener((obs, oldS, newS) -> {
            if (newS != null) {
                newS.setOnKeyPressed(ke -> {
                    if (ke.getCode() == KeyCode.ESCAPE) close();
                    else if (ke.getCode() == KeyCode.ENTER) onConfirm();
                });
            }
        });
    }

    private void onConfirm() {
        if (!validar()) return;

        result = new AplicacionDTO(
                dpFecha.getValue(),
                cbLoteGalpon.getValue(),
                cbMedicamento.getValue(),
                txtDosis.getText().trim(),
                cbVia.getValue(),
                cbResponsable.getValue(),
                txtObs.getText() == null ? "" : txtObs.getText().trim()
        );
        close();
    }

    private boolean validar() {
        StringBuilder sb = new StringBuilder();

        if (dpFecha.getValue() == null) sb.append("• Fecha\n");
        if (isEmpty(cbLoteGalpon.getValue())) sb.append("• Lote/Galpón\n");
        if (isEmpty(cbMedicamento.getValue())) sb.append("• Medicamento\n");
        if (isEmpty(txtDosis.getText())) sb.append("• Dosis\n");
        if (isEmpty(cbVia.getValue())) sb.append("• Vía de aplicación\n");
        if (isEmpty(cbResponsable.getValue())) sb.append("• Responsable\n");

        if (sb.length() > 0) {
            new Alert(Alert.AlertType.WARNING,
                    "Completa los campos obligatorios:\n\n" + sb).showAndWait();
            return false;
        }
        return true;
    }

    private void close() {
        ModalUtil.closeModal(btnCancelar);
    }



    private static boolean isEmpty(String s) { return s == null || s.isBlank(); }
    private static <T> Collection<T> nullSafe(Collection<T> c) { return c == null ? FXCollections.<T>observableArrayList() : c; }
}
