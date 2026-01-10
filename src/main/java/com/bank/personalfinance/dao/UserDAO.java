package com.bank.personalfinance.dao;

import com.bank.personalfinance.model.User;
import com.bank.personalfinance.util.DatabaseCon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    // --- GİRİŞ İŞLEMİ ---
    public User login(String tcNo, String password) {
        // SQL düzeltildi: 'sifre' yerine 'password'
        String sql = "SELECT * FROM users WHERE tc_no = ? AND password = ?";

        try (Connection conn = DatabaseCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tcNo);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("tc_no"),
                        rs.getString("password"), // Kolon adı 'password'
                        rs.getString("ad_soyad"),
                        rs.getString("role"),
                        rs.getString("email"),
                        rs.getString("telefon")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // --- KULLANICI KAYDI ---
    public boolean register(User user) {
        // SQL düzeltildi: 'sifre' yerine 'password'
        String sql = "INSERT INTO users (tc_no, password, ad_soyad, role, email, telefon) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getTcNo());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getAdSoyad());
            stmt.setString(4, "USER"); // Varsayılan rol
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getTelefon());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- KULLANICI BİLGİLERİNİ GÜNCELLE ---
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET telefon = ?, email = ? WHERE id = ?";
        try (Connection conn = DatabaseCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getTelefon());
            stmt.setString(2, user.getEmail());
            stmt.setInt(3, user.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- ŞİFRE DEĞİŞTİRME ---
    public boolean updatePassword(int userId, String newPassword) {
        // SQL düzeltildi: 'sifre' yerine 'password'
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = DatabaseCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- MEVCUT ŞİFRE KONTROLÜ ---
    public boolean checkPassword(int userId, String password) {
        // SQL düzeltildi: 'sifre' yerine 'password'
        String sql = "SELECT id FROM users WHERE id = ? AND password = ?";
        try (Connection conn = DatabaseCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Eğer kayıt varsa şifre doğrudur
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- YARDIMCI METOT ---
    public boolean createUser(User user) {
        return register(user);
    }
}