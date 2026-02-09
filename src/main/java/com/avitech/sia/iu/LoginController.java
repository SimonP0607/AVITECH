package com.avitech.sia.iu;

import com.avitech.sia.App;
import com.avitech.sia.db.UsuarioDAO;
import com.avitech.sia.db.PasswordUtil;
import com.avitech.sia.security.Session;
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

        // Asegurar que el label de error esté oculto inicialmente
        lblError.setVisible(false);
        lblError.setManaged(false);

        // Asegurar que el botón de login tiene un handler (defensivo: si el onAction FXML no se enlaza por algún motivo)
        if (btnLogin != null) {
            btnLogin.setOnAction(this::doLogin);
            btnLogin.setDisable(false); // asegurar que no esté deshabilitado
        } else {
            logger.warn("btnLogin es null en initialize — revisa el FXML para fx:id=\"btnLogin\"");
        }
    }

    @FXML
    private void togglePassword() {
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
        logger.info("btnLogin pulsado (doLogin) -> iniciando login");
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
                // Log para debug: mostrar una muestra enmascarada del hash almacenado
                try {
                    String ph = u.getPassHash();
                    String phSafe = (ph == null) ? "<null>" : (ph.length() > 12 ? ph.substring(0, 6) + "... (len=" + ph.length() + ")" : ph);
                    logger.debug("Usuario encontrado id={} rol={} passSample={}", u.getId(), u.getRol(), phSafe);
                    logger.debug("¿Parece BCrypt? {}", (ph != null && (ph.startsWith("$2a$") || ph.startsWith("$2b$") || ph.startsWith("$2y$"))));
                } catch (Exception __logEx) {
                    logger.debug("No fue posible obtener muestra del hash de contraseña", __logEx);
                }

                String stored = u.getPassHash();
                boolean seemsBCrypt = (stored != null && (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")));

                // Caso 1: stored parece BCrypt -> verificar con PasswordUtil
                if (seemsBCrypt) {
                    if (PasswordUtil.check(pass, stored)) {
                        Session.setCurrentUser(u);
                        logger.info("Login exitoso para usuario: {} con rol: {}", user, u.getRol());
                        redirectByRole(u.getRol());
                        return;
                    } else {
                        logger.warn("Contraseña incorrecta para usuario: {}", user);
                    }
                } else {
                    // Caso 2: stored no es BCrypt -> puede ser texto plano. Comparamos directamente.
                    if (stored != null && stored.equals(pass)) {
                        // Coincide: migramos a BCrypt
                        try {
                            String newHash = PasswordUtil.hash(pass);
                            UsuarioDAO.updatePassword(u.getId(), newHash);
                            logger.info("Contraseña en texto plano migrada a BCrypt para usuario id={}", u.getId());

                            // Actualizamos el objeto en memoria (opcional)
                            Session.setCurrentUser(new UsuarioDAO.Usuario(u.getId(), u.getUsuario(), u.getRol(), newHash));
                            logger.info("Login exitoso (migrado) para usuario: {} con rol: {}", user, u.getRol());
                            redirectByRole(u.getRol());
                            return;
                        } catch (Exception updEx) {
                            logger.error("Error al migrar contraseña a BCrypt para usuario id={}", u.getId(), updEx);
                            showError("Error interno al actualizar contraseña.");
                            return;
                        }
                    } else {
                        logger.warn("Contraseña almacenada no coincide (texto plano) para usuario: {}", user);
                    }
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

    private void redirectByRole(String rolRaw) {
        String rol = (rolRaw == null) ? "" : rolRaw.toUpperCase();
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
                logger.warn("Rol de usuario no reconocido: {}", rolRaw);
                showError("Rol de usuario no reconocido: " + rolRaw);
        }
    }

    // --- Mensajes de error ---
    private void showError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }
}
