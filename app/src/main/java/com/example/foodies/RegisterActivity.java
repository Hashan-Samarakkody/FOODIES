package com.example.foodies;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/*
 * By Dinithi Mahathanthri
 * IM/2021/110
 * This class handles user registration using Firebase Authentication
 * and stores user data in Firebase Realtime Database.
 */

public class RegisterActivity extends AppCompatActivity {

    EditText emailInput, passwordInput, nameInput; // Input fields for email, password, and name IM/2021/110
    Button registerButton; // Button to trigger registration IM/2021/110
    TextView haveAccount; // TextView for navigating to login IM/2021/110
    FirebaseAuth auth; // Firebase authentication instance IM/2021/110
    DatabaseReference databaseReference; // Reference to the Firebase Realtime Database IM/2021/110

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Set the layout for this activity IM/2021/110

        // Initialize UI components
        haveAccount = findViewById(R.id.haveAcc); // Reference to the "have account" TextView IM/2021/110
        emailInput = findViewById(R.id.etEmail); // Reference to the email input field IM/2021/110
        nameInput = findViewById(R.id.etName); // Reference to the name input field IM/2021/110
        passwordInput = findViewById(R.id.etpassword); // Reference to the password input field IM/2021/110
        registerButton = findViewById(R.id.register); // Reference to the register button IM/2021/110

        auth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth instance IM/2021/110
        databaseReference = FirebaseDatabase.getInstance().getReference("users"); // Reference to the "users" node in the database IM/2021/110

        // Set onClickListener for register button
        registerButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim(); // Get trimmed email input IM/2021/110
            String password = passwordInput.getText().toString().trim(); // Get trimmed password input IM/2021/110
            String name = nameInput.getText().toString().trim(); // Get trimmed name input IM/2021/110

            Users user = new Users(name, email); // Create a new user object IM/2021/110

            if (!user.isValidInputs(name, email, password)) { // Validate user inputs IM/2021/110
                Toast.makeText(RegisterActivity.this, "Invalid Input!", Toast.LENGTH_SHORT).show(); // Show invalid input message IM/2021/110
                return; // Exit the listener if input is invalid IM/2021/110
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { // Check if email is valid IM/2021/110
                Toast.makeText(RegisterActivity.this, "Email not valid!", Toast.LENGTH_SHORT).show(); // Show invalid email message IM/2021/110
                return; // Exit the listener if email is invalid IM/2021/110
            }

            // Create user account with email and password
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) { // Check if registration was successful IM/2021/110
                    // Save user data in Realtime Database
                    String userId = auth.getCurrentUser().getUid(); // Get the unique user ID IM/2021/110
                    databaseReference.child(userId).setValue(user) // Save user data under the user's ID in the database
                            .addOnSuccessListener(aVoid -> { // Listener for successful data save
                                Toast.makeText(RegisterActivity.this, "User Registered Successfully!", Toast.LENGTH_SHORT).show(); // Show success message IM/2021/110
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class)); // Navigate to login activity IM/2021/110
                                finish(); // Finish current activity IM/2021/110
                            }).addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Failed to save user data!", Toast.LENGTH_SHORT).show()); // Handle failure to save data IM/2021/110
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show(); // Show registration failure message IM/2021/110
                }
            }).addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "User cannot be registered!", Toast.LENGTH_SHORT).show()); // Handle failure to create user IM/2021/110
        });

        haveAccount.setOnClickListener(view -> { // Set onClickListener for "have account" text
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class)); // Navigate to login activity IM/2021/110
            finish(); // Finish current activity IM/2021/110
        });
    }
}
