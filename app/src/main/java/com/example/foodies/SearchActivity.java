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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    ImageView backIcon;
    SearchView searchView;
    SearchRecipeAdapter adapter;
    List<DataClass> dataList;
    RecyclerView recyclerView;
    ValueEventListener eventListener;
    DatabaseReference databaseReference;

    //IM/2020/116
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Set up RecyclerView with GridLayoutManager to display items in a single column layout
        GridLayoutManager gridLayoutManager = new GridLayoutManager(SearchActivity.this, 1);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Initialize and show loading dialog while fetching data from Firebase
        AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Initialize adapter and data list
        dataList = new ArrayList<>();
        adapter = new SearchRecipeAdapter(SearchActivity.this, dataList);
        recyclerView.setAdapter(adapter);

        // Reference to Firebase database for "Recipes" node
        databaseReference = FirebaseDatabase.getInstance().getReference("Recipes");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch recipes owned by the current user from Firebase and update data list
        eventListener = databaseReference.orderByChild("owner").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                    if (dataClass != null) {
                        dataClass.setKey(itemSnapshot.getKey());
                        dataList.add(dataClass);
                    }
                }
                // Notify adapter of data changes and dismiss loading dialog
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

        // Set up SearchView to listen for text changes and trigger search filtering
        searchView = findViewById(R.id.search);
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

        // Set up back navigation to return to MainActivity
        backIcon = findViewById(R.id.back);
        backIcon.setOnClickListener(view -> {
            startActivity(new Intent(SearchActivity.this,MainActivity.class));
            finish();
        });
    }

    // Method to filter data list based on search query and update adapter
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