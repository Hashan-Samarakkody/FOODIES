package com.example.foodies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import com.bumptech.glide.Glide;


public class OthersRecipeDetailActivity extends AppCompatActivity {

    PlayerView detailVideo;
    ExoPlayer player;
    TextView detailName, detailTime, detailCategory, detailIngredients, detailDesc;
    ImageView shareDataImage, backIcon;
    String key = "";
    String imageUrl = "";
    String videoUrl = ""; // Variable for video URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_recipe_detail);

        detailVideo = findViewById(R.id.detailVideo);
        shareDataImage = findViewById(R.id.shareData);
        backIcon = findViewById(R.id.back);

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
            videoUrl = bundle.getString("Video"); // Get video URL


            if (videoUrl != null && !videoUrl.isEmpty()) {
                setupPlayer(Uri.parse(videoUrl)); // Setup player
            }
        }

        shareDataImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareRecipe();
            }
        });

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OthersRecipeDetailActivity.this, ViewOthersRecipeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupPlayer(Uri videoUri) {
        player = new ExoPlayer.Builder(this).build();
        detailVideo.setPlayer(player);

        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play(); // Start playback
    }

    private void shareRecipe() {
        String recipeDetails = "Check out this recipe:\n\n" +
                "Name: " + detailName.getText().toString() + "\n\n" +
                "Time: " + detailTime.getText().toString() + "\n\n" +
                "Category: " + detailCategory.getText().toString() + "\n" +
                "Ingredients: " + detailIngredients.getText().toString() + "\n\n" +
                "Description: " + detailDesc.getText().toString() + "\n\n" +
                "Click here to view image: " + imageUrl; // Optionally include image URL

        if (videoUrl != null && !videoUrl.isEmpty()) {
            recipeDetails += "\nWatch video: " + videoUrl; // Optionally include video URL
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, recipeDetails);

        startActivity(Intent.createChooser(shareIntent, "Share Recipe via"));
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
