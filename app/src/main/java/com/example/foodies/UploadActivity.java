package com.example.foodies;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class UploadActivity extends AppCompatActivity {

    ImageView uploadImage, backIcon;
    Button save_button, uploadVideoButton;
    EditText uploadName, uploadCategory, uploadTime, uploadIngredients, uploadDescription;
    String imageURL, videoURL;
    Uri imageUri, videoUri;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // Initialize UI components
        uploadImage = findViewById(R.id.uploadImage);
        backIcon = findViewById(R.id.back);
        uploadName = findViewById(R.id.uploadName);
        uploadCategory = findViewById(R.id.uploadCategory);
        uploadTime = findViewById(R.id.uploadTime);
        uploadIngredients = findViewById(R.id.uploadIngredients);
        uploadDescription = findViewById(R.id.uploadDescription);
        save_button = findViewById(R.id.saveButton);
        uploadVideoButton = findViewById(R.id.uploadVideo); // Add video upload button

        // Initialize the progress dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();

        // Image selection
        ActivityResultLauncher<Intent> imageActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        imageUri = data.getData();
                        uploadImage.setImageURI(imageUri);
                    } else {
                        Toast.makeText(UploadActivity.this, "No Image Selected!", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Video selection
        ActivityResultLauncher<Intent> videoActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        videoUri = data.getData();
                        Toast.makeText(UploadActivity.this, "Video selected!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UploadActivity.this, "No Video Selected!", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Set up listeners
        uploadImage.setOnClickListener(view -> {
            Intent photoPicker = new Intent(Intent.ACTION_PICK);
            photoPicker.setType("image/*");
            imageActivityResultLauncher.launch(photoPicker);
        });

        uploadVideoButton.setOnClickListener(view -> {
            Intent videoPicker = new Intent(Intent.ACTION_PICK);
            videoPicker.setType("video/*");
            videoActivityResultLauncher.launch(videoPicker);
        });

        save_button.setOnClickListener(view -> saveData());

        backIcon.setOnClickListener(view -> {
            startActivity(new Intent(UploadActivity.this, MainActivity.class));
            finish();
        });
    }

    public void saveData() {
        if (imageUri == null) {
            Toast.makeText(this, "No image selected!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (videoUri == null) {
            Toast.makeText(this, "No video selected!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (uploadName.getText().toString().isEmpty() ||
                uploadCategory.getText().toString().isEmpty() ||
                uploadTime.getText().toString().isEmpty() ||
                uploadIngredients.getText().toString().isEmpty() ||
                uploadDescription.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        String imageFileName = "image_" + System.currentTimeMillis() + "_" + Objects.requireNonNull(imageUri.getLastPathSegment());
        String videoFileName = "video_" + System.currentTimeMillis() + "_" + Objects.requireNonNull(videoUri.getLastPathSegment());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        dialog.show(); // Show the progress dialog before starting uploads

        // Upload image
        StorageReference imageRef = storageReference.child("Android Images").child(imageFileName);
        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            imageURL = uri.toString();

            // Upload video
            StorageReference videoRef = storageReference.child("Android Videos").child(videoFileName);
            videoRef.putFile(videoUri).addOnSuccessListener(taskSnapshot1 -> videoRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
                videoURL = uri1.toString();
                uploadData(); // Call uploadData without dialog parameter
            })).addOnFailureListener(e -> {
                Toast.makeText(UploadActivity.this, "Failed to upload video: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss(); // Dismiss on failure
            });
        })).addOnFailureListener(e -> {
            Toast.makeText(UploadActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            dialog.dismiss(); // Dismiss on failure
        });
    }

    public void uploadData() {
        String name = uploadName.getText().toString();
        String category = uploadCategory.getText().toString();
        String time = uploadTime.getText().toString();
        String ingredients = uploadIngredients.getText().toString();
        String description = uploadDescription.getText().toString();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DataClass dataClass = new DataClass(name, category, time, ingredients, description, imageURL, videoURL, currentUser.getUid());

        // Validate the data using DataClass methods
        if (!dataClass.isValidTime(time)) {
            Toast.makeText(this, "Invalid Time Format!", Toast.LENGTH_SHORT).show();
            dialog.dismiss(); // Dismiss the dialog here
            return;  // Exit the method after dismissing
        } else if (!dataClass.isValidName(name)) {
            Toast.makeText(this, "Invalid Name!", Toast.LENGTH_SHORT).show();
            dialog.dismiss(); // Dismiss the dialog here
            return;  // Exit the method after dismissing
        } else if (!dataClass.isValidCategory(category)) {
            Toast.makeText(this, "Invalid Category!", Toast.LENGTH_SHORT).show();
            dialog.dismiss(); // Dismiss the dialog here
            return;  // Exit the method after dismissing
        }

        // If all validations pass, proceed to upload the data
        String recipeId = FirebaseDatabase.getInstance().getReference("Recipes").push().getKey();
        FirebaseDatabase.getInstance().getReference("Recipes").child(recipeId).setValue(dataClass)
                .addOnCompleteListener(task -> {
                    dialog.dismiss(); // Dismiss the dialog here as well
                    if (task.isSuccessful()) {
                        Toast.makeText(UploadActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UploadActivity.this, MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(e -> {
                    dialog.dismiss(); // Dismiss the dialog on failure as well
                    Toast.makeText(UploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
