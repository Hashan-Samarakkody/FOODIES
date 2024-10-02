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

public class UpdateActivity extends AppCompatActivity {

    ImageView updateImage, backIcon;
    Button updateButton, updateVideoButton;
    EditText updateName, updateCategory, updateTime, updateIngredients, updateDescription;
    String name, category, time, ingredients, description;
    String imageUrl, videoUrl;
    String key, oldImageURL, oldVideoURL;
    Uri newImageUri, newVideoUri, oldImageUri, oldVideoUri;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        updateButton = findViewById(R.id.updateButton);
        updateName = findViewById(R.id.updateName);
        updateCategory = findViewById(R.id.updateCategory);
        updateTime = findViewById(R.id.updateTime);
        updateIngredients = findViewById(R.id.updateIngredients);
        updateDescription = findViewById(R.id.updateDescription);
        updateImage = findViewById(R.id.updateImage);
        backIcon = findViewById(R.id.back);
        updateVideoButton = findViewById(R.id.updateVideo);

        ActivityResultLauncher<Intent> imageActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            newImageUri = data.getData();
                            updateImage.setImageURI(newImageUri);
                        } else {
                            Toast.makeText(UpdateActivity.this, "No Image Selected!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        ActivityResultLauncher<Intent> videoActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            newVideoUri = data.getData();
                            Toast.makeText(UpdateActivity.this, "Video selected!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UpdateActivity.this, "No Video Selected!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Glide.with(UpdateActivity.this).load(bundle.getString("Image")).into(updateImage);

            updateName.setText(bundle.getString("Name"));
            updateTime.setText(bundle.getString("Time"));
            updateCategory.setText(bundle.getString("Category"));
            updateIngredients.setText(bundle.getString("Ingredients"));
            updateDescription.setText(bundle.getString("Description"));
            key = bundle.getString("Key");
            oldImageURL = bundle.getString("Image");
            oldVideoURL = bundle.getString("Video");

            oldImageUri = Uri.parse(oldImageURL);
            oldVideoUri = Uri.parse(oldVideoURL);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Recipes").child(key);

        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                imageActivityResultLauncher.launch(photoPicker);
            }
        });

        updateVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent videoPicker = new Intent(Intent.ACTION_PICK);
                videoPicker.setType("video/*");
                videoActivityResultLauncher.launch(videoPicker);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void saveData() {
        Uri uploadImageUri = (newImageUri != null) ? newImageUri : oldImageUri;
        Uri uploadVideoUri = (newVideoUri != null) ? newVideoUri : oldVideoUri;

        if (uploadImageUri == null) {
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

        if (newImageUri != null) {
            String imageFileName = System.currentTimeMillis() + "_" + uploadImageUri.getLastPathSegment();
            storageReference = FirebaseStorage.getInstance().getReference("Android Images").child(imageFileName);

            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
            builder.setCancelable(false);
            builder.setView(R.layout.progress_layout);
            AlertDialog dialog = builder.create();
            dialog.show();

            storageReference.putFile(uploadImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageUrl = uri.toString();
                                    uploadVideo(dialog);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UpdateActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
        } else {
            imageUrl = oldImageURL;
            uploadVideo(null);
        }
    }

    private void uploadVideo(AlertDialog dialog) {
        if (newVideoUri != null) {
            String videoFileName = System.currentTimeMillis() + "_" + newVideoUri.getLastPathSegment();
            StorageReference videoStorageReference = FirebaseStorage.getInstance().getReference("Android Videos").child(videoFileName);

            videoStorageReference.putFile(newVideoUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            videoStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    videoUrl = uri.toString();
                                    updateData(dialog);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UpdateActivity.this, "Failed to get video URL", Toast.LENGTH_SHORT).show();
                                    if (dialog != null) dialog.dismiss();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            if (dialog != null) dialog.dismiss();
                        }
                    });
        } else {
            videoUrl = oldVideoURL;
            updateData(dialog);
        }
    }

    public void updateData(AlertDialog dialog) {
        name = updateName.getText().toString().trim();
        category = updateCategory.getText().toString();
        time = updateTime.getText().toString();
        ingredients = updateIngredients.getText().toString();
        description = updateDescription.getText().toString();

        if (name.isEmpty() || category.isEmpty() || time.isEmpty() || ingredients.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DataClass dataClass = new DataClass(name, category, time, ingredients, description, imageUrl, currentUser.getUid(), videoUrl);

        databaseReference.setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (newImageUri != null) {
                        StorageReference oldImageReference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL);
                        oldImageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(UpdateActivity.this, "Recipe updated and old image deleted", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(UpdateActivity.this, "Failed to delete old image", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    // Toast for video update
                    Toast.makeText(UpdateActivity.this, "Video updated successfully!", Toast.LENGTH_SHORT).show();

                    if (dialog != null) dialog.dismiss();
                    finish();
                } else {
                    Toast.makeText(UpdateActivity.this, "Failed to update recipe", Toast.LENGTH_SHORT).show();
                    if (dialog != null) dialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                if (dialog != null) dialog.dismiss();
            }
        });
    }
}
