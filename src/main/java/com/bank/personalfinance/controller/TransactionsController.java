package com.bank.personalfinance.controller;

import com.bank.personalfinance.model.Account;
import com.bank.personalfinance.model.Transaction;
import com.bank.personalfinance.model.User;
import com.bank.personalfinance.service.AccountService;
import com.bank.personalfinance.service.TransactionService;
import com.bank.personalfinance.util.UserSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class TransactionsController implements Initializable {

    @FXML private DatePicker dateStart;
    @FXML private DatePicker dateEnd;
    @FXML private ComboBox<String> comboType;
    @FXML private ComboBox<String> comboCategory;

    @FXML private TableView<Transaction> tableTransactions;
    @FXML private TableColumn<Transaction, String> colDate;
    @FXML private TableColumn<Transaction, String> colDesc;
    @FXML private TableColumn<Transaction, String> colCategory;
    @FXML private TableColumn<Transaction, String> colType;
    @FXML private TableColumn<Transaction, String> colAmount;

    private final TransactionService transactionService = new TransactionService();
    private final AccountService accountService = new AccountService();

    // Şu anki aktif hesap ID'si (Şimdilik ilk hesap)
    private int currentAccountId = -1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupFilters();
        setupTable();
        loadUserAccount();
        // Sayfa açılınca varsayılan olarak tüm işlemleri getir
        handleFilter(null);
    }

    /**
     * Filtre kutularını (ComboBox) doldurur.
     */
    private void setupFilters() {
        comboType.getItems().addAll("HEPSİ", "GELİR", "GİDER", "HAVALE");
        comboType.setValue("HEPSİ");

        comboCategory.getItems().addAll("HEPSİ", "Market", "Fatura", "Kira", "Giyim", "Eğlence", "Transfer", "Diğer");
        comboCategory.setValue("HEPSİ");
    }

    /**
     * Kullanıcının hesabını bulur.
     */
    private void loadUserAccount() {
        User user = UserSession.getInstance().getCurrentUser();
        if (user != null) {
            List<Account> accounts = accountService.getAccountsByUserId(user.getId());
            if (!accounts.isEmpty()) {
                currentAccountId = accounts.get(0).getId();
            }
        }
    }

    /**
     * Tablo kolonlarını ve veri formatlarını ayarlar.
     */
    private void setupTable() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("tr", "TR"));

        colDate.setCellValueFactory(cell ->
                new SimpleStringProperty(dateFormat.format(cell.getValue().getTransactionDate())));

        colDesc.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getDescription()));

        colCategory.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getCategory()));

        colType.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTransactionType()));

        // --- DÜZELTİLEN KISIM BAŞLIYOR ---

        // Tutar Kolonu: Hem Rengi Hem de (+/-) İşaretini Ayarlar
        colAmount.setCellFactory(column -> new TableCell<Transaction, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getTableView() == null || getTableView().getItems().get(getIndex()) == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Transaction t = getTableView().getItems().get(getIndex());
                    double amount = t.getAmount();

                    // MANTIK: Eğer kaynak hesap (Source) BEN İSEM -> Para çıkmıştır (GİDER)
                    if (t.getSourceAccountId() == currentAccountId) {
                        setText("- " + currencyFormat.format(amount));
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;"); // KIRMIZI
                    }
                    // Eğer kaynak ben değilsem -> Para bana gelmiştir (GELİR)
                    else {
                        setText("+ " + currencyFormat.format(amount));
                        setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;"); // YEŞİL
                    }
                }
            }
        });
    }

    /**
     * Filtrele butonuna basılınca çalışır.
     */
    @FXML
    public void handleFilter(ActionEvent event) {
        if (currentAccountId == -1) return;

        List<Transaction> results = transactionService.searchTransactions(
                currentAccountId,
                dateStart.getValue(),
                dateEnd.getValue(),
                comboType.getValue(),
                comboCategory.getValue()
        );

        ObservableList<Transaction> list = FXCollections.observableArrayList(results);
        tableTransactions.setItems(list);
    }

    @FXML
    public void handleClearFilters(ActionEvent event) {
        dateStart.setValue(null);
        dateEnd.setValue(null);
        comboType.setValue("HEPSİ");
        comboCategory.setValue("HEPSİ");
        handleFilter(event); // Listeyi sıfırla
    }

    @FXML
    public void handleBack(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/bank/personalfinance/view/Dashboard.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
            scene.getStylesheets().add(getClass().getResource("/com/bank/personalfinance/style/style.css").toExternalForm());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Finova - Dashboard");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}