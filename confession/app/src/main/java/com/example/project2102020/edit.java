package com.example.project2102020;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;

public class edit extends AppCompatActivity {

    ImageView dp;
    EditText name, username, instagram;

    Uri selectedImage;
    Switch aSwitch;

    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]+$";

    // Function to validate the username
    private boolean isValidUsername(String username) {
        return username.matches(USERNAME_PATTERN);
    }
    private final int Gallery_Req_code = 100;
    AppCompatButton save,logOut;
    private final int UCrop_Result_code=123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        dp = findViewById(R.id.dpP);
        aSwitch = findViewById(R.id.instagramSwitch);
        name = findViewById(R.id.nameP);
        save = findViewById(R.id.save);
        logOut = findViewById(R.id.logOut);
        username = findViewById(R.id.userNameP);
        instagram = findViewById(R.id.profileUrl); // Assuming this is the correct view ID
        SharedPreferences pre = getSharedPreferences("project2102020", MODE_PRIVATE);
        String phone = pre.getString("uid", "");
        String name2 = pre.getString("name", "");
        String username2 = pre.getString("username", "");
        String instagram2 = pre.getString("instagram", "");
        String image = pre.getString("image", "");
        Boolean isShown = pre.getBoolean("shown", false);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        // Load the user's image if available
        if (image != null && !image.isEmpty()) {
            Glide.with(getApplicationContext())
                    .load(image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(dp);
        } else {
            // Set a default placeholder image or hide the ImageView
            dp.setImageResource(R.drawable.backuse); // Replace with your placeholder image resource
            // OR
            // dp.setVisibility(View.GONE); // Hide the ImageView
        }

        name.setText(name2);
        username.setText(username2);
        instagram.setText(instagram2);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name1 = name.getText().toString().trim();
                String username1 = username.getText().toString().trim();
                String instagram1 = instagram.getText().toString().trim();

                if (name1.isEmpty() || username1.isEmpty()) {
                    Toast.makeText(edit.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (!isValidUsername(username1)) {

                    Toast.makeText(edit.this, "Invalid username", Toast.LENGTH_SHORT).show();
                } else {

                    if (aSwitch.isChecked()){

                        reference.child("users").child(phone).child("shown").setValue(true);

                        // Denormalize data to the 'instagram' node
                        reference.child("instagram").child(phone).child("name").setValue(name1);
                        reference.child("instagram").child(phone).child("username").setValue(username1);
                        reference.child("instagram").child(phone).child("instagram").setValue(instagram1);
                    }

                    DatabaseReference userReference = reference.child("users").child(phone);
                    userReference.child("name").setValue(name1);
                    userReference.child("username").setValue(username1);

                    if (!instagram1.isEmpty()) {
                        userReference.child("instagram").setValue(instagram1);
                    } else {
                        userReference.child("instagram").removeValue();
                    }

                    if (selectedImage != null) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageReference = storage.getReference().child(phone);

                        storageReference.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        if (aSwitch.isChecked()){

                                            reference.child("users").child(phone).child("shown").setValue(true);

                                            // Denormalize data to the 'instagram' node
                                            reference.child("instagram").child(phone).child("name").setValue(name1);
                                            reference.child("instagram").child(phone).child("username").setValue(username1);
                                            reference.child("instagram").child(phone).child("image").setValue(uri.toString());
                                            reference.child("instagram").child(phone).child("instagram").setValue(instagram1);
                                        }
                                        userReference.child("imageUrl").setValue(uri.toString());
                                        Toast.makeText(edit.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    } else {
                        Toast.makeText(edit.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Set the switch based on the stored value
        aSwitch.setChecked(isShown);

        // Switch state change listener
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    // Check if the entered Instagram URL is valid
                    String instagramUrl = instagram.getText().toString().trim();
                    if (!instagramUrl.isEmpty() && isValidInstagramUrl(instagramUrl)) {
                        reference.child("users").child(phone).child("shown").setValue(true);

                        // Denormalize data to the 'instagram' node
                        reference.child("instagram").child(phone).child("name").setValue(name2);
                        reference.child("instagram").child(phone).child("username").setValue(username2);
                        reference.child("instagram").child(phone).child("image").setValue(image);
                        reference.child("instagram").child(phone).child("instagram").setValue(instagram2);
                    } else {
                        // Display a message to enter a valid Instagram address
                        Toast.makeText(edit.this, "Please enter a valid Instagram address", Toast.LENGTH_SHORT).show();
                        aSwitch.setChecked(false);
                    }
                } else {
                    reference.child("users").child(phone).child("shown").removeValue();
                    reference.child("instagram").child(phone).removeValue();
                }
            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("project2102020", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear(); // Clear all stored values
                editor.apply(); // Apply the changes
                Intent intent = new Intent(getApplicationContext(), googleSignIn.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, Gallery_Req_code);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Req_code && resultCode == Activity.RESULT_OK && data != null) {
            Uri croppedUri = data.getData();
            selectedImage=croppedUri;

            dp.setImageURI(selectedImage);
        }

    }
    // Function to validate Instagram URL format
    private boolean isValidInstagramUrl(String url) {
        // Define the regular expression pattern for valid Instagram URLs
        String pattern = "^https://(www\\.)?instagram\\.com/.*$";
        return url.matches(pattern);
    }

}
