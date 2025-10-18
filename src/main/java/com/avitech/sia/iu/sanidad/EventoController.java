package com.avitech.sia.iu.sanidad;

import com.avitech.sia.iu.sanidad.dto.EventoDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;

import java.time.LocalDate;

public class EventoController {

    // Fields FXML (de evento.fxml)
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cbLoteGalpon;
    @FXML private ComboBox<String> cbTipoEvento;
    @FXML private TextField txtAvesAfectadas;
    @FXML private TextArea  txtDescripcion;
    @FXML private TextArea  txtAcciones;
    @FXML private ComboBox<String> cbResponsable;

    @FXML private Button btnClose;
    @FXML private Button btnCancelar;
    @FXML private Button btnRegistrar;

    private EventoDTO result;

    @FXML
    private void initialize() {
        dpFecha.setValue(LocalDate.now());

        cbLoteGalpon.getItems().setAll("Galpón 1", "Galpón 2", "Galpón 3");
        cbTipoEvento.getItems().setAll("Enfermedad", "Mortalidad", "Otro");
        cbResponsable.getItems().setAll("Dr. María González", "Dr. Carlos Ruiz", "Téc. Ana López");

        btnClose.setOnAction(e -> close());
        btnCancelar.setOnAction(e -> close());

        btnRegistrar.setOnAction(e -> {
            if (!validar()) return;
            Integer aves = Integer.valueOf(txtAvesAfectadas.getText().trim());
            result = new EventoDTO(
                    dpFecha.getValue(),
                    cbLoteGalpon.getValue(),
                    cbTipoEvento.getValue(),
                    aves,
                    txtDescripcion.getText() == null ? "" : txtDescripcion.getText().trim(),
                    txtAcciones.getText() == null ? "" : txtAcciones.getText().trim(),
                    cbResponsable.getValue()
            );
            close();
        });
    }

    public EventoDTO getResult() { return result; }

    private boolean validar() {
        if (dpFecha.getValue() == null
                || cbLoteGalpon.getValue() == null
                || cbTipoEvento.getValue() == null
                || cbResponsable.getValue() == null
                || txtAvesAfectadas.getText() == null || txtAvesAfectadas.getText().isBlank()
                || txtDescripcion.getText() == null || txtDescripcion.getText().isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Completa los campos obligatorios (*)").showAndWait();
            return false;
        }
        try {
            int n = Integer.parseInt(txtAvesAfectadas.getText().trim());
            if (n < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.WARNING, "Aves afectadas debe ser un número válido.").showAndWait();
            return false;
        }
        return true;
    }

    private void close() {
        Window w = btnCancelar.getScene().getWindow();
        if (w != null) w.hide();
    }
}
