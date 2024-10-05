package com.example.foodies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fabAdd, fabView, fabProfile, fabFavourite; // Floating action buttons for different actions IM/2021/070
    RecyclerView recyclerView; // RecyclerView to display the list of recipes IM/2021/070
    List<DataClass> dataList; // List to hold recipe data IM/2021/070
    DatabaseReference databaseReference; // Reference to Firebase Database IM/2021/070
    ValueEventListener eventListener; // Listener to respond to database changes IM/2021/070
    MyRecipeAdapter adapter; // Adapter to bind data to the RecyclerView IM/2021/070
    ImageView search; // ImageView for the search functionality IM/2021/070

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Redirect to login activity if user is not authenticated IM/2021/070
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        ImageSlider imageSlider = findViewById(R.id.imageSlider); // Initialize the image slider IM/2021/070
        ArrayList<SlideModel> slideModels = new ArrayList<>(); // List to hold slide models for the slider IM/2021/070

        // Adding slide models with images to the slider IM/2021/070
        slideModels.add(new SlideModel(R.drawable.r_1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.r_2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.r_3, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.r_4, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.r_5, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.r_6, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.r_7, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.r_8, ScaleTypes.FIT));

        imageSlider.setImageList(slideModels, ScaleTypes.FIT); // Set the image list to the slider IM/2021/070

        recyclerView = findViewById(R.id.recyclerView); // Initialize the RecyclerView IM/2021/070
        search = findViewById(R.id.search); // Initialize the search ImageView IM/2021/070

        // Set click listener for the search ImageView to open the search activity IM/2021/070
        search.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
            finish();
        });

        // Set up the layout manager for the RecyclerView IM/2021/070
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this); // Builder for the progress dialog IM/2021/070
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout); // Set the layout for the dialog IM/2021/070
        AlertDialog dialog = builder.create(); // Create the dialog instance IM/2021/070
        dialog.show(); // Show the dialog IM/2021/070

        dataList = new ArrayList<>(); // Initialize the data list IM/2021/070
        adapter = new MyRecipeAdapter(MainActivity.this, dataList); // Initialize the adapter with the data list IM/2021/070
        recyclerView.setAdapter(adapter); // Set the adapter to the RecyclerView IM/2021/070

        // Set the database reference and listener to fetch user recipes IM/2021/070
        databaseReference = FirebaseDatabase.getInstance().getReference("Recipes");

        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(); // Get the current user ID IM/2021/070

        eventListener = databaseReference.orderByChild("owner").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear(); // Clear the data list before adding new items IM/2021/070
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = itemSnapshot.getValue(DataClass.class); // Retrieve recipe data from snapshot IM/2021/070
                    if (dataClass != null) {
                        dataClass.setKey(itemSnapshot.getKey()); // Set the key for the data item IM/2021/070
                        dataList.add(dataClass); // Add the data item to the list IM/2021/070
                    }
                }
                adapter.notifyDataSetChanged(); // Notify the adapter of data changes IM/2021/070
                dialog.dismiss(); // Dismiss the progress dialog IM/2021/070
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss(); // Dismiss the dialog in case of error IM/2021/070
            }
        });

        // Initialize the floating action buttons IM/2021/070
        fabFavourite = findViewById(R.id.fabFav);
        fabAdd = findViewById(R.id.fabAdd);
        fabView = findViewById(R.id.fabView);
        fabProfile = findViewById(R.id.fabProfile);

        // Set click listener for the Favourite button to open SavedRecipesActivity IM/2021/070
        fabFavourite.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SavedRecipesActivity.class);
            startActivity(intent);
            finish();
        });

        // Set click listener for the Add button to open UploadActivity IM/2021/070
        fabAdd.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, UploadActivity.class);
            startActivity(intent);
            finish();
        });

        // Set click listener for the View button to open ViewOthersRecipeActivity IM/2021/070
        fabView.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ViewOthersRecipeActivity.class);
            startActivity(intent);
            finish();
        });

        // Set click listener for the Profile button to open ProfileActivity IM/2021/070
        fabProfile.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
