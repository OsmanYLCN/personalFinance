package com.bank.personalfinance.controller;

import com.bank.personalfinance.model.Account;
import com.bank.personalfinance.model.User;
import com.bank.personalfinance.service.AccountService;
import com.bank.personalfinance.service.TransactionService;
import com.bank.personalfinance.util.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class TransferController implements Initializable {

    @FXML private ComboBox<Account> comboSourceAccount;
    @FXML private TextField txtTargetIban;
    @FXML private TextField txtAmount;
    @FXML private TextField txtDescription;
    @FXML private Label lblStatus;
    @FXML private Button btnSend;

    private final AccountService accountService = new AccountService();
    private final TransactionService transactionService = new TransactionService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User user = UserSession.getInstance().getCurrentUser();
        if (user != null) {
            comboSourceAccount.getItems().addAll(accountService.getAccountsByUserId(user.getId()));
        }
    }

    @FXML
    public void handleTransfer(ActionEvent event) {
        Account source = comboSourceAccount.getValue();
        String targetIban = txtTargetIban.getText().trim();
        String amountStr = txtAmount.getText().trim();
        String description = txtDescription.getText().trim();

        if (source == null) {
            showError("Lütfen gönderen hesabı seçiniz.");
            return;
        }
        if (targetIban.isEmpty() || amountStr.isEmpty()) {
            showError("Lütfen IBAN ve Tutar giriniz.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            boolean success = transactionService.transferMoney(source.getId(), targetIban, amount, description);

            if (success) {
                lblStatus.setText("Transfer Başarılı!");
                lblStatus.setStyle("-fx-text-fill: #2ecc71;");
                txtAmount.clear();
                txtDescription.clear();
                txtTargetIban.clear();
            } else {
                showError("İşlem Başarısız! (Bakiye yetersiz veya IBAN hatalı)");
            }
        } catch (NumberFormatException e) {
            showError("Tutar sayısal olmalıdır (Örn: 150.50)");
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Veritabanı hatası oluştu.");
        }
    }

    @FXML
    public void handleBack(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/bank/personalfinance/view/Dashboard.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
            String cssPath = getClass().getResource("/com/bank/personalfinance/style/style.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Banka Otomasyonu - Dashboard");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(String msg) {
        lblStatus.setText(msg);
        lblStatus.setStyle("-fx-text-fill: #e74c3c;");
    }
}