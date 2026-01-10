package com.bank.personalfinance.controller;

import com.bank.personalfinance.model.User;
import com.bank.personalfinance.service.UserService;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML private TextField txtAdSoyad;
    @FXML private TextField txtTcNo;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private Button btnRegister;
    @FXML private PasswordField txtPasswordConfirm;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;
    private final UserService userService = new UserService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prepareAnimation(txtAdSoyad, 0);
        prepareAnimation(txtTcNo, 100);
        prepareAnimation(txtPassword, 200);
        prepareAnimation(btnRegister, 300);
        prepareAnimation(txtPasswordConfirm, 250);
        Platform.runLater(() -> txtAdSoyad.requestFocus());
    }

    @FXML
    public void handleRegisterBtn(ActionEvent event) {
        // Form verilerini al
        String adSoyad = txtAdSoyad.getText().trim();
        String tcNo = txtTcNo.getText().trim();
        String email = txtEmail.getText().trim(); // YENİ
        String phone = txtPhone.getText().trim(); // YENİ
        String password = txtPassword.getText();
        String passwordConfirm = txtPasswordConfirm.getText();

        // 1. Boş Alan Kontrolü (Email ve Telefon eklendi)
        if (adSoyad.isEmpty() || tcNo.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            lblError.setText("Lütfen tüm alanları doldurunuz!");
            lblError.setStyle("-fx-text-fill: #e74c3c;");
            if (btnRegister != null) shakeNode(btnRegister); // Hata vermemesi için null kontrolü
            return;
        }

        // 2. Şifre Eşleşme Kontrolü
        if (!password.equals(passwordConfirm)) {
            lblError.setText("Şifreler uyuşmuyor!");
            lblError.setStyle("-fx-text-fill: #e74c3c;");

            txtPassword.clear();
            txtPasswordConfirm.clear();
            if (txtPassword != null) shakeNode(txtPassword);
            if (txtPasswordConfirm != null) shakeNode(txtPasswordConfirm);
            return;
        }

        // 3. Veritabanına Kayıt (GÜNCELLENEN KISIM)
        // Artık tek tek string değil, dolu bir User nesnesi gönderiyoruz.
        // Sıralama: ID(0), TC, Şifre, AdSoyad, Rol, Email, Telefon
        User newUser = new User(0, tcNo, password, adSoyad, "CUSTOMER", email, phone);

        // UserService'deki register metodunun User nesnesi alması lazım.
        boolean isSuccess = userService.register(newUser);

        if (isSuccess) {
            lblError.setText("Kayıt Başarılı! Giriş ekranına dönülüyor...");
            lblError.setStyle("-fx-text-fill: #2ecc71;"); // Yeşil

            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(() -> handleLoginSwitch(event));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } else {
            lblError.setText("Kayıt Başarısız! (TC veya Email kullanımda)");
            lblError.setStyle("-fx-text-fill: #e74c3c;"); // Kırmızı
            if (btnRegister != null) shakeNode(btnRegister);
        }
    }

    @FXML
    public void handleLoginSwitch(ActionEvent event) {
        changeScene(event, "Login.fxml");
    }

    private void changeScene(ActionEvent event, String fxmlFileName) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/bank/personalfinance/view/" + fxmlFileName));
            javafx.scene.Parent root = fxmlLoader.load();

            Scene scene = new Scene(root, 1280, 720);

            String cssPath = "/com/bank/personalfinance/style/style.css";
            URL cssUrl = getClass().getResource(cssPath);

            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.out.println("HATA: CSS dosyası bulunamadı! Yol: " + cssPath);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            lblError.setText("Sayfa geçişinde hata oluştu!");
        }
    }

    private void prepareAnimation(Node node, int delayMillis) {
        node.setOpacity(0);
        node.setTranslateY(20);

        TranslateTransition tt = new TranslateTransition(Duration.millis(700), node);
        tt.setDelay(Duration.millis(delayMillis));
        tt.setToY(0);
        tt.play();

        FadeTransition ft = new FadeTransition(Duration.millis(700), node);
        ft.setDelay(Duration.millis(delayMillis));
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void shakeNode(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.play();
    }
}