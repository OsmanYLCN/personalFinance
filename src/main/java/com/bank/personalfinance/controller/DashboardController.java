package com.bank.personalfinance.controller;

import com.bank.personalfinance.model.Account;
import com.bank.personalfinance.model.Transaction;
import com.bank.personalfinance.model.User;
import com.bank.personalfinance.service.AccountService;
import com.bank.personalfinance.service.TransactionService;
import com.bank.personalfinance.util.UserSession;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private Label lblWelcome;
    @FXML private Label lblTotalBalance;
    @FXML private Label lblMonthlyExpense;
    @FXML private VBox sidebar;

    // FXML'deki ID ile birebir aynı olmalı:
    @FXML private TableView<Transaction> tableTransactions;
    @FXML private TableColumn<Transaction, String> colDate;
    @FXML private TableColumn<Transaction, String> colDescription;
    @FXML private TableColumn<Transaction, String> colAmount;

    private int currentAccountId = -1;
    private boolean isMenuOpen = false;
    private final AccountService accountService = new AccountService();
    private final TransactionService transactionService = new TransactionService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Tablo ayarlarını yap
        setupTable();
        // Verileri yükle
        refreshDashboard();
    }

    /**
     * Dashboard verilerini (Bakiye, İsim, Tablo) yeniler.
     */
    public void refreshDashboard() {
        User user = UserSession.getInstance().getCurrentUser();
        if (user != null) {
            // Hoşgeldin mesajı (İster AdSoyad kullan ister Kullanıcı Adı)
            // User modelinde getAdSoyad() yoksa getUsername() kullan.
            if (lblWelcome != null) {
                lblWelcome.setText(user.getAdSoyad());
            }

            // Hesapları Çek
            List<Account> accounts = accountService.getAccountsByUserId(user.getId());
            if (!accounts.isEmpty()) {
                Account account = accounts.get(0); // İlk hesabı varsayılan al
                this.currentAccountId = account.getId(); // ID'yi kaydet (Renklendirme için şart)

                // Bakiyeyi Yaz
                if (lblTotalBalance != null) {
                    lblTotalBalance.setText(formatCurrency(account.getBalance()));
                }

                // Aylık gideri hesaplayıp yazabilirsin (Şimdilik 0)
                if (lblMonthlyExpense != null) {
                    lblMonthlyExpense.setText("₺ 0.00");
                }

                // Tabloyu Doldur
                loadRecentTransactions(account.getId());
            } else {
                if (lblTotalBalance != null) lblTotalBalance.setText("₺ 0.00");
            }
        }
    }

    /**
     * Tablo kolonlarını ve Renklendirme kuralını ayarlar.
     */
    private void setupTable() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("tr", "TR"));

        // Tarih Kolonu
        colDate.setCellValueFactory(cell ->
                new SimpleStringProperty(dateFormat.format(cell.getValue().getTransactionDate())));

        // Açıklama Kolonu
        colDescription.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getDescription()));

        // Tutar Kolonu
        colAmount.setCellValueFactory(cell ->
                new SimpleStringProperty(currencyFormat.format(cell.getValue().getAmount())));

        // --- AKILLI RENKLENDİRME ---
        colAmount.setCellFactory(column -> new TableCell<Transaction, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || getTableView() == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Transaction t = getTableView().getItems().get(getIndex());

                    // Kaynak hesap bensem -> GİDER (Kırmızı)
                    if (t.getSourceAccountId() == currentAccountId) {
                        setText("- " + item);
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    }
                    // Değilsem -> GELİR (Yeşil)
                    else {
                        setText("+ " + item);
                        setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    /**
     * Son işlemleri çeker ve tabloya basar.
     */
    private void loadRecentTransactions(int accountId) {
        // Tüm işlemleri çek
        List<Transaction> allTransactions = transactionService.searchTransactions(accountId, null, null, "HEPSİ", "HEPSİ");

        // Sadece son 5 tanesini al
        List<Transaction> recentTransactions;
        if (allTransactions.size() > 5) {
            recentTransactions = allTransactions.subList(0, 5);
        } else {
            recentTransactions = allTransactions;
        }

        // Tabloyu doldur (FXML'deki fx:id="tableTransactions" ile eşleşti)
        if (tableTransactions != null) {
            tableTransactions.setItems(FXCollections.observableArrayList(recentTransactions));
        }
    }

    // --- YARDIMCI METOTLAR ---

    private String formatCurrency(double amount) {
        return NumberFormat.getCurrencyInstance(new Locale("tr", "TR")).format(amount);
    }

    @FXML
    public void handleMenuToggle() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sidebar);
        if (isMenuOpen) { transition.setToX(-260); isMenuOpen = false; }
        else { transition.setToX(0); isMenuOpen = true; }
        transition.play();
    }

    // --- SAYFA GEÇİŞLERİ ---

    @FXML
    public void handleGoToTransfer(ActionEvent event) {
        changeScene(event, "Transfer.fxml");
    }

    @FXML
    public void handleGoToAccounts(ActionEvent event) {
        changeScene(event, "Accounts.fxml");
    }

    @FXML
    public void handleGoToBudget(ActionEvent event) {
        changeScene(event, "Budget.fxml");
    }

    @FXML
    public void handleGoToTransactions(ActionEvent event) {
        changeScene(event, "Transactions.fxml");
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        UserSession.getInstance().cleanUserSession();
        changeScene(event, "Login.fxml");
    }

    @FXML
    public void handleAddExpenseDialog(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bank/personalfinance/view/ExpenseForm.fxml"));
            javafx.scene.Parent root = loader.load();

            com.bank.personalfinance.controller.ExpenseFormController controller = loader.getController();

            // Hesap ID kontrolü
            if (currentAccountId != -1) {
                controller.setParentController(this, currentAccountId);
            }

            Stage stage = new Stage();
            stage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeScene(ActionEvent event, String fxmlName) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/bank/personalfinance/view/" + fxmlName));
            Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
            String cssPath = getClass().getResource("/com/bank/personalfinance/style/style.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void handleGoToProfile(ActionEvent event) {
        changeScene(event, "Profile.fxml");
    }
}