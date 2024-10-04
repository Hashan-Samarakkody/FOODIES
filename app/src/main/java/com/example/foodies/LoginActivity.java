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

public class LoginActivity extends AppCompatActivity {

    static final int RC_SIGN_IN = 20;

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

        forgotPassword = findViewById(R.id.forgotPass);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        login = findViewById(R.id.login);
        gotoRegister = findViewById(R.id.backIcon);
        googleBtn = findViewById(R.id.googleBtn);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
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

        login.setOnClickListener(view -> {
            String userEmail = etEmail.getText().toString().trim();
            String userPassword = etPassword.getText().toString().trim();

            if (userEmail.isEmpty()) {
                etEmail.setError("Email required!");
            } else if (userPassword.isEmpty()) {
                etPassword.setError("Password required!");
            } else if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                etEmail.setError("Invalid Email");
            }
            else {
                // Save the email and password in SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(EMAIL_KEY, userEmail);
                editor.putString(PASSWORD_KEY, userPassword);
                editor.apply();

                // Authenticate the user
                auth.signInWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Logged In Successfully!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish(); // End the LoginActivity
                            } else {
                                Toast.makeText(LoginActivity.this, "User Authentication Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "User Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        gotoRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish(); // End the LoginActivity
        });

        gOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Update with your client ID
                .requestEmail()
                .build();

        gClient = GoogleSignIn.getClient(LoginActivity.this, gOptions);

        googleBtn.setOnClickListener(view -> signIn());

        forgotPassword.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
            finish();
        });
    }

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
                auth(account.getIdToken());
            } catch (ApiException e) {
                Log.e("LoginActivity", "Google sign-in failed: " + e.getStatusCode());
                Toast.makeText(this, "Sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void auth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("email", user.getEmail());
                        map.put("name", user.getDisplayName());
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