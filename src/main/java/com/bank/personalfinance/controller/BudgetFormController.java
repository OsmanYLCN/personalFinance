package com.bank.personalfinance.controller;

import com.bank.personalfinance.service.BudgetService;
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

public class BudgetFormController implements Initializable {

    @FXML private ComboBox<String> cmbCategory;
    @FXML private TextField txtAmount;
    @FXML private Label lblError;

    private final BudgetService budgetService = new BudgetService();
    private BudgetController parentController;
    private int accountId; // Hangi hesaba limit eklediğimizi bilmemiz lazım

    /**
     * Ana pencere ile bağlantıyı kurar.
     */
    public void setParentController(BudgetController parentController, int accountId) {
        this.parentController = parentController;
        this.accountId = accountId;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Standart kategorilerimiz
        cmbCategory.getItems().addAll(
                "Market", "Fatura", "Kira", "Giyim",
                "Eğlence", "Ulaşım", "Sağlık", "Eğitim", "Diğer"
        );
    }

    @FXML
    public void handleSave(ActionEvent event) {
        String category = cmbCategory.getValue();
        String amountStr = txtAmount.getText().trim();

        // 1. Validasyonlar
        if (category == null || category.isEmpty()) {
            lblError.setText("Lütfen bir kategori seçiniz.");
            return;
        }

        if (amountStr.isEmpty()) {
            lblError.setText("Lütfen bir tutar giriniz.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                lblError.setText("Tutar 0'dan büyük olmalıdır.");
                return;
            }

            // 2. Kaydetme İşlemi
            boolean success = budgetService.saveLimit(accountId, category, amount);

            if (success) {
                // Ana sayfayı yenile ve pencreyi kapat
                if (parentController != null) {
                    parentController.refreshData();
                }
                closeWindow(event);
            } else {
                lblError.setText("Kaydedilirken veritabanı hatası oluştu!");
            }

        } catch (NumberFormatException e) {
            lblError.setText("Geçersiz tutar formatı! (Örn: 1500.50)");
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