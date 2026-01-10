package com.bank.personalfinance.service;

import com.bank.personalfinance.dao.UserDAO;
import com.bank.personalfinance.model.User;

public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    // GİRİŞ İŞLEMİ
    public User login(String tcNo, String password) {
        return userDAO.login(tcNo, password);
    }

    // --- GÜNCELLENEN KAYIT METODU (User Nesnesi Alır) ---
    // Controller artık buraya User nesnesi gönderiyor.
    public boolean register(User user) {
        // DAO'daki register metodunu çağırıyoruz
        return userDAO.register(user);
    }

    // Eski String alan metodu SİLEBİLİRSİN veya aşırı yükleme (overload) olarak bırakabilirsin.
    // Ama Controller'da yeni yapıyı kullandığın için buna ihtiyacın kalmadı.
    /*
    public boolean register(String adSoyad, String tcNo, String password) {
         // Bu eski yöntem artık kullanılmıyor
         return false;
    }
    */

    // PROFİL GÜNCELLEME
    public boolean updateUserInfo(User user) {
        return userDAO.updateUser(user);
    }

    // ŞİFRE DEĞİŞTİRME
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        if (userDAO.checkPassword(userId, oldPassword)) {
            return userDAO.updatePassword(userId, newPassword);
        }
        return false;
    }
}