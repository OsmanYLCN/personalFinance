package com.bank.personalfinance.service;

import com.bank.personalfinance.dao.AccountDAO;
import com.bank.personalfinance.model.Account;
import java.util.List;

public class AccountService {

    private final AccountDAO accountDAO;

    public AccountService() {
        this.accountDAO = new AccountDAO();
    }

    // Dashboard'da hesapları listelemek için bunu kullanacaksın
    public List<Account> getAccountsByUserId(int userId) {
        return accountDAO.getAccountsByUserId(userId);
    }

    // İleride para transferinde bakiyeyi buradan kontrol edeceksin
    public boolean createAccount(Account account) {
        return accountDAO.createAccount(account);
    }

    public double getTotalBalance(int userId) {
        List<Account> accounts = accountDAO.getAccountsByUserId(userId);
        double total = 0;
        for (Account acc : accounts) {
            // Farklı para birimleri varsa kur çevirmek gerekir ama şimdilik düz topluyoruz
            total += acc.getBalance();
        }
        return total;
    }
}