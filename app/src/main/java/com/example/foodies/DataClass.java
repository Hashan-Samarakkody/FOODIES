// Done by IM/2021/120 - Rajapaksha.L.S
package com.example.foodies;

import java.util.regex.Pattern;

public class DataClass {
    // Instance variables to hold data about a food item
    private String dataName;        // Name of the food item
    private String dataCategory;    // Category of the food item (e.g., dessert, main course)
    private String dataTime;        // Time required to prepare the food item
    private String dataIngredients;  // Ingredients required for the food item
    private String dataDescription;  // Description of the food item
    private String dataImage;        // URL or path of the food item's image
    private String dataVideo;        // URL or path of the food item's video
    private String key;              // Unique key for the food item (e.g., database ID)
    private String owner;            // Owner of the food item (e.g., creator or uploader)

    // Regex patterns for validating input data
    // TIME_PATTERN: matches valid time formats
    private static final Pattern TIME_PATTERN = Pattern.compile(
            "^(\\d{1,2}h\\s*\\d{1,2}min|\\d{1,2}h|\\d{1,2}min|\\d{1,2}-\\d{1,2}|\\d{1,2})$"
    );
    // NAME_PATTERN: allows letters, spaces, and special characters in names
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s\\W]+$");
    // CATEGORY_PATTERN: allows letters, spaces, and special characters in categories
    private static final Pattern CATEGORY_PATTERN = Pattern.compile("^[a-zA-Z\\s\\W]+$");

    // Default constructor
    public DataClass() {
    }

    // Parameterized constructor to initialize all fields
    public DataClass(String dataName, String dataCategory, String dataTime, String dataIngredients,
                     String dataDescription, String dataImage, String dataVideo, String owner) {
        this.dataName = dataName;
        this.dataCategory = dataCategory;
        this.dataTime = dataTime;
        this.dataIngredients = dataIngredients;
        this.dataDescription = dataDescription;
        this.dataImage = dataImage;
        this.dataVideo = dataVideo;
        this.owner = owner;
    }

    // Getters for accessing private fields
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

    public String getDataVideo() {
        return dataVideo;
    }

    public String getKey() {
        return key;
    }

    public String getOwner() {
        return owner;
    }

    // Setter for the key field
    public void setKey(String key) {
        this.key = key;
    }

    // Method to validate time input
    public boolean isValidTime(String time) {
        // Check if the time string matches the defined pattern
        if (!TIME_PATTERN.matcher(time).matches()) {
            return false; // Invalid format
        }

        // Ensure that the time contains either "h" or "min"
        boolean containsHours = time.contains("h");
        boolean containsMinutes = time.contains("min");

        // If it doesn't contain either, return false
        if (!containsHours && !containsMinutes) {
            return false;
        }

        // Time validation logic
        if (containsHours) {
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
        }

        if (containsMinutes) {
            String[] parts = time.split("min");
            int minutes = Integer.parseInt(parts[0].trim());
            return (minutes >= 0 && minutes < 60);
        }

        // Check for time ranges like "1-30"
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

        return false; // Time format is invalid
    }

    // Method to validate food name input
    public boolean isValidName(String name) {
        return NAME_PATTERN.matcher(name).matches();
    }

    // Method to validate food category input
    public boolean isValidCategory(String category) {
        return CATEGORY_PATTERN.matcher(category).matches();
    }
}
