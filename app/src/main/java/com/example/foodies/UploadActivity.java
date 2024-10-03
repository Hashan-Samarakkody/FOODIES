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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.Objects;

public class UploadActivity extends AppCompatActivity {

    ImageView uploadImage, backIcon;
    Button save_button, uploadVideoButton;
    EditText uploadName, uploadCategory, uploadTime, uploadIngredients, uploadDescription;
    String imageURL, videoURL;
    Uri imageUri, videoUri;

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

        // Image selection
        ActivityResultLauncher<Intent> imageActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            assert data != null;
                            imageUri = data.getData();
                            uploadImage.setImageURI(imageUri);
                        } else {
                            Toast.makeText(UploadActivity.this, "No Image Selected!", Toast.LENGTH_SHORT).show();
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
                            assert data != null;
                            videoUri = data.getData();
                            Toast.makeText(UploadActivity.this, "Video selected!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UploadActivity.this, "No Video Selected!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Set up listeners
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                imageActivityResultLauncher.launch(photoPicker);
            }
        });

        uploadVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent videoPicker = new Intent(Intent.ACTION_PICK);
                videoPicker.setType("video/*");
                videoActivityResultLauncher.launch(videoPicker);
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UploadActivity.this, MainActivity.class));
                finish();
            }
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

        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Upload image
        StorageReference imageRef = storageReference.child("Android Images").child(imageFileName);
        imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageURL = uri.toString();

                        // Upload video
                        StorageReference videoRef = storageReference.child("Android Videos").child(videoFileName);
                        videoRef.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                videoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        videoURL = uri.toString();
                                        uploadData();
                                        dialog.dismiss();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UploadActivity.this, "Failed to upload video: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
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

        if (!dataClass.isValidTime(time)) {
            Toast.makeText(this, "Invalid Time Format!", Toast.LENGTH_SHORT).show();
        } else if (!dataClass.isValidName(name)) {
            Toast.makeText(this, "Invalid Name!", Toast.LENGTH_SHORT).show();
        } else if (!dataClass.isValidCategory(category)) {
            Toast.makeText(this, "Invalid Category", Toast.LENGTH_SHORT).show();
        } else {
            String recipeId = FirebaseDatabase.getInstance().getReference("Recipes").push().getKey();
            FirebaseDatabase.getInstance().getReference("Recipes").child(recipeId).setValue(dataClass)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(UploadActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
