package com.avitech.sia.iu;

import com.avitech.sia.App;
import com.avitech.sia.db.UsuarioDAO;
import com.avitech.sia.db.PasswordUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

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
        logger.info("Inicializando controlador de login");

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
            logger.info("Intentando login para usuario: {}", user);
            var optUsuario = UsuarioDAO.findByUsuario(user);

            if (optUsuario.isPresent()) {
                UsuarioDAO.Usuario u = optUsuario.get();
                // Usamos BCrypt para verificar el hash de la contraseña
                if (PasswordUtil.check(pass, u.getPassHash())) {
                    logger.info("Login exitoso para usuario: {} con rol: {}", user, u.getRol());
                    // TODO: Guardar usuario en sesión global
                    String rol = u.getRol().toUpperCase();
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
                            logger.warn("Rol de usuario no reconocido: {}", u.getRol());
                            showError("Rol de usuario no reconocido: " + u.getRol());
                    }
                    return; // Salir del método si el login es exitoso
                } else {
                    logger.warn("Contraseña incorrecta para usuario: {}", user);
                }
            } else {
                logger.warn("Usuario no encontrado: {}", user);
            }
        } catch (Exception ex) {
            logger.error("Error al conectar con la base de datos durante login", ex);
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
