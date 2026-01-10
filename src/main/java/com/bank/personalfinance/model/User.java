package com.bank.personalfinance.model;

public class User {
    private int id;
    private String tcNo;       // SQL: tc_no
    private String password;   // SQL: password
    private String adSoyad;    // SQL: ad_soyad
    private String role;       // SQL: role
    private String email;
    private String telefon;
    public User() {}

    public User(String tcNo, String password, String adSoyad, String role) {
        this.tcNo = tcNo;
        this.password = password;
        this.adSoyad = adSoyad;
        this.role = role;

    }

    public User(int id, String tcNo, String password, String adSoyad, String role, String email, String telefon) {
        this.id = id;
        this.tcNo = tcNo;
        this.password = password;
        this.adSoyad = adSoyad;
        this.role = role;
        this.email = email;
        this.telefon = telefon;
    }

    // Getter ve Setter Metotları
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTcNo() { return tcNo; }
    public void setTcNo(String tcNo) { this.tcNo = tcNo; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getAdSoyad() { return adSoyad; }
    public void setAdSoyad(String adSoyad) { this.adSoyad = adSoyad; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }
}