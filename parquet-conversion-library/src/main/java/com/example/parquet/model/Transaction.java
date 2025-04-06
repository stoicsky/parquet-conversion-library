package com.example.parquet.model;

public class Transaction {
    private String transactionId;
    private String userId;
    private double amount;
    private String currency;
    private String timestamp;

    // Constructors
    public Transaction() {}

    public Transaction(String transactionId, String userId, double amount, String currency, String timestamp) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
