package com.example.pe_prm.ui.home;

public class Item {
    private String id;
    private String name;
    private String creator;
    private String releaseDate;
    private String type;      // Represents a description of the type (e.g., the name of the major)
    private String identifier; // Additional identifier if needed
    private String idType;     // ID referencing the Type class

    // Constructor
    public Item(String id, String name, String creator, String releaseDate, String type, String identifier, String idType) {
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.releaseDate = releaseDate;
        this.type = type;
        this.identifier = identifier;
        this.idType = idType;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCreator() { return creator; }
    public void setCreator(String creator) { this.creator = creator; }

    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }

    public String getIdType() { return idType; }
    public void setIdType(String idType) { this.idType = idType; }
}
