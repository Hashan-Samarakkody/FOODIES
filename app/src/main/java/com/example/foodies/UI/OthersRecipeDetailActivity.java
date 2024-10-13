package com.example.foodies.UI;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.example.foodies.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class OthersRecipeDetailActivity extends AppCompatActivity {

    // Declare UI components IM/2021/038
    RatingBar ratingBar;
    PlayerView detailVideo;
    ExoPlayer player;
    TextView detailName, detailTime, detailCategory, detailIngredients, detailDesc;
    ImageView shareDataImage, backIcon, saveIcon;
    String key = "";
    String imageUrl = "";
    String videoUrl = ""; // Variable for video URL IM/2021/038
    FirebaseUser currentUser;
    DatabaseReference favoritesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_recipe_detail);

        // Initialize views IM/2021/038
        detailVideo = findViewById(R.id.detailVideo);
        ratingBar = findViewById(R.id.ratingBar); // Initialize the rating bar IM/2021/038
        shareDataImage = findViewById(R.id.shareData);
        backIcon = findViewById(R.id.back);
        saveIcon = findViewById(R.id.save);
        detailName = findViewById(R.id.detailName);
        detailTime = findViewById(R.id.detailTime);
        detailCategory = findViewById(R.id.detailCategory);
        detailIngredients = findViewById(R.id.detailIngredients);
        detailDesc = findViewById(R.id.detailDesc);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            detailName.setText(bundle.getString("Name"));
            detailTime.setText(bundle.getString("Time"));
            detailCategory.setText(bundle.getString("Category"));
            detailIngredients.setText(bundle.getString("Ingredients"));
            detailDesc.setText(bundle.getString("Description"));
            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image");
            videoUrl = bundle.getString("Video"); // Get video URL IM/2021/038

            if (videoUrl != null && !videoUrl.isEmpty()) {
                setupPlayer(Uri.parse(videoUrl)); // Setup player IM/2021/038
            }
        }

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        favoritesRef = FirebaseDatabase.getInstance().getReference("Favorites").child(currentUser.getUid());

        // Check if the recipe is already saved IM/2021/038
        checkIfFavorite();

        // Set up a listener for the recipe IM/2021/038
        setupRecipeListener();

        // Set click listeners IM/2021/038
        shareDataImage.setOnClickListener(view -> shareRecipe());
        backIcon.setOnClickListener(view -> {
            startActivity(new Intent(OthersRecipeDetailActivity.this, ViewOthersRecipeActivity.class));
            finish();
        });
        saveIcon.setOnClickListener(view -> toggleFavorite());

        // Setup rating bar listener IM/2021/038
        setupRatingBarListener();

        // Load existing rating from the database IM/2021/038
        loadExistingRating();
    }

    private void setupPlayer(Uri videoUri) {
        player = new ExoPlayer.Builder(this).build();
        detailVideo.setPlayer(player);

        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        player.setMediaItem(mediaItem);
        player.prepare();
    }

    private void setupRecipeListener() {
        DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("Recipes").child(key);
        recipeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // Recipe has been deleted, remove from favorites and finish the activity IM/2021/038
                    favoritesRef.child(key).removeValue(); // Ensure it's removed from favorites IM/2021/038
                    Toast.makeText(OthersRecipeDetailActivity.this, "This recipe has been deleted.", Toast.LENGTH_SHORT).show();
                    finish(); // Optionally finish the activity IM/2021/038
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors IM/2021/038
            }
        });
    }

    private void checkIfFavorite() {
        favoritesRef.child(key).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                saveIcon.setImageResource(R.drawable.img_5_2); // Change to saved icon IM/2021/038
            } else {
                saveIcon.setImageResource(R.drawable.img_5); // Change to save icon IM/2021/038
            }
        });
    }

    private void toggleFavorite() {
        favoritesRef.child(key).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                // Recipe is currently saved, so remove it IM/2021/038
                favoritesRef.child(key).removeValue().addOnCompleteListener(removeTask -> {
                    if (removeTask.isSuccessful()) {
                        saveIcon.setImageResource(R.drawable.img_5); // Change to save icon IM/2021/038
                        Toast.makeText(OthersRecipeDetailActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Recipe is not saved, so add it IM/2021/038
                favoritesRef.child(key).setValue(true).addOnCompleteListener(addTask -> {
                    if (addTask.isSuccessful()) {
                        saveIcon.setImageResource(R.drawable.img_5_2); // Change to saved icon IM/2021/038
                        Toast.makeText(OthersRecipeDetailActivity.this, "Saved to favorites", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void shareRecipe() {
        String recipeDetails = "Check out this recipe:\n\n" + "Name: " + detailName.getText().toString() + "\n\n" + "Time: " + detailTime.getText().toString() + "\n\n" + "Category: " + detailCategory.getText().toString() + "\n\n" + "Ingredients: " + detailIngredients.getText().toString() + "\n\n" + "Description: " + detailDesc.getText().toString() + "\n\n" + "Click here to view image: " + imageUrl; // Optionally include image URL IM/2021/038

        if (videoUrl != null && !videoUrl.isEmpty()) {
            recipeDetails += "\nWatch video: " + videoUrl; // Optionally include video URL IM/2021/038
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, recipeDetails);

        startActivity(Intent.createChooser(shareIntent, "Share Recipe via"));
    }

    // Rating functionality IM/2021/038
    private void setupRatingBarListener() {
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                showSubmitRatingDialog(rating); // Show dialog for rating IM/2021/038
            }
        });
    }

    private void showSubmitRatingDialog(float rating) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Submit Rating").setMessage("Are you sure you want to submit a rating of " + rating + " stars?") // Confirm rating IM/2021/038
                .setPositiveButton("Yes", (dialog, which) -> {
                    submitRating(rating); // Submit the rating IM/2021/038
                }).setNegativeButton("No", (dialog, which) -> {
                    // Reset rating if cancelled, but keep the current rating IM/2021/038
                    loadExistingRating();
                }).show();
    }

    private void submitRating(float rating) {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference recipeRatingsRef = FirebaseDatabase.getInstance().getReference("Recipes").child(key).child("ratings").child(userId);
            recipeRatingsRef.setValue(new RatingData(rating)).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(OthersRecipeDetailActivity.this, "Rating submitted!", Toast.LENGTH_SHORT).show();
                    ratingBar.setRating(rating); // Update the displayed rating IM/2021/038
                } else {
                    Toast.makeText(OthersRecipeDetailActivity.this, "Failed to submit rating.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadExistingRating() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference recipeRatingsRef = FirebaseDatabase.getInstance().getReference("Recipes").child(key).child("ratings").child(userId);
            recipeRatingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        RatingData ratingData = dataSnapshot.getValue(RatingData.class);
                        if (ratingData != null) {
                            ratingBar.setRating(ratingData.rating); // Set the existing rating IM/2021/038
                        }
                    } else {
                        ratingBar.setRating(0); // No existing rating IM/2021/038
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle possible errors IM/2021/038
                }
            });
        }
    }

    // This is a simple model for storing ratings IM/2021/038
    public static class RatingData {
        public float rating;

        public RatingData() {
            // Default constructor required for calls to DataSnapshot.getValue(RatingData.class) IM/2021/038
        }

        public RatingData(float rating) {
            this.rating = rating;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
            player = null; // Release player resources IM/2021/038
        }
    }
}
