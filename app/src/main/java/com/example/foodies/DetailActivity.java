package com.example.foodies;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
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

    RatingBar ratingBar;
    PlayerView detailVideo;
    ExoPlayer player;
    TextView detailName, detailTime, detailCategory, detailIngredients, detailDesc;
    ImageView deleteDataImage, editDataImage, shareDataImage, backIcon;
    String key = "";
    String imageUrl = "";
    String videoUrl = ""; // Variable for video URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailVideo = findViewById(R.id.detailVideo); // Ensure you have a PlayerView in your layout
        deleteDataImage = findViewById(R.id.deleteData);
        shareDataImage = findViewById(R.id.shareData);
        editDataImage = findViewById(R.id.editData);
        backIcon = findViewById(R.id.back);

        ratingBar = findViewById(R.id.ratingBar);
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
            imageUrl = bundle.getString("Image"); // Get image URL
            videoUrl = bundle.getString("Video"); // Get video URL

            if (videoUrl != null && !videoUrl.isEmpty()) {
                setupPlayer(Uri.parse(videoUrl)); // Setup player
            }
        }

        deleteDataImage.setOnClickListener(view -> showDeleteConfirmationDialog());
        editDataImage.setOnClickListener(view -> openUpdateActivity());
        shareDataImage.setOnClickListener(view -> shareRecipe());

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DetailActivity.this,MainActivity.class));
                finish();
            }
        });
    }

    private void setupPlayer(Uri videoUri) {
        player = new ExoPlayer.Builder(this).build();
        detailVideo.setPlayer(player);

        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        player.setMediaItem(mediaItem);
        player.prepare();
    }

    private void showDeleteConfirmationDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        TextView yesButton = dialogView.findViewById(R.id.button_yes);
        TextView noButton = dialogView.findViewById(R.id.button_no);

        yesButton.setOnClickListener(v -> {
            deleteRecipe();
            dialog.dismiss();
        });

        noButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void deleteRecipe() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Recipes");
        FirebaseStorage storage = FirebaseStorage.getInstance();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
            storageReference.delete().addOnSuccessListener(unused -> {
                reference.child(key).removeValue().addOnSuccessListener(unused1 -> {
                    if (videoUrl != null && !videoUrl.isEmpty()) {
                        deleteVideoFromStorage(videoUrl);
                    } else {
                        showToast("Deleted Successfully!");
                        navigateToMainActivity();
                    }
                }).addOnFailureListener(e -> handleDeleteFailure(e, "recipe from database"));
            }).addOnFailureListener(e -> handleDeleteFailure(e, "image from storage"));
        } else {
            reference.child(key).removeValue().addOnSuccessListener(unused -> {
                showToast("Deleted Successfully without image!");
                navigateToMainActivity();
            }).addOnFailureListener(e -> handleDeleteFailure(e, "recipe from database"));
        }
    }

    private void deleteVideoFromStorage(String videoUrl) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference videoReference = storage.getReferenceFromUrl(videoUrl);
        videoReference.delete().addOnSuccessListener(aVoid -> {
            showToast("Deleted Successfully!");
            navigateToMainActivity();
        }).addOnFailureListener(e -> handleDeleteFailure(e, "video from storage"));
    }

    private void handleDeleteFailure(Exception e, String type) {
        showToast("Error deleting " + type + "!");
        Log.e("DetailActivity", "Deletion error: " + e.getMessage());
    }

    private void openUpdateActivity() {
        Intent intent = new Intent(DetailActivity.this, UpdateActivity.class);
        intent.putExtra("Name", detailName.getText().toString());
        intent.putExtra("Time", detailTime.getText().toString());
        intent.putExtra("Category", detailCategory.getText().toString());
        intent.putExtra("Ingredients", detailIngredients.getText().toString());
        intent.putExtra("Description", detailDesc.getText().toString());
        intent.putExtra("Image", imageUrl);
        intent.putExtra("Key", key);
        intent.putExtra("Video", videoUrl); // Pass video URL

        startActivity(intent);
        finish();
    }

    private void shareRecipe() {
        String recipeDetails = "Check out this recipe:\n\n" +
                "Name: " + detailName.getText().toString() + "\n\n" +
                "Time: " + detailTime.getText().toString() + "\n\n" +
                "Category: " + detailCategory.getText().toString() + "\n" +
                "Ingredients: " + detailIngredients.getText().toString() + "\n\n" +
                "Description: " + detailDesc.getText().toString() + "\n\n" +
                "Click here to view image: " + imageUrl + "\n" +
                "Watch video: " + videoUrl; // Optionally include video URL

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, recipeDetails);

        startActivity(Intent.createChooser(shareIntent, "Share Recipe via"));
    }

    private void navigateToMainActivity() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(DetailActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
