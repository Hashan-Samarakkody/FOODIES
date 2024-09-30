package com.example.foodies;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText emailInput, passwordInput, nameInput;
    Button registerButton;
    TextView haveAccount;
    FirebaseAuth auth;
    DatabaseReference databaseReference; // Add this line

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI components
        haveAccount = findViewById(R.id.haveAcc);
        emailInput = findViewById(R.id.etEmail);
        nameInput = findViewById(R.id.etName);
        passwordInput = findViewById(R.id.etpassword);
        registerButton = findViewById(R.id.register);

        auth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth
        databaseReference = FirebaseDatabase.getInstance().getReference("users"); // Reference to the "users" node

        // Set onClickListener for register button
        registerButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String name = nameInput.getText().toString().trim();

            Users user = new Users(name, email);

            if (!user.isValidInputs(name, email, password)) { // Fix condition
                Toast.makeText(RegisterActivity.this, "Invalid Input!", Toast.LENGTH_SHORT).show();
                return; // Remove finish()
            }

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Save user data in Realtime Database
                    String userId = auth.getCurrentUser().getUid(); // Get user ID
                    databaseReference.child(userId).setValue(user)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(RegisterActivity.this, "User Registered Successfully!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(RegisterActivity.this, "Failed to save user data!", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(RegisterActivity.this, "User cannot be registered!", Toast.LENGTH_SHORT).show();
            });
        });

        haveAccount.setOnClickListener(view -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }
}
