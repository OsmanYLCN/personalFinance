package com.bank.personalfinance.controller;

import com.bank.personalfinance.service.TransactionService;
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

public class ExpenseFormController implements Initializable {

    @FXML private ComboBox<String> cmbCategory;
    @FXML private TextField txtAmount;
    @FXML private TextField txtDescription;
    @FXML private Label lblError;

    private final TransactionService transactionService = new TransactionService();
    private DashboardController parentController;
    private int accountId;

    public void setParentController(DashboardController parentController, int accountId) {
        this.parentController = parentController;
        this.accountId = accountId;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbCategory.getItems().addAll("Market", "Fatura", "Kira", "Giyim", "Eğlence", "Ulaşım", "Sağlık", "Eğitim", "Diğer");
    }

    @FXML
    public void handleSave(ActionEvent event) {
        String category = cmbCategory.getValue();
        String amountStr = txtAmount.getText();
        String desc = txtDescription.getText();

        if (category == null || amountStr.isEmpty()) {
            lblError.setText("Lütfen kategori ve tutar giriniz.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            boolean success = transactionService.addExpense(accountId, category, amount, desc);

            if (success) {
                if (parentController != null) parentController.refreshDashboard();
                ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
            } else {
                lblError.setText("Harcama kaydedilemedi!");
            }
        } catch (NumberFormatException e) {
            lblError.setText("Geçersiz tutar!");
        }
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }
}