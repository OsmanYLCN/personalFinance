module com.bank.personalfinance {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires mysql.connector.j;

    opens com.bank.personalfinance.controller to javafx.fxml;
    exports com.bank.personalfinance;
}