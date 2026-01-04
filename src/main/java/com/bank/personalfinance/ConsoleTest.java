package com.bank.personalfinance;

import com.bank.personalfinance.dao.UserDAO;
import com.bank.personalfinance.model.User;
import com.bank.personalfinance.dao.AccountDAO;
import com.bank.personalfinance.model.Account;

import java.sql.SQLException;
import java.util.List;

public class ConsoleTest {
    public static void main(String[] args) throws SQLException {
        System.out.println("Personal Finance Management System Test");

        System.out.println("\n1. User Login Test");
        UserDAO userDAO = new UserDAO();

        User girisYapanKullanici = userDAO.login("22222222222", "1234");

        if (girisYapanKullanici != null) {
            System.out.println("Basarili: Giris yapildi!");
            System.out.println("Kullanici: " + girisYapanKullanici.getAdSoyad());
            System.out.println("Kullanici ID: " + girisYapanKullanici.getId());

            System.out.println("\nKullanicinin hesaplari getiriliyor...");
            AccountDAO accountDAO = new AccountDAO();
            List<Account> hesaplar = accountDAO.getAccountsByUserId(girisYapanKullanici.getId());

            if (hesaplar.isEmpty()) {
                System.out.println("Bu kullanicinin hiç hesabı mevcut değil");
            } else {
                System.out.println("Hesaplar bulundu (" + hesaplar.size() + " adet):");
                for (Account acc : hesaplar) {
                    System.out.println("Hesap ismi: " + acc.getAccountName() + " | " + acc.getIban() + " | Bakiye: " + acc.getBalance() + " " + acc.getCurrency());
                }
            }
        } else {
            System.out.println("Giriş başarısız. Sistem ile veritabanı bağlantısında hata mevcut!");
        }

        System.out.println("\n------TEST BITTI------");
    }
}
