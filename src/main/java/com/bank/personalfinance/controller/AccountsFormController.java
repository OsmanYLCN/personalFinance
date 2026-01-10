package com.bank.personalfinance.controller;

import com.bank.personalfinance.model.Account;
import com.bank.personalfinance.model.User;
import com.bank.personalfinance.service.AccountService;
import com.bank.personalfinance.util.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AccountsFormController implements Initializable {

    @FXML private TextField txtAccountName;
    @FXML private ComboBox<String> cmbCurrency;
    @FXML private Label lblError;

    private final AccountService accountService = new AccountService();
    private AccountsController parentController; // Ana sayfayı yenilemek için

    public void setParentController(AccountsController parentController) {
        this.parentController = parentController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbCurrency.getItems().addAll("TL", "USD", "EUR", "GAU");
        cmbCurrency.setValue("TL");
    }

    @FXML
    public void handleSave(ActionEvent event) {
        String name = txtAccountName.getText().trim();
        String currency = cmbCurrency.getValue();

        if (name.isEmpty()) {
            lblError.setText("Lütfen bir hesap adı giriniz!");
            return;
        }

        User user = UserSession.getInstance().getCurrentUser();
        // Rastgele bir IBAN üret (Gerçek hayatta sırayla verilir)
        String randomIban = "TR" + (long) (Math.random() * 1_000_000_0000L);

        Account newAccount = new Account(user.getId(), name, randomIban, 0.0, currency);

        boolean success = accountService.createAccount(newAccount);

        if (success) {
            // Ana sayfayı yenile
            if (parentController != null) {
                parentController.refreshAccounts();
            }
            closeWindow(event);
        } else {
            lblError.setText("Veritabanı hatası oluştu!");
        }
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        closeWindow(event);
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}