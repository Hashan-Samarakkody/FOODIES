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
    Button btnResetPass, btnCancel; // IM/2021/110 Buttons for resetting and canceling
    EditText emailBox; // IM/2021/110 EditText for user email input
    FirebaseAuth auth; // IM/2021/110 Firebase authentication instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_password); // IM/2021/110 Set the content view for the activity

        btnResetPass = findViewById(R.id.btnResetPass); // IM/2021/110 Initialize reset password button
        btnCancel = findViewById(R.id.btnCancel); // IM/2021/110 Initialize cancel button
        emailBox = findViewById(R.id.sendEmailBox); // IM/2021/110 Initialize email input box

        auth = FirebaseAuth.getInstance(); // IM/2021/110 Initialize FirebaseAuth instance

        // IM/2021/110 Set onClickListener for reset password button
        btnResetPass.setOnClickListener(view -> {
            String userEmail = emailBox.getText().toString().trim(); // IM/2021/110 Get user input from email box

            // IM/2021/110 Validate email input
            if (TextUtils.isEmpty(userEmail) || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                Toast.makeText(ForgotPasswordActivity.this, "Invalid Email!", Toast.LENGTH_SHORT).show(); // IM/2021/110 Show error message
                return; // IM/2021/110 Exit if email is invalid
            }

            // IM/2021/110 Send password reset email
            auth.sendPasswordResetEmail(userEmail).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please check the email you provided!", Toast.LENGTH_SHORT).show(); // IM/2021/110 Show success message
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Unable to send the reset link!", Toast.LENGTH_SHORT).show(); // IM/2021/110 Show error message
                }
            });
        });

        // IM/2021/110 Set onClickListener for cancel button

        btnCancel.setOnClickListener(view -> {
            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class)); // IM/2021/110 Navigate to LoginActivity
            finish(); // IM/2021/110 Close this activity
        });
    }
}
