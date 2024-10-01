package com.example.foodies;

public class Users {
    private String name;
    private String email;
    private String key;

    public Users(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isValidInputs(String name, String email, String password) {
        if (isEmpty(name) || isEmpty(email) || isEmpty(password)) {
            return false;
        }
        if (!isValidName(name)) {
            return false;
        }
        if (!isValidEmail(email)) {
            return false;
        }
        if (!isValidPassword(password)) {
            return false;
        }else{
        return true;}
    }

    public boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public boolean isValidName(String name) {
        return name.matches("[A-Za-z\\s]+"); // Allow letters and spaces
    }

    public boolean isValidEmail(String email) {
        return email.matches("^[\\w-\\.]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    }

    public boolean isValidPassword(String password) {
        return password.length() >= 6;
    }
}
