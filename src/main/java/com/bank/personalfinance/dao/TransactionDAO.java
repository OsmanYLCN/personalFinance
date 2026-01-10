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
    /**
     * İşlem geçmişinde detaylı arama ve filtreleme yapar.
     * Kullanıcı null veya boş değer gönderirse o filtreyi yok sayar.
     *
     * @param accountId  İşlemlerin ait olduğu hesap ID'si
     * @param startDate  Başlangıç tarihi (Opsiyonel)
     * @param endDate    Bitiş tarihi (Opsiyonel)
     * @param type       İşlem türü: "GELİR", "GİDER" veya "HEPSİ" (Opsiyonel)
     * @param category   Kategori adı (Opsiyonel)
     * @return Filtrelenmiş işlem listesi
     */
    public List<Transaction> searchTransactions(int accountId, java.time.LocalDate startDate, java.time.LocalDate endDate, String type, String category) {
        List<Transaction> list = new ArrayList<>();

        // Temel Sorgu: Bu hesaba ait (Gönderen veya Alan) işlemleri getir
        StringBuilder sql = new StringBuilder("SELECT * FROM transactions WHERE (source_account_id = ? OR target_account_id = ?) ");
        List<Object> params = new ArrayList<>();
        params.add(accountId);
        params.add(accountId);

        // --- DİNAMİK FİLTRELER ---

        // 1. Tarih Aralığı Filtresi
        if (startDate != null) {
            sql.append(" AND transaction_date >= ?");
            params.add(java.sql.Date.valueOf(startDate));
        }
        if (endDate != null) {
            // Bitiş tarihinin gün sonunu (23:59:59) kapsamak için +1 gün ekleyip küçüktür (<) diyebiliriz veya saat ekleyebiliriz.
            // Basitlik adına sadece günü baz alıyoruz.
            sql.append(" AND transaction_date <= ?");
            params.add(java.sql.Date.valueOf(endDate));
        }

        // 2. İşlem Türü Filtresi (GELİR / GİDER)
        if (type != null && !type.equals("HEPSİ")) {
            sql.append(" AND transaction_type = ?");
            params.add(type);
        }

        // 3. Kategori Filtresi
        if (category != null && !category.equals("HEPSİ") && !category.isEmpty()) {
            sql.append(" AND category = ?");
            params.add(category);
        }

        // Sıralama: En yeniden en eskiye
        sql.append(" ORDER BY transaction_date DESC");

        try (Connection conn = DatabaseCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            // Parametreleri sırasıyla sorguya yerleştir
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Transaction(
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
                ));
            }

        } catch (SQLException e) {
            System.err.println("[HATA] İşlem geçmişi aranırken SQL hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    /**
     * IBAN olmadan, doğrudan harcama (Market, Fatura vb.) ekler.
     */
    public boolean addExpense(int accountId, String category, double amount, String description) {
        // target_account_id = 0 yapıyoruz çünkü para başka bir kullanıcıya gitmiyor, dışarı gidiyor.
        String sql = "INSERT INTO transactions (source_account_id, target_account_id, source_iban, target_iban, source_name, target_name, amount, transaction_type, category, transaction_date, description) " +
                "VALUES (?, NULL, (SELECT iban FROM accounts WHERE id=?), 'HARCAMA', 'Benim Hesap', ?, ?, 'GİDER', ?, NOW(), ?)";

        // Hesaptan parayı düşmemiz de lazım
        String updateBalanceSql = "UPDATE accounts SET balance = balance - ? WHERE id = ?";

        Connection conn = null;
        try {
            conn = DatabaseCon.getConnection();
            conn.setAutoCommit(false); // Transaction başlat

            // 1. İşlemi Kaydet
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, accountId);
                stmt.setInt(2, accountId); // IBAN'ı bulmak için tekrar ID
                stmt.setString(3, category.toUpperCase(java.util.Locale.ENGLISH)); // Alıcı Adı yerine Kategori yazsın
                stmt.setDouble(4, amount);
                stmt.setString(5, category);
                stmt.setString(6, description);
                stmt.executeUpdate();
            }

            // 2. Bakiyeden Düş
            try (PreparedStatement stmt = conn.prepareStatement(updateBalanceSql)) {
                stmt.setDouble(1, amount);
                stmt.setInt(2, accountId);
                stmt.executeUpdate();
            }

            conn.commit(); // Onayla
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}