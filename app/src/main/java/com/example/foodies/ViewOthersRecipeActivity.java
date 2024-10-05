package com.example.foodies;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewOthersRecipeActivity extends AppCompatActivity {

    FloatingActionButton fabAdd, fabFavourite, fabProfile, fabHome;
    SearchView searchView;
    RecyclerView recyclerView;
    List<DataClass> dataList;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    ViewOthersRecipeAdapter adapter;
    ImageView backIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_others_recipe);

// IM/2021/038 - Check if the user is logged in. If not, redirect to LoginActivity.
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerView);
        backIcon = findViewById(R.id.back);

// IM/2021/038 - Set up RecyclerView with a grid layout manager (1 column).
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ViewOthersRecipeActivity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

// IM/2021/038 - Create a loading dialog to show progress while data is being loaded.
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewOthersRecipeActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();
        adapter = new ViewOthersRecipeAdapter(ViewOthersRecipeActivity.this, dataList);
        recyclerView.setAdapter(adapter);

// IM/2021/038 - Set up the database reference to fetch recipes from Firebase.
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Recipes");

// IM/2021/038 - Add a ValueEventListener to listen for data changes in Firebase.
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                    if (dataClass != null && !dataClass.getOwner().equals(userId)) {
                        dataClass.setKey(itemSnapshot.getKey());
                        dataList.add(dataClass);
                    }
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();  // IM/2021/038 - Dismiss the loading dialog once data is loaded.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // IM/2021/038 - Dismiss the loading dialog if there is an error.
                dialog.dismiss();
            }
        });

// IM/2021/038 - Handle the back button click to return to the main activity.
        backIcon.setOnClickListener(view -> {
            startActivity(new Intent(ViewOthersRecipeActivity.this, MainActivity.class));
            finish();
        });


        searchView = findViewById(R.id.searchOthers);
        searchView.clearFocus();
// IM/2021/038 - Set up search functionality to filter recipes based on query.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });

        fabFavourite = findViewById(R.id.fabFav);
        fabHome = findViewById(R.id.fabHome);
        fabAdd = findViewById(R.id.fabAdd);
        fabProfile = findViewById(R.id.fabProfile);

// IM/2021/038 - Redirect to SavedRecipesActivity when the Favourite button is clicked.
        fabFavourite.setOnClickListener(view -> {
            Intent intent = new Intent(ViewOthersRecipeActivity.this, SavedRecipesActivity.class);
            startActivity(intent);
            finish();
        });

// IM/2021/038 - Redirect to MainActivity when the Home button is clicked.
        fabHome.setOnClickListener(view -> {
            Intent intent = new Intent(ViewOthersRecipeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

// IM/2021/038 - Redirect to UploadActivity when the Add button is clicked.
        fabAdd.setOnClickListener(view -> {
            Intent intent = new Intent(ViewOthersRecipeActivity.this, UploadActivity.class);
            startActivity(intent);
            finish();
        });

// IM/2021/038 - Redirect to ProfileActivity when the Profile button is clicked.
        fabProfile.setOnClickListener(view -> {
            Intent intent = new Intent(ViewOthersRecipeActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // IM/2021/038 - Search the recipe list and filter based on the search query.
    public void searchList(String text) {
        ArrayList<DataClass> searchList = new ArrayList<>();
        for (DataClass dataClass : dataList) {
            if (dataClass.getDataName().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(dataClass);
            }
        }

// IM/2021/038 - Update the adapter with the filtered search results.
        if (adapter != null) {
            adapter.searchDataList(searchList);
        }
    }
}