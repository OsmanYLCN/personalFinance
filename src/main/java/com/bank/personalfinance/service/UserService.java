package com.bank.personalfinance.service;

import com.bank.personalfinance.dao.UserDAO;
import com.bank.personalfinance.model.User;
import java.sql.SQLException;

public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public User login(String tcNo, String password) {
        // İleride buraya "Şifreleme" (Hashing) mantığı da eklenebilir.
        try {
            return userDAO.login(tcNo, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}