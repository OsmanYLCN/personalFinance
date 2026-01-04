package com.bank.personalfinance;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;

public class BankingApp extends Application {

    @Override
    public void start(Stage stage) {
        // 1. Basit bir Label (Yazı)
        Label helloLabel = new Label("JavaFX Kurulumu Başarılı!");
        helloLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // 2. Basit bir Buton
        Button testButton = new Button("Bana Tıkla");


        testButton.setOnAction(e -> {
            helloLabel.setText("Buton Çalışıyor! ✅");
            helloLabel.setStyle("-fx-text-fill: green; -fx-font-size: 18px;");
        });

        // 3. Elemanları ekrana diz (VBox: Alt alta dizer)
        VBox root = new VBox(20); // Elemanlar arası 20px boşluk
        root.setAlignment(Pos.CENTER); // Ortala
        root.getChildren().addAll(helloLabel, testButton);

        // 4. Sahneyi oluştur ve göster
        Scene scene = new Scene(root, 400, 300);
        stage.setTitle("Sistem Kontrol Ekranı");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}