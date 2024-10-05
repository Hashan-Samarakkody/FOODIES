package com.example.foodies;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    FloatingActionButton fabHome;
    FirebaseAuth auth;
    Button logOut, save, deleteAccount, cancel;
    ImageView backIcon;
    final String SHARED_PREFS = "shared_prefs";
    TextView userName, userEmail;
    EditText editName;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        editName = findViewById(R.id.editName);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        logOut = findViewById(R.id.logOutButton);
        save = findViewById(R.id.saveButton);
        cancel = findViewById(R.id.cancelButton);
        deleteAccount = findViewById(R.id.deleteButton);
        backIcon = findViewById(R.id.backIcon);
        fabHome = findViewById(R.id.fabHome);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            userEmail.setText(user.getEmail());
            loadUserData(user.getUid());
        }

        logOut.setOnClickListener(v -> showLogOutConfirmationDialog());

        save.setOnClickListener(v -> {
            String newName = editName.getText().toString().trim();
            if (newName.isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Name cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            assert user != null;
            updateUserData(user.getUid(), newName);
        });
        
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadUserData(user.getUid());
                Toast.makeText(ProfileActivity.this, "Update canceled!", Toast.LENGTH_SHORT).show();
            }
        });
        
        deleteAccount.setOnClickListener(v -> showDeleteAccountConfirmationDialog());

        backIcon.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            finish();
        });

        fabHome.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private void loadUserData(String userId) {
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                if (user != null) {
                    userName.setText(user.getName());
                    editName.setText(user.getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserData(String userId, String newName) {
        databaseReference.child(userId).child("name").setValue(newName)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProfileActivity.this, "Name updated successfully!", Toast.LENGTH_SHORT).show();
                    userName.setText(newName);
                })
                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to update name!", Toast.LENGTH_SHORT).show());
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
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
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

    private void showDeleteAccountConfirmationDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.delete_account_pop_up, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        TextView yesButton = dialogView.findViewById(R.id.button_yes);
        TextView noButton = dialogView.findViewById(R.id.button_no);

        yesButton.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            deleteAccount();
            dialog.dismiss();
        });

        noButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void deleteAccount() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Check if the user is authenticated with Google
            boolean isGoogleUser = user.getProviderData().stream().anyMatch(profile -> "google.com".equals(profile.getProviderId()));

            user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Delete user data from the database
                            databaseReference.child(userId).removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(ProfileActivity.this, "Account deleted successfully!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to delete user data!", Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(ProfileActivity.this,
                                    isGoogleUser ? "Failed to delete Google account!" : "Failed to delete email account!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
