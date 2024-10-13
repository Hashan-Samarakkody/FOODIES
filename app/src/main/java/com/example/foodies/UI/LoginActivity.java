package com.example.foodies.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.developer.gbuttons.GoogleSignInButton;
import com.example.foodies.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/*
 * By Dinithi Mahathanthri
 * IM/2021/110
 * This activity allows users to reset their password by providing their email address.
 * It sends a password reset link to the user's email via Firebase Authentication.
 */

public class LoginActivity extends AppCompatActivity {

    static final int RC_SIGN_IN = 21; // Request code for Google sign-in

    GoogleSignInButton googleBtn;
    GoogleSignInOptions gOptions;
    GoogleSignInClient gClient;
    FirebaseDatabase database;

    TextView forgotPassword;
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

        // Initialize views and components
        forgotPassword = findViewById(R.id.forgotPass);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        login = findViewById(R.id.login);
        gotoRegister = findViewById(R.id.backIcon);
        googleBtn = findViewById(R.id.googleBtn);

        // Firebase and SharedPreferences setup
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        // Load email and password from SharedPreferences, if available
        String email = sharedPreferences.getString(EMAIL_KEY, null);
        String password = sharedPreferences.getString(PASSWORD_KEY, null);

        if (email != null) {
            etEmail.setText(email);
        }

        if (password != null) {
            etPassword.setText(password);
        }

        // Login button click listener
        login.setOnClickListener(view -> {
            String userEmail = etEmail.getText().toString().trim();
            String userPassword = etPassword.getText().toString().trim();

            // Validate user input
            if (userEmail.isEmpty()) {
                etEmail.setError("Email required!");
            } else if (userPassword.isEmpty()) {
                etPassword.setError("Password required!");
            } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                etEmail.setError("Invalid Email");
            } else {
                // Save credentials and authenticate user
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(EMAIL_KEY, userEmail);
                editor.putString(PASSWORD_KEY, userPassword);
                editor.apply();


                auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Logged In Successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish(); // IM/2021/110 End the LoginActivity
                    } else {
                        Toast.makeText(LoginActivity.this, "User Authentication Failed!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "User Login Failed!", Toast.LENGTH_SHORT).show());
            }
        });

        // Navigate to Registration screen
        gotoRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        // Configure Google Sign-in options
        gOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Use client ID from Firebase setup
                .requestEmail().build();

        gClient = GoogleSignIn.getClient(LoginActivity.this, gOptions);


        // Google Sign-in button click listener
        googleBtn.setOnClickListener(view -> signIn());

        // Navigate to Forgot Password screen
        forgotPassword.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            finish();
        });
    }

    // Start Google sign-in process
    private void signIn() {
        Intent intent = gClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                auth(account.getIdToken()); // Authenticate with Firebase
            } catch (ApiException e) {
                Toast.makeText(this, "Sign-in failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Authenticate Google user with Firebase
    private void auth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("email", user.getEmail());
                        map.put("name", user.getDisplayName());

                        // Store user data in Firebase Realtime Database
                        database.getReference("users").child(user.getUid()).setValue(map);
                        Toast.makeText(LoginActivity.this, "Logged In Successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
