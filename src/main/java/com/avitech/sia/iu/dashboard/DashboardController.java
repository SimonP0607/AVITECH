package com.avitech.sia.iu.dashboard;

import com.avitech.sia.App;
import com.avitech.sia.iu.BaseController;
import com.avitech.sia.security.UserRole;
import com.avitech.sia.security.UserRole.Module;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

/**
 * Controlador base para todos los dashboards del sistema.
 * Proporciona funcionalidad común de navegación y visualización de datos.
 */
public abstract class DashboardController extends BaseController {

    // ===== Componentes del menú lateral =====
    @FXML protected ScrollPane spMenu;
    @FXML protected VBox sidebar;
    @FXML protected ToggleGroup sideGroup;
    @FXML protected Label lblUserInfo;

    // ===== Componentes de la barra superior =====
    @FXML protected Label lblSystemStatus;
    @FXML protected Label lblHeader;

    // ===== Componentes del contenido central =====
    @FXML protected ScrollPane spContent;
    @FXML protected VBox contentRoot;

    // ===== KPIs =====
    @FXML protected Label lblAvesActivas;
    @FXML protected Label lblAvesActivasDelta;
    @FXML protected Label lblHuevosDia;
    @FXML protected Label lblHuevosDiaDelta;
    @FXML protected Label lblAlertasActivas;
    @FXML protected Label lblAlertasActivasDelta;
    @FXML protected Label lblMortalidadSemana;
    @FXML protected Label lblMortalidadSemanaDelta;

    protected UserRole currentUserRole;
    protected DashboardDataSource dataSource;

    /**
     * Establece el rol del usuario antes de inicializar.
     */
    protected void setUserRole(UserRole role) {
        this.currentUserRole = role;
    }

    @Override
    @FXML
    public void initialize() {
        super.initialize();

        // Inicializar fuente de datos (puede ser real o mock)
        dataSource = createDataSource();

        // Configurar información del usuario
        configureUserInfo();

        // Configurar visibilidad de botones según permisos
        configureMenuPermissions();

        // Cargar datos del dashboard
        loadDashboardData();
    }

    /**
     * Crea la fuente de datos para el dashboard.
     * Las subclases pueden sobrescribir este método para proporcionar datos reales.
     */
    protected DashboardDataSource createDataSource() {
        return new EmptyDashboardDataSource();
    }

    /**
     * Configura la información del usuario en el menú lateral.
     */
    protected void configureUserInfo() {
        if (lblUserInfo != null && sessionManager != null) {
            lblUserInfo.setText(sessionManager.getFullName());
        }

        if (lblHeader != null && sessionManager != null) {
            String roleName = getRoleName(sessionManager.getUserRole());
            lblHeader.setText(roleName);
        }

        if (lblSystemStatus != null) {
            lblSystemStatus.setText("Sistema Offline – MySQL Local");
        }
    }

    /**
     * Obtiene el nombre legible del rol.
     */
    protected String getRoleName(UserRole role) {
        return switch (role) {
            case ADMIN -> "Administrador";
            case SUPERVISOR -> "Supervisor";
            case OPERADOR -> "Operador";
        };
    }

    /**
     * Configura la visibilidad de los botones del menú según los permisos del usuario.
     */
    protected void configureMenuPermissions() {
        if (sidebar == null || currentUserRole == null) return;

        // Recorrer todos los ToggleButton del sidebar
        sidebar.getChildren().stream()
            .filter(node -> node instanceof ToggleButton)
            .map(node -> (ToggleButton) node)
            .forEach(button -> {
                Module module = getModuleFromButton(button);
                if (module != null) {
                    boolean hasAccess = currentUserRole.hasAccessTo(module);
                    button.setVisible(hasAccess);
                    button.setManaged(hasAccess);
                }
            });
    }

