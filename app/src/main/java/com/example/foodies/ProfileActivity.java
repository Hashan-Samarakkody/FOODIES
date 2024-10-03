package com.example.foodies;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button logOut;
    ImageView backIcon;
    final String SHARED_PREFS = "shared_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        logOut = findViewById(R.id.logOutButton);
        backIcon = findViewById(R.id.back);

        auth = FirebaseAuth.getInstance();
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogOutConfirmationDialog();
            }
        });

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this,MainActivity.class));
                finish();
            }
        });
    }

    private void showLogOutConfirmationDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.log_out_pop_up, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        TextView yesButton = dialogView.findViewById(R.id.button_yes);
        TextView noButton = dialogView.findViewById(R.id.button_no);

        yesButton.setOnClickListener(v -> {
            auth.signOut();
            SharedPreferences sharedPreferences;

            sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            Toast.makeText(ProfileActivity.this, "You have been logged out!", Toast.LENGTH_SHORT).show();
            finish();
            dialog.dismiss();
        });

        noButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}