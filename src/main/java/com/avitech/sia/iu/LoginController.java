package com.avitech.sia.iu;

import com.avitech.sia.App;
import com.avitech.sia.db.UsuarioDAO;
import com.avitech.sia.db.PasswordUtil;
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

        try {
            var optUsuario = UsuarioDAO.findByUsuario(user);

            if (optUsuario.isPresent()) {
                UsuarioDAO.Usuario u = optUsuario.get();
                // Usamos BCrypt para verificar el hash de la contraseña
                if (PasswordUtil.check(pass, u.passHash())) {
                    // TODO: Guardar usuario en sesión global
                    String rol = u.rol().toUpperCase();
                    switch (rol) {
                        case "ADMIN":
                            App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN");
                            break;
                        case "SUPERVISOR":
                            App.goTo("/fxml/dashboard_super.fxml", "SIA Avitech — SUPERVISOR");
                            break;
                        case "OPERADOR":
                            App.goTo("/fxml/dashboard_oper.fxml", "SIA Avitech — OPERADOR");
                            break;
                        default:
                            showError("Rol de usuario no reconocido: " + u.rol());
                    }
                    return; // Salir del método si el login es exitoso
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Error al conectar con la base de datos.");
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
