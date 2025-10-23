package com.avitech.sia.iu.parametros;

import com.avitech.sia.App;
import com.avitech.sia.iu.BaseController;
import com.avitech.sia.security.UserRole.Module;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ParametrosController extends BaseController {

    @Override
    protected Module getRequiredModule() {
        return Module.PARAMETROS;
    }

    @FXML private Label lblSystemStatus, lblHeader, lblUserInfo;
    @FXML private VBox sidebar;
    @FXML private ToggleGroup sideGroup;
    @FXML private ToggleButton btnUnidades, btnCategorias, btnMedicamentos, btnLotes, btnUbicaciones;
    @FXML private StackPane contentStack;

    @FXML
    @Override
    public void initialize() {
        super.initialize();
        lblSystemStatus.setText("Sistema Offline – MySQL Local");
        lblHeader.setText("Administrador");
        // Configurar permisos del menú según el rol del usuario
        configureMenuPermissions();

        lblUserInfo.setText("Administrador");

        // vista por defecto
        show("/fxml/Parametros/parametros_unidades.fxml");
    }

    // Segmentos
    @FXML private void showUnidades()    { show("/fxml/Parametros/parametros_unidades.fxml"); }
    @FXML private void showCategorias()  { show("/fxml/Parametros/parametros_categorias.fxml"); }
    @FXML private void showMedicamentos(){ show("/fxml/Parametros/parametros_medicamentos.fxml"); }
    @FXML private void showLotes()       { show("/fxml/Parametros/parametros_lotes.fxml"); }
    @FXML private void showUbicaciones() { show("/fxml/Parametros/parametros_ubicaciones.fxml"); }

    private void show(String fxmlPath) {
        try {
            Node view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentStack.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ====== NAV (mismo patrón del proyecto) ====== */
    @FXML private void goDashboard()  { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
    @FXML private void goSupplies()   { App.goTo("/fxml/suministros.fxml",     "SIA Avitech — Suministros"); }
    @FXML private void goHealth()     { App.goTo("/fxml/sanidad/sanidad.fxml",         "SIA Avitech — Sanidad"); }
    @FXML private void goProduction() { App.goTo("/fxml/produccion/produccion.fxml",      "SIA Avitech — Producción"); }
    @FXML private void goReports()    { App.goTo("/fxml/reportes.fxml",        "SIA Avitech — Reportes"); }
    @FXML private void goAlerts()     { App.goTo("/fxml/alertas.fxml",         "SIA Avitech — Alertas"); }
    @FXML private void goAudit()      { App.goTo("/fxml/auditoria.fxml",       "SIA Avitech — Auditoría"); }
    @FXML private void goParams()     { /* ya estás aquí */ }
    @FXML private void goUsers()      { App.goTo("/fxml/usuarios/usuarios.fxml",        "SIA Avitech — Usuarios"); }
    @FXML private void goBackup()     { App.goTo("/fxml/respaldos/respaldos.fxml",       "SIA Avitech — Respaldos"); }
    @FXML private void onExit()       { App.goTo("/fxml/login.fxml",           "SIA Avitech — Inicio de sesión"); }

    /**
     * Configura la visibilidad de los botones del menú según los permisos del usuario.
     */
    private void configureMenuPermissions() {
        if (sidebar == null || sessionManager == null) return;

        sidebar.getChildren().stream()
            .filter(node -> node instanceof ToggleButton)
            .map(node -> (ToggleButton) node)
            .forEach(button -> {
                Module module = getModuleFromButtonText(button.getText());
                if (module != null) {
                    boolean hasAccess = sessionManager.hasAccessTo(module);
                    button.setVisible(hasAccess);
                    button.setManaged(hasAccess);
                }
            });
    }

    private Module getModuleFromButtonText(String text) {
        return switch (text) {
            case "Tablero" -> Module.DASHBOARD;
            case "Suministros" -> Module.SUMINISTROS;
            case "Sanidad" -> Module.SANIDAD;
            case "Producción" -> Module.PRODUCCION;
            case "Reportes" -> Module.REPORTES;
            case "Alertas" -> Module.ALERTAS;
            case "Auditoría" -> Module.AUDITORIA;
            case "Parámetros" -> Module.PARAMETROS;
            case "Usuarios" -> Module.USUARIOS;
            case "Respaldos" -> Module.RESPALDOS;
            default -> null;
        };
    }
}
