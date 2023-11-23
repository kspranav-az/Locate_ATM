module com.example.atmmachine {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires mysql.connector.java;

    opens com.example.atmmachine to javafx.fxml;
    exports com.example.atmmachine;
}