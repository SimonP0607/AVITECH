package com.avitech.sia.iu;

import com.avitech.sia.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class LoginController {

    // --- Campos FXML ---
    @FXML private TextField txtUser;

    @FXML private PasswordField txtPass;
    @FXML private TextField txtPassPlain;

    @FXML private Button btnTogglePass;
    @FXML private Button btnLogin;

    @FXML private Label lblError;

    // --- Inicialización ---
    @FXML
    private void initialize() {
        // Sincroniza el texto entre los dos campos
        txtPassPlain.textProperty().bindBidirectional(txtPass.textProperty());

        txtPass.managedProperty().bind(txtPass.visibleProperty());
        txtPassPlain.managedProperty().bind(txtPassPlain.visibleProperty());

        txtPass.setVisible(true);
        txtPassPlain.setVisible(false);

        // Permitir presionar Enter para iniciar sesión
        txtPass.setOnKeyPressed(this::onEnterKey);
        txtPassPlain.setOnKeyPressed(this::onEnterKey);
        txtUser.setOnKeyPressed(this::onEnterKey);
    }

    @FXML
    private void togglePassword(ActionEvent e) {
        boolean showingPlain = txtPassPlain.isVisible();

        txtPassPlain.setVisible(!showingPlain);
        txtPass.setVisible(showingPlain);

        btnTogglePass.setText(showingPlain ? "👁" : "🙈");

        // Mantener foco y cursor
        if (txtPassPlain.isVisible()) {
            txtPassPlain.requestFocus();
            txtPassPlain.positionCaret(txtPassPlain.getText().length());
        } else {
            txtPass.requestFocus();
            txtPass.positionCaret(txtPass.getText().length());
        }
    }

    @FXML
    private void doLogin(ActionEvent e) {
        login();
    }

    // --- Permite iniciar sesión con Enter ---
    private void onEnterKey(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            login();
        }
    }

    // === Redirección por rol ===
    private void login() {
        String user = txtUser.getText().trim();
        String pass = (txtPass.isVisible() ? txtPass.getText() : txtPassPlain.getText());

        if (user.isEmpty() || pass.isEmpty()) {
            showError("Por favor complete todos los campos.");
            return;
        }

        // DEMO: credenciales por defecto (puedes reemplazar por consulta a BD)
        if (user.equals("admin") && pass.equals("admin123")) {
            // Navega al dashboard de ADMIN
            App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN");
            return;
        }
        if (user.equals("supervisor") && pass.equals("super123")) {
            // Si ya tienes el FXML del supervisor, cámbialo aquí:
            // App.goTo("/fxml/dashboard_supervisor.fxml", "SIA Avitech — SUPERVISOR");
            App.goTo("/fxml/dashboard_oper.fxml", "SIA Avitech — SUPERVISOR (demo)");
            return;
        }
        if (user.equals("operador") && pass.equals("oper123")) {
            // Si ya tienes el FXML del operador, cámbialo aquí:
            // App.goTo("/fxml/dashboard_oper.fxml", "SIA Avitech — OPERADOR");
            App.goTo("/fxml/dashboard_super.fxml", "SIA Avitech — OPERADOR (demo)");
            return;
        }

        showError("Usuario o contraseña incorrectos.");
    }

    // --- Mensajes de error ---
    private void showError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }
}
