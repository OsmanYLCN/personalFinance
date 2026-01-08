package com.bank.personalfinance;

import com.bank.personalfinance.dao.AccountDAO;
import com.bank.personalfinance.dao.BudgetLimitDAO;
import com.bank.personalfinance.dao.UserDAO;
import com.bank.personalfinance.model.Account;
import com.bank.personalfinance.model.BudgetLimit;
import com.bank.personalfinance.model.User;
import com.bank.personalfinance.service.TransactionService;

import java.sql.SQLException;
import java.util.List;

public class ConsoleTest {
    public static void main(String[] args) throws SQLException {
        System.out.println("==========================================");
        System.out.println("🚀 BANKA BACKEND SİSTEM TESTİ BAŞLIYOR 🚀");
        System.out.println("==========================================\n");

        // 1. GİRİŞ İŞLEMİ
        System.out.println("[ADIM 1] Kullanıcı Girişi...");
        UserDAO userDAO = new UserDAO();
        User user = userDAO.login("11111111111", "1234"); // Hamza

        if (user == null) {
            System.out.println("❌ Giriş Başarısız! Kullanıcı bulunamadı.");
            return;
        }
        System.out.println("✅ Giriş Başarılı: " + user.getAdSoyad() + "\n");

        // 2. HESAP SEÇİMİ
        System.out.println("[ADIM 2] Hesap Bilgileri Alınıyor...");
        AccountDAO accountDAO = new AccountDAO();
        List<Account> accounts = accountDAO.getAccountsByUserId(user.getId());

        if (accounts.isEmpty()) {
            System.out.println("❌ Kullanıcının hesabı yok!");
            return;
        }

        Account aktifHesap = accounts.get(0); // İlk hesabı (Maaş Hesabım) seçtik
        System.out.println("🏦 Seçilen Hesap: " + aktifHesap.getAccountName());
        System.out.println("💰 Mevcut Bakiye: " + aktifHesap.getBalance() + " TL\n");

        TransactionService transactionService = new TransactionService();

        // 3. GELİR EKLEME TESTİ
        System.out.println("[ADIM 3] Gelir Ekleme Testi (Freelance: +5000 TL)...");
        boolean gelirSonuc = transactionService.addIncome(aktifHesap.getId(), 5000, "Freelance Proje Ödemesi", "Ek Gelir");

        if (gelirSonuc) {
            aktifHesap = accountDAO.getAccountById(aktifHesap.getId()); // Bakiyeyi güncelle
            System.out.println("✅ Gelir Eklendi! Yeni Bakiye: " + aktifHesap.getBalance() + " TL\n");
        } else {
            System.out.println("❌ Gelir Eklenemedi!\n");
        }

        // 4. BÜTÇE LİMİTİ KOYMA TESTİ
        System.out.println("[ADIM 4] Bütçe Limiti Tanımlama (Mutfak: 2000 TL)...");
        BudgetLimitDAO budgetDAO = new BudgetLimitDAO();
        boolean limitSonuc = budgetDAO.saveLimit(new BudgetLimit(aktifHesap.getId(), "Mutfak", 2000.0));

        if (limitSonuc) {
            System.out.println("✅ 'Mutfak' kategorisine 2000 TL limit koyuldu.\n");
        } else {
            System.out.println("❌ Limit Koyulamadı!\n");
        }

        // 5. GİDER TESTİ - LİMİT İÇİNDE (Sorunsuz Olmalı)
        System.out.println("[ADIM 5] Normal Harcama Testi (Market: 500 TL)...");
        // Önce kontrol et
        String durum1 = transactionService.checkBudgetStatus(aktifHesap.getId(), "Mutfak", 500);
        System.out.println("🔍 Limit Kontrolü: " + durum1);

        if (durum1.equals("OK")) {
            transactionService.addExpense(aktifHesap.getId(), 500, "Haftalık Alışveriş", "Mutfak");
            aktifHesap = accountDAO.getAccountById(aktifHesap.getId());
            System.out.println("✅ Harcama Yapıldı. Yeni Bakiye: " + aktifHesap.getBalance() + " TL\n");
        } else {
            System.out.println("❌ Beklenmedik Uyarı: " + durum1 + "\n");
        }

        // 6. GİDER TESTİ - LİMİT AŞIMI (Uyarı Vermeli)
        System.out.println("[ADIM 6] Limit Aşımı Testi (Market: 2500 TL Daha Harcıyoruz)...");
        // Şu an Mutfak'ta 500 TL harcandı. Limit 2000 TL. Kalan hak 1500 TL.
        // Biz 2500 TL harcamaya çalışacağız.

        String durum2 = transactionService.checkBudgetStatus(aktifHesap.getId(), "Mutfak", 2500);
        System.out.println("🔍 Limit Kontrolü Sonucu: " + durum2);

        if (durum2.startsWith("UYARI")) {
            System.out.println("✅ BAŞARILI! Sistem limiti fark etti ve uyardı.");
            System.out.println("👉 Kullanıcıya şu mesaj gösterilecek: \"" + durum2 + "\"");

            // Simülasyon: Kullanıcı "Yine de harca" dedi varsayalım
            System.out.println("⚠️ Kullanıcı uyarıya rağmen işlemi onayladı...");
            transactionService.addExpense(aktifHesap.getId(), 2500, "Büyük Market Alışverişi", "Mutfak");
            aktifHesap = accountDAO.getAccountById(aktifHesap.getId());
            System.out.println("✅ İşlem zorla yapıldı. Son Bakiye: " + aktifHesap.getBalance() + " TL");
        } else {
            System.out.println("❌ HATA! Sistem limiti fark etmedi. 'OK' döndü.");
        }

        System.out.println("\n==========================================");
        System.out.println("🏁 TEST TAMAMLANDI 🏁");
        System.out.println("==========================================");
    }
}