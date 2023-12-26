package com.example.project2102020;

import androidx.annotation.NonNull;

import android.Manifest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.util.ArrayList;

public class grpChat extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageView openDrawerLay,send,galleryG,messagePic;
    EditText message;
    RecyclerView availableUsers,chatView;

    ArrayList<chatModel>chatList;
    String userName;
    ArrayList<userModel>list;
    AdapterChat adapterChat;
    private final int gallery=100;
    String uid;
    String userName1;
    Uri selectedImage;

    availableAdapter availableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grp_chat);
        chatList=new ArrayList<>();
        Intent intent=getIntent();
        userName1=intent.getStringExtra("user");
        message=findViewById(R.id.message);
        messagePic=findViewById(R.id.pic);
        send=findViewById(R.id.send);
        drawerLayout=findViewById(R.id.drawerLayout);
        galleryG=findViewById(R.id.galleryG);
        openDrawerLay=findViewById(R.id.openDrawerLay);

        SharedPreferences pre=getSharedPreferences("project2102020",MODE_PRIVATE);
        SharedPreferences.Editor editor=pre.edit();
        editor.putString("randomName",userName1);
        editor.apply();
        uid=pre.getString("uid","");
        availableUsers=findViewById(R.id.availableUsers);
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        chatView=findViewById(R.id.chatView);
        list=new ArrayList<>();
        availableAdapter=new availableAdapter(getApplicationContext(),list);
        adapterChat =new AdapterChat(getApplicationContext(),chatList,uid);
        availableUsers.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);   // Stack new items from the end
        chatView.setLayoutManager(layoutManager); // Set the layout manager to your RecyclerView



        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();

        reference.keepSynced(true);



        galleryG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent1,gallery);
            }
        });
        reference.child("grp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){

                    String  push=snapshot.getKey();
                    String message1=snapshot1.child("message").getValue(String.class);
                    String userName=snapshot1.child("userName").getValue(String.class);
                    String uid1=snapshot1.child("uid").getValue(String.class);
                    String image=snapshot1.child("imageUrl").getValue(String.class);




                    chatModel chatModel=new chatModel(userName,message1,uid1, mAuth.getUid(),image);
                    chatList.add(chatModel);
                    adapterChat.notifyDataSetChanged();
                    chatView.smoothScrollToPosition(adapterChat.getItemCount()-1);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
reference.child("available").addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        list.clear();
        for (DataSnapshot snapshot1:snapshot.getChildren()){
            String userName=snapshot1.getKey();
            String uid=snapshot1.child("uid").getValue(String.class);
            String gender=snapshot1.child("gender").getValue(String.class);
            userModel userModel=new userModel(gender,userName,uid);
            list.add(userModel);
            availableAdapter.notifyDataSetChanged();


        }

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});





        openDrawerLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message1=message.getText().toString().trim();

                if (message1.isEmpty()){
                    message.setText("");
                } else if (selectedImage!=null ) {

                    String push= reference.child("grp").push().getKey();

                    FirebaseStorage storage=FirebaseStorage.getInstance();
                    StorageReference storageReference=storage.getReference().child(push);


                    storageReference.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    reference.child("grp").child(push).child("imageUrl").setValue(uri.toString());
                                    reference.child("grp").child(push).child("message").setValue(message1);
                                    reference.child("grp").child(push).child("userName").setValue(userName1);
                                    reference.child("grp").child(push).child("uid").setValue(uid);
                                    message.setText("");

                                }
                            });
                        }
                    });

                } else {


                    String push= reference.child("grp").push().getKey();
                    reference.child("grp").child(push).child("message").setValue(message1);
                    reference.child("grp").child(push).child("userName").setValue(userName1);
                    reference.child("grp").child(push).child("uid").setValue(uid);
                    message.setText("");

                }



            }
        });


        availableUsers.setAdapter(availableAdapter);
        chatView.setAdapter(adapterChat);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == gallery && resultCode == Activity.RESULT_OK && data != null) {
            Uri croppedUri = data.getData();
            selectedImage=croppedUri;
            messagePic.setVisibility(View.VISIBLE);
            // Display the cropped image in the ImageView
            messagePic.setImageURI(selectedImage);
        }

    }
}