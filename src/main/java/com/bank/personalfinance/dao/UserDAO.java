package com.bank.personalfinance.dao;

import com.bank.personalfinance.model.User;
import com.bank.personalfinance.util.DatabaseCon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    public User login(String tcNo, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE tc_no = ? AND password = ?";

        try (Connection conn = DatabaseCon.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tcNo);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                //Kullanıcı bulunur ise
                return new User(
                        rs.getInt("id"),
                        rs.getString("tc_no"),
                        rs.getString("password"),
                        rs.getString("ad_soyad"),
                        rs.getString("role")
                );
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return  null; // Kullanıcı yoksa NULL döner.
    }

    // Kullanıcı Kaydı
    public boolean register(User user) {
        String sql = "INSERT INTO users (tc_no, password, ad_soyad, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getTcNo());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getAdSoyad());
            stmt.setString(4, "CUSTOMER");

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
