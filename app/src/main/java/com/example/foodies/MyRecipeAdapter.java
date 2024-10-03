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

public class MyRecipeAdapter extends RecyclerView.Adapter<MyViewHolder> {

    final Context context;
    private List<DataClass> dataList;

    public MyRecipeAdapter(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_recipe_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataClass data = dataList.get(position);

        // Load image using Glide
        Glide.with(context)
                .load(data.getDataImage())
                .placeholder(R.drawable.r_2) // Optional: add a placeholder image
                .into(holder.recImage);

        // Set text views
        holder.recTitle.setText(data.getDataName());
        holder.recTime.setText(data.getDataTime());

        // Fetch average rating and set it to the rating bar
        getAverageRating(data.getKey(), new OnAverageRatingReceivedListener() {
            @Override
            public void onAverageRatingReceived(float averageRating) {
                holder.recRatingBar.setRating(averageRating); // Set the average rating
            }
        });

        // Set click listener
        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("Image", data.getDataImage());
                intent.putExtra("Name", data.getDataName());
                intent.putExtra("Time", data.getDataTime());
                intent.putExtra("Category", data.getDataCategory());
                intent.putExtra("Ingredients", data.getDataIngredients());
                intent.putExtra("Description", data.getDataDescription());
                intent.putExtra("Key", data.getKey());
                intent.putExtra("Owner", data.getOwner());
                intent.putExtra("Video", data.getDataVideo()); // Pass video URL

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // Method to fetch average rating from the database
    public void getAverageRating(String recipeKey, OnAverageRatingReceivedListener listener) {
        DatabaseReference ratingsRef = FirebaseDatabase.getInstance().getReference("Recipes").child(recipeKey).child("ratings");

        ratingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Float> ratingsList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    OthersRecipeDetailActivity.RatingData ratingData = snapshot.getValue(OthersRecipeDetailActivity.RatingData.class);
                    if (ratingData != null) {
                        ratingsList.add(ratingData.rating);
                    }
                }
                float averageRating = 0;
                if (!ratingsList.isEmpty()) {
                    float total = 0;
                    for (float rating : ratingsList) {
                        total += rating;
                    }
                    averageRating = roundToFirstDecimal(total / ratingsList.size());
                }
                listener.onAverageRatingReceived(averageRating);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onAverageRatingReceived(0); // Return 0 if there was an error
            }
        });
    }

    // Method to round the rating to the first decimal place
    private float roundToFirstDecimal(float rating) {
        return (float) (Math.round(rating * 10)) / 10; // Round to the first decimal
    }
}

class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView recImage;
    TextView recTitle, recTime;
    CardView recCard;
    RatingBar recRatingBar; // Add RatingBar reference

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        recImage = itemView.findViewById(R.id.rImage);
        recTitle = itemView.findViewById(R.id.rTitle);
        recTime = itemView.findViewById(R.id.rTime);
        recCard = itemView.findViewById(R.id.rCard);
        recRatingBar = itemView.findViewById(R.id.recRatingBar); // Initialize RatingBar
    }
}
