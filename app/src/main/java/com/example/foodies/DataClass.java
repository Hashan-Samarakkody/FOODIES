package com.example.foodies;

public class DataClass {
    private String dataName;
    private String dataCategory;
    private String dataTime;
    private String dataIngredients;
    private String dataDescription;
    private String dataImage;
    private String key;
    private String owner;

    public DataClass() {
        // Required for Firebase
    }

    // Parameterized constructor
    public DataClass(String dataName, String dataCategory, String dataTime, String dataIngredients, String dataDescription, String dataImage, String owner) {
        this.dataName = dataName;
        this.dataCategory = dataCategory;
        this.dataTime = dataTime;
        this.dataIngredients = dataIngredients;
        this.dataDescription = dataDescription;
        this.dataImage = dataImage;
        this.owner = owner;
    }

    // Getters
    public String getDataName() {
        return dataName;
    }

    public String getDataCategory() {
        return dataCategory;
    }

    public String getDataTime() {
        return dataTime;
    }

    public String getDataIngredients() {
        return dataIngredients;
    }

    public String getDataDescription() {
        return dataDescription;
    }

    public String getDataImage() {
        return dataImage;
    }

    public String getKey() {
        return key;
    }


    public String getOwner() {
        return owner;
    }


    public void setKey(String key) {
        this.key = key;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
