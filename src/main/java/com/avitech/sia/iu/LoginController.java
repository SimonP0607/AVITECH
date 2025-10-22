package com.avitech.sia.iu;

import com.avitech.sia.App;
import com.avitech.sia.security.SessionManager;
import com.avitech.sia.security.UserRole;
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
    public void initialize() {
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

    // === Redirección por rol con SessionManager ===
    private void login() {
        String user = txtUser.getText().trim();
        String pass = (txtPass.isVisible() ? txtPass.getText() : txtPassPlain.getText());

        if (user.isEmpty() || pass.isEmpty()) {
            showError("Por favor complete todos los campos.");
            return;
        }

        SessionManager sessionManager = SessionManager.getInstance();

        // DEMO: credenciales por defecto (reemplazar por consulta a BD)
        if (user.equals("admin") && pass.equals("admin123")) {
            // Establecer sesión como ADMIN
            sessionManager.login("admin", "Administrador del Sistema", UserRole.ADMIN);
            App.goTo(sessionManager.getDashboardPath(), sessionManager.getDashboardTitle());
            return;
        }

        if (user.equals("supervisor") && pass.equals("super123")) {
            // Establecer sesión como SUPERVISOR
            sessionManager.login("supervisor", "Supervisor de Operaciones", UserRole.SUPERVISOR);
            App.goTo(sessionManager.getDashboardPath(), sessionManager.getDashboardTitle());
            return;
        }

        if (user.equals("operador") && pass.equals("oper123")) {
            // Establecer sesión como OPERADOR
            sessionManager.login("operador", "Operador de Campo", UserRole.OPERADOR);
            App.goTo(sessionManager.getDashboardPath(), sessionManager.getDashboardTitle());
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
