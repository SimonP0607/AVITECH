package com.avitech.sia.iu.sanidad;

import com.avitech.sia.iu.sanidad.dto.EventoDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Window;
import com.avitech.sia.iu.ModalUtil;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

public class EventoController {

    // ---- FXML ----
    @FXML
    private DatePicker dpFecha;
    @FXML
    private ComboBox<String> cbLoteGalpon;
    @FXML
    private ComboBox<String> cbTipoEvento;
    @FXML
    private TextField txtAvesAfectadas;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private TextArea txtAcciones;
    @FXML
    private ComboBox<String> cbResponsable;

    @FXML
    private Button btnClose;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnRegistrar;

    // ---- Resultado (DTO que leerá el controlador padre) ----
    private EventoDTO result;

    public EventoDTO getResult() {
        return result;
    }

    // ====== Ciclo de vida ======
    @FXML
    public void initialize() {
        // Defaults/placeholder (se reemplazan al inyectar catálogos desde SanidadController)
        dpFecha.setValue(LocalDate.now());
        cbLoteGalpon.setItems(FXCollections.observableArrayList("Galpón 1", "Galpón 2", "Galpón 3"));
        cbTipoEvento.setItems(FXCollections.observableArrayList("Enfermedad", "Mortalidad", "Otro"));
        cbResponsable.setItems(FXCollections.observableArrayList("Dr. María González", "Dr. Carlos Ruiz", "Téc. Ana López"));

        // Aves afectadas: sólo números
        txtAvesAfectadas.textProperty().addListener((obs, oldV, newV) -> {
            if (newV == null) return;
            String digits = newV.replaceAll("\\D+", "");
            if (!digits.equals(newV)) txtAvesAfectadas.setText(digits);
        });

        // Hotkeys básicos
        btnRegistrar.sceneProperty().addListener((obs, o, scene) -> {
            if (scene == null) return;
            scene.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) btnRegistrar.fire();
                if (e.getCode() == KeyCode.ESCAPE) btnCancelar.fire();
            });
        });

        // Cerrar
        btnClose.setOnAction(e -> close());
        btnCancelar.setOnAction(e -> close());

        // Habilitar/deshabilitar Registrar por campos obligatorios
        btnRegistrar.disableProperty().bind(
                dpFecha.valueProperty().isNull()
                        .or(cbLoteGalpon.valueProperty().isNull())
                        .or(cbTipoEvento.valueProperty().isNull())
                        .or(cbResponsable.valueProperty().isNull())
                        .or(txtAvesAfectadas.textProperty().isEmpty())
                        .or(txtDescripcion.textProperty().isEmpty())
        );

        // Registrar
        btnRegistrar.setOnAction(e -> {
            if (!validar()) return;

            int aves = Integer.parseInt(txtAvesAfectadas.getText().trim());
            result = new EventoDTO(
                    dpFecha.getValue(),
                    cbLoteGalpon.getValue(),
                    cbTipoEvento.getValue(),
                    aves,
                    safeTrim(txtDescripcion.getText()),
                    safeTrim(txtAcciones.getText()),
                    cbResponsable.getValue()
            );
            close();
        });
    }

    // ====== Inyección de catálogos (desde SanidadController) ======
    public void setLotes(Collection<String> lotes) {
        cbLoteGalpon.setItems(FXCollections.observableArrayList(
                lotes == null ? FXCollections.observableArrayList() : lotes
        ));
    }

    public void setTiposEvento(Collection<String> tipos) {
        cbTipoEvento.setItems(FXCollections.observableArrayList(
                tipos == null ? FXCollections.observableArrayList() : tipos
        ));
    }

    public void setResponsables(Collection<String> responsables) {
        cbResponsable.setItems(FXCollections.observableArrayList(
                responsables == null ? FXCollections.observableArrayList() : responsables
        ));
    }

    // ====== Validación mínima ======
    private boolean validar() {
        if (dpFecha.getValue() == null
                || cbLoteGalpon.getValue() == null
                || cbTipoEvento.getValue() == null
                || cbResponsable.getValue() == null
                || isBlank(txtAvesAfectadas.getText())
                || isBlank(txtDescripcion.getText())) {

            new Alert(Alert.AlertType.WARNING,
                    "Completa los campos obligatorios (*)").showAndWait();
            return false;
        }
        try {
            int n = Integer.parseInt(txtAvesAfectadas.getText().trim());
            if (n < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.WARNING,
                    "Aves afectadas debe ser un número válido.").showAndWait();
            return false;
        }
        return true;
    }

    // ====== Util ======
    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private static String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }

    private void close() {
        ModalUtil.closeModal(btnCancelar);
    }
}


