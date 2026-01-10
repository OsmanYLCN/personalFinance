package com.bank.personalfinance.controller;

import com.bank.personalfinance.model.User;
import com.bank.personalfinance.service.UserService;
import com.bank.personalfinance.util.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    @FXML private TextField txtName;
    @FXML private TextField txtTC;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;
    @FXML private Label lblInfoStatus;

    @FXML private PasswordField txtOldPass;
    @FXML private PasswordField txtNewPass;
    @FXML private Label lblPassStatus;

    private final UserService userService = new UserService();
    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Oturumu açan kullanıcıyı al
        currentUser = UserSession.getInstance().getCurrentUser();

        if (currentUser != null) {
            txtName.setText(currentUser.getAdSoyad());
            txtTC.setText(currentUser.getTcNo());
            txtEmail.setText(currentUser.getEmail());
            txtPhone.setText(currentUser.getTelefon());
        }
    }

    @FXML
    public void handleUpdateInfo(ActionEvent event) {
        if (currentUser == null) return;

        currentUser.setEmail(txtEmail.getText());
        currentUser.setTelefon(txtPhone.getText());

        boolean success = userService.updateUserInfo(currentUser);
        if (success) {
            lblInfoStatus.setText("Bilgiler güncellendi! ✅");
            lblInfoStatus.setStyle("-fx-text-fill: #2ecc71"); // Yeşil
        } else {
            lblInfoStatus.setText("Hata oluştu! ❌");
            lblInfoStatus.setStyle("-fx-text-fill: #e74c3c"); // Kırmızı
        }
    }

    @FXML
    public void handleChangePassword(ActionEvent event) {
        if (currentUser == null) return;

        String oldPass = txtOldPass.getText();
        String newPass = txtNewPass.getText();

        if (oldPass.isEmpty() || newPass.isEmpty()) {
            lblPassStatus.setText("Alanlar boş olamaz!");
            lblPassStatus.setStyle("-fx-text-fill: #e74c3c");
            return;
        }

        boolean success = userService.changePassword(currentUser.getId(), oldPass, newPass);

        if (success) {
            lblPassStatus.setText("Şifre değişti! ✅");
            lblPassStatus.setStyle("-fx-text-fill: #2ecc71");
            txtOldPass.clear();
            txtNewPass.clear();
        } else {
            lblPassStatus.setText("Eski şifre yanlış! ❌");
            lblPassStatus.setStyle("-fx-text-fill: #e74c3c");
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
}