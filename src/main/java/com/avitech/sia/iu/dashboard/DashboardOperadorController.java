package com.avitech.sia.iu.dashboard;

import com.avitech.sia.security.UserRole;
import javafx.fxml.FXML;

/**
 * Controlador específico para el dashboard del Operador.
 * Acceso limitado a: Dashboard, Suministros, Sanidad y Producción.
 */
public class DashboardOperadorController extends DashboardController {

    @FXML
    @Override
    public void initialize() {
        // Configurar rol de OPERADOR antes de inicializar
        setUserRole(UserRole.OPERADOR);
        super.initialize();
    }
}

