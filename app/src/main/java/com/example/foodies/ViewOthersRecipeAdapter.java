package com.example.foodies;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

// IM/2021/007 - Interface for callback to get average rating
interface OnAverageRatingReceivedListener {
    void onAverageRatingReceived(float averageRating);
}

// IM/2021/007 - Adapter class for displaying others' recipes in a RecyclerView
public class ViewOthersRecipeAdapter extends RecyclerView.Adapter<ViewOthersViewHolder> {

    // IM/2021/007 - Context for accessing resources and starting activities
    final Context context;
    // IM/2021/007 - List to hold recipe data
    private List<DataClass> dataList;

    // IM/2021/007 - Constructor to initialize context and data list
    public ViewOthersRecipeAdapter(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    // IM/2021/007 - Inflate the layout for each item in the RecyclerView
    @NonNull
    @Override
    public ViewOthersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_recipe_item, parent, false);
        return new ViewOthersViewHolder(view);
    }

    // IM/2021/007 - Bind data to the views in each item of the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull ViewOthersViewHolder holder, int position) {
        DataClass data = dataList.get(position);

        // IM/2021/007 - Load recipe image using Glide
        Glide.with(context).load(data.getDataImage()).placeholder(R.drawable.r_2) // Optional: add a placeholder image
                .into(holder.recImage);

        // IM/2021/007 - Set recipe title and cooking time text
        holder.recTitle.setText(data.getDataName());
        holder.recTime.setText(data.getDataTime());

        // IM/2021/007 - Fetch average rating for the recipe and update the RatingBar
        getAverageRating(data.getKey(), averageRating -> {
            // IM/2021/007 - Cap the rating at 4 if it's higher than 4
            if (averageRating > 4) {
                holder.recRatingbar.setRating(4);
            } else {
                holder.recRatingbar.setRating(averageRating); // Set the average rating
            }
        });

        // IM/2021/007 - Set click listener to navigate to recipe detail activity
        holder.recCard.setOnClickListener(view -> {
            Intent intent = new Intent(context, OthersRecipeDetailActivity.class);
            // IM/2021/007 - Pass recipe data to the detail activity
            intent.putExtra("Image", data.getDataImage());
            intent.putExtra("Name", data.getDataName());
            intent.putExtra("Time", data.getDataTime());
            intent.putExtra("Category", data.getDataCategory());
            intent.putExtra("Ingredients", data.getDataIngredients());
            intent.putExtra("Description", data.getDataDescription());
            intent.putExtra("Key", data.getKey());
            intent.putExtra("Owner", data.getOwner());
            intent.putExtra("Video", data.getDataVideo());

            context.startActivity(intent); // Start the detail activity
        });
    }

    // IM/2021/007 - Return the total number of items in the data list
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // IM/2021/007 - Method to fetch average rating from the database
    public void getAverageRating(String recipeKey, OnAverageRatingReceivedListener listener) {
        DatabaseReference ratingsRef = FirebaseDatabase.getInstance().getReference("Recipes").child(recipeKey).child("ratings");

        // IM/2021/007 - Listen for changes in the ratings data
        ratingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Float> ratingsList = new ArrayList<>();
                // IM/2021/007 - Collect ratings from the snapshot
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    OthersRecipeDetailActivity.RatingData ratingData = snapshot.getValue(OthersRecipeDetailActivity.RatingData.class);
                    if (ratingData != null) {
                        ratingsList.add(ratingData.rating);
                    }
                }
                float averageRating = 0;
                // IM/2021/007 - Calculate average rating if ratings exist
                if (!ratingsList.isEmpty()) {
                    float total = 0;
                    for (float rating : ratingsList) {
                        total += rating;
                    }
                    averageRating = roundToFirstDecimal(total / ratingsList.size());
                }
                listener.onAverageRatingReceived(averageRating); // Return the average rating
            }

            // IM/2021/007 - Handle errors by returning a default rating of 0
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onAverageRatingReceived(0); // Return 0 if there was an error
            }
        });
    }

    // IM/2021/007 - Method to round the rating to the first decimal place
    private float roundToFirstDecimal(float rating) {
        return (float) (Math.round(rating * 10)) / 10; // Round to the first decimal
    }

    // IM/2021/007 - Update the data list with search results and refresh the
    // RecyclerView
    public void searchDataList(ArrayList<DataClass> searchList) {
        dataList = searchList;
        notifyDataSetChanged(); // Notify the adapter to refresh the view
    }
}

// IM/2021/007 - ViewHolder class to hold the UI components for each recipe item
class ViewOthersViewHolder extends RecyclerView.ViewHolder {

    ImageView recImage; // IM/2021/007 - ImageView for displaying recipe image
    TextView recTitle, recTime; // IM/2021/007 - TextViews for recipe title and time
    CardView recCard; // IM/2021/007 - CardView to hold the recipe item
    RatingBar recRatingbar; // IM/2021/007 - RatingBar for displaying the recipe rating

    // IM/2021/007 - Constructor to initialize UI components
    public ViewOthersViewHolder(@NonNull View itemView) {
        super(itemView);

        recImage = itemView.findViewById(R.id.otherRecImage);
        recTitle = itemView.findViewById(R.id.otherRecTitle);
        recTime = itemView.findViewById(R.id.otherRecTime);
        recCard = itemView.findViewById(R.id.otherRecCard);
        recRatingbar = itemView.findViewById(R.id.recRatingBar); // Initialize RatingBar
    }
}
