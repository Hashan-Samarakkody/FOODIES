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
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class UpdateActivity extends AppCompatActivity {

    // Declare UI components and variables for data storage IM/2022/120
    ImageView updateImage, backIcon; // ImageView for displaying the updated image and back button IM/2022/120
    Button updateButton, updateVideoButton; // Buttons for updating the recipe and selecting video IM/2022/120
    EditText updateName, updateCategory, updateTime, updateIngredients, updateDescription; // Input fields for recipe details IM/2022/120
    String imageUrl, videoUrl; // URLs for the uploaded image and video IM/2022/120
    String key, oldImageURL, oldVideoURL; // Key for database reference and old URLs IM/2022/120
    Uri newImageUri, newVideoUri; // URIs for the newly selected image and video IM/2022/120
    DatabaseReference databaseReference; // Reference to the Firebase database IM/2022/120

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Calls the superclass constructor IM/2022/120
        setContentView(R.layout.activity_update); // Sets the layout for the update activity IM/2022/120

        // Initialize UI components IM/2022/120
        updateImage = findViewById(R.id.updateImage); // Links the ImageView for image update IM/2022/120
        backIcon = findViewById(R.id.back); // Links the back icon ImageView IM/2022/120
        updateName = findViewById(R.id.updateName); // Links the EditText for recipe name IM/2022/120
        updateCategory = findViewById(R.id.updateCategory); // Links the EditText for recipe category IM/2022/120
        updateTime = findViewById(R.id.updateTime); // Links the EditText for recipe time IM/2022/120
        updateIngredients = findViewById(R.id.updateIngredients); // Links the EditText for ingredients IM/2022/120
        updateDescription = findViewById(R.id.updateDescription); // Links the EditText for description IM/2022/120
        updateButton = findViewById(R.id.updateButton); // Links the button for updating the recipe IM/2022/120
        updateVideoButton = findViewById(R.id.updateVideo); // Links the button for selecting a video IM/2022/120

        // Load existing data from Intent IM/2022/120
        Bundle bundle = getIntent().getExtras(); // Retrieves the extras from the intent IM/2022/120
        if (bundle != null) { // Checks if the bundle is not null IM/2022/120
            Glide.with(UpdateActivity.this).load(bundle.getString("Image")).into(updateImage); // Loads the existing image using Glide IM/2022/120
            updateName.setText(bundle.getString("Name")); // Sets the existing name in the EditText IM/2022/120
            updateCategory.setText(bundle.getString("Category")); // Sets the existing category IM/2022/120
            updateTime.setText(bundle.getString("Time")); // Sets the existing time IM/2022/120
            updateIngredients.setText(bundle.getString("Ingredients")); // Sets the existing ingredients IM/2022/120
            updateDescription.setText(bundle.getString("Description")); // Sets the existing description IM/2022/120
            key = bundle.getString("Key"); // Retrieves the key for database reference IM/2022/120
            oldImageURL = bundle.getString("Image"); // Retrieves the old image URL IM/2022/120
            oldVideoURL = bundle.getString("Video"); // Retrieves the old video URL IM/2022/120
        }

        // Initialize Firebase database reference IM/2022/120
        databaseReference = FirebaseDatabase.getInstance().getReference("Recipes").child(key); // Sets the database reference to the specific recipe IM/2022/120

        // Image selection logic using activity result launcher IM/2022/120
        ActivityResultLauncher<Intent> imageActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) { // Checks if the result is OK IM/2022/120
                        Intent data = result.getData(); // Retrieves the data from the result IM/2022/120
                        newImageUri = data != null ? data.getData() : null; // Gets the image URI IM/2022/120
                        updateImage.setImageURI(newImageUri); // Updates the ImageView with the new image IM/2022/120
                    } else {
                        Toast.makeText(UpdateActivity.this, "No Image Selected!", Toast.LENGTH_SHORT).show(); // Displays a toast if no image is selected IM/2022/120
                    }
                }
        );

        // Video selection logic using activity result launcher IM/2022/120
        ActivityResultLauncher<Intent> videoActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) { // Checks if the result is OK IM/2022/120
                        Intent data = result.getData(); // Retrieves the data from the result IM/2022/120
                        newVideoUri = data != null ? data.getData() : null; // Gets the video URI IM/2022/120
                        Toast.makeText(UpdateActivity.this, "Video selected!", Toast.LENGTH_SHORT).show(); // Displays a toast confirming video selection IM/2022/120
                    } else {
                        Toast.makeText(UpdateActivity.this, "No Video Selected!", Toast.LENGTH_SHORT).show(); // Displays a toast if no video is selected IM/2022/120
                    }
                }
        );

        // Set up listeners for selecting images and videos, and updating data IM/2022/120
        updateImage.setOnClickListener(view -> { // Listener for updating the image IM/2022/120
            Intent photoPicker = new Intent(Intent.ACTION_PICK); // Creates an intent for picking an image IM/2022/120
            photoPicker.setType("image/*"); // Sets the type to image IM/2022/120
            imageActivityResultLauncher.launch(photoPicker); // Launches the image picker activity IM/2022/120
        });

        updateVideoButton.setOnClickListener(view -> { // Listener for updating the video IM/2022/120
            Intent videoPicker = new Intent(Intent.ACTION_PICK); // Creates an intent for picking a video IM/2022/120
            videoPicker.setType("video/*"); // Sets the type to video IM/2022/120
            videoActivityResultLauncher.launch(videoPicker); // Launches the video picker activity IM/2022/120
        });

        updateButton.setOnClickListener(view -> saveData()); // Listener for updating data IM/2022/120

        backIcon.setOnClickListener(view -> { // Listener for back icon click IM/2022/120
            startActivity(new Intent(UpdateActivity.this, DetailActivity.class)); // Starts the DetailActivity IM/2022/120
            finish(); // Closes the current activity IM/2022/120
        });
    }

    // Method to validate and save the updated data IM/2022/120
    public void saveData() {
        // Check if an image is selected IM/2022/120
        if (newImageUri == null && oldImageURL == null) { // Checks if no new or old image is available IM/2022/120
            Toast.makeText(this, "No image selected!", Toast.LENGTH_SHORT).show(); // Displays a toast IM/2022/120
            return; // Exits the method IM/2022/120
        }

        // Validate input fields IM/2022/120
        if (updateName.getText().toString().isEmpty() || // Checks if name is empty IM/2022/120
                updateCategory.getText().toString().isEmpty() || // Checks if category is empty IM/2022/120
                updateTime.getText().toString().isEmpty() || // Checks if time is empty IM/2022/120
                updateIngredients.getText().toString().isEmpty() || // Checks if ingredients are empty IM/2022/120
                updateDescription.getText().toString().isEmpty()) { // Checks if description is empty IM/2022/120
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show(); // Displays a toast for empty fields IM/2022/120
            return; // Exits the method IM/2022/120
        }

        // Generate unique filenames for image and video IM/2022/120
        String imageFileName = "image_" + System.currentTimeMillis() + "_" + (newImageUri != null ? Objects.requireNonNull(newImageUri.getLastPathSegment()) : ""); // Generates a unique filename for the image IM/2022/120
        String videoFileName = "video_" + System.currentTimeMillis() + "_" + (newVideoUri != null ? Objects.requireNonNull(newVideoUri.getLastPathSegment()) : ""); // Generates a unique filename for the video IM/2022/120
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(); // Gets the storage reference IM/2022/120

        // Show progress dialog while uploading IM/2022/120
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this); // Creates a dialog builder IM/2022/120
        builder.setCancelable(false); // Sets the dialog to be non-cancelable IM/2022/120
        builder.setView(R.layout.progress_layout); // Sets the layout for the dialog IM/2022/120
        AlertDialog dialog = builder.create(); // Creates the dialog IM/2022/120
        dialog.show(); // Shows the dialog IM/2022/120

        // Upload image if selected IM/2022/120
        if (newImageUri != null) { // Checks if a new image is selected IM/2022/120
            StorageReference imageRef = storageReference.child("Android Images").child(imageFileName); // Creates a reference for the image IM/2022/120
            imageRef.putFile(newImageUri).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> { // Uploads the image and gets the download URL
                imageUrl = uri.toString(); // Gets the download URL IM/2022/120
                uploadVideo(dialog, storageReference, videoFileName); // Proceeds to upload video IM/2022/120
            }).addOnFailureListener(e -> {
                Toast.makeText(UpdateActivity.this, "Failed to get image URL", Toast.LENGTH_SHORT).show(); // Displays a toast for failure to get URL IM/2022/120
                dialog.dismiss(); // Dismisses the dialog IM/2022/120
            })).addOnFailureListener(e -> {
                Toast.makeText(UpdateActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show(); // Displays a toast for failure to upload image IM/2022/120
                dialog.dismiss(); // Dismisses the dialog IM/2022/120
            });
        } else {
            imageUrl = oldImageURL; // Keeps the old image URL if no new image IM/2022/120
            uploadVideo(dialog, storageReference, videoFileName); // Proceeds to upload video IM/2022/120
        }
    }

    // Method to upload video to Firebase Storage IM/2022/120
    private void uploadVideo(AlertDialog dialog, StorageReference storageReference, String videoFileName) {
        if (newVideoUri != null) { // Checks if a new video is selected IM/2022/120
            StorageReference videoRef = storageReference.child("Android Videos").child(videoFileName); // Creates a reference for the video IM/2022/120
            videoRef.putFile(newVideoUri).addOnSuccessListener(taskSnapshot -> videoRef.getDownloadUrl().addOnSuccessListener(uri -> { // Uploads the video and gets the download URL
                videoUrl = uri.toString(); // Gets the download URL IM/2022/120
                updateData(dialog); // Proceeds to update the database with the new data IM/2022/120
                dialog.dismiss(); // Dismisses the dialog IM/2022/120
            }).addOnFailureListener(e -> {
                Toast.makeText(UpdateActivity.this, "Failed to get video URL", Toast.LENGTH_SHORT).show(); // Displays a toast for failure to get URL IM/2022/120
                dialog.dismiss(); // Dismisses the dialog IM/2022/120
            })).addOnFailureListener(e -> {
                Toast.makeText(UpdateActivity.this, "Failed to upload video: " + e.getMessage(), Toast.LENGTH_SHORT).show(); // Displays a toast for failure to upload video IM/2022/120
                dialog.dismiss(); // Dismisses the dialog IM/2022/120
            });
        } else {
            videoUrl = oldVideoURL; // Keeps the old video URL if no new video IM/2022/120
            updateData(dialog); // Proceeds to update the database IM/2022/120
        }
    }

    // Method to update recipe data in Firebase IM/2022/120
    public void updateData(AlertDialog dialog) {
        String name = updateName.getText().toString().trim(); // Retrieves and trims the recipe name IM/2022/120
        String category = updateCategory.getText().toString(); // Retrieves the category IM/2022/120
        String time = updateTime.getText().toString(); // Retrieves the time IM/2022/120
        String ingredients = updateIngredients.getText().toString(); // Retrieves the ingredients IM/2022/120
        String description = updateDescription.getText().toString(); // Retrieves the description IM/2022/120

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser(); // Gets the current user IM/2022/120
        DataClass dataClass = new DataClass(name, category, time, ingredients, description, imageUrl, videoUrl, currentUser.getUid()); // Creates a DataClass instance with the new data IM/2022/120

        // Validate the data using DataClass methods IM/2022/120
        if (!dataClass.isValidTime(time)) { // Checks if the time format is valid IM/2022/120
            Toast.makeText(this, "Invalid Time Format!", Toast.LENGTH_SHORT).show(); // Displays a toast for invalid time IM/2022/120
            dialog.dismiss(); // Dismisses the dialog IM/2022/120
        } else if (!dataClass.isValidName(name)) { // Checks if the name is valid IM/2022/120
            Toast.makeText(this, "Invalid Name!", Toast.LENGTH_SHORT).show(); // Displays a toast for invalid name IM/2022/120
            dialog.dismiss(); // Dismisses the dialog IM/2022/120
        } else if (!dataClass.isValidCategory(category)) { // Checks if the category is valid IM/2022/120
            Toast.makeText(this, "Invalid Category", Toast.LENGTH_SHORT).show(); // Displays a toast for invalid category IM/2022/120
            dialog.dismiss(); // Dismisses the dialog IM/2022/120
        } else {
            // Update the database IM/2022/120
            databaseReference.setValue(dataClass).addOnCompleteListener(task -> { // Updates the database with the new data IM/2022/120
                dialog.dismiss(); // Dismisses the dialog IM/2022/120
                if (task.isSuccessful()) { // Checks if the update was successful IM/2022/120
                    Toast.makeText(UpdateActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show(); // Displays success message IM/2022/120
                    startActivity(new Intent(UpdateActivity.this, MainActivity.class)); // Navigates to MainActivity IM/2022/120
                    finish(); // Closes the current activity IM/2022/120
                } else {
                    Toast.makeText(UpdateActivity.this, "Update Failed", Toast.LENGTH_SHORT).show(); // Displays failure message IM/2022/120
                }
            });
        }
    }
}
