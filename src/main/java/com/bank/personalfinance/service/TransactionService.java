package com.bank.personalfinance.service;

import com.bank.personalfinance.dao.TransactionDAO;
import com.bank.personalfinance.model.Transaction;
import java.sql.SQLException;
import java.util.List;

public class TransactionService {

    private final TransactionDAO transactionDAO;

    public TransactionService() {
        this.transactionDAO = new TransactionDAO();
    }

    public List<Transaction> getHistory(int accountId) {
        try {
            return transactionDAO.getTransactionsByAccountId(accountId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // TODO: Transfer işlemi (Para düşme/ekleme) mantığını ileride buraya yazacağız.
    // Şimdilik boş dursun.
}