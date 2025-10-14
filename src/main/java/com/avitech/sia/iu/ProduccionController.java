package com.avitech.sia.iu;

import com.avitech.sia.App;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ProduccionController {

    @FXML private void goDashboard()  { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
    @FXML private void goSupplies()   { App.goTo("/fxml/supplies.fxml",        "SIA Avitech — Suministros"); }
    @FXML private void goHealth()     { App.goTo("/fxml/sanidad.fxml",         "SIA Avitech — Sanidad"); }
    @FXML private void goProduction() { /* ya estás en Producción */ }
    @FXML private void goReports()    { /* App.goTo("/fxml/reportes.fxml",   "Reportes"); */ System.out.println("Ir a Reportes (WIP)"); }
    @FXML private void goAlerts()     { /* App.goTo("/fxml/alertas.fxml",    "Alertas");  */ System.out.println("Ir a Alertas (WIP)"); }
    @FXML private void goAudit()      { /* App.goTo("/fxml/auditoria.fxml",  "Auditoría");*/ System.out.println("Ir a Auditoría (WIP)"); }
    @FXML private void goParams()     { /* App.goTo("/fxml/params.fxml",     "Parámetros");*/ System.out.println("Ir a Parámetros (WIP)"); }
    @FXML private void goUsers()      { /* App.goTo("/fxml/usuarios.fxml",   "Usuarios");  */ System.out.println("Ir a Usuarios (WIP)"); }
    @FXML private void goBackup()     { /* App.goTo("/fxml/backup.fxml",     "Respaldos"); */ System.out.println("Ir a Respaldos (WIP)"); }




    @FXML
    private Label lblKpiProduccionTotal, lblKpiPosturaPromedio, lblKpiPesoPromedio, lblKpiMortalidadTotal;

    @FXML
    private TableView<?> tblDiario;

    @FXML
    private ComboBox<?> cbFiltroGalpon, cbGalpon;

    @FXML
    private TextField tfProduccionTotal, tfPesoPromedio, tfMortalidad, tfHuevosB, tfHuevosA, tfHuevosAA, tfHuevosAAA;

    @FXML
    private Button btnGuardarRegistro, btnCancelarRegistro;

    @FXML
    private void onGuardarRegistro() {
        System.out.println("Guardar registro de producción (demo)");
    }

    @FXML
    private void onCancelarRegistro() {
        System.out.println("Cancelar registro de producción (demo)");
    }

    @FXML
    private void onFiltrar() {
        System.out.println("Filtro aplicado (demo)");
    }

    @FXML
    private void onLimpiarFiltro() {
        System.out.println("Filtro limpiado (demo)");
    }

    @FXML
    private void onExit() {
        // Acción de cerrar sesión o volver al login
        App.goTo("/fxml/login.fxml", "Inicio de sesión — SIA Avitech");
    }

}
