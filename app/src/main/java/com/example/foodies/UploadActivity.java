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

    ImageView uploadImage, backIcon; // ImageView for selecting the image and back button IM/2022/120
    Button save_button, uploadVideoButton; // Buttons for saving the recipe and uploading a video IM/2022/120
    EditText uploadName, uploadCategory, uploadTime, uploadIngredients, uploadDescription; // Input fields for recipe details IM/2022/120
    String imageURL, videoURL; // URLs for the uploaded image and video IM/2022/120
    Uri imageUri, videoUri; // URIs for the selected image and video IM/2022/120
    AlertDialog dialog; // Progress dialog for uploads IM/2022/120

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload); // Sets the layout for the upload activity IM/2022/120

        // Initialize UI components IM/2022/120
        uploadImage = findViewById(R.id.uploadImage); // Links the ImageView for image upload IM/2022/120
        backIcon = findViewById(R.id.back); // Links the back icon ImageView IM/2022/120
        uploadName = findViewById(R.id.uploadName); // Links the EditText for recipe name IM/2022/120
        uploadCategory = findViewById(R.id.uploadCategory); // Links the EditText for recipe category IM/2022/120
        uploadTime = findViewById(R.id.uploadTime); // Links the EditText for recipe time IM/2022/120
        uploadIngredients = findViewById(R.id.uploadIngredients); // Links the EditText for ingredients IM/2022/120
        uploadDescription = findViewById(R.id.uploadDescription); // Links the EditText for description IM/2022/120
        save_button = findViewById(R.id.saveButton); // Links the button for saving the recipe IM/2022/120
        uploadVideoButton = findViewById(R.id.uploadVideo); // Links the button for uploading a video IM/2022/120

        // Initialize the progress dialog IM/2022/120
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this); // Creates a dialog builder IM/2022/120
        builder.setCancelable(false); // Sets the dialog to be non-cancelable IM/2022/120
        builder.setView(R.layout.progress_layout); // Sets the layout for the dialog IM/2022/120
        dialog = builder.create(); // Creates the dialog IM/2022/120

        // Image selection logic using activity result launcher IM/2022/120
        ActivityResultLauncher<Intent> imageActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) { // Checks if the result is OK IM/2022/120
                        Intent data = result.getData(); // Retrieves the data from the result IM/2022/120
                        assert data != null; // Asserts that data is not null IM/2022/120
                        imageUri = data.getData(); // Gets the image URI IM/2022/120
                        uploadImage.setImageURI(imageUri); // Updates the ImageView with the selected image IM/2022/120
                    } else {
                        Toast.makeText(UploadActivity.this, "No Image Selected!", Toast.LENGTH_SHORT).show(); // Displays a toast if no image is selected IM/2022/120
                    }
                }
        );

        // Video selection logic using activity result launcher IM/2022/120
        ActivityResultLauncher<Intent> videoActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) { // Checks if the result is OK IM/2022/120
                        Intent data = result.getData(); // Retrieves the data from the result IM/2022/120
                        assert data != null; // Asserts that data is not null IM/2022/120
                        videoUri = data.getData(); // Gets the video URI IM/2022/120
                        Toast.makeText(UploadActivity.this, "Video selected!", Toast.LENGTH_SHORT).show(); // Displays a toast confirming video selection IM/2022/120
                    } else {
                        Toast.makeText(UploadActivity.this, "No Video Selected!", Toast.LENGTH_SHORT).show(); // Displays a toast if no video is selected IM/2022/120
                    }
                }
        );

        // Set up listeners for image and video selection, and saving data IM/2022/120
        uploadImage.setOnClickListener(view -> { // Listener for selecting an image IM/2022/120
            Intent photoPicker = new Intent(Intent.ACTION_PICK); // Creates an intent for picking an image IM/2022/120
            photoPicker.setType("image/*"); // Sets the type to image IM/2022/120
            imageActivityResultLauncher.launch(photoPicker); // Launches the image picker activity IM/2022/120
        });

        uploadVideoButton.setOnClickListener(view -> { // Listener for selecting a video IM/2022/120
            Intent videoPicker = new Intent(Intent.ACTION_PICK); // Creates an intent for picking a video IM/2022/120
            videoPicker.setType("video/*"); // Sets the type to video IM/2022/120
            videoActivityResultLauncher.launch(videoPicker); // Launches the video picker activity IM/2022/120
        });

        save_button.setOnClickListener(view -> saveData()); // Listener for saving data IM/2022/120

        backIcon.setOnClickListener(view -> { // Listener for back icon click IM/2022/120
            startActivity(new Intent(UploadActivity.this, MainActivity.class)); // Starts the MainActivity IM/2022/120
            finish(); // Closes the current activity IM/2022/120
        });
    }

    // Method to validate and save data IM/2022/120
    public void saveData() {
        if (imageUri == null) { // Checks if an image is selected IM/2022/120
            Toast.makeText(this, "No image selected!", Toast.LENGTH_SHORT).show(); // Displays a toast IM/2022/120
            return; // Exits the method IM/2022/120
        }

        if (videoUri == null) { // Checks if a video is selected IM/2022/120
            Toast.makeText(this, "No video selected!", Toast.LENGTH_SHORT).show(); // Displays a toast IM/2022/120
            return; // Exits the method IM/2022/120
        }

        // Validate input fields IM/2022/120
        if (uploadName.getText().toString().isEmpty() ||
                uploadCategory.getText().toString().isEmpty() ||
                uploadTime.getText().toString().isEmpty() ||
                uploadIngredients.getText().toString().isEmpty() ||
                uploadDescription.getText().toString().isEmpty()) { // Checks if any field is empty IM/2022/120
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show(); // Displays a toast for empty fields IM/2022/120
            return; // Exits the method IM/2022/120
        }

        // Generate unique filenames for image and video IM/2022/120
        String imageFileName = "image_" + System.currentTimeMillis() + "_" + Objects.requireNonNull(imageUri.getLastPathSegment()); // Generates a unique filename for the image IM/2022/120
        String videoFileName = "video_" + System.currentTimeMillis() + "_" + Objects.requireNonNull(videoUri.getLastPathSegment()); // Generates a unique filename for the video IM/2022/120
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(); // Gets the storage reference IM/2022/120

        dialog.show(); // Show the progress dialog before starting uploads IM/2022/120

        // Upload image to Firebase Storage IM/2022/120
        StorageReference imageRef = storageReference.child("Android Images").child(imageFileName); // Creates a reference for the image IM/2022/120
        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> { // Uploads the image and gets the download URL
            imageURL = uri.toString(); // Gets the download URL for the image IM/2022/120

            // Upload video to Firebase Storage IM/2022/120
            StorageReference videoRef = storageReference.child("Android Videos").child(videoFileName); // Creates a reference for the video IM/2022/120
            videoRef.putFile(videoUri).addOnSuccessListener(taskSnapshot1 -> videoRef.getDownloadUrl().addOnSuccessListener(uri1 -> { // Uploads the video and gets the download URL
                videoURL = uri1.toString(); // Gets the download URL for the video IM/2022/120
                uploadData(); // Calls method to upload data to the database IM/2022/120
            })).addOnFailureListener(e -> {
                Toast.makeText(UploadActivity.this, "Failed to upload video: " + e.getMessage(), Toast.LENGTH_SHORT).show(); // Displays a toast for video upload failure IM/2022/120
                dialog.dismiss(); // Dismisses the progress dialog IM/2022/120
            });
        })).addOnFailureListener(e -> {
            Toast.makeText(UploadActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show(); // Displays a toast for image upload failure IM/2022/120
            dialog.dismiss(); // Dismisses the progress dialog IM/2022/120
        });
    }

    // Method to upload recipe data to Firebase IM/2022/120
    public void uploadData() {
        // Retrieve input data from fields IM/2022/120
        String name = uploadName.getText().toString(); // Gets the recipe name IM/2022/120
        String category = uploadCategory.getText().toString(); // Gets the recipe category IM/2022/120
        String time = uploadTime.getText().toString(); // Gets the recipe time IM/2022/120
        String ingredients = uploadIngredients.getText().toString(); // Gets the ingredients IM/2022/120
        String description = uploadDescription.getText().toString(); // Gets the recipe description IM/2022/120

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser(); // Gets the current user IM/2022/120
        DataClass dataClass = new DataClass(name, category, time, ingredients, description, imageURL, videoURL, currentUser.getUid()); // Creates a DataClass instance with the new data IM/2022/120

        // Validate the data using DataClass methods IM/2022/120
        if (!dataClass.isValidTime(time)) { // Checks if the time format is valid IM/2022/120
            Toast.makeText(this, "Invalid Time Format!", Toast.LENGTH_SHORT).show(); // Displays a toast for invalid time format IM/2022/120
            dialog.dismiss(); // Dismisses the progress dialog IM/2022/120
            return; // Exits the method after dismissing IM/2022/120
        } else if (!dataClass.isValidName(name)) { // Checks if the name is valid IM/2022/120
            Toast.makeText(this, "Invalid Name!", Toast.LENGTH_SHORT).show(); // Displays a toast for invalid name IM/2022/120
            dialog.dismiss(); // Dismisses the progress dialog IM/2022/120
            return; // Exits the method after dismissing IM/2022/120
        } else if (!dataClass.isValidCategory(category)) { // Checks if the category is valid IM/2022/120
            Toast.makeText(this, "Invalid Category!", Toast.LENGTH_SHORT).show(); // Displays a toast for invalid category IM/2022/120
            dialog.dismiss(); // Dismisses the progress dialog IM/2022/120
            return; // Exits the method after dismissing IM/2022/120
        }

        // If all validations pass, proceed to upload the data to Firebase IM/2022/120
        String recipeId = FirebaseDatabase.getInstance().getReference("Recipes").push().getKey(); // Creates a unique ID for the recipe IM/2022/120
        FirebaseDatabase.getInstance().getReference("Recipes").child(recipeId).setValue(dataClass)
                .addOnCompleteListener(task -> {
                    dialog.dismiss(); // Dismisses the progress dialog after upload completes IM/2022/120
                    if (task.isSuccessful()) { // Checks if the upload was successful IM/2022/120
                        Toast.makeText(UploadActivity.this, "Saved", Toast.LENGTH_SHORT).show(); // Displays a success message IM/2022/120
                        startActivity(new Intent(UploadActivity.this, MainActivity.class)); // Navigates back to the MainActivity IM/2022/120
                        finish(); // Closes the current activity IM/2022/120
                    }
                }).addOnFailureListener(e -> {
                    dialog.dismiss(); // Dismisses the dialog on failure IM/2022/120
                    Toast.makeText(UploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show(); // Displays an error message IM/2022/120
                });
    }
}
