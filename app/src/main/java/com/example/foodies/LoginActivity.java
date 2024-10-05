package com.example.foodies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.developer.gbuttons.GoogleSignInButton;
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

    static final int RC_SIGN_IN = 21; // IM/2021/110 Request code for Google sign-in

    GoogleSignInButton googleBtn; // IM/2021/110 Button for Google sign-in
    GoogleSignInOptions gOptions; // IM/2021/110 Options for Google sign-in
    GoogleSignInClient gClient; // IM/2021/110 Google sign-in client
    FirebaseDatabase database; // IM/2021/110 Firebase database instance

    TextView forgotPassword; // IM/2021/110 TextView for forgot password link
    EditText etEmail, etPassword; // IM/2021/110 EditTexts for email and password input
    Button login; // IM/2021/110 Button for login
    ImageView gotoRegister; // IM/2021/110 ImageView for navigating to registration
    FirebaseAuth auth; // IM/2021/110 Firebase authentication instance
    SharedPreferences sharedPreferences; // IM/2021/110 SharedPreferences for storing credentials

    final String EMAIL_KEY = "email_key"; // IM/2021/110 Key for email in SharedPreferences
    final String PASSWORD_KEY = "password_key"; // IM/2021/110 Key for password in SharedPreferences
    final String SHARED_PREFS = "shared_prefs"; // IM/2021/110 Name of SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // IM/2021/110 Set the content view for the activity

        forgotPassword = findViewById(R.id.forgotPass); // IM/2021/110 Initialize forgot password TextView
        etEmail = findViewById(R.id.etEmail); // IM/2021/110 Initialize email EditText
        etPassword = findViewById(R.id.etPassword); // IM/2021/110 Initialize password EditText
        login = findViewById(R.id.login); // IM/2021/110 Initialize login Button
        gotoRegister = findViewById(R.id.backIcon); // IM/2021/110 Initialize register navigation ImageView
        googleBtn = findViewById(R.id.googleBtn); // IM/2021/110 Initialize Google sign-in Button

        auth = FirebaseAuth.getInstance(); // IM/2021/110 Initialize FirebaseAuth instance
        database = FirebaseDatabase.getInstance(); // IM/2021/110 Initialize FirebaseDatabase instance
        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE); // IM/2021/110 Initialize SharedPreferences

        // IM/2021/110 Fetch and set email and password if they exist
        String email = sharedPreferences.getString(EMAIL_KEY, null);
        String password = sharedPreferences.getString(PASSWORD_KEY, null);

        if (email != null) {
            etEmail.setText(email); // IM/2021/110 Set email in EditText
        }

        if (password != null) {
            etPassword.setText(password); // IM/2021/110 Set password in EditText
        }

        // IM/2021/110 Set onClickListener for login button
        login.setOnClickListener(view -> {
            String userEmail = etEmail.getText().toString().trim(); // IM/2021/110 Get user email input
            String userPassword = etPassword.getText().toString().trim(); // IM/2021/110 Get user password input

            // IM/2021/110 Validate user input
            if (userEmail.isEmpty()) {
                etEmail.setError("Email required!"); // IM/2021/110 Show error for empty email
            } else if (userPassword.isEmpty()) {
                etPassword.setError("Password required!"); // IM/2021/110 Show error for empty password
            } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                etEmail.setError("Invalid Email"); // IM/2021/110 Show error for invalid email format
            } else {
                // IM/2021/110 Save the email and password in SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(EMAIL_KEY, userEmail);
                editor.putString(PASSWORD_KEY, userPassword);
                editor.apply(); // IM/2021/110 Apply changes to SharedPreferences

                // IM/2021/110 Authenticate the user
                auth.signInWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Logged In Successfully!", Toast.LENGTH_SHORT).show(); // IM/2021/110 Show success message
                                startActivity(new Intent(LoginActivity.this, MainActivity.class)); // IM/2021/110 Navigate to MainActivity
                                finish(); // IM/2021/110 End the LoginActivity
                            } else {
                                Toast.makeText(LoginActivity.this, "User Authentication Failed!", Toast.LENGTH_SHORT).show(); // IM/2021/110 Show error message
                            }
                        }).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "User Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()); // IM/2021/110 Handle login failure
            }
        });

        // IM/2021/110 Set onClickListener for register navigation
        gotoRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class); // IM/2021/110 Navigate to RegisterActivity
            startActivity(intent);
            finish(); // IM/2021/110 End the LoginActivity
        });

        gOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) // IM/2021/110 Build Google Sign-In options
                .requestIdToken(getString(R.string.default_web_client_id)) // IM/2021/110 Update with your client ID
                .requestEmail()
                .build();

        gClient = GoogleSignIn.getClient(LoginActivity.this, gOptions); // IM/2021/110 Initialize GoogleSignInClient

        googleBtn.setOnClickListener(view -> signIn()); // IM/2021/110 Set onClickListener for Google sign-in

        // IM/2021/110 Set onClickListener for forgot password
        forgotPassword.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)); // IM/2021/110 Navigate to ForgotPasswordActivity
            finish(); // IM/2021/110 End the LoginActivity
        });
    }

    // IM/2021/110 Method to handle Google sign-in
    private void signIn() {
        Intent intent = gClient.getSignInIntent(); // IM/2021/110 Get Google sign-in intent
        startActivityForResult(intent, RC_SIGN_IN); // IM/2021/110 Start the sign-in activity
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // IM/2021/110 Check if the result is from Google sign-in
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data); // IM/2021/110 Get the signed-in account

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class); // IM/2021/110 Get Google account
                auth(account.getIdToken()); // IM/2021/110 Authenticate with Firebase using the ID token
            } catch (ApiException e) {
                Log.e("LoginActivity", "Google sign-in failed: " + e.getStatusCode()); // IM/2021/110 Log sign-in failure
                Toast.makeText(this, "Sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show(); // IM/2021/110 Show error message
            }
        }
    }

    // IM/2021/110 Authenticate with Firebase using Google credentials
    private void auth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null); // IM/2021/110 Get GoogleAuthProvider credential
        auth.signInWithCredential(credential) // IM/2021/110 Sign in with Firebase using the credential
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser(); // IM/2021/110 Get the currently signed-in user
                        HashMap<String, Object> map = new HashMap<>(); // IM/2021/110 Create a map to store user information
                        map.put("email", user.getEmail()); // IM/2021/110 Put user email in the map
                        map.put("name", user.getDisplayName()); // IM/2021/110 Put user name in the map
                        database.getReference("users").child(user.getUid()).setValue(map); // IM/2021/110 Save user data to Firebase
                        Toast.makeText(LoginActivity.this, "Logged In Successfully!", Toast.LENGTH_SHORT).show(); // IM/2021/110 Show success message
                        startActivity(new Intent(LoginActivity.this, MainActivity.class)); // IM/2021/110 Navigate to MainActivity
                        finish(); // IM/2021/110 End the LoginActivity
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication Failed!", Toast.LENGTH_SHORT).show(); // IM/2021/110 Show authentication failure message
                    }
                });
    }
}
