package com.bank.personalfinance.dao;

import com.bank.personalfinance.model.Account;
import com.bank.personalfinance.util.DatabaseCon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    // 1. Kullanıcının Tüm Hesaplarını Getir (Dashboard'da göstermek için)
    public List<Account> getAccountsByUserId(int userId) {
        List<Account> accounts = new ArrayList<>();
            // SQL: O kişiye ait hesapları bul
        String sql = "SELECT * FROM accounts WHERE user_id = ?";

        try (Connection conn = DatabaseCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Senin yazdığın "ID'li Constructor"ı burada kullanıyoruz!
                Account account = new Account(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("account_name"),
                        rs.getString("iban"),
                        rs.getDouble("balance"),
                        rs.getString("currency")
                );
                accounts.add(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    // 2. Yeni Hesap Oluşturma (İleride lazım olur)
    public boolean createAccount(Account account) {
        String sql = "INSERT INTO accounts (user_id, account_name, iban, balance, currency) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, account.getUserId());
            stmt.setString(2, account.getAccountName());
            stmt.setString(3, account.getIban());
            stmt.setDouble(4, account.getBalance());
            stmt.setString(5, account.getCurrency());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // IBAN ile hesap bulma
    public Account getAccountByIban(String iban) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE iban = ?";
        try (Connection conn = DatabaseCon.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, iban);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Account(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("account_name"),
                        rs.getString("iban"),
                        rs.getDouble("balance"),
                        rs.getString("currency")

                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Bakiye güncellenmesi
    public boolean updateBalance(int accountId, double newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?";
        try (Connection conn = DatabaseCon.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, newBalance);
            stmt.setInt(2, accountId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Account getAccountById(int accountId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE id = ?";
        try (Connection conn = DatabaseCon.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Account(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("account_name"),
                        rs.getString("iban"),
                        rs.getDouble("balance"),
                        rs.getString("currency")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    // HESAP SİLME METODU
    public boolean deleteAccount(int accountId) {
        String sql = "DELETE FROM accounts WHERE id = ?";

        try (Connection conn = DatabaseCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, accountId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}