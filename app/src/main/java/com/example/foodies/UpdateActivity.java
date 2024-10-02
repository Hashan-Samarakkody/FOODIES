package com.example.foodies;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class UpdateActivity extends AppCompatActivity {

    ImageView updateImage, backIcon;
    Button updateButton, updateVideoButton;
    EditText updateName, updateCategory, updateTime, updateIngredients, updateDescription;
    String imageUrl, videoUrl;
    String key, oldImageURL, oldVideoURL;
    Uri newImageUri, newVideoUri;
    DatabaseReference databaseReference; // Declare databaseReference here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        // Initialize UI components
        updateImage = findViewById(R.id.updateImage);
        backIcon = findViewById(R.id.back);
        updateName = findViewById(R.id.updateName);
        updateCategory = findViewById(R.id.updateCategory);
        updateTime = findViewById(R.id.updateTime);
        updateIngredients = findViewById(R.id.updateIngredients);
        updateDescription = findViewById(R.id.updateDescription);
        updateButton = findViewById(R.id.updateButton);
        updateVideoButton = findViewById(R.id.updateVideo);

        // Load existing data
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Glide.with(UpdateActivity.this).load(bundle.getString("Image")).into(updateImage);
            updateName.setText(bundle.getString("Name"));
            updateCategory.setText(bundle.getString("Category"));
            updateTime.setText(bundle.getString("Time"));
            updateIngredients.setText(bundle.getString("Ingredients"));
            updateDescription.setText(bundle.getString("Description"));
            key = bundle.getString("Key");
            oldImageURL = bundle.getString("Image");
            oldVideoURL = bundle.getString("Video");
        }

        // Initialize database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Recipes").child(key);

        // Image selection
        ActivityResultLauncher<Intent> imageActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            newImageUri = data != null ? data.getData() : null;
                            updateImage.setImageURI(newImageUri);
                        } else {
                            Toast.makeText(UpdateActivity.this, "No Image Selected!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Video selection
        ActivityResultLauncher<Intent> videoActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            newVideoUri = data != null ? data.getData() : null;
                            Toast.makeText(UpdateActivity.this, "Video selected!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UpdateActivity.this, "No Video Selected!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Set up listeners
        updateImage.setOnClickListener(view -> {
            Intent photoPicker = new Intent(Intent.ACTION_PICK);
            photoPicker.setType("image/*");
            imageActivityResultLauncher.launch(photoPicker);
        });

        updateVideoButton.setOnClickListener(view -> {
            Intent videoPicker = new Intent(Intent.ACTION_PICK);
            videoPicker.setType("video/*");
            videoActivityResultLauncher.launch(videoPicker);
        });

        updateButton.setOnClickListener(view -> saveData());

        backIcon.setOnClickListener(view -> finish());
    }

    public void saveData() {
        if (newImageUri == null && oldImageURL == null) {
            Toast.makeText(this, "No image selected!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (updateName.getText().toString().isEmpty() ||
                updateCategory.getText().toString().isEmpty() ||
                updateTime.getText().toString().isEmpty() ||
                updateIngredients.getText().toString().isEmpty() ||
                updateDescription.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        String imageFileName = "image_" + System.currentTimeMillis() + "_" + (newImageUri != null ? Objects.requireNonNull(newImageUri.getLastPathSegment()) : "");
        String videoFileName = "video_" + System.currentTimeMillis() + "_" + (newVideoUri != null ? Objects.requireNonNull(newVideoUri.getLastPathSegment()) : "");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Upload image
        if (newImageUri != null) {
            StorageReference imageRef = storageReference.child("Android Images").child(imageFileName);
            imageRef.putFile(newImageUri).addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    imageUrl = uri.toString();
                    uploadVideo(dialog, storageReference, videoFileName);
                }).addOnFailureListener(e -> {
                    Toast.makeText(UpdateActivity.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(UpdateActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        } else {
            imageUrl = oldImageURL; // Keep the old image URL if no new image
            uploadVideo(dialog, storageReference, videoFileName);
        }
    }

    private void uploadVideo(AlertDialog dialog, StorageReference storageReference, String videoFileName) {
        if (newVideoUri != null) {
            StorageReference videoRef = storageReference.child("Android Videos").child(videoFileName);
            videoRef.putFile(newVideoUri).addOnSuccessListener(taskSnapshot -> {
                videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    videoUrl = uri.toString();
                    updateData(dialog);
                }).addOnFailureListener(e -> {
                    Toast.makeText(UpdateActivity.this, "Failed to get video URL", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(UpdateActivity.this, "Failed to upload video: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        } else {
            videoUrl = oldVideoURL; // Keep the old video URL if no new video
            updateData(dialog);
        }
    }

    public void updateData(AlertDialog dialog) {
        String name = updateName.getText().toString().trim();
        String category = updateCategory.getText().toString();
        String time = updateTime.getText().toString();
        String ingredients = updateIngredients.getText().toString();
        String description = updateDescription.getText().toString();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DataClass dataClass = new DataClass(name, category, time, ingredients, description, imageUrl, videoUrl, currentUser.getUid());

        // Validate the data using DataClass methods
        if (!dataClass.isValidTime(time)) {
            Toast.makeText(this, "Invalid Time Format!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        } else if (!dataClass.isValidName(name)) {
            Toast.makeText(this, "Invalid Name!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        } else if (!dataClass.isValidCategory(category)) {
            Toast.makeText(this, "Invalid Category!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }

        databaseReference.setValue(dataClass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(UpdateActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UpdateActivity.this,MainActivity.class));
            }
            dialog.dismiss();
        }).addOnFailureListener(e -> {
            Toast.makeText(UpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }
}
