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

    FloatingActionButton fabHome; // Floating action button for home IM/2021/070
    FirebaseAuth auth; // Firebase authentication instance IM/2021/070
    Button logOut, save, deleteAccount, cancel; // Button instances IM/2021/070
    ImageView backIcon; // Back icon image view IM/2021/070
    final String SHARED_PREFS = "shared_prefs"; // Shared preferences key IM/2021/070
    TextView userName, userEmail; // Text views for user info IM/2021/070
    EditText editName; // Edit text for name input IM/2021/070
    DatabaseReference databaseReference; // Database reference for user data IM/2021/070

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Set layout for the activity IM/2021/070

        editName = findViewById(R.id.editName);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        logOut = findViewById(R.id.logOutButton);
        save = findViewById(R.id.saveButton);
        cancel = findViewById(R.id.cancelButton);
        deleteAccount = findViewById(R.id.deleteButton);
        backIcon = findViewById(R.id.backIcon);
        fabHome = findViewById(R.id.fabHome);

        auth = FirebaseAuth.getInstance(); // Initialize Firebase authentication IM/2021/070
        databaseReference = FirebaseDatabase.getInstance().getReference("users"); // Reference to users in the database IM/2021/070

        FirebaseUser user = auth.getCurrentUser(); // Get current user
        if (user != null) {
            userEmail.setText(user.getEmail()); // Display user email
            loadUserData(user.getUid()); // Load user data
        }

        logOut.setOnClickListener(v -> showLogOutConfirmationDialog()); // Log out button click listener IM/2021/070

        save.setOnClickListener(v -> {
            String newName = editName.getText().toString().trim(); // Get trimmed name
            if (newName.isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Name cannot be empty!", Toast.LENGTH_SHORT).show(); // Check for empty name
                return;
            }
            assert user != null; // Ensure user is not null
            updateUserData(user.getUid(), newName); // Update user data
        });

        cancel.setOnClickListener(view -> {
            loadUserData(user.getUid()); // Load user data if canceled
            Toast.makeText(ProfileActivity.this, "Update canceled!", Toast.LENGTH_SHORT).show(); // Notify user
        });

        deleteAccount.setOnClickListener(v -> showDeleteAccountConfirmationDialog()); // Delete account confirmation dialog IM/2021/070

        backIcon.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, MainActivity.class)); // Navigate back to main activity
            finish(); // Finish current activity
        });

        fabHome.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class); // Intent to main activity
            startActivity(intent);
            finish(); // Finish current activity
        });
    }

    private void loadUserData(String userId) {
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class); // Get user data
                if (user != null) {
                    userName.setText(user.getName()); // Set username
                    editName.setText(user.getName()); // Set editable username
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show(); // Handle errors
            }
        });
    }

    private void updateUserData(String userId, String newName) {
        databaseReference.child(userId).child("name").setValue(newName) // Update user name in database
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProfileActivity.this, "Name updated successfully!", Toast.LENGTH_SHORT).show(); // Notify user
                    userName.setText(newName); // Update displayed name
                })
                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to update name!", Toast.LENGTH_SHORT).show()); // Handle errors
    }

    private void showLogOutConfirmationDialog() {
        LayoutInflater inflater = getLayoutInflater(); // Inflate log out dialog layout
        View dialogView = inflater.inflate(R.layout.log_out_pop_up, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        TextView yesButton = dialogView.findViewById(R.id.button_yes); // Yes button
        TextView noButton = dialogView.findViewById(R.id.button_no); // No button

        yesButton.setOnClickListener(v -> {
            auth.signOut(); // Sign out user
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE); // Clear shared preferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class)); // Navigate to login activity
            Toast.makeText(ProfileActivity.this, "You have been logged out!", Toast.LENGTH_SHORT).show(); // Notify user
            finish(); // Finish current activity
            dialog.dismiss(); // Dismiss dialog
        });

        noButton.setOnClickListener(v -> dialog.dismiss()); // Dismiss dialog if no
        dialog.show(); // Show dialog
    }

    private void showDeleteAccountConfirmationDialog() {
        LayoutInflater inflater = getLayoutInflater(); // Inflate delete account dialog layout
        View dialogView = inflater.inflate(R.layout.delete_account_pop_up, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        TextView yesButton = dialogView.findViewById(R.id.button_yes); // Yes button
        TextView noButton = dialogView.findViewById(R.id.button_no); // No button

        yesButton.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE); // Clear shared preferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            deleteAccount(); // Call delete account method
            dialog.dismiss(); // Dismiss dialog
        });

        noButton.setOnClickListener(v -> dialog.dismiss()); // Dismiss dialog if no
        dialog.show(); // Show dialog
    }

    private void deleteAccount() {
        FirebaseUser user = auth.getCurrentUser(); // Get current user
        if (user != null) {
            String userId = user.getUid();

            // Check if the user is authenticated with Google
            boolean isGoogleUser = user.getProviderData().stream().anyMatch(profile -> "google.com".equals(profile.getProviderId())); // Check provider

            user.delete() // Delete user
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Delete user data from the database
                            databaseReference.child(userId).removeValue() // Remove user data from database
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(ProfileActivity.this, "Account deleted successfully!", Toast.LENGTH_SHORT).show(); // Notify user
                                        startActivity(new Intent(ProfileActivity.this, LoginActivity.class)); // Navigate to login activity
                                        finish(); // Finish current activity
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to delete user data!", Toast.LENGTH_SHORT).show()); // Handle errors
                        } else {
                            Toast.makeText(ProfileActivity.this,
                                    isGoogleUser ? "Failed to delete Google account!" : "Failed to delete email account!", // Notify user of failure
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
