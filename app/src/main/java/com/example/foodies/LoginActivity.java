package com.example.foodies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText etEmail, etPassword;
    Button login;
    ImageView gotoRegister;
    FirebaseAuth auth;
    SharedPreferences sharedPreferences;

    final String EMAIL_KEY = "email_key";
    final String PASSWORD_KEY = "password_key";
    final String SHARED_PREFS = "shared_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        login = findViewById(R.id.login);
        gotoRegister = findViewById(R.id.backIcon);

        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        // Fetch and set email and password if they exist
        String email = sharedPreferences.getString(EMAIL_KEY, null);
        String password = sharedPreferences.getString(PASSWORD_KEY, null);

        if (email != null) {
            etEmail.setText(email);
        }

        if (password != null) {
            etPassword.setText(password);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = etEmail.getText().toString().trim();
                String userPassword = etPassword.getText().toString().trim();

                if (userEmail.isEmpty()) {
                    etEmail.setError("Email required!");
                } else if (userPassword.isEmpty()) {
                    etPassword.setError("Password required!");
                } else {
                    // Save the email and password in SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(EMAIL_KEY, userEmail);
                    editor.putString(PASSWORD_KEY, userPassword);
                    editor.apply();

                    // Authenticate the user
                    auth = FirebaseAuth.getInstance();
                    auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Logged In Successfully!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish(); // End the LoginActivity
                            } else {
                                Toast.makeText(LoginActivity.this, "User Authentication Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "User Login Failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish(); // End the LoginActivity
            }
        });
    }
}
