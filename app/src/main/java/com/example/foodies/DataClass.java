package com.example.foodies;

import java.util.regex.Pattern;

public class DataClass {
    private String dataName;
    private String dataCategory;
    private String dataTime;
    private String dataIngredients;
    private String dataDescription;
    private String dataImage;
    private String key;
    private String owner;

    private static final Pattern TIME_PATTERN = Pattern.compile(
            "^(\\d{1,2}h\\s*\\d{1,2}min|\\d{1,2}h|\\d{1,2}-\\d{1,2})$"
    );
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]+$");
    private static final Pattern CATEGORY_PATTERN = Pattern.compile("^[a-zA-Z\\s]+$");

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

    public boolean isValidTime(String time) {
        if (!TIME_PATTERN.matcher(time).matches()) {
            return false;
        }

        if (time.contains("h")) {
            String[] parts = time.split("h");
            int hours = Integer.parseInt(parts[0].trim());

            if (parts.length == 1) {
                // Only hours are present
                return (hours >= 0 && hours < 24);
            } else if (parts[1].contains("min")) {
                String[] minutesPart = parts[1].split("min");
                int minutes = Integer.parseInt(minutesPart[0].trim());
                return (hours >= 0 && hours < 24 && minutes >= 0 && minutes < 60);
            }
        } else {
            String[] parts = time.split("-");
            if (parts.length == 2) {
                // Validate hour and minute
                int hours = Integer.parseInt(parts[0].trim());
                int minutes = Integer.parseInt(parts[1].trim());
                return (hours >= 0 && hours < 24 && minutes >= 0 && minutes < 60);
            } else if (parts.length == 1) {
                // Validate if only hours are present
                int hours = Integer.parseInt(parts[0].trim());
                return (hours >= 0 && hours < 24);
            }
        }
        return false;
    }

    public boolean isValidName(String name) {
        return NAME_PATTERN.matcher(name).matches();
    }

    public boolean isValidCategory(String category) {
        return CATEGORY_PATTERN.matcher(category).matches();
    }
}
