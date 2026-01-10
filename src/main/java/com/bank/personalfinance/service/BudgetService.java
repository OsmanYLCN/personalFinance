package com.bank.personalfinance.service;

import com.bank.personalfinance.dao.BudgetLimitDAO;
import com.bank.personalfinance.dao.TransactionDAO;
import com.bank.personalfinance.model.BudgetLimit;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BudgetService {

    private final BudgetLimitDAO budgetLimitDAO;
    private final TransactionDAO transactionDAO;

    public BudgetService() {
        this.budgetLimitDAO = new BudgetLimitDAO();
        this.transactionDAO = new TransactionDAO();
    }

    /**
     * Yeni bir bütçe limiti oluşturur veya günceller.
     */
    public boolean saveLimit(int accountId, String category, double amount) {
        try {
            BudgetLimit limit = new BudgetLimit(accountId, category, amount);
            return budgetLimitDAO.saveLimit(limit);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Hesaba ait tüm limitleri getirir.
     */
    public List<BudgetLimit> getLimits(int accountId) {
        return budgetLimitDAO.getLimitsByAccountId(accountId);
    }

    /**
     * Limit siler.
     */
    public void removeLimit(int accountId, String category) {
        budgetLimitDAO.deleteLimit(accountId, category);
    }

    /**
     * Her kategori için o ay ne kadar harcandığını hesaplar.
     * @return Kategori İsmi -> Harcanan Tutar (Map)
     */
    public Map<String, Double> getSpendingStatus(int accountId) {
        try {
            // TransactionDAO'daki mevcut metodu kullanıyoruz
            // Not: TransactionDAO'da getExpensesByCategory metodu tüm zamanları getiriyorsa,
            // sadece BU AY'ı getirecek şekilde revize edilmesi gerekebilir.
            // Şimdilik mevcut yapıyı kullanıyoruz.
            return transactionDAO.getExpensesByCategory(accountId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    /**
     * Belirli bir kategori için bu ayki toplam harcamayı döner.
     */
    public double getSpentAmountByCategory(int accountId, String category) {
        try {
            return transactionDAO.getTotalExpenseByCategory(accountId, category);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}