package com.avitech.sia.iu.dashboard;

import com.avitech.sia.App;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Tablero Principal (sin datos quemados).
 * Carga la UI desde una fuente de datos inyectable (DashboardDataSource).
 */
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

    // Sidebar
    @FXML private VBox sidebar;

    // ====== Fuente de datos (inyectable) ======
    private DashboardDataSource dataSource = new EmptyDashboardDataSource();
    public void setDataSource(DashboardDataSource ds) {
        this.dataSource = (ds != null) ? ds : new EmptyDashboardDataSource();
    }

    @FXML
    private void initialize() {
        if (lblSystemStatus != null) lblSystemStatus.setText("Sistema Offline — MySQL Local");
        setHeader("Administrador");

        // Placeholders seguros
        setKpi(lblAvesActivas, lblAvesActivasDelta, null, null, false);
        setKpi(lblHuevosDia,    lblHuevosDiaDelta,    null, null, true);
        setKpi(lblSuministros,  lblSuministrosDelta,  null, null, false);
        setKpi(lblAlertas,      lblAlertasDelta,      null, null, false);

        chartSemanal.getData().clear();
        listAlertas.setItems(FXCollections.observableArrayList());

        setGalpon(pbG1, lblG1, 0, 0);
        setGalpon(pbG2, lblG2, 0, 0);
        setGalpon(pbG3, lblG3, 0, 0);
        setGalpon(pbG4, lblG4, 0, 0);
        setGalpon(pbG5, lblG5, 0, 0);
        setGalpon(pbG6, lblG6, 0, 0);

        Platform.runLater(this::refresh);
    }

    /** Recarga toda la pantalla desde la fuente de datos. */
    public void refresh() {
        // KPIs
        DashboardDataSource.Kpis k = dataSource.getKpis();
        setKpi(lblAvesActivas, lblAvesActivasDelta, k.avesActivas(), k.deltaAves(), false);
        setKpi(lblHuevosDia,    lblHuevosDiaDelta,    k.huevosDia(),   k.deltaHuevosDia(), true);
        setKpi(lblSuministros,  lblSuministrosDelta,  k.suministros(), k.deltaSuministros(), false);
        setKpi(lblAlertas,      lblAlertasDelta,      k.alertas(),     k.deltaAlertas(), false);

        // Gráfico semanal
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        chartSemanal.getData().clear();
        for (DashboardDataSource.Point p : dataSource.getWeeklyProduction()) {
            serie.getData().add(new XYChart.Data<>(p.label(), p.value()));
        }
        chartSemanal.getData().add(serie);

        // Alertas recientes
        List<String> items = new ArrayList<>();
        for (DashboardDataSource.Alert a : dataSource.getRecentAlerts()) {
            items.add(a.when() + " · " + a.message());
        }
        listAlertas.setItems(FXCollections.observableArrayList(items));

        // Galpones
        setCoopFrom(1, pbG1, lblG1);
        setCoopFrom(2, pbG2, lblG2);
        setCoopFrom(3, pbG3, lblG3);
        setCoopFrom(4, pbG4, lblG4);
        setCoopFrom(5, pbG5, lblG5);
        setCoopFrom(6, pbG6, lblG6);
    }

    // ====== Navegación (stubs) ======
    @FXML private void onExit()       { App.goTo("/fxml/login.fxml", "SIA Avitech — Inicio de sesión"); }
    @FXML private void goDashboard()  { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
    @FXML private void goSupplies()   { App.goTo("/fxml/suministros/suministros.fxml", "SIA Avitech — Suministros"); }
    @FXML private void goHealth()     { App.goTo("/fxml/sanidad/sanidad.fxml", "SIA Avitech — Sanidad"); }
    @FXML private void goProduction() { App.goTo("/fxml/produccion/produccion.fxml", "SIA Avitech — Producción"); }
    @FXML private void goReports()    { App.goTo("/fxml/reportes.fxml", "SIA Avitech — Reportes"); }
    @FXML private void goAlerts()     { App.goTo("/fxml/alertas.fxml", "SIA Avitech — Alertas"); }
    @FXML private void goAudit()      { App.goTo("/fxml/auditoria.fxml", "SIA Avitech — Auditoría"); }
    @FXML private void goParams()     { App.goTo("/fxml/parametros.fxml", "SIA Avitech — Parámetros"); }
    @FXML private void goUsers()      { App.goTo("/fxml/usuarios/usuarios.fxml", "SIA Avitech — Usuarios"); }
    @FXML private void goBackup()     { App.goTo("/fxml/respaldos.fxml", "SIA Avitech — Respaldos"); }

    // ====== API pública ======
    public void setHeader(String header) {
        if (lblHeader != null) lblHeader.setText(header);
        if (lblUserInfo != null) lblUserInfo.setText(header);
    }

    // ====== Helpers ======
    private void setKpi(Label value, Label delta, Integer v, Integer d, boolean deltaIsPercent) {
        value.setText(v == null ? "—" : formatInt(v));
        if (d == null) { delta.setText(""); delta.getStyleClass().remove("bad"); return; }
        String sign = d > 0 ? "+" : "";
        delta.setText(sign + d + (deltaIsPercent ? "%" : ""));
        if (d < 0 && !delta.getStyleClass().contains("bad")) delta.getStyleClass().add("bad");
        if (d >= 0) delta.getStyleClass().remove("bad");
    }

    private void setCoopFrom(int n, ProgressBar pb, Label lbl) {
        DashboardDataSource.Coop c = dataSource.getCoopsProduction().stream()
                .filter(x -> x.num() == n).findFirst()
                .orElse(new DashboardDataSource.Coop(n, 0, 0));
        setGalpon(pb, lbl, c.current(), c.target());
    }

    private void setGalpon(ProgressBar pb, Label lbl, int actual, int meta) {
        double pct = meta == 0 ? 0 : Math.min(1.0, actual / (double) meta);
        pb.setProgress(pct);
        lbl.setText(actual + "/" + meta + " (" + Math.round(pct * 100) + "%)");
    }

    private String formatInt(int v) {
        return String.format("%,d", v).replace(',', '.'); // 12.450
    }
}