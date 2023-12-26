package com.example.project2102020;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class comment extends AppCompatActivity {

    RecyclerView comments;
    TextView textTime,textMessage;
    commentAdapter adapter;
    ArrayList<String>list;
    EditText comment;
    ImageView send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        comments=findViewById(R.id.comments);
        textTime=findViewById(R.id.textTime);
        textMessage=findViewById(R.id.textMessage);
        comment=findViewById(R.id.comment);
        list=new ArrayList<>();
        adapter=new commentAdapter(getApplicationContext(),list);
        send=findViewById(R.id.send);
        Intent intent=getIntent();
        String message=intent.getStringExtra("message");
        String time=intent.getStringExtra("time");
        String id=intent.getStringExtra("id");
        textTime.setText(time);
        textMessage.setText(message);
        comments.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {String comment1=comment.getText().toString().trim();

                if (comment1.isEmpty()||comment1==null){}


                else {

                    comment.setText("");
                    String commentId= reference.child("posts").child(id).child("comment").push().getKey();
                    reference.child("posts").child(id).child("comment").child(commentId).child("comment").setValue(comment1);
                    list.add(comment1);
                    adapter.notifyDataSetChanged();

                }
            }
        });


        reference.child("posts").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1:snapshot.child("comment").getChildren()){
                    String comment=snapshot1.child("comment").getValue(String.class);
                    list.add(comment);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        comments.setAdapter(adapter);
    }
}