module com.avitech.sia {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.zaxxer.hikari;


    opens com.avitech.sia to javafx.fxml;
    opens com.avitech.sia.ui to javafx.fxml;

    exports com.avitech.sia;
}
