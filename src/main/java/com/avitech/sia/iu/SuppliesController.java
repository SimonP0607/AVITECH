package com.avitech.sia.iu;

import com.avitech.sia.App;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;

public class SuppliesController {
    @FXML private VBox sidebar;

    @FXML
    private void initialize() {
        markActive("Suministros");
    }

    @FXML private void goDashboard()  { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
    @FXML private void goSupplies()   { App.goTo("/fxml/supplies.fxml", "SIA Avitech — Suministros"); }
    @FXML private void goHealth()     { markActive("Sanidad"); }
    @FXML private void goProduction() { markActive("Producción"); }
    @FXML private void goReports()    { markActive("Reportes"); }
    @FXML private void goAlerts()     { markActive("Alertas"); }
    @FXML private void goAudit()      { markActive("Auditoría"); }
    @FXML private void goParams()     { markActive("Parámetros"); }
    @FXML private void goUsers()      { markActive("Usuarios"); }
    @FXML private void goBackup()     { markActive("Respaldos"); }

    @FXML
    private void onExit() {
        App.goTo("/fxml/login.fxml", "SIA Avitech — Inicio de sesión");
    }

    private void markActive(String text) {
        if (sidebar == null) {
            return;
        }

        sidebar.lookupAll(".side-btn").forEach(n -> n.getStyleClass().remove("active"));
        sidebar.lookupAll(".side-btn").stream()
                .filter(n -> n instanceof ToggleButton tb && tb.getText().equals(text))
                .findFirst()
                .ifPresent(n -> n.getStyleClass().add("active"));
    }
}
