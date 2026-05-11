package com.example.travelling2.model;

public enum CountryCode {
    MDA("Moldova (MD)"),
    ROU("Romania (RO)"),
    UKR("Ukraine (UA)"),
    USA("USA (US)"),
    DEU("Germany (DE)");

    private final String displayValue;
    CountryCode(String displayValue) { this.displayValue = displayValue; }
    public String getDisplayValue() { return displayValue; }
}