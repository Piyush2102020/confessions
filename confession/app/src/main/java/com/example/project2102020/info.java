package com.example.project2102020;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class info extends AppCompatActivity {

    EditText userName;
    Spinner spinner;
    Button go;
    RadioGroup genderGroup;
    ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        go=findViewById(R.id.go);
        genderGroup = findViewById(R.id.genderGroup);
        userName=findViewById(R.id.randomName);
        list=new ArrayList<>();
        SharedPreferences pre=getSharedPreferences("project2102020",MODE_PRIVATE);
        String uid=pre.getString("uid","");
        SharedPreferences.Editor editor=pre.edit();




        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName1=userName.getText().toString().trim();
                String gender1 = getSelectedGender();
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("available").hasChild(userName1)){
                            Toast.makeText(info.this, "username already taken", Toast.LENGTH_SHORT).show();
                        } else if (userName1.isEmpty()||userName1==null) {

                            Toast.makeText(info.this, "UserName cannot be empty", Toast.LENGTH_SHORT).show();
                            
                        } else if (gender1.isEmpty()) {

                            Toast.makeText(info.this, "Please Select a gender", Toast.LENGTH_SHORT).show();
                            return;

                        } else {
                            reference.child("available").child(userName1).child("uid").setValue(uid);
                            reference.child("available").child(userName1).child("gender").setValue(gender1);
                            Intent intent=new Intent(getApplicationContext(),grpChat.class);
                            editor.putString("randomName",userName1);
                            editor.apply();
                            intent.putExtra("user",userName1);
                            intent.putExtra("gender",gender1);
                            startActivity(intent);
                            finish();


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private String getSelectedGender() {
        int selectedId = genderGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.radioMale) {
            return "Male";
        } else if (selectedId == R.id.radioFemale) {
            return "Female";
        } else {
            return ""; // No gender selected
        }
    }}