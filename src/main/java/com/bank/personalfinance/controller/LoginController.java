package com.bank.personalfinance.controller;

import com.bank.personalfinance.model.User;
import com.bank.personalfinance.service.UserService;
import com.bank.personalfinance.util.UserSession;
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
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField txtTcNo;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private Button btnLogin;

    private final UserService userService = new UserService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtPassword.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) handleLogin();
        });

        prepareAnimation(txtTcNo, 0);
        prepareAnimation(txtPassword, 150);
        prepareAnimation(btnLogin, 300);

        Platform.runLater(() -> txtTcNo.requestFocus());
    }

    @FXML
    protected void handleLoginBtn(ActionEvent event) {
        handleLogin();
    }

    private void handleLogin() {
        String tcNo = txtTcNo.getText().trim();
        String password = txtPassword.getText();

        if (tcNo.isEmpty() || password.isEmpty()) {
            lblError.setText("Lütfen boş alan bırakmayınız.");
            lblError.setStyle("-fx-text-fill: #e74c3c;");
            shakeNode(txtTcNo);
            shakeNode(txtPassword);
            return;
        }

        User user = userService.login(tcNo, password);

        if (user != null) {
            UserSession.getInstance().setCurrentUser(user);
            lblError.setText("Giriş Başarılı! Yönlendiriliyorsunuz...");
            lblError.setStyle("-fx-text-fill: #2ecc71;");
            navigateToDashboard();
        } else {
            lblError.setText("Hatalı TC veya Şifre!");
            lblError.setStyle("-fx-text-fill: #e74c3c;");
            shakeNode(txtTcNo);
            shakeNode(txtPassword);
            txtPassword.clear();
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

    private void navigateToDashboard() {
        try {
            Stage stage = (Stage) txtTcNo.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/bank/personalfinance/view/Dashboard.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1280, 720);

            stage.setTitle("Finova Bank - Ana Sayfa");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            lblError.setText("Dashboard yüklenirken hata oluştu!");
        }
    }

    @FXML
    private void handleRegisterSwitch(ActionEvent event) {
        changeScene(event, "Register.fxml");
    }

    private void changeScene(ActionEvent event, String fxmlFileName) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/bank/personalfinance/view/" + fxmlFileName));
            javafx.scene.Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 1280, 720);

            URL cssUrl = getClass().getResource("/com/bank/personalfinance/style/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}