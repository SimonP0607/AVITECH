package com.avitech.sia.iu.sanidad;

import com.avitech.sia.iu.sanidad.dto.AplicacionDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;

import java.time.LocalDate;

public class AplicacionController {

    // Fields FXML (de aplicacion.fxml)
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

    private AplicacionDTO result;

    @FXML
    private void initialize() {
        // Valores demo / placeholders (cuando conectes BD, llenas estos combos con el datasource)
        dpFecha.setValue(LocalDate.now());

        cbLoteGalpon.getItems().setAll("Galpón 1", "Galpón 2", "Galpón 3");
        cbMedicamento.getItems().setAll("Vacuna Newcastle", "Antibiótico Respiratorio", "Vitamina E+Selenio");
        cbVia.getItems().setAll("Oral (agua)", "Inyección", "Aerosol");
        cbResponsable.getItems().setAll("Dr. María González", "Dr. Carlos Ruiz", "Téc. Ana López");

        // Cierre
        btnClose.setOnAction(e -> close());
        btnCancelar.setOnAction(e -> close());

        btnRegistrar.setOnAction(e -> {
            if (!validar()) return;
            result = new AplicacionDTO(
                    dpFecha.getValue(),
                    cbLoteGalpon.getValue(),
                    cbMedicamento.getValue(),
                    txtDosis.getText(),
                    cbVia.getValue(),
                    cbResponsable.getValue(),
                    txtObs.getText() == null ? "" : txtObs.getText().trim()
            );
            close();
        });
    }

    public AplicacionDTO getResult() { return result; }

    private boolean validar() {
        if (dpFecha.getValue() == null
                || cbLoteGalpon.getValue() == null
                || cbMedicamento.getValue() == null
                || cbVia.getValue() == null
                || cbResponsable.getValue() == null
                || txtDosis.getText() == null || txtDosis.getText().isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Completa los campos obligatorios (*)").showAndWait();
            return false;
        }
        return true;
    }

    private void close() {
        Window w = btnCancelar.getScene().getWindow();
        if (w != null) w.hide();
    }
}
