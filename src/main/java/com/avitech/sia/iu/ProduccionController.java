package com.avitech.sia.iu;

import com.avitech.sia.App;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ProduccionController {

    /* ================== Navegación ================== */
    @FXML private void goDashboard()  { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
    @FXML private void goSupplies()   { App.goTo("/fxml/suministros.fxml", "SIA Avitech — Suministros"); }
    @FXML private void goHealth()     { App.goTo("/fxml/sanidad/sanidad.fxml", "SIA Avitech — Sanidad"); }
    @FXML private void goProduction() { App.goTo("/fxml/produccion.fxml", "SIA Avitech — Producción"); }
    @FXML private void goReports()    { App.goTo("/fxml/reportes.fxml", "SIA Avitech — Reportes"); }
    @FXML private void goAlerts()     { App.goTo("/fxml/alertas.fxml", "SIA Avitech — Alertas"); }
    @FXML private void goAudit()      { App.goTo("/fxml/auditoria.fxml", "SIA Avitech — Auditoría"); }
    @FXML private void goParams()     { App.goTo("/fxml/parametros.fxml", "SIA Avitech — Parámetros"); }
    @FXML private void goUsers()      { App.goTo("/fxml/usuarios.fxml", "SIA Avitech — Usuarios"); }
    @FXML private void goBackup()     { App.goTo("/fxml/respaldos.fxml", "SIA Avitech — Respaldos"); }
    @FXML private void onExit() {
        App.goTo("/fxml/login.fxml", "SIA Avitech — Inicio de sesión");
    }




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


}
