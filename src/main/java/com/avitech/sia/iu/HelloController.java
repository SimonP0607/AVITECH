package com.avitech.sia.iu;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label lblMsg;

    @FXML
    private void onClick() {
        lblMsg.setText("¡JavaFX corriendo OK!");
    }

    @FXML
    private void onTestDb() {
        try (var cn = com.avitech.sia.db.DB.get();
             var st = cn.createStatement();
             var rs = st.executeQuery("SELECT 1")) {
            if (rs.next()) lblMsg.setText("BD OK (SELECT 1 = " + rs.getInt(1) + ")");
        } catch (Exception e) {
            lblMsg.setText("Error BD: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
