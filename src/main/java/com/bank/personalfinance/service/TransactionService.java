package com.bank.personalfinance.service;

import com.bank.personalfinance.dao.TransactionDAO;
import com.bank.personalfinance.model.Transaction;
import com.bank.personalfinance.dao.AccountDAO;
import com.bank.personalfinance.model.Account;
import com.bank.personalfinance.dao.BudgetLimitDAO;
import com.bank.personalfinance.model.BudgetLimit;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class TransactionService {

    private final TransactionDAO transactionDAO;
    private final AccountDAO accountDAO;

    public TransactionService() {
        this.transactionDAO = new TransactionDAO();
        this.accountDAO = new AccountDAO();
    }

    public List<Transaction> getHistory(int accountId) {
        try {
            return transactionDAO.getTransactionsByAccountId(accountId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // TODO: Transfer işlemi (Para düşme/ekleme) mantığını ileride buraya yazacağız. Osman Yazdı :)

    public boolean transferMoney(int sourceAccountId, String targetIban, double amount, String description) throws SQLException {

        // Hesapları bulma
        Account sourceAccount = accountDAO.getAccountById(sourceAccountId);
        Account targetAccount = accountDAO.getAccountByIban(targetIban);

        if (sourceAccount == null || targetAccount == null) {
            System.out.println("Hata: Kaynak veya hedef hesap bulunamadı.");
            return false;
        }

        if (sourceAccount.getBalance() < amount) {
            System.out.println("Hata: Yetersiz bakiye!");
            return false;
        }

        if (sourceAccount.getIban().equals(targetIban)) {
            System.out.println("Hata: Kendi hesabınıza bu menüden gönderemezsiniz.");
            return false;
        }

        double newSourceBalance = sourceAccount.getBalance() - amount;
        double newTargetBalance = targetAccount.getBalance() + amount;

        boolean sourceUpdated = accountDAO.updateBalance(sourceAccount.getId(), newSourceBalance);
        if (!sourceUpdated) return false;

        boolean targetUpdated = accountDAO.updateBalance(targetAccount.getId(), newTargetBalance);
        // Eğer hedef güncellenemezse parayı iade etmemiz gerekir (Rollback mantığı) ama şimdilik pas geçiyoruz.

        if (sourceUpdated && targetUpdated) {
            // 4. İşlem Kaydını (Dekont) Oluştur
            Transaction transaction = new Transaction();
            transaction.setSourceAccountId(sourceAccount.getId());
            transaction.setTargetAccountId(targetAccount.getId());
            transaction.setSourceIban(sourceAccount.getIban());
            transaction.setTargetIban(targetAccount.getIban());
            transaction.setSourceName("Benim Hesabım"); // İsimleri UserDAO'dan çekebiliriz ilerde
            transaction.setTargetName("Alıcı Hesap");
            transaction.setAmount(amount);
            transaction.setTransactionType("HAVALE");
            transaction.setCategory("Transfer");
            transaction.setDescription(description);
            // Tarih veritabanında otomatik atılır ama modelde set etmek iyidir
            transaction.setTransactionDate(new Timestamp(System.currentTimeMillis()));

            try {
                transactionDAO.saveTransaction(transaction);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // GELİR EKLEME (Ayrı olarak)
    public boolean addIncome(int targetAccountId, double amount, String description, String category) throws SQLException {
        Account targetAccount = accountDAO.getAccountById(targetAccountId);
        if (targetAccount == null) return false;

        double newBalance = targetAccount.getBalance() + amount;
        boolean updated = accountDAO.updateBalance(targetAccountId, newBalance);

        if (updated) {
            Transaction transaction = new Transaction();
            transaction.setSourceAccountId(0);
            transaction.setTargetAccountId(targetAccount.getId());
            transaction.setSourceIban("Dış Kaynak");
            transaction.setTargetIban(targetAccount.getIban());
            transaction.setSourceName("Nakit/Maaş");
            transaction.setTargetName(targetAccount.getAccountName());
            transaction.setAmount(amount);
            transaction.setTransactionType("GELİR");
            transaction.setCategory(category);
            transaction.setDescription(description);
            transaction.setTransactionDate(new Timestamp(System.currentTimeMillis()));

            try {
                return transactionDAO.saveTransaction(transaction);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // GİDER EKLEME (Ayrı olarak)
    public boolean addExpense(int sourceAccountId, double amount, String description, String category) throws SQLException {

        Account sourceAccount = accountDAO.getAccountById(sourceAccountId);
        if (sourceAccount == null) return false;

        if (sourceAccount.getBalance() < amount) return false;

        double newBalance = sourceAccount.getBalance() - amount;
        boolean updated = accountDAO.updateBalance(sourceAccountId, newBalance);

        if (updated) {
            Transaction transaction = new Transaction();
            transaction.setSourceAccountId(sourceAccount.getId());
            transaction.setTargetAccountId(0);
            transaction.setSourceIban(sourceAccount.getIban());
            transaction.setTargetIban("Dış Ödeme");
            transaction.setSourceName(sourceAccount.getAccountName());
            transaction.setTargetName("Harcama Yeri");
            transaction.setAmount(amount);
            transaction.setTransactionType("GİDER");
            transaction.setCategory(category);
            transaction.setDescription(description);
            transaction.setTransactionDate(new Timestamp(System.currentTimeMillis()));

            try {
                return transactionDAO.saveTransaction(transaction);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // BÜTÇE KONTROL (Limit aşılır mı?)
    public String checkBudgetStatus(int accountId, String category, double newAmount) throws SQLException {
        BudgetLimitDAO budgetDAO = new BudgetLimitDAO();

        BudgetLimit limit = budgetDAO.getLimit(accountId, category);
        if (limit == null) {
            return "OK"; // Limit yok, sorun yok.
        }

        double currentTotal = transactionDAO.getTotalExpenseByCategory(accountId, category);

        if ((currentTotal + newAmount) > limit.getLimitAmount()) {
            double asimMiktari = (currentTotal + newAmount) - limit.getLimitAmount();
            return "UYARI: " + category + " bütçesini " + asimMiktari + " TL aşıyorsunuz! (Limit: " + limit.getLimitAmount() + " TL)";
        }

        return "OK";
    }

    // GRAFİK VERİSİ İÇİN SERVİS KATMANI
    public java.util.Map<String, Double> getExpenseReport(int accountId) throws SQLException {
        return transactionDAO.getExpensesByCategory(accountId);
    }



}