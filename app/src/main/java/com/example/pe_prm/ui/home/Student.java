package com.example.pe_prm.ui.home;


public class Student {
    private String id;
    private String name;
    private String date;
    private String gender;
    private String email;
    private String address;
    private String idMajor;

    // Constructor for full details
    public Student(String id, String name, String date, String gender, String email, String address, String idMajor) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.gender = gender;
        this.email = email;
        this.address = address;
        this.idMajor = idMajor;
    }

    // Simple constructor
    public Student(String id, String name, String major) {
        this.id = id;
        this.name = name;
        this.idMajor = major;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getIdMajor() { return idMajor; }
    public void setIdMajor(String idMajor) { this.idMajor = idMajor; }

    // This method is needed for the filtering functionality
    public String getMajor() { return idMajor; }
}
