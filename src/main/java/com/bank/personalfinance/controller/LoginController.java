package com.bank.personalfinance.controller;

import com.bank.personalfinance.model.User;
import com.bank.personalfinance.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.bank.personalfinance.util.UserSession;

import java.io.IOException;

public class LoginController {

    @FXML private TextField txtTcNo;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    // ARTIK DAO DEĞİL, SERVICE KULLANIYORUZ!
    private final UserService userService = new UserService();

    @FXML
    protected void handleLoginBtn(ActionEvent event) {
        String tcNo = txtTcNo.getText().trim(); // trim boşlukları siler
        String password = txtPassword.getText();

        if (tcNo.isEmpty() || password.isEmpty()) {
            lblError.setText("Lütfen boş alan bırakmayınız.");
            return;
        }

        // Service üzerinden giriş denemesi
        User user = userService.login(tcNo, password);

        if (user != null) {
            // Oturumu kaydet
            UserSession.getInstance().setCurrentUser(user);
            System.out.println("Giriş Başarılı: " + user.getAdSoyad());

            // Dashboard'a Yönlendir (Henüz Dashboard.fxml yoksa hata verir, sonraki adımda yapacağız)
            // changeScene(event, "/com/bank/personalfinance/view/Dashboard.fxml");
            lblError.setStyle("-fx-text-fill: green;");
            lblError.setText("Giriş Başarılı! Dashboard hazırlanıyor...");
        } else {
            lblError.setStyle("-fx-text-fill: red;");
            lblError.setText("Hatalı TC veya Şifre!");
        }
    }

    // Sahne değiştirme metodu (Standart kalıp)
    private void changeScene(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            lblError.setText("Sayfa yüklenirken hata oluştu.");
        }
    }
}