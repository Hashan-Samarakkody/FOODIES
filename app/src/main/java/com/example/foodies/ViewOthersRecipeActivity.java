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

    FloatingActionButton fabAdd,fabFavourite,fabProfile,fabHome;
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

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerView);
        backIcon = findViewById(R.id.back);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(ViewOthersRecipeActivity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(ViewOthersRecipeActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();
        adapter = new ViewOthersRecipeAdapter(ViewOthersRecipeActivity.this, dataList);
        recyclerView.setAdapter(adapter);

        // Set the database reference and listener
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Recipes");

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                    if (dataClass != null  && !dataClass.getOwner().equals(userId)) {
                        dataClass.setKey(itemSnapshot.getKey());
                        dataList.add(dataClass);
                    }
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

        backIcon.setOnClickListener(view -> {
            startActivity(new Intent(ViewOthersRecipeActivity.this,MainActivity.class));
            finish();
        });

        searchView = findViewById(R.id.searchOthers);
        searchView.clearFocus();
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

        fabFavourite.setOnClickListener(view -> {
            Intent intent = new Intent(ViewOthersRecipeActivity.this, SavedRecipesActivity.class);
            startActivity(intent);
            finish();
        });

        fabHome.setOnClickListener(view -> {
            Intent intent = new Intent(ViewOthersRecipeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        fabAdd.setOnClickListener(view -> {
            Intent intent = new Intent(ViewOthersRecipeActivity.this, UploadActivity.class);
            startActivity(intent);
            finish();
        });


        fabProfile.setOnClickListener(view -> {
            Intent intent = new Intent(ViewOthersRecipeActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }
    public void searchList(String text) {
        ArrayList<DataClass> searchList = new ArrayList<>();
        for (DataClass dataClass : dataList) {
            if (dataClass.getDataName().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(dataClass);
            }
        }
        // Check if adapter is not null before calling its method
        if (adapter != null) {
            adapter.searchDataList(searchList);
        }
    }
}