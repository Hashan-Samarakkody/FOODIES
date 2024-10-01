package com.example.foodies;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailActivity extends AppCompatActivity {

    RatingBar ratingBar;
    TextView detailName,detailTime,detailCategory,detailIngredients,detailDesc;
    ImageView detailImage,deleteDataImage,editDataImage,shareDataImage,backIcon;
    String key = "";
    String  imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailImage = findViewById(R.id.detailImage);
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

        deleteDataImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.popup_layout, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                TextView yesButton = dialogView.findViewById(R.id.button_yes);
                TextView noButton = dialogView.findViewById(R.id.button_no);

                yesButton.setOnClickListener(v -> {
                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Recipes");
                    FirebaseStorage storage = FirebaseStorage.getInstance();

                    // Log the current image URL
                    Log.d("DetailActivity", "Image URL: " + imageUrl);

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);

                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                reference.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(DetailActivity.this, "Deleted Successfully!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(DetailActivity.this, "Error deleting recipe from database!", Toast.LENGTH_SHORT).show();
                                        Log.e("DetailActivity", "Database deletion error: " + e.getMessage());
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DetailActivity.this, "Error deleting image from storage!", Toast.LENGTH_SHORT).show();
                                Log.e("DetailActivity", "Storage deletion error: " + e.getMessage());
                            }
                        });
                    } else {
                        // Handle case where image URL is empty
                        reference.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(DetailActivity.this, "Deleted Successfully without image!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DetailActivity.this, "Error deleting recipe from database!", Toast.LENGTH_SHORT).show();
                                Log.e("DetailActivity", "Database deletion error: " + e.getMessage());
                            }
                        });
                    }
                    dialog.dismiss();
                });

                noButton.setOnClickListener(v -> dialog.dismiss());
                dialog.show();
            }
        });

        editDataImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this,UpdateActivity.class);
                intent.putExtra("Name",detailName.getText().toString());
                intent.putExtra("Time",detailTime.getText().toString());
                intent.putExtra("Category",detailCategory.getText().toString());
                intent.putExtra("Ingredients",detailIngredients.getText().toString());
                intent.putExtra("Description",detailDesc.getText().toString());
                intent.putExtra("Image",imageUrl);
                intent.putExtra("Key",key);

                startActivity(intent);
            }
        });

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
                Intent intent = new Intent(DetailActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });



    }
}