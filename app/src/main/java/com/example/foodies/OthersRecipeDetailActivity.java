package com.example.foodies;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class OthersRecipeDetailActivity extends AppCompatActivity {

    TextView detailName,detailTime,detailCategory,detailIngredients,detailDesc;
    ImageView detailImage,shareDataImage,backIcon;
    String key = "";
    String  imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_recipe_detail);

        detailImage = findViewById(R.id.detailImage);
        shareDataImage = findViewById(R.id.shareData);
        backIcon = findViewById(R.id.back);

        detailName = findViewById(R.id.detailName);
        detailTime = findViewById(R.id.detailTime);
        detailCategory = findViewById(R.id.detailCategory);
        detailIngredients = findViewById(R.id.detailIngredients);
        detailDesc = findViewById(R.id.detailDesc);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            detailName.setText(bundle.getString("Name"));
            detailTime.setText(bundle.getString("Time"));
            detailCategory.setText(bundle.getString("Category"));
            detailIngredients.setText(bundle.getString("Ingredients"));
            detailDesc.setText(bundle.getString("Description"));
            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image");

            Glide.with(this).load(bundle.getString("Image")).into(detailImage);

        }

        shareDataImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String recipeDetails = "Check out this recipe:\n\n" +
                        "Name: " + detailName.getText().toString() + "\n\n" +
                        "Time: " + detailTime.getText().toString() + "\n\n" +
                        "Category: " + detailCategory.getText().toString() + "\n" +
                        "Ingredients: " + detailIngredients.getText().toString() + "\n\n" +
                        "Description: " + detailDesc.getText().toString() + "\n\n" +
                        "Click here to view image:"+ imageUrl; // Optionally include image URL

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, recipeDetails);


                startActivity(Intent.createChooser(shareIntent, "Share Recipe via"));
            }
        });

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OthersRecipeDetailActivity.this,ViewOthersRecipeActivity.class);
                startActivity(intent);
            }
        });

    }
}