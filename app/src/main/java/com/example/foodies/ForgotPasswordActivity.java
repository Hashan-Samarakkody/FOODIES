package com.example.foodies;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

/*
 * By Dinithi Mahathanthri
 * IM/2021/110
 * This activity allows users to reset their password by providing their email address.
 * It sends a password reset link to the user's email via Firebase Authentication.
 */

public class ForgotPasswordActivity extends AppCompatActivity {
    // UI components
    Button btnResetPass, btnCancel;
    EditText emailBox;

    // Firebase authentication instance
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_password); // Set the content view for this activity

        // Initialize UI components
        btnResetPass = findViewById(R.id.btnResetPass);
        btnCancel = findViewById(R.id.btnCancel);
        emailBox = findViewById(R.id.sendEmailBox);

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance();


        btnResetPass.setOnClickListener(view -> {
            String userEmail = emailBox.getText().toString().trim(); // Get user input

            // Validate email input
            if (TextUtils.isEmpty(userEmail) || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                Toast.makeText(ForgotPasswordActivity.this, "Invalid Email!", Toast.LENGTH_SHORT).show();
                return; // Stop if email is invalid
            }

            // Send password reset email via Firebase
            auth.sendPasswordResetEmail(userEmail).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please check the email you provided!", Toast.LENGTH_SHORT).show(); // IM/2021/110 Show success message
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Unable to send the reset link!", Toast.LENGTH_SHORT).show(); // IM/2021/110 Show error message
                }
            });
        });

        // Handle cancel button click
        btnCancel.setOnClickListener(view -> {
            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class)); // Navigate to LoginActivity
            finish(); // Close this activity
        });
    }
}
