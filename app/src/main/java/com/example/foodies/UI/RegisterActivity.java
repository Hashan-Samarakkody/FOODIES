package com.example.foodies.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodies.R;
import com.example.foodies.HelperClass.Users;
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

    final String SHARED_PREFS = "shared_prefs";

    // UI components
    EditText emailInput, passwordInput, nameInput;
    Button registerButton;
    TextView haveAccount;
    // Firebase authentication and database references
    FirebaseAuth auth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Set the layout for this activity IM/2021/110

        // Initialize UI components
        haveAccount = findViewById(R.id.haveAcc);
        emailInput = findViewById(R.id.etEmail);
        nameInput = findViewById(R.id.etName);
        passwordInput = findViewById(R.id.etpassword);
        registerButton = findViewById(R.id.register);

        // Initialize Firebase authentication instance
        auth = FirebaseAuth.getInstance();
        // Reference to the "users" node in the Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Set up register button click listener
        registerButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String name = nameInput.getText().toString().trim();

            Users user = new Users(name, email); // Create a new user object

            // Validate user inputs
            if (!user.isValidInputs(name, email, password)) {
                Toast.makeText(RegisterActivity.this, "Invalid Input!", Toast.LENGTH_SHORT).show();
                return;// Stop registration if input is invalid
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(RegisterActivity.this, "Email not valid!", Toast.LENGTH_SHORT).show();
                return;// Stop registration if email format is invalid
            }

            // Create user account with email and password
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) { // If registration is successful, save the user data in Firebase Realtime Database
                    String userId = auth.getCurrentUser().getUid(); // Get unique user ID from Firebase
                    databaseReference.child(userId).setValue(user)
                            .addOnSuccessListener(aVoid -> {
                                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE); // Clear shared preferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.apply();

                                Toast.makeText(RegisterActivity.this, "User Registered Successfully!", Toast.LENGTH_SHORT).show();
                                // Redirect to login screen after successful registration
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                            }).addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Failed to save user data!", Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show(); // Show registration failure message IM/2021/110
                }
            }).addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "User cannot be registered!", Toast.LENGTH_SHORT).show()); // Handle failure to create user IM/2021/110
        });

        // Listener for navigating to the login activity
        haveAccount.setOnClickListener(view -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class)); // Navigate to login activity IM/2021/110
            finish();
        });
    }
}
