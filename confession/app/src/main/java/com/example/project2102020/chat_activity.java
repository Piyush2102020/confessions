package com.example.project2102020;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class chat_activity extends AppCompatActivity {

    RecyclerView chatPrivate;
    ImageView sendPrivate,galleryPrivate,messagePic;
    TextView userNamePrivate;
    EditText messagePrivate;
    Uri selectedImage;
    Boolean temp=false;
    ArrayList<chatModel>list;
    AdapterChat adapterChat;
    String newChat;
    String prevChatId;
    private final int galleryCode=150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatPrivate=findViewById(R.id.chatPrivate);
        messagePrivate=findViewById(R.id.messagePrivate);
        sendPrivate=findViewById(R.id.sendPrivate);
        galleryPrivate=findViewById(R.id.galleryPrivate);
        messagePic=findViewById(R.id.messagePic);
        userNamePrivate=findViewById(R.id.userNamePrivate);
        list=new ArrayList<>();
        SharedPreferences pre=getSharedPreferences("project2102020",MODE_PRIVATE);
        String uid=pre.getString("uid","");
        String myUserName=pre.getString("randomName","");
        String targetUser=getIntent().getStringExtra("targetUser");
        String targetUid=getIntent().getStringExtra("targetUid");
        adapterChat=new AdapterChat(getApplicationContext(),list,uid);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        userNamePrivate.setText(targetUser);
        chatPrivate.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        reference.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                temp=snapshot.child("chat").hasChild(targetUser);
                if (temp){
                    prevChatId=snapshot.child("chat").child(targetUser).child("chatId").getValue(String.class);
                    Toast.makeText(chat_activity.this, "Chat available of user+"+targetUser +"chatid is "+prevChatId+temp, Toast.LENGTH_SHORT).show();
                    loadChatMessages(prevChatId,reference);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        sendPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messagePrivate.getText().toString().trim();

                if (temp) {
                    String prevChat = prevChatId;
                    setPrevChat(prevChat,message,myUserName,uid,selectedImage);
                    // Do something with prevChat if needed
                } else {



                                        setNewChat(message, targetUser, uid,myUserName,selectedImage); // Call the setNewChat method
                }
            }
        });



        galleryPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,galleryCode);

            }
        });

        chatPrivate.setAdapter(adapterChat);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == galleryCode && resultCode == Activity.RESULT_OK && data != null) {
            Uri croppedUri = data.getData();
            selectedImage=croppedUri;
            messagePic.setVisibility(View.VISIBLE);
            // Display the cropped image in the ImageView
            messagePic.setImageURI(selectedImage);
        }
    }

    public void setPrevChat(String prevChatId, String message,String userName,String uid,Uri selectedImage){
        if (message.isEmpty() || message==null){

        }
        else if (selectedImage!=null){


            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            String messagePushId = reference.child("privateChat").child(prevChatId).push().getKey();
            FirebaseStorage storage=FirebaseStorage.getInstance();
            StorageReference storageReference=storage.getReference().child(messagePushId);
            storageReference.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl=uri.toString();
                            reference.child("privateChat").child(prevChatId).child(messagePushId).child("message").setValue(message);
                            reference.child("privateChat").child(prevChatId).child(messagePushId).child("userName").setValue(userName);
                            reference.child("privateChat").child(prevChatId).child(messagePushId).child("uid").setValue(uid);
                            reference.child("privateChat").child(prevChatId).child(messagePushId).child("imageUrl").setValue(imageUrl);



                        }
                    });
                }
            });

        }

        else {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            String messagePushId = reference.child("privateChat").child(prevChatId).push().getKey();
            reference.child("privateChat").child(prevChatId).child(messagePushId).child("message").setValue(message);
            reference.child("privateChat").child(prevChatId).child(messagePushId).child("userName").setValue(userName);
            reference.child("privateChat").child(prevChatId).child(messagePushId).child("uid").setValue(uid);


        }
         }
    public void setNewChat(String message, String targetUser, String uid,String userName,Uri selectedImage) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        if (message.isEmpty() || message==null){

        } else if (selectedImage!=null) {


            String chatPushId = reference.child("privateChat").push().getKey();
            temp = true;
            newChat = chatPushId;
            reference.child("users").child(uid).child("chat").child(targetUser).child("chatId").setValue(chatPushId);
            String messagePushId = reference.child("privateChat").child(chatPushId).push().getKey();

            FirebaseStorage storage=FirebaseStorage.getInstance();
            StorageReference storageReference=storage.getReference().child(messagePushId);
            storageReference.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl=uri.toString();
                            reference.child("privateChat").child(prevChatId).child(messagePushId).child("message").setValue(message);
                            reference.child("privateChat").child(prevChatId).child(messagePushId).child("userName").setValue(userName);
                            reference.child("privateChat").child(prevChatId).child(messagePushId).child("uid").setValue(uid);
                            reference.child("privateChat").child(prevChatId).child(messagePushId).child("imageUrl").setValue(imageUrl);



                        }
                    });
                }
            });



        }


        else {
            String chatPushId = reference.child("privateChat").push().getKey();
            temp = true;
            newChat = chatPushId;
            reference.child("users").child(uid).child("chat").child(targetUser).child("chatId").setValue(chatPushId);

            String messagePushId = reference.child("privateChat").child(chatPushId).push().getKey();
            reference.child("privateChat").child(chatPushId).child(messagePushId).child("message").setValue(message);
            reference.child("privateChat").child(chatPushId).child(messagePushId).child("userName").setValue(userName);
            reference.child("privateChat").child(chatPushId).child(messagePushId).child("uid").setValue(uid);

        }
        loadChatMessages(newChat,reference);
    }

    private void loadChatMessages(String chatId, DatabaseReference reference) {
        if (prevChatId != null) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            reference.child("privateChat").child(chatId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                        String message = messageSnapshot.child("message").getValue(String.class);
                        String userName = messageSnapshot.child("userName").getValue(String.class);
                        String uid1 = messageSnapshot.child("uid").getValue(String.class);
                        String image=messageSnapshot.child("imageUrl").getValue(String.class);

                        chatModel chatModel = new chatModel(userName, message, uid1, mAuth.getUid(),image);
                        list.add(chatModel);
                        adapterChat.notifyDataSetChanged();
                        chatPrivate.smoothScrollToPosition(adapterChat.getItemCount() - 1);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled if needed
                }
            });
        } else {
            // Handle the case when prevChatId is null
        }
    }

}