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

public class ForgotPasswordActivity extends AppCompatActivity {
    Button btnResetPass, btnCancel;
    EditText emailBox;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_password); // Move this line up

        btnResetPass = findViewById(R.id.btnResetPass);
        btnCancel = findViewById(R.id.btnCancel);
        emailBox = findViewById(R.id.sendEmailBox); // Don't forget to initialize emailBox

        auth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth

        btnResetPass.setOnClickListener(view -> {
            String userEmail = emailBox.getText().toString().trim();

            if (TextUtils.isEmpty(userEmail) || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                Toast.makeText(ForgotPasswordActivity.this, "Invalid Email!", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.sendPasswordResetEmail(userEmail).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please check the email you provided!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Unable to send the reset link!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnCancel.setOnClickListener(view -> {
            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            finish();
        });
    }
}
