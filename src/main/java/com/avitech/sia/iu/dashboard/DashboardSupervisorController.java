package com.avitech.sia.iu.dashboard;

import com.avitech.sia.security.UserRole;
import javafx.fxml.FXML;

/**
 * Controlador específico para el dashboard del Supervisor.
 * Acceso a: Dashboard, Suministros, Sanidad, Producción, Reportes, Alertas y Auditoría.
 */
public class DashboardSupervisorController extends DashboardController {

    @FXML
    @Override
    public void initialize() {
        // Configurar rol de SUPERVISOR antes de inicializar
        setUserRole(UserRole.SUPERVISOR);
        super.initialize();
    }
}

