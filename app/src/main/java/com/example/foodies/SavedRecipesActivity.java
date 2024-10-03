package com.example.foodies;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    RecyclerView recyclerView;
    List<DataClass> dataList;
    DatabaseReference favoritesRef;
    ViewOthersRecipeAdapter adapter;
    ImageView backIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_recipes);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        backIcon = findViewById(R.id.back);

        // Setup RecyclerView
        dataList = new ArrayList<>();
        adapter = new ViewOthersRecipeAdapter(SavedRecipesActivity.this, dataList);
        recyclerView.setLayoutManager(new GridLayoutManager(SavedRecipesActivity.this, 1));
        recyclerView.setAdapter(adapter);

        // Check if user is authenticated
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Setup Firebase reference for favorites
        favoritesRef = FirebaseDatabase.getInstance().getReference("Favorites").child(currentUser.getUid());

        // Fetch saved recipes
        fetchSavedRecipes();

        backIcon.setOnClickListener(view -> {
            startActivity(new Intent(SavedRecipesActivity.this, MainActivity.class));
            finish();
        });
    }

    private void fetchSavedRecipes() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SavedRecipesActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        favoritesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String recipeKey = itemSnapshot.getKey();
                    // Retrieve the recipe details by its key
                    DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("Recipes").child(recipeKey);
                    recipeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            DataClass recipe = dataSnapshot.getValue(DataClass.class);
                            if (recipe != null) {
                                recipe.setKey(recipeKey);
                                dataList.add(recipe);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(SavedRecipesActivity.this, "Failed to load saved recipes.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                Toast.makeText(SavedRecipesActivity.this, "Failed to load favorites.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
