package com.bank.personalfinance.model;
import java.sql.Timestamp;

public class Transaction {
    private int id;
    private int sourceAccountId; // SQL: source_account_id
    private int targetAccountId; // SQL: target_account_id


    private String sourceIban;   // SQL: source_iban
    private String targetIban;   // SQL: target_iban
    private String sourceName;   // SQL: source_name
    private String targetName;   // SQL: target_name

    private double amount;          // SQL: amount
    private String transactionType; // SQL: transaction_type
    private String category;        // SQL: category
    private Timestamp transactionDate; // SQL: transaction_date
    private String description;     // SQL: description

    public Transaction() {}

    public Transaction(int sourceAccountId, int targetAccountId, String sourceIban, String targetIban, String sourceName, String targetName, double amount, String transactionType, String category, Timestamp transactionDate, String description) {
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.sourceIban = sourceIban;
        this.targetIban = targetIban;
        this.sourceName = sourceName;
        this.targetName = targetName;
        this.amount = amount;
        this.transactionType = transactionType;
        this.category = category;
        this.transactionDate = transactionDate;
        this.description = description;
    }

    public Transaction(int id, int sourceAccountId, int targetAccountId, String sourceIban, String targetIban, String sourceName, String targetName, double amount, String transactionType, String category, Timestamp transactionDate, String description) {
        this.id = id;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.sourceIban = sourceIban;
        this.targetIban = targetIban;
        this.sourceName = sourceName;
        this.targetName = targetName;
        this.amount = amount;
        this.transactionType = transactionType;
        this.category = category;
        this.transactionDate = transactionDate;
        this.description = description;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public int getSourceAccountId() {return sourceAccountId;}
    public void setSourceAccountId(int sourceAccountId) {this.sourceAccountId = sourceAccountId;}
    public int getTargetAccountId() {return targetAccountId;}
    public void setTargetAccountId(int targetAccountId) {this.targetAccountId = targetAccountId;}
    public String getSourceIban() {return sourceIban;}
    public void setSourceIban(String sourceIban) {this.sourceIban = sourceIban;}
    public String getTargetIban() {return targetIban;}
    public void setTargetIban(String targetIban) {this.targetIban = targetIban;}
    public String getSourceName() {return sourceName;}
    public void setSourceName(String sourceName) {this.sourceName = sourceName;}
    public String getTargetName() {return targetName;}
    public void setTargetName(String targetName) {this.targetName = targetName;}
    public String getTransactionType() {return transactionType;}
    public void setTransactionType(String transactionType) {this.transactionType = transactionType;}
    public String getCategory() {return category;}
    public void setCategory(String category) {this.category = category;}
    public Timestamp getTransactionDate() {return transactionDate;}
    public void setTransactionDate(Timestamp transactionDate) {this.transactionDate = transactionDate;}
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
    public double getAmount() {return amount;}
    public void setAmount(double amount) {this.amount = amount;}
}