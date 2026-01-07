package com.bank.personalfinance;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BankingApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Test kodlarını sildik, artık Login.fxml dosyasını yüklüyoruz
        FXMLLoader fxmlLoader = new FXMLLoader(BankingApp.class.getResource("/com/bank/personalfinance/view/Login.fxml"));

        // Sahne boyutu (Scene Builder'da 600x400 yapmıştık)
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);

        stage.setTitle("Banka Otomasyonu - Giriş");
        stage.setScene(scene);
        stage.setResizable(false); // Kullanıcı ekran boyutunu bozamasın
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}