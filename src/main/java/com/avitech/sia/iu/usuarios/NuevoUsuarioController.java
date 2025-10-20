package com.avitech.sia.iu.usuarios;

import com.avitech.sia.iu.usuarios.dto.UsuarioDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class NuevoUsuarioController {

    @FXML private TextField tfNombre;
    @FXML private TextField tfUsuario;
    @FXML private PasswordField pfPwd;
    @FXML private PasswordField pfPwd2;
    @FXML private ComboBox<String> cbEstado;
    @FXML private ComboBox<String> cbRol;
    @FXML private TextArea taRolDesc;
    @FXML private Button btnClose;
    @FXML private Button btnCancelar;
    @FXML private Button btnCrear;

    private UsuarioDTO result;
    private Runnable onClose;
    private Runnable onSave;

    @FXML
    private void initialize() {
        if (cbEstado.getItems().isEmpty()) {
            cbEstado.getItems().addAll("Activo", "Inactivo");
            cbEstado.getSelectionModel().selectFirst();
        }

        btnClose.setOnAction(e -> close());
        btnCancelar.setOnAction(e -> close());
        btnCrear.setOnAction(e -> onCrear());
    }

    /* ====== Catálogos ====== */
    public void setCatalogos(List<String> roles, List<String> estados) {
        setRoles(roles);
        setEstados(estados);
    }

    public void setRoles(List<String> roles) {
        if (roles != null && !roles.isEmpty()) {
            cbRol.getItems().setAll(roles);
            cbRol.getSelectionModel().selectFirst();
        }
    }

    public void setRoleDescriptions(Map<String, String> desc) {
        if (desc != null) {
            cbRol.valueProperty().addListener((o, a, b) -> {
                String d = (b != null) ? desc.getOrDefault(b, "") : "";
                taRolDesc.setText(d);
            });
        }
    }

    public void setEstados(List<String> estados) {
        if (estados != null && !estados.isEmpty()) {
            cbEstado.getItems().setAll(estados);
            cbEstado.getSelectionModel().selectFirst();
        } else if (cbEstado.getItems().isEmpty()) {
            cbEstado.getItems().setAll("Activo", "Inactivo");
            cbEstado.getSelectionModel().selectFirst();
        }
    }

    /* ====== Eventos externos ====== */
    public void setOnClose(Runnable r) { this.onClose = r; }
    public void setOnSave(Runnable r)  { this.onSave  = r; }

    /* ====== Crear usuario ====== */
    private void onCrear() {
        if (!validar()) return;

        String usuario = tfUsuario.getText().trim();
        if (!usuario.startsWith("@")) usuario = "@" + usuario;

        result = new UsuarioDTO(
                tfNombre.getText().trim(),
                usuario,
                cbEstado.getValue(),
                cbRol.getValue(),
                (taRolDesc.getText() == null ? "" : taRolDesc.getText().trim()),
                pfPwd.getText()
        );

        if (onSave != null) onSave.run();
        close();
    }

    private boolean validar() {
        if (isBlank(tfNombre) || isBlank(tfUsuario) || isBlank(pfPwd) || isBlank(pfPwd2)) {
            alert("Completa todos los campos obligatorios (*).");
            return false;
        }
        if (!pfPwd.getText().equals(pfPwd2.getText())) {
            alert("Las contraseñas no coinciden.");
            return false;
        }
        if (cbRol.getValue() == null) {
            alert("Selecciona un rol.");
            return false;
        }
        if (cbEstado.getValue() == null) {
            alert("Selecciona un estado.");
            return false;
        }
        return true;
    }

    private boolean isBlank(TextInputControl c) {
        return c.getText() == null || c.getText().trim().isEmpty();
    }

    private void alert(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }

    private void close() {
        Window w = btnCancelar.getScene().getWindow();
        if (onClose != null) onClose.run();
        if (w != null) w.hide();
    }

    public UsuarioDTO getResult() { return result; }

    public UsuarioDTO waitAndGetResult() {
        AtomicReference<UsuarioDTO> ref = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        setOnSave(() -> {
            ref.set(result);
            latch.countDown();
        });
        setOnClose(latch::countDown);

        try { latch.await(); } catch (InterruptedException ignored) {}
        return ref.get();
    }
}
