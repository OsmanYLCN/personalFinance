package com.bank.personalfinance.model;

public class Account {
    private int id;
    private int userId;         // SQL: user_id
    private String accountName; // SQL: account_name (Yeni alan!)
    private String iban;        // SQL: iban
    private double balance;     // SQL: balance
    private String currency;    // SQL: currency

    public Account() {}

    public Account(int userId, String accountName, String iban, double balance, String currency) {
        this.userId = userId;
        this.accountName = accountName;
        this.iban = iban;
        this.balance = balance;
        this.currency = currency;
    }

    public Account(int id, int userId, String accountName, String iban, double balance, String currency) {
        this.id = id;
        this.userId = userId;
        this.accountName = accountName;
        this.iban = iban;
        this.balance = balance;
        this.currency = currency;
    }

    // Getter ve Setter Metotları
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    // Listview'de güzel görünsün diye toString ekleyelim
    @Override
    public String toString() {
        return accountName + " (" + currency + ") - " + balance;
    }
}