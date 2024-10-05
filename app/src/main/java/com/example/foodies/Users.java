package com.example.foodies;

public class Users { // Users class to represent user data IM/2022/070
    private String name; // Variable to store the user's name IM/2022/070
    private String email; // Variable to store the user's email IM/2022/070
    private String key; // Variable to store the user's unique key IM/2022/070

    // No-argument constructor (required for Firebase) IM/2022/070
    public Users() {
    }

    // Constructor with parameters to initialize user data IM/2022/070
    public Users(String name, String email) {
        this.name = name; // Set the user's name IM/2022/070
        this.email = email; // Set the user's email IM/2022/070
    }

    // Getters and Setters for name IM/2022/070
    public String getName() {
        return name; // Returns the user's name IM/2022/070
    }

    public void setName(String name) {
        this.name = name; // Sets the user's name IM/2022/070
    }

    // Getters and Setters for email IM/2022/070
    public String getEmail() {
        return email; // Returns the user's email IM/2022/070
    }

    public void setEmail(String email) {
        this.email = email; // Sets the user's email IM/2022/070
    }

    // Getters and Setters for key IM/2022/070
    public String getKey() {
        return key; // Returns the user's unique key IM/2022/070
    }

    public void setKey(String key) {
        this.key = key; // Sets the user's unique key IM/2022/070
    }

    // Validation methods for user input IM/2022/070
    public boolean isValidInputs(String name, String email, String password) {
        if (isEmpty(name) || isEmpty(email) || isEmpty(password)) { // Checks if any input is empty IM/2022/070
            return false; // Returns false if any input is empty IM/2022/070
        }
        if (!isValidName(name)) { // Checks if the name is valid IM/2022/070
            return false; // Returns false if the name is invalid IM/2022/070
        }
        if (!isValidEmail(email)) { // Checks if the email is valid IM/2022/070
            return false; // Returns false if the email is invalid IM/2022/070
        }
        if (!isValidPassword(password)) { // Checks if the password is valid IM/2022/070
            return false; // Returns false if the password is invalid IM/2022/070
        } else {
            return true; // Returns true if all validations pass IM/2022/070
        }
    }

    // Method to check if a string is empty IM/2022/070
    public boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty(); // Returns true if the value is null or empty IM/2022/070
    }

    // Method to validate the user's name IM/2022/070
    public boolean isValidName(String name) {
        return name.matches("[A-Za-z\\s]+"); // Allows only letters and spaces in the name IM/2022/070
    }

    // Method to validate the user's email IM/2022/070
    public boolean isValidEmail(String email) {
        return email.matches("^[\\w-\\.]+@[\\w-]+\\.[a-zA-Z]{2,}$"); // Regular expression for valid email format IM/2022/070
    }

    // Method to validate the user's password IM/2022/070
    public boolean isValidPassword(String password) {
        return password.length() >= 6; // Checks if the password is at least 6 characters long IM/2022/070
    }
}
