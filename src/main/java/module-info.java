module com.bank.personalfinance {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;

    opens com.bank.personalfinance to javafx.fxml;
    exports com.bank.personalfinance;
}