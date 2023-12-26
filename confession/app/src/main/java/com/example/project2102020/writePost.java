package com.example.project2102020;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;

public class writePost extends AppCompatActivity {

    EditText post;
    ImageView gallery,pic;
    private final int Gallery_code=100;
    Uri selectedImage;
    AppCompatButton share;
    CardView card;
    Uri croppedUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);
        SharedPreferences pre=getSharedPreferences("project2102020", MODE_PRIVATE);
        String uid=pre.getString("uid","");
        gallery=findViewById(R.id.gallery);
        pic=findViewById(R.id.pic);
        post=findViewById(R.id.post);
        card=findViewById(R.id.card);
        share=findViewById(R.id.buttonShare);


        FirebaseStorage storage=FirebaseStorage.getInstance();


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String post1 = post.getText().toString().trim();
                long currentTimestamp = System.currentTimeMillis();

                if (TextUtils.isEmpty(post1) && croppedUri != null) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    String pushId = reference.child("posts").push().getKey();

                    StorageReference storageReference = storage.getReference().child(uid).child(pushId);
                    storageReference.putFile(croppedUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    reference.child("posts").child(pushId).child("imageUrl").setValue(uri.toString());
                                    reference.child("posts").child(pushId).child("time").setValue(currentTimestamp);
                                    reference.child("posts").child(pushId).child("sender").setValue(uid);
                                    reference.child("users").child(uid).child("posted").child(pushId).setValue(true);
                                    Toast.makeText(writePost.this, "Posted", Toast.LENGTH_SHORT).show();
                                    pic.setVisibility(View.GONE);
                                    card.setVisibility(View.GONE);
                                    post.setText("");
                                }
                            });
                        }
                    });
                } else if (!TextUtils.isEmpty(post1)) {
                    if (croppedUri != null) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        String pushId = reference.child("posts").push().getKey();

                        StorageReference storageReference = storage.getReference().child(uid).child(pushId);
                        storageReference.putFile(croppedUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        reference.child("posts").child(pushId).child("imageUrl").setValue(uri.toString());
                                        reference.child("posts").child(pushId).child("content").setValue(post1);
                                        reference.child("posts").child(pushId).child("time").setValue(currentTimestamp);
                                        reference.child("posts").child(pushId).child("sender").setValue(uid);
                                        reference.child("users").child(uid).child("posted").child(pushId).setValue(true);
                                        Toast.makeText(writePost.this, "Posted", Toast.LENGTH_SHORT).show();
                                        pic.setVisibility(View.GONE);
                                        card.setVisibility(View.GONE);
                                        post.setText("");
                                    }
                                });
                            }
                        });
                    } else {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        String pushId = reference.child("posts").push().getKey();
                        reference.child("posts").child(pushId).child("content").setValue(post1);
                        reference.child("posts").child(pushId).child("time").setValue(currentTimestamp);
                        reference.child("posts").child(pushId).child("sender").setValue(uid);
                        reference.child("users").child(uid).child("posted").child(pushId).setValue(true);
                        Toast.makeText(writePost.this, "Posted", Toast.LENGTH_SHORT).show();
                        post.setText("");
                    }
                }
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                // Start the activity to pick an image
                startActivityForResult(intent,Gallery_code);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_code && resultCode == Activity.RESULT_OK && data != null) {
            Uri croppedUri = data.getData();
            selectedImage=croppedUri;
            pic.setVisibility(View.VISIBLE);
            // Display the cropped image in the ImageView
            pic.setImageURI(selectedImage);
        }

    }


}