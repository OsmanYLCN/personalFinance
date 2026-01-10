package com.bank.personalfinance.controller;

import com.bank.personalfinance.model.Account;
import com.bank.personalfinance.model.BudgetLimit;
import com.bank.personalfinance.model.User;
import com.bank.personalfinance.service.AccountService;
import com.bank.personalfinance.service.BudgetService;
import com.bank.personalfinance.util.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class BudgetController implements Initializable {

    @FXML private Label lblTotalBudget;
    @FXML private Label lblTotalSpent;
    @FXML private Label lblRemaining;
    @FXML private VBox budgetContainer;

    private final BudgetService budgetService = new BudgetService();
    private final AccountService accountService = new AccountService();

    // Şimdilik varsayılan olarak kullanıcının ilk hesabını baz alıyoruz.
    // İleride buraya "Hesap Seçme" kutusu da koyabiliriz.
    private int currentAccountId = -1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadUserData();
    }

    /**
     * Kullanıcının hesabını bulur ve verileri yükler.
     */
    public void refreshData() {
        if (currentAccountId != -1) {
            loadBudgetView(currentAccountId);
        }
    }

    private void loadUserData() {
        User user = UserSession.getInstance().getCurrentUser();
        if (user != null) {
            List<Account> accounts = accountService.getAccountsByUserId(user.getId());
            if (!accounts.isEmpty()) {
                // İlk hesabı varsayılan al
                currentAccountId = accounts.get(0).getId();
                loadBudgetView(currentAccountId);
            } else {
                showError("Hesap bulunamadı! Lütfen önce bir hesap oluşturun.");
            }
        }
    }

    /**
     * Bütçe ve limit verilerini yükleyip ekrana basar.
     */
    private void loadBudgetView(int accountId) {
        budgetContainer.getChildren().clear(); // Temizle

        List<BudgetLimit> limits = budgetService.getLimits(accountId);

        double totalBudget = 0;
        double totalSpentAll = 0;

        for (BudgetLimit limit : limits) {
            // Bu kategori için harcanan tutarı bul
            double spent = budgetService.getSpentAmountByCategory(accountId, limit.getLimitType());

            // Kartı oluştur ve ekle
            Node card = createBudgetCard(limit, spent);
            budgetContainer.getChildren().add(card);

            // Toplamları hesapla
            totalBudget += limit.getLimitAmount();
            // Sadece limiti olan kategorilerin harcamasını toplama dahil ediyoruz (Bütçe takibi için)
            totalSpentAll += spent;
        }

        // Üst Kartları Güncelle
        lblTotalBudget.setText(formatCurrency(totalBudget));
        lblTotalSpent.setText(formatCurrency(totalSpentAll));

        double remaining = totalBudget - totalSpentAll;
        lblRemaining.setText(formatCurrency(remaining));

        if (remaining < 0) {
            lblRemaining.setTextFill(Color.web("#e74c3c")); // Kırmızı (Eksi Bakiye)
        } else {
            lblRemaining.setTextFill(Color.web("#2ecc71")); // Yeşil
        }
    }

    /**
     * Dinamik Bütçe Kartı (Progress Bar + Yazılar) oluşturur.
     */
    private Node createBudgetCard(BudgetLimit limit, double spentAmount) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");

        // 1. Başlık Satırı (Kategori Adı ve Tutar Bilgisi)
        HBox header = new HBox();
        Label lblTitle = new Label(limit.getLimitType());
        lblTitle.setTextFill(Color.WHITE);
        lblTitle.setFont(new Font("System Bold", 16));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblInfo = new Label(formatCurrency(spentAmount) + " / " + formatCurrency(limit.getLimitAmount()));
        lblInfo.setTextFill(Color.web("#bdc3c7"));

        // Silme Butonu (Küçük Çöp Kutusu)
        Button btnDelete = new Button("🗑");
        btnDelete.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d; -fx-cursor: hand;");
        btnDelete.setOnAction(e -> deleteLimit(limit));

        header.getChildren().addAll(lblTitle, spacer, lblInfo, btnDelete);

        // 2. İlerleme Çubuğu (Progress Bar)
        ProgressBar progressBar = new ProgressBar();
        progressBar.setMaxWidth(Double.MAX_VALUE); // Tüm genişliği kapla

        double progress = 0;
        if (limit.getLimitAmount() > 0) {
            progress = spentAmount / limit.getLimitAmount();
        }
        progressBar.setProgress(progress);

        // Renk Ayarı (Yeşil -> Sarı -> Kırmızı)
        if (progress > 1.0) {
            // Limit aşıldı! (Kırmızı ve Uyarı)
            progressBar.setStyle("-fx-accent: #e74c3c;");
            Label lblWarning = new Label("⚠️ LİMİT AŞILDI!");
            lblWarning.setTextFill(Color.web("#e74c3c"));
            lblWarning.setFont(new Font("System Bold", 12));
            card.getChildren().add(lblWarning); // Uyarıyı en başa ekleyebiliriz veya barın altına
        } else if (progress > 0.75) {
            progressBar.setStyle("-fx-accent: #f39c12;"); // Turuncu (Tehlike)
        } else {
            progressBar.setStyle("-fx-accent: #2ecc71;"); // Yeşil (Güvenli)
        }

        card.getChildren().addAll(header, progressBar);
        return card;
    }

    private void deleteLimit(BudgetLimit limit) {
        budgetService.removeLimit(limit.getAccountId(), limit.getLimitType());
        refreshData(); // Ekranı yenile
    }

    private String formatCurrency(double amount) {
        return NumberFormat.getCurrencyInstance(new Locale("tr", "TR")).format(amount);
    }

    // --- NAVİGASYON VE DİALOG ---

    @FXML
    public void handleAddLimitDialog(ActionEvent event) {
        if (currentAccountId == -1) {
            showError("İşlem yapılacak hesap seçilemedi!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bank/personalfinance/view/BudgetForm.fxml"));
            javafx.scene.Parent root = loader.load();

            // Controller'a verileri aktar
            com.bank.personalfinance.controller.BudgetFormController formController = loader.getController();
            formController.setParentController(this, currentAccountId);

            Stage stage = new Stage();
            stage.initStyle(javafx.stage.StageStyle.TRANSPARENT); // Çerçevesiz
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL); // Arkaya tıklanmasın

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Pencere açılırken hata oluştu: " + e.getMessage());
        }
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

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Hata");
        alert.setContentText(msg);
        alert.show();
    }


}