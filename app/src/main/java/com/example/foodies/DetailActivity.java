package com.example.foodies;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailActivity extends AppCompatActivity {

    // IM/2021/116 UI elements
    PlayerView detailVideo;               // IM/2021/116 Video player for displaying recipe video
    ExoPlayer player;                     // IM/2021/116 ExoPlayer instance
    TextView detailName, detailTime, detailCategory, detailIngredients, detailDesc;
    ImageView deleteDataImage, editDataImage, shareDataImage, backIcon;

    // IM/2021/116 Variables for recipe data
    String key = "";                       // IM/2021/116 Unique key for the recipe
    String imageUrl = "";                  // IM/2021/116 URL for the recipe image
    String videoUrl = "";                  // IM/2021/116 URL for the recipe video

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // IM/2021/116 Initialize UI elements
        detailVideo = findViewById(R.id.detailVideo); // IM/2021/116 Ensure you have a PlayerView in your layout
        deleteDataImage = findViewById(R.id.deleteData);
        shareDataImage = findViewById(R.id.shareData);
        editDataImage = findViewById(R.id.editData);
        backIcon = findViewById(R.id.back);
        detailName = findViewById(R.id.detailName);
        detailTime = findViewById(R.id.detailTime);
        detailCategory = findViewById(R.id.detailCategory);
        detailIngredients = findViewById(R.id.detailIngredients);
        detailDesc = findViewById(R.id.detailDesc);

        // IM/2021/116 Retrieve data passed from previous activity
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            // IM/2021/116 Set the text views with received data
            detailName.setText(bundle.getString("Name"));
            detailTime.setText(bundle.getString("Time"));
            detailCategory.setText(bundle.getString("Category"));
            detailIngredients.setText(bundle.getString("Ingredients"));
            detailDesc.setText(bundle.getString("Description"));
            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image"); // IM/2021/116 Get image URL
            videoUrl = bundle.getString("Video"); // IM/2021/116 Get video URL

            // IM/2021/116 Set up video player if a video URL is provided
            if (videoUrl != null && !videoUrl.isEmpty()) {
                setupPlayer(Uri.parse(videoUrl)); // IM/2021/116 Setup player
            }
        }

        // IM/2021/116 Set up click listeners for actions
        deleteDataImage.setOnClickListener(view -> showDeleteConfirmationDialog());
        editDataImage.setOnClickListener(view -> openUpdateActivity());
        shareDataImage.setOnClickListener(view -> shareRecipe());

        backIcon.setOnClickListener(view -> {
            startActivity(new Intent(DetailActivity.this, MainActivity.class));
            finish(); // IM/2021/116 Close this activity
        });
    }

    // IM/2021/116 Method to set up the ExoPlayer with the video URI
    private void setupPlayer(Uri videoUri) {
        player = new ExoPlayer.Builder(this).build(); // IM/2021/116 Create a new player instance
        detailVideo.setPlayer(player); // IM/2021/116 Bind the player to the PlayerView

        MediaItem mediaItem = MediaItem.fromUri(videoUri); // IM/2021/116 Create media item from URI
        player.setMediaItem(mediaItem); // IM/2021/116 Set the media item for the player
        player.prepare(); // IM/2021/116 Prepare the player
    }

    // IM/2021/116 Method to show confirmation dialog before deleting a recipe
    private void showDeleteConfirmationDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_layout, null); // IM/2021/116 Inflate custom dialog layout
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create(); // IM/2021/116 Create the dialog

        // IM/2021/116 Set up buttons in the dialog
        TextView yesButton = dialogView.findViewById(R.id.button_yes);
        TextView noButton = dialogView.findViewById(R.id.button_no);

        yesButton.setOnClickListener(v -> {
            deleteRecipe(); // IM/2021/116 Call method to delete the recipe
            dialog.dismiss(); // IM/2021/116 Dismiss the dialog
        });

        noButton.setOnClickListener(v -> dialog.dismiss()); // IM/2021/116 Just dismiss if "No" is clicked
        dialog.show(); // IM/2021/116 Show the dialog
    }

    // IM/2021/116 Method to delete the recipe from the database and storage
    private void deleteRecipe() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Recipes");
        FirebaseStorage storage = FirebaseStorage.getInstance();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            // IM/2021/116 Delete the associated image if URL is valid
            StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
            storageReference.delete().addOnSuccessListener(unused -> reference.child(key).removeValue().addOnSuccessListener(unused1 -> {
                        if (videoUrl != null && !videoUrl.isEmpty()) {
                            deleteVideoFromStorage(videoUrl); // IM/2021/116 Delete video if URL is valid
                        } else {
                            showToast("Deleted Successfully!"); // IM/2021/116 Show success message
                            navigateToMainActivity(); // IM/2021/116 Navigate back to main activity
                        }
                    }).addOnFailureListener(e -> handleDeleteFailure(e, "recipe from database"))) // IM/2021/116 Handle failure to delete recipe
                    .addOnFailureListener(e -> handleDeleteFailure(e, "image from storage")); // IM/2021/116 Handle failure to delete image
        } else {
            // IM/2021/116 Delete recipe without image
            reference.child(key).removeValue().addOnSuccessListener(unused -> {
                showToast("Deleted Successfully without image!"); // IM/2021/116 Show success message
                navigateToMainActivity(); // IM/2021/116 Navigate back to main activity
            }).addOnFailureListener(e -> handleDeleteFailure(e, "recipe from database")); // IM/2021/116 Handle failure
        }
    }

    // IM/2021/116 Method to delete the video from storage
    private void deleteVideoFromStorage(String videoUrl) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference videoReference = storage.getReferenceFromUrl(videoUrl);
        videoReference.delete().addOnSuccessListener(aVoid -> {
            showToast("Deleted Successfully!"); // IM/2021/116 Show success message
            navigateToMainActivity(); // IM/2021/116 Navigate back to main activity
        }).addOnFailureListener(e -> handleDeleteFailure(e, "video from storage")); // IM/2021/116 Handle failure
    }

    // IM/2021/116 Method to handle deletion failures and show an error message
    private void handleDeleteFailure(Exception e, String type) {
        showToast("Error deleting " + type + "!"); // IM/2021/116 Show error message
        Log.e("DetailActivity", "Deletion error: " + e.getMessage()); // IM/2021/116 Log error
    }

    // IM/2021/116 Method to open the update activity for editing recipe details
    private void openUpdateActivity() {
        Intent intent = new Intent(DetailActivity.this, UpdateActivity.class);
        // IM/2021/116 Pass recipe data to the update activity
        intent.putExtra("Name", detailName.getText().toString());
        intent.putExtra("Time", detailTime.getText().toString());
        intent.putExtra("Category", detailCategory.getText().toString());
        intent.putExtra("Ingredients", detailIngredients.getText().toString());
        intent.putExtra("Description", detailDesc.getText().toString());
        intent.putExtra("Image", imageUrl);
        intent.putExtra("Key", key);
        intent.putExtra("Video", videoUrl); // IM/2021/116 Pass video URL

        startActivity(intent); // IM/2021/116 Start the update activity
        finish(); // IM/2021/116 Close this activity
    }

    // IM/2021/116 Method to share the recipe details via other apps
    private void shareRecipe() {
        String recipeDetails = "Check out this recipe:\n\n" +
                "Name: " + detailName.getText().toString() + "\n\n" +
                "Time: " + detailTime.getText().toString() + "\n\n" +
                "Category: " + detailCategory.getText().toString() + "\n\n" +
                "Ingredients: " +detailIngredients.getText().toString() + "\n\n" +
                "Description: " + detailDesc.getText().toString() + "\n\n" +
                "Click here to view image: " + imageUrl + "\n\n" +
                "Watch video: " + videoUrl; // IM/2021/116 Optionally include video URL

        Intent shareIntent = new Intent(Intent.ACTION_SEND); // IM/2021/116 Create share intent
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, recipeDetails); // IM/2021/116 Add recipe details to share

        startActivity(Intent.createChooser(shareIntent, "Share Recipe via")); // IM/2021/116 Show chooser for sharing
    }

    // IM/2021/116 Navigate back to the main activity
    private void navigateToMainActivity() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish(); // IM/2021/116 Close this activity
    }

    // IM/2021/116 Show a toast message
    private void showToast(String message) {
        Toast.makeText(DetailActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    // IM/2021/116 Release the player resources when the activity is stopped
    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release(); // IM/2021/116 Release player resources
            player = null; // IM/2021/116 Set player to null
        }
    }
}
