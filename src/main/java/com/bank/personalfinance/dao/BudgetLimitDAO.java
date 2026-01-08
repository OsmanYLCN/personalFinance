package com.bank.personalfinance.dao;

import com.bank.personalfinance.model.BudgetLimit;
import com.bank.personalfinance.util.DatabaseCon;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public class BudgetLimitDAO {

    // BÜTÇE LİMİTİ EKLEME
    public boolean saveLimit(BudgetLimit limit) throws SQLException {

        deleteLimit(limit.getAccountId(), limit.getLimitType());

        String sql = "INSERT INTO budget_limits (account_id, limit_type, limit_amount) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseCon.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit.getAccountId());
            stmt.setString(2, limit.getLimitType());
            stmt.setDouble(3, limit.getLimitAmount());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // BİR KATEGORİNİN LİMİTİNİ ÇAĞIRMA
    public BudgetLimit getLimit(int accountId, String category) throws SQLException {
        String sql = "SELECT * FROM budget_limits WHERE account_id = ? AND limit_type = ?";

        try (Connection conn = DatabaseCon.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, accountId);
            stmt.setString(2, category);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new BudgetLimit(
                        rs.getInt("id"),
                        rs.getInt("account_id"),
                        rs.getString("limit_type"),
                        rs.getDouble("limit_amount")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ESKİ LİMİT SİLME
    public void deleteLimit(int accountId, String category) {
        String sql = "DELETE FROM budget_limits WHERE account_id = ? AND limit_type = ?";
        try (Connection conn = DatabaseCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            stmt.setString(2, category);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
