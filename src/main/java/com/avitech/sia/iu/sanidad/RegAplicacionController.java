package com.avitech.sia.iu.sanidad;

import com.avitech.sia.db.SanidadDAO;
import com.avitech.sia.db.UsuarioDAO;
import com.avitech.sia.iu.sanidad.dto.AplicacionDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class RegAplicacionController {

    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cbLoteGalpon; // Renombrado para coincidir con DTO
    @FXML private ComboBox<String> cbMedicamento;
    @FXML private TextField tfDosis;
    @FXML private ComboBox<String> cbViaAplicacion; // Renombrado para coincidir con DTO
    @FXML private ComboBox<String> cbResponsable;
    @FXML private TextArea taObs;

    private AplicacionDTO result;

    @FXML
    public void initialize() {
        dpFecha.setValue(LocalDate.now());
        cbViaAplicacion.getItems().addAll("Oral", "Agua", "Inyectable", "Tópica");

        try {
            cbLoteGalpon.getItems().setAll(SanidadDAO.getLotes());
            cbMedicamento.getItems().setAll(SanidadDAO.getMedicamentosNombres());
            cbResponsable.getItems().setAll(UsuarioDAO.getAllUsernames());
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudieron cargar los datos para el formulario: " + e.getMessage()).showAndWait();
        }
    }

    public AplicacionDTO getResult() { return result; }

    @FXML
    private void onClose() {
        Stage stage = (Stage) dpFecha.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onSave() {
        if (!valid()) return;

        // Corregida la creación del DTO para que coincida con los campos del record
        result = new AplicacionDTO(
                dpFecha.getValue(),
                cbLoteGalpon.getValue(),
                cbMedicamento.getValue(),
                tfDosis.getText().trim(),
                cbViaAplicacion.getValue(),
                cbResponsable.getValue(),
                taObs.getText().trim()
        );

        onClose();
    }

    private boolean valid() {
        StringBuilder sb = new StringBuilder();
        if (dpFecha.getValue() == null) sb.append("• Fecha.\n");
        if (isEmpty(cbLoteGalpon)) sb.append("• Lote/Galpón.\n");
        if (isEmpty(cbMedicamento)) sb.append("• Medicamento.\n");
        if (tfDosis.getText() == null || tfDosis.getText().isBlank()) sb.append("• Dosis.\n");
        if (isEmpty(cbViaAplicacion)) sb.append("• Vía de aplicación.\n");
        if (isEmpty(cbResponsable)) sb.append("• Responsable.\n");

        if (sb.length() > 0) {
            new Alert(Alert.AlertType.WARNING, "Completa los campos:\n\n" + sb).showAndWait();
            return false;
        }
        return true;
    }

    private boolean isEmpty(ComboBox<String> cb) {
        return cb.getValue() == null || cb.getValue().isBlank();
    }
}
