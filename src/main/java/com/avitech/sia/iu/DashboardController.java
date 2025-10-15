package com.avitech.sia.iu;

import com.avitech.sia.App;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class DashboardController {
    // Topbar / user
    @FXML private Label lblHeader;
    @FXML private Label lblSystemStatus;
    @FXML private Label lblUserInfo;

    // KPIs
    @FXML private Label lblAvesActivas;
    @FXML private Label lblAvesActivasDelta;
    @FXML private Label lblHuevosDia;
    @FXML private Label lblHuevosDiaDelta;
    @FXML private Label lblSuministros;
    @FXML private Label lblSuministrosDelta;
    @FXML private Label lblAlertas;
    @FXML private Label lblAlertasDelta;

    // Chart + alertas
    @FXML private LineChart<String, Number> chartSemanal;
    @FXML private ListView<String> listAlertas;

    // Galpones
    @FXML private ProgressBar pbG1; @FXML private Label lblG1;
    @FXML private ProgressBar pbG2; @FXML private Label lblG2;
    @FXML private ProgressBar pbG3; @FXML private Label lblG3;
    @FXML private ProgressBar pbG4; @FXML private Label lblG4;
    @FXML private ProgressBar pbG5; @FXML private Label lblG5;
    @FXML private ProgressBar pbG6; @FXML private Label lblG6;

    // Sidebar (para gestionar “activo”)
    @FXML private VBox sidebar;

    @FXML
    private void initialize() {
        loadMock();
    }

    public void setHeader(String header) {
        if (lblHeader != null) lblHeader.setText(header);
        if (lblUserInfo != null) lblUserInfo.setText(header);
    }

    @FXML private void onExit() {
        App.goTo("/fxml/login.fxml", "SIA Avitech — Inicio de sesión");
    }

    /* ======== NAV (stubs) ======== */
    @FXML private void goDashboard()  { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
    @FXML private void goSupplies()   { App.goTo("/fxml/suministros.fxml", "SIA Avitech — Suministros"); }
    @FXML private void goHealth()     { App.goTo("/fxml/sanidad.fxml", "SIA Avitech — Sanidad"); }
    @FXML private void goProduction() { App.goTo("/fxml/produccion.fxml", "SIA Avitech — Producción"); }
    @FXML private void goReports()    { App.goTo("/fxml/reportes.fxml", "SIA Avitech — Reportes"); }
    @FXML private void goAlerts()     { App.goTo("/fxml/alertas.fxml", "SIA Avitech — Alertas"); }
    @FXML private void goAudit()      { App.goTo("/fxml/auditoria.fxml", "SIA Avitech — Auditoría"); }
    @FXML private void goParams()     { App.goTo("/fxml/parametros.fxml", "SIA Avitech — Parámetros"); }
    @FXML private void goUsers()      { App.goTo("/fxml/usuarios.fxml", "SIA Avitech — Usuarios"); }
    @FXML private void goBackup()     { App.goTo("/fxml/respaldos.fxml", "SIA Avitech — Respaldos"); }

    private void markActive(String text) {
        if (sidebar == null) return;
        sidebar.lookupAll(".side-btn").forEach(n -> n.getStyleClass().remove("active"));
        sidebar.lookupAll(".side-btn").stream()
                .filter(n -> n instanceof ToggleButton tb && tb.getText().equals(text))
                .findFirst().ifPresent(n -> n.getStyleClass().add("active"));
    }

    /* ======== MOCK DATA para visualizar ======== */
    private void loadMock() {
        if (lblSystemStatus != null) lblSystemStatus.setText("Sistema Online – MySQL Local");

        lblAvesActivas.setText("15,280 gallinas");
        lblAvesActivasDelta.setText("+120");
        lblHuevosDia.setText("12,450 huevos");
        lblHuevosDiaDelta.setText("+2.3%");
        lblSuministros.setText("85 ítems");
        lblSuministrosDelta.setText("-5.2%");
        lblAlertas.setText("7 productos");
        lblAlertasDelta.setText("+3");

        XYChart.Series<String, Number> real = new XYChart.Series<>();
        real.getData().add(new XYChart.Data<>("Lun", 11800));
        real.getData().add(new XYChart.Data<>("Mar", 12350));
        real.getData().add(new XYChart.Data<>("Mié", 12020));
        real.getData().add(new XYChart.Data<>("Jue", 13260));
        real.getData().add(new XYChart.Data<>("Vie", 12940));
        real.getData().add(new XYChart.Data<>("Sáb", 11880));
        real.getData().add(new XYChart.Data<>("Dom", 12110));
        chartSemanal.getData().setAll(real);

        listAlertas.setItems(FXCollections.observableArrayList(
                "Nivel de alimento bajo en Silo 2 · hace 1 hora",
                "Mantenimiento programado completado · hace 2 horas",
                "Stock crítico: Vitamina D · hace 3 horas"
        ));

        setGalpon(pbG1, lblG1, 2100, 2500);
        setGalpon(pbG2, lblG2, 2380, 2800);
        setGalpon(pbG3, lblG3, 2080, 2600);
        setGalpon(pbG4, lblG4, 2610, 2900);
        setGalpon(pbG5, lblG5, 2295, 2700);
        setGalpon(pbG6, lblG6, 2503, 2780);
    }

    private void setGalpon(ProgressBar pb, Label lbl, int actual, int meta) {
        double pct = meta == 0 ? 0 : Math.min(1.0, actual / (double) meta);
        pb.setProgress(pct);
        lbl.setText(actual + "/" + meta + " (" + Math.round(pct * 100) + "%)");
    }

}
