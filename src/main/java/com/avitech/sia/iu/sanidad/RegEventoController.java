package com.avitech.sia.iu.sanidad;

import com.avitech.sia.db.SanidadDAO;
import com.avitech.sia.db.UsuarioDAO;
import com.avitech.sia.iu.sanidad.dto.EventoDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class RegEventoController {

    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cbLoteAfectado;
    @FXML private ComboBox<String> cbTipoEvento;
    @FXML private TextField tfAvesAfectadas;
    @FXML private TextArea taDescripcion;
    @FXML private TextArea taAcciones;
    @FXML private ComboBox<String> cbResponsable;

    private EventoDTO result;

    @FXML
    public void initialize() {
        dpFecha.setValue(LocalDate.now());
        try {
            cbLoteAfectado.getItems().setAll(SanidadDAO.getLotes());
            cbTipoEvento.getItems().setAll(SanidadDAO.getTiposEvento());
            cbResponsable.getItems().setAll(UsuarioDAO.getAllUsernames());
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al cargar datos para el formulario de evento: " + e.getMessage()).showAndWait();
        }
    }

    public EventoDTO getResult() { return result; }

    @FXML
    private void onClose() {
        Stage stage = (Stage) dpFecha.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onSave() {
        if (!valid()) return;

        result = new EventoDTO(
                dpFecha.getValue(),
                valueOf(cbLoteAfectado),
                valueOf(cbTipoEvento),
                tfAvesAfectadas.getText().trim(),
                taDescripcion.getText().trim(),
                taAcciones.getText().trim(),
                valueOf(cbResponsable)
        );
        onClose();
    }

    private boolean valid() {
        StringBuilder sb = new StringBuilder();

        if (dpFecha.getValue() == null) sb.append("• Fecha.\n");
        if (isEmpty(cbLoteAfectado)) sb.append("• Lote/Galpón afectado.\n");
        if (isEmpty(cbTipoEvento)) sb.append("• Tipo de evento.\n");
        if (tfAvesAfectadas.getText() == null || tfAvesAfectadas.getText().isBlank()) sb.append("• Aves afectadas.\n");
        if (taDescripcion.getText() == null || taDescripcion.getText().isBlank()) sb.append("• Descripción del evento.\n");
        if (taAcciones.getText() == null || taAcciones.getText().isBlank()) sb.append("• Acciones tomadas.\n");
        if (isEmpty(cbResponsable)) sb.append("• Responsable del reporte.\n");

        // Validar que Aves Afectadas sea un número
        try { Integer.parseInt(tfAvesAfectadas.getText()); } catch (NumberFormatException e) { sb.append("• Aves Afectadas debe ser un número válido.\n"); }

        if (sb.length() > 0) {
            new Alert(Alert.AlertType.WARNING, "Completa los campos:\n\n" + sb).showAndWait();
            return false;
        }
        return true;
    }

    private boolean isEmpty(ComboBox<String> cb) {
        return cb.getValue() == null || cb.getValue().isBlank();
    }

    private String valueOf(ComboBox<String> cb) {
        return cb.getValue() == null ? "" : cb.getValue();
    }
}
