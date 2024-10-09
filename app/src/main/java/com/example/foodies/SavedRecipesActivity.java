package com.example.foodies;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SavedRecipesActivity extends AppCompatActivity {

    FloatingActionButton fabAdd, fabView, fabProfile, fabHome; // Floating action buttons for various actions IM/2021/038
    RecyclerView recyclerView; // RecyclerView to display saved recipes IM/2021/038
    List<DataClass> dataList; // List to hold recipe data IM/2021/038
    DatabaseReference favoritesRef; // Reference to the Firebase database for favorites IM/2021/038
    ViewOthersRecipeAdapter adapter; // Adapter for the RecyclerView IM/2021/038
    ImageView backIcon; // Back button icon IM/2021/038

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_recipes); // Set the layout for this activity IM/2021/038

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView); // Reference to RecyclerView IM/2021/038
        backIcon = findViewById(R.id.back); // Reference to back button IM/2021/038

        // Setup RecyclerView
        dataList = new ArrayList<>(); // Initialize the data list IM/2021/038
        adapter = new ViewOthersRecipeAdapter(SavedRecipesActivity.this, dataList); // Create adapter for RecyclerView IM/2021/038
        recyclerView.setLayoutManager(new GridLayoutManager(SavedRecipesActivity.this, 1)); // Set layout manager for RecyclerView IM/2021/038
        recyclerView.setAdapter(adapter); // Set the adapter to RecyclerView IM/2021/038

        // Check if user is authenticated
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser(); // Get current authenticated user IM/2021/038
        if (currentUser == null) { // If no user is authenticated IM/2021/038
            startActivity(new Intent(this, LoginActivity.class)); // Navigate to LoginActivity IM/2021/038
            finish(); // Finish current activity IM/2021/038
            return; // Exit the method IM/2021/038
        }

        // Setup Firebase reference for favorites
        favoritesRef = FirebaseDatabase.getInstance().getReference("Favorites").child(currentUser.getUid()); // Reference to user's favorites IM/2021/038

        // Fetch saved recipes
        fetchSavedRecipes(); // Call method to fetch saved recipes IM/2021/038

        // Set onClickListener for back icon
        backIcon.setOnClickListener(view -> {
            startActivity(new Intent(SavedRecipesActivity.this, MainActivity.class)); // Navigate to MainActivity IM/2021/038
            finish(); // Finish current activity IM/2021/038
        });

        // Initialize floating action buttons
        fabAdd = findViewById(R.id.fabAdd); // Reference to add recipe button IM/2021/038
        fabView = findViewById(R.id.fabView); // Reference to view others' recipes button IM/2021/038
        fabHome = findViewById(R.id.fabHome); // Reference to home button IM/2021/038
        fabProfile = findViewById(R.id.fabProfile); // Reference to profile button IM/2021/038

        // Set onClickListener for fabAdd
        fabAdd.setOnClickListener(view -> {
            Intent intent = new Intent(SavedRecipesActivity.this, UploadActivity.class); // Intent to navigate to UploadActivity IM/2021/038
            startActivity(intent); // Start the activity IM/2021/038
            finish(); // Finish current activity IM/2021/038
        });

        // Set onClickListener for fabHome
        fabHome.setOnClickListener(view -> {
            Intent intent = new Intent(SavedRecipesActivity.this, MainActivity.class); // Intent to navigate to MainActivity IM/2021/038
            startActivity(intent); // Start the activity IM/2021/038
            finish(); // Finish current activity IM/2021/038
        });

        // Set onClickListener for fabView
        fabView.setOnClickListener(view -> {
            Intent intent = new Intent(SavedRecipesActivity.this, ViewOthersRecipeActivity.class); // Intent to navigate to ViewOthersRecipeActivity IM/2021/038
            startActivity(intent); // Start the activity IM/2021/038
            finish(); // Finish current activity IM/2021/038
        });

        // Set onClickListener for fabProfile
        fabProfile.setOnClickListener(view -> {
            Intent intent = new Intent(SavedRecipesActivity.this, ProfileActivity.class); // Intent to navigate to ProfileActivity IM/2021/038
            startActivity(intent); // Start the activity IM/2021/038
            finish(); // Finish current activity IM/2021/038
        });
    }

    private void fetchSavedRecipes() { // Method to fetch saved recipes from Firebase IM/2021/038
        AlertDialog.Builder builder = new AlertDialog.Builder(SavedRecipesActivity.this); // Create an AlertDialog builder IM/2021/038
        builder.setCancelable(false); // Prevent cancellation of the dialog by tapping outside IM/2021/038
        builder.setView(R.layout.progress_layout); // Set a progress layout for the dialog IM/2021/038
        AlertDialog dialog = builder.create(); // Create the dialog IM/2021/038
        dialog.show(); // Show the dialog IM/2021/038

        // Retrieve saved recipes from Firebase
        favoritesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { // On successful data retrieval IM/2021/038
                dataList.clear(); // Clear the previous data list IM/2021/038
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) { // Iterate through each favorite recipe IM/2021/038
                    String recipeKey = itemSnapshot.getKey(); // Get the recipe key IM/2021/038
                    // Retrieve the recipe details by its key
                    DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("Recipes").child(recipeKey); // Reference to the specific recipe IM/2021/038
                    recipeRef.addListenerForSingleValueEvent(new ValueEventListener() { // Add listener for a single value event
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) { // On successful recipe data retrieval IM/2021/038
                            DataClass recipe = dataSnapshot.getValue(DataClass.class); // Get recipe data as DataClass object IM/2021/038
                            if (recipe != null) { // If recipe is not null IM/2021/038
                                recipe.setKey(recipeKey); // Set the recipe key in the recipe object IM/2021/038
                                dataList.add(recipe); // Add recipe to the data list IM/2021/038
                                adapter.notifyDataSetChanged(); // Notify adapter to update the RecyclerView IM/2021/038
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { // On failure to retrieve recipe data IM/2021/038
                            Toast.makeText(SavedRecipesActivity.this, "Failed to load saved recipes.", Toast.LENGTH_SHORT).show(); // Show error message IM/2021/038
                        }
                    });
                }
                dialog.dismiss(); // Dismiss the dialog after fetching data IM/2021/038
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { // On failure to retrieve favorites data IM/2021/038
                dialog.dismiss(); // Dismiss the dialog IM/2021/038
                Toast.makeText(SavedRecipesActivity.this, "Failed to load favorites.", Toast.LENGTH_SHORT).show(); // Show error message IM/2021/038
            }
        });
    }
}
