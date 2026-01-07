package com.bank.personalfinance.controller;

import com.bank.personalfinance.model.User;
import com.bank.personalfinance.util.UserSession;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private Label lblWelcome;
    @FXML private VBox sidebar;

    // --- TABLO BİLEŞENLERİ ---
    @FXML private TableView<Transaction> tableTransactions;
    @FXML private TableColumn<Transaction, String> colDate;
    @FXML private TableColumn<Transaction, String> colDescription;
    @FXML private TableColumn<Transaction, String> colAmount;

    private boolean isMenuOpen = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Kullanıcı Adını Yaz
        User user = UserSession.getInstance().getCurrentUser();
        if (user != null) {
            lblWelcome.setText(user.getAdSoyad());
        } else {
            lblWelcome.setText("Misafir");
        }

        // 2. Tablo Kolonlarını Ayarla
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        // 3. Tabloya Sahte Veri Ekle (Tasarım görünsün diye)
        loadDummyData();
    }

    private void loadDummyData() {
        ObservableList<Transaction> list = FXCollections.observableArrayList(
                new Transaction("07.01.2026", "Spotify Abonelik", "- ₺119.99"),
                new Transaction("06.01.2026", "Migros Alışveriş", "- ₺840.50"),
                new Transaction("05.01.2026", "Ahmet Yılmaz (Gelen)", "+ ₺2,500.00"),
                new Transaction("03.01.2026", "Starbucks Coffee", "- ₺120.00"),
                new Transaction("01.01.2026", "Maaş Ödemesi", "+ ₺45,000.00")
        );
        tableTransactions.setItems(list);
    }

    @FXML
    public void handleMenuToggle() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sidebar);
        if (isMenuOpen) {
            transition.setToX(-260);
            isMenuOpen = false;
        } else {
            transition.setToX(0);
            isMenuOpen = true;
        }
        transition.play();
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        UserSession.getInstance().cleanUserSession();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/bank/personalfinance/view/Login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1280, 720);

            // CSS Yükle
            String cssPath = getClass().getResource("/com/bank/personalfinance/style/style.css").toExternalForm();
            scene.getStylesheets().add(cssPath);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Banka Otomasyonu - Giriş");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- TABLO İÇİN BASİT BİR İÇ SINIF ---
    public static class Transaction {
        private final SimpleStringProperty date;
        private final SimpleStringProperty description;
        private final SimpleStringProperty amount;

        public Transaction(String date, String description, String amount) {
            this.date = new SimpleStringProperty(date);
            this.description = new SimpleStringProperty(description);
            this.amount = new SimpleStringProperty(amount);
        }

        public String getDate() { return date.get(); }
        public String getDescription() { return description.get(); }
        public String getAmount() { return amount.get(); }
    }
}