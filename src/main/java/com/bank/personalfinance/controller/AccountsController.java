package com.bank.personalfinance.controller;

import com.bank.personalfinance.model.Account;
import com.bank.personalfinance.model.User;
import com.bank.personalfinance.service.AccountService;
import com.bank.personalfinance.util.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
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
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.control.*;


public class AccountsController implements Initializable {

    @FXML private FlowPane cardsContainer;

    // Servis bağlantısı
    private final AccountService accountService = new AccountService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadAccounts();
    }

    public void loadAccounts() {
        // 1. Mevcut kullanıcıyı al
        User user = UserSession.getInstance().getCurrentUser();
        if (user == null) return;

        // 2. Önce ekranı temizle (Eski kartlar kalmasın)
        cardsContainer.getChildren().clear();

        // 3. Hesapları veritabanından çek
        List<Account> accounts = accountService.getAccountsByUserId(user.getId());

        // 4. Her hesap için bir kart oluştur ve ekrana koy
        for (int i = 0; i < accounts.size(); i++) {
            Node card = createAccountCard(accounts.get(i), i);
            cardsContainer.getChildren().add(card);
        }
    }

    // Bunu ekle veya mevcut loadAccounts'u public yap
    public void refreshAccounts() {
        loadAccounts();
    }
    // --- DİNAMİK KART ÜRETİCİSİ (JAVA KODUYLA TASARIM) ---
    private Node createAccountCard(Account account, int index) {
        VBox card = new VBox();
        card.setPrefSize(350, 200);
        card.setPadding(new Insets(20));
        card.setSpacing(10);

        // Kartın Rengi (Sırayla değişsin: Mavi, Turuncu, Yeşil, Mor...)
        String gradientStyle = getGradientStyle(index);
        card.setStyle(gradientStyle + "-fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);");

        // 1. Üst Satır: Hesap Adı ve Sil Butonu
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label lblName = new Label(account.getAccountName());
        lblName.setTextFill(Color.WHITE);
        lblName.setFont(new Font("System Bold", 18));

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        topRow.getChildren().addAll(lblName, spacer);

        // Eğer bakiye 0 ise Silme Butonu ekle
        if (account.getBalance() == 0) {
            Button btnDelete = new Button("🗑");
            btnDelete.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-background-radius: 10; -fx-cursor: hand;");
            btnDelete.setOnAction(e -> deleteAccount(account));
            topRow.getChildren().add(btnDelete);
        }

        // 2. IBAN Satırı (Kopyalanabilir)
        Label lblIban = new Label(account.getIban());
        lblIban.setTextFill(Color.rgb(255, 255, 255, 0.8));
        lblIban.setFont(new Font("Monospaced Regular", 14));
        lblIban.setOnMouseClicked(e -> copyToClipboard(account.getIban())); // Tıklayınca kopyalar
        lblIban.setStyle("-fx-cursor: hand;"); // Üzerine gelince el işareti çıksın

        // 3. Bakiye Kısmı (En altta)
        Region vSpacer = new Region();
        VBox.setVgrow(vSpacer, javafx.scene.layout.Priority.ALWAYS);

        Label lblBalance = new Label(formatCurrency(account.getBalance()));
        lblBalance.setTextFill(Color.WHITE);
        lblBalance.setFont(new Font("System Bold", 26));

        card.getChildren().addAll(topRow, lblIban, vSpacer, lblBalance);

        return card;
    }

    // --- YARDIMCI METOTLAR ---

    private String getGradientStyle(int index) {
        String[] gradients = {
                "-fx-background-color: linear-gradient(to bottom right, #00c6ff, #0072ff);", // Mavi
                "-fx-background-color: linear-gradient(to bottom right, #f2994a, #f2c94c);", // Turuncu
                "-fx-background-color: linear-gradient(to bottom right, #8e44ad, #c0392b);", // Mor/Kırmızı
                "-fx-background-color: linear-gradient(to bottom right, #11998e, #38ef7d);"  // Yeşil
        };
        return gradients[index % gradients.length];
    }

    private String formatCurrency(double amount) {
        return NumberFormat.getCurrencyInstance(new Locale("tr", "TR")).format(amount);
    }

    private void copyToClipboard(String text) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }

    private void deleteAccount(Account account) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Hesap Sil");
        alert.setHeaderText(account.getAccountName() + " silinecek.");
        alert.setContentText("Bu işlem geri alınamaz. Emin misiniz?");

        // CSS'i ekle (Karanlık tema bozulmasın)
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/bank/personalfinance/style/style.css").toExternalForm());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            // 1. Backend'e git ve sil
            boolean success = accountService.deleteAccount(account.getId());

            if (success) {
                // 2. Başarılıysa listeyi yenile (Kart ekrandan kaybolsun)
                refreshAccounts();
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Hesap silinirken hata oluştu!");
                errorAlert.show();
            }
        }
    }

    @FXML
    public void handleCreateAccountDialog(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bank/personalfinance/view/AccountsForm.fxml"));
            javafx.scene.Parent root = loader.load();

            // Controller'a erişip "Benim referansımı al" diyoruz (Yenileme yapabilsin diye)
            com.bank.personalfinance.controller.AccountsFormController formController = loader.getController();
            formController.setParentController(this);

            Stage stage = new Stage();
            // İŞTE SİHİR BURADA: TRANSPARENT (Çerçevesiz)
            stage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
            // Modality: Arkadaki ana ekrana tıklanmasını engeller
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT); // Köşelerin yuvarlak görünmesi için şart
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
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
            stage.setTitle("Finova - Dashboard");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}