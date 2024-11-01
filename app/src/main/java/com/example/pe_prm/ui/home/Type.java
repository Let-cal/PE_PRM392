package com.example.pe_prm.ui.home;

public class Type {
    private String idType;
    private String nameType;

    // Constructor
    public Type(String idType, String nameType) {
        this.idType = idType;
        this.nameType = nameType;
    }

    // Getters and Setters
    public String getIdType() { return idType; }
    public void setIdType(String idType) { this.idType = idType; }

    public String getNameType() { return nameType; }
    public void setNameType(String nameType) { this.nameType = nameType; }
}
