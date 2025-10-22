package com.avitech.sia.iu.dashboard;

import com.avitech.sia.security.UserRole;
import javafx.fxml.FXML;

/**
 * Controlador específico para el dashboard del Administrador.
 * Tiene acceso completo a todos los módulos del sistema.
 */
public class DashboardAdminController extends DashboardController {

    @FXML
    @Override
    public void initialize() {
        // Configurar rol de ADMIN antes de inicializar
        setUserRole(UserRole.ADMIN);
        super.initialize();
    }
}

