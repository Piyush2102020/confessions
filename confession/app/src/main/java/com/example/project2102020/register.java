package com.example.project2102020;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class register extends AppCompatActivity {
    EditText username,name,instaUrl;
    TextView register;

    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]+$";

    // Function to validate the username
    private boolean isValidUsername(String username) {
        return username.matches(USERNAME_PATTERN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username=findViewById(R.id.username);
        name=findViewById(R.id.name);
        instaUrl=findViewById(R.id.instaUrl);
        register=findViewById(R.id.register);
        SharedPreferences pre=getSharedPreferences("project2102020",MODE_PRIVATE);
        String uid=pre.getString("uid","");


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username1=username.getText().toString().trim();
                String name1=name.getText().toString().trim();
                String instaUrl1=instaUrl.getText().toString().trim();
                if (username1.isEmpty() || username1==null){
                    Toast.makeText(register.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }


                else if (!isValidUsername(username1)){

                    Toast.makeText(register.this, "Invalid username", Toast.LENGTH_SHORT).show();
                }
                
                else {

                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.child("username").hasChild(username1)){
                                Toast.makeText(register.this, "Username Already Taken", Toast.LENGTH_SHORT).show();
                            }

                            else {
                                reference.child("users").child(uid).child("username").setValue(username1);
                                reference.child("users").child(uid).child("name").setValue(name1);

                                if (instaUrl1!=null || !instaUrl1.isEmpty()){

                                    reference.child("users").child(uid).child("instagram").setValue(instaUrl1);

                                }
                                reference.child("users").child(uid).child("name").setValue(name1);
                                SharedPreferences.Editor editor=pre.edit();
                                editor.putBoolean("isLoggedIn",true);
                                editor.commit();
                                Intent intent=new Intent(getApplicationContext(),frameMain.class);
                                startActivity(intent);
                                finish();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });
    }
}