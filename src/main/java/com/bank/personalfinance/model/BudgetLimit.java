package com.bank.personalfinance.model;

public class BudgetLimit {
    private int id;
    private int accountId;     // SQL: account_id (User değil Account bazlı yapmışsınız)
    private String limitType;  // SQL: limit_type
    private double limitAmount;// SQL: limit_amount

    public BudgetLimit() {}

    public BudgetLimit(int accountId, String limitType, double limitAmount) {
        this.accountId = accountId;
        this.limitType = limitType;
        this.limitAmount = limitAmount;
    }

    public BudgetLimit(int id, int accountId, String limitType, double limitAmount) {
        this.id = id;
        this.accountId = accountId;
        this.limitType = limitType;
        this.limitAmount = limitAmount;
    }

    // Getter ve Setter Metotları (Generate etmeyi unutma)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }
    public String getLimitType() { return limitType; }
    public void setLimitType(String limitType) { this.limitType = limitType; }
    public double getLimitAmount() { return limitAmount; }
    public void setLimitAmount(double limitAmount) { this.limitAmount = limitAmount; }
}