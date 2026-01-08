package com.bank.personalfinance.dao;

import com.bank.personalfinance.model.Transaction;
import com.bank.personalfinance.util.DatabaseCon;

import java.security.PublicKey;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public boolean saveTransaction(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions " + "(source_account_id, target_account_id, " + "source_iban, target_iban, source_name, target_name, " + "amount, transaction_type, category, description) " +  "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseCon.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
            if (transaction.getSourceAccountId() == 0) {
                stmt.setNull(1, Types.INTEGER);
            } else {
                stmt.setInt(1, transaction.getSourceAccountId());
            }

            if (transaction.getTargetAccountId() == 0) {
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt.setInt(2, transaction.getTargetAccountId());
            }

            stmt.setString(3, transaction.getSourceIban());
            stmt.setString(4, transaction.getTargetIban());
            stmt.setString(5, transaction.getSourceName());
            stmt.setString(6, transaction.getTargetName());

            stmt.setDouble(7, transaction.getAmount());
            stmt.setString(8, transaction.getTransactionType());
            stmt.setString(9, transaction.getCategory());
            stmt.setString(10, transaction.getDescription());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Transaction> getTransactionsByAccountId(int accountId) throws SQLException{
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE source_account_id = ? OR target_account_id = ? ORDER BY transaction_date DESC";

        try (Connection conn = DatabaseCon.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, accountId);
            stmt.setInt(2, accountId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Transaction tr = new Transaction(
                        rs.getInt("id"),
                        rs.getInt("source_account_id"),
                        rs.getInt("target_account_id"),
                        rs.getString("source_iban"),
                        rs.getString("target_iban"),
                        rs.getString("source_name"),
                        rs.getString("target_name"),
                        rs.getDouble("amount"),
                        rs.getString("transaction_type"),
                        rs.getString("category"),
                        rs.getTimestamp("transaction_date"),
                        rs.getString("description")
                );
                list.add(tr);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // KATEGORİYE GÖRE AYLIK TOPLAM HARCAMA MİKTARINI GETİR
    public double getTotalExpenseByCategory(int accountId, String category) throws SQLException {
        String sql = "SELECT SUM(amount) FROM transactions " +
                "WHERE source_account_id = ? " +
                "AND category = ? " +
                "AND transaction_type = 'GİDER' " +
                "AND MONTH(transaction_date) = MONTH(CURRENT_DATE()) " +
                "AND YEAR(transaction_date) = YEAR(CURRENT_DATE())";

        try (Connection conn = DatabaseCon.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, accountId);
            stmt.setString(2, category);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1); // Toplam tutar döndürülür (Aylık toplam harcama cinsinden)
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // HARCAMA GRAFİĞİ VE RAPORLAMA İÇİN METOD
    public java.util.Map<String, Double> getExpensesByCategory(int accountId) throws SQLException {
        java.util.Map<String, Double> data = new java.util.HashMap<>();

        // SQL: Sadece GİDER olanları kategoriye göre grupla ve topla
        String sql = "SELECT category, SUM(amount) as total FROM transactions " +
                "WHERE source_account_id = ? AND transaction_type = 'GİDER' " +
                "GROUP BY category";

        try (Connection conn = DatabaseCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String cat = rs.getString("category");
                double amount = rs.getDouble("total");
                data.put(cat, amount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
}