    /**
     * Obtiene el módulo asociado a un botón del menú.
     */
    protected Module getModuleFromButton(ToggleButton button) {
        String text = button.getText();
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

    /**
     * Carga los datos del dashboard desde la fuente de datos.
     */
    protected void loadDashboardData() {
        if (dataSource == null) return;

        Platform.runLater(() -> {
            // Cargar KPIs
            DashboardDataSource.Kpis kpis = dataSource.getKpis();

            if (lblAvesActivas != null) {
                lblAvesActivas.setText(kpis.avesActivas() != null ?
                    String.format("%,d", kpis.avesActivas()) : "—");
            }
            if (lblAvesActivasDelta != null) {
                lblAvesActivasDelta.setText(kpis.deltaAves() != null ?
                    formatDelta(kpis.deltaAves()) : "—");
            }

            if (lblHuevosDia != null) {
                lblHuevosDia.setText(kpis.huevosDia() != null ?
                    String.format("%,d", kpis.huevosDia()) : "—");
            }
            if (lblHuevosDiaDelta != null) {
                lblHuevosDiaDelta.setText(kpis.deltaHuevosDia() != null ?
                    formatDelta(kpis.deltaHuevosDia()) : "—");
            }

            if (lblAlertasActivas != null) {
                lblAlertasActivas.setText(kpis.alertas() != null ?
                    String.valueOf(kpis.alertas()) : "—");
            }
            if (lblAlertasActivasDelta != null) {
                lblAlertasActivasDelta.setText(kpis.deltaAlertas() != null ?
                    formatDelta(kpis.deltaAlertas()) : "—");
            }

            if (lblMortalidadSemana != null) {
                lblMortalidadSemana.setText(kpis.suministros() != null ?
                    String.format("%,d", kpis.suministros()) : "—");
            }
            if (lblMortalidadSemanaDelta != null) {
                lblMortalidadSemanaDelta.setText(kpis.deltaSuministros() != null ?
                    formatDelta(kpis.deltaSuministros()) : "—");
            }
        });
    }

    /**
     * Formatea un valor delta con signo.
     */
    protected String formatDelta(int delta) {
        if (delta > 0) {
            return "+" + delta;
        }
        return String.valueOf(delta);
    }

    // ===== Métodos de navegación =====

    @FXML
    protected void goDashboard() {
        // Ya estamos en el dashboard
    }

    @FXML
    protected void goSupplies() {
        if (hasAccessTo(Module.SUMINISTROS)) {
            App.goTo("/fxml/suministros/suministros.fxml", "SIA Avitech — Suministros");
        }
    }

    @FXML
    protected void goHealth() {
        if (hasAccessTo(Module.SANIDAD)) {
            App.goTo("/fxml/sanidad/sanidad.fxml", "SIA Avitech — Sanidad");
        }
    }

    @FXML
    protected void goProduction() {
        if (hasAccessTo(Module.PRODUCCION)) {
            App.goTo("/fxml/produccion/produccion.fxml", "SIA Avitech — Producción");
        }
    }

    @FXML
    protected void goReports() {
        if (hasAccessTo(Module.REPORTES)) {
            App.goTo("/fxml/reportes.fxml", "SIA Avitech — Reportes");
        }
    }

    @FXML
    protected void goAlerts() {
        if (hasAccessTo(Module.ALERTAS)) {
            App.goTo("/fxml/alertas/alertas.fxml", "SIA Avitech — Alertas");
        }
    }

    @FXML
    protected void goAudit() {
        if (hasAccessTo(Module.AUDITORIA)) {
            App.goTo("/fxml/auditoria/auditoria.fxml", "SIA Avitech — Auditoría");
        }
    }

    @FXML
    protected void goParams() {
        if (hasAccessTo(Module.PARAMETROS)) {
            App.goTo("/fxml/parametros.fxml", "SIA Avitech — Parámetros");
        }
    }

    @FXML
    protected void goUsers() {
        if (hasAccessTo(Module.USUARIOS)) {
            App.goTo("/fxml/usuarios/usuarios.fxml", "SIA Avitech — Usuarios");
        }
    }

    @FXML
    protected void goBackup() {
        if (hasAccessTo(Module.RESPALDOS)) {
            App.goTo("/fxml/respaldos/respaldos.fxml", "SIA Avitech — Respaldos");
        }
    }

    @FXML
    protected void onExit() {
        logout();
    }

    @Override
    protected Module getRequiredModule() {
        return Module.DASHBOARD;
    }
}